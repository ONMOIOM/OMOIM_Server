package backend.onmoim.domain.auth.service.command;

import backend.onmoim.domain.auth.converter.EmailAuthConverter;
import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.dto.response.EmailAuthResponseDTO;
import backend.onmoim.domain.auth.entity.EmailAuth;
import backend.onmoim.domain.auth.exception.EmailAuthErrorCode;
import backend.onmoim.domain.auth.exception.EmailAuthException;
import backend.onmoim.domain.auth.repository.EmailAuthRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // 반드시 Spring의 Value를 임포트해야 함
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
class EmailAuthCommandServiceImpl implements EmailAuthCommandService {

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final EmailAuthRepository emailAuthRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // 외부 API 호출을 위한 템플릿

    @Value("${spring.cloudflare.turnstile.secret-key}")
    private String turnstileSecret;

    @Value("${spring.cloudflare.turnstile.verify-url}")
    private String turnstileVerifyUrl;

    private static final String AUTH_PREFIX = "auth:email:";

    @Override
    public EmailAuthResponseDTO.VerificationResultDTO sendCode(EmailAuthRequestDTO.SendCodeDTO request, String ip) {
        // Cloudflare Turnstile 검증 실행
        verifyTurnstile(request.turnstileToken());

        // 6자리 난수 생성 (인증 코드)
        String code = String.format("%06d", new Random().nextInt(1000000));

        // Redis에 5분간 저장 (만료 시간 설정)
        redisTemplate.opsForValue().set(AUTH_PREFIX + request.email(), code, Duration.ofMinutes(5));

        // DB에 발송 이력 로깅 (운영 및 장애 대응용)
        emailAuthRepository.save(EmailAuth.builder()
                .email(request.email())
                .code(code)
                .ip(ip)
                .isUsed(false)
                .build());

        // 메일 발송 수행
        sendMail(request.email(), code);

        return EmailAuthConverter.toResultDTO(request.email(), 300L);
    }

    @Override
    public EmailAuthResponseDTO.VerifyResponseDTO verifyCode(EmailAuthRequestDTO.VerifyCodeDTO request) {
        // DB에서 가장 최근 인증 요청을 가져옴
        EmailAuth log = emailAuthRepository.findTopByEmailOrderByCreatedAtDesc(request.email())
                .orElseThrow(() -> new EmailAuthException(EmailAuthErrorCode.DATA_NOT_FOUND));

        try {
            // 이미 성공(isUsed=true)한 요청인지 체크 중복 검증 방지
            if (log.getIsUsed()) {
                throw new GeneralException(EmailAuthErrorCode.TOKEN_ALREADY_USED);
            }

            //  Redis에서 현재 유효한 코드 조회
            String savedCode = redisTemplate.opsForValue().get(AUTH_PREFIX + request.email());

            // 코드가 없으면 만료된 것으로 판단
            if (savedCode == null) throw new GeneralException(EmailAuthErrorCode.TOKEN_EXPIRED);

            // 입력한 코드와 불일치할 경우
            if (!savedCode.equals(request.code())) throw new GeneralException(EmailAuthErrorCode.INVALID_CODE);

            // 성공 처리- DB 상태 전이 및 Redis 데이터 즉시 삭제 (1회용 보장)
            log.markAsUsed();
            redisTemplate.delete(AUTH_PREFIX + request.email());

            return new EmailAuthResponseDTO.VerifyResponseDTO(request.email(), LocalDateTime.now(), "SUCCESS");

        } catch (GeneralException e) {
            // 실패 로깅 - 예외 발생 시 DB에 실패 사유 코드를 남김
            log.recordFailure(e.getCode().getCode());
            throw e;
        }
    }

    // Cloudflare Turnstile 서버 사이드 검증
    private void verifyTurnstile(String token) {
        Map<String, String> body = Map.of("secret", turnstileSecret, "response", token);
        Map response = restTemplate.postForObject(turnstileVerifyUrl, body, Map.class);

        if (response == null || !Boolean.TRUE.equals(response.get("success"))) {
            throw new GeneralException(EmailAuthErrorCode.BOT_DETECTED);
        }
    }

    // 이메일 발송
    private void sendMail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[ONMOIM] 인증 코드 안내");
        message.setText("인증 코드: [" + code + "] 입니다. 5분 이내에 입력해 주세요.");
        mailSender.send(message);
    }
}