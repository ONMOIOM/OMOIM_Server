package backend.onmoim.domain.auth.service.command;

import backend.onmoim.domain.auth.converter.EmailAuthConverter;
import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.dto.response.EmailAuthResponseDTO;
import backend.onmoim.domain.auth.entity.EmailAuth;
import backend.onmoim.domain.auth.exception.EmailAuthErrorCode;
import backend.onmoim.domain.auth.exception.EmailAuthException;
import backend.onmoim.domain.auth.repository.EmailAuthRepository;
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
    private final EmailAuthLogService emailAuthLogService;
    private final RestTemplate restTemplate = new RestTemplate(); // Cloudflare Turnstile 서버 검증용 HTTP 클라이언트

    @Value("${spring.cloudflare.turnstile.secret-key}")
    private String turnstileSecret;

    @Value("${spring.cloudflare.turnstile.verify-url}")
    private String turnstileVerifyUrl;

    @Override
    public EmailAuthResponseDTO.VerificationResultDTO sendCode(EmailAuthRequestDTO.SendCodeDTO request, String ip) {
        // 동일 이메일에 대한 1분 쿨타임 체크 (도배 방지)
        String cooldownKey = "auth:cooldown:" + request.email();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cooldownKey))) {
            throw new EmailAuthException(EmailAuthErrorCode.RATE_LIMITED);
        }

        verifyTurnstile(request.turnstileToken()); //봇 방지 - Cloudflare Turnstile

        String code = String.format("%06d", new Random().nextInt(1000000)); // 인증코드 생성
        redisTemplate.opsForValue().set("auth:email:" + request.email(), code, Duration.ofMinutes(5)); //redis에 인증코드저장, TTL 5분

        // 발송 성공 시 1분간 재발송 금지 설정
        redisTemplate.opsForValue().set(cooldownKey, "true", Duration.ofMinutes(1));

        // IP 해싱 처리 후 저장, log남기기
        emailAuthRepository.save(EmailAuth.builder()
                .email(request.email())
                .code(code)
                .hashedIp(hashIp(ip)) // 해싱 ip, SHA-256해싱
                .isUsed(false)
                .build());

        sendMail(request.email(), code); //이메일 전송
        return EmailAuthConverter.toResultDTO(request.email(), 300L);
    }

    @Override
    public EmailAuthResponseDTO.VerifyResponseDTO verifyCode(EmailAuthRequestDTO.VerifyCodeDTO request) {
        EmailAuth log = emailAuthRepository.findTopByEmailOrderByCreatedAtDesc(request.email())
                .orElseThrow(() -> new EmailAuthException(EmailAuthErrorCode.DATA_NOT_FOUND)); // 최근 인증번호 요청 기준으로 검증

        try { // 정상흐름
            if (log.getIsUsed()) throw new EmailAuthException(EmailAuthErrorCode.TOKEN_ALREADY_USED); //이미 사용된 인증코드 차단

            String savedCode = redisTemplate.opsForValue().get("auth:email:" + request.email());  //redis로 코드 검증
            if (savedCode == null) throw new EmailAuthException(EmailAuthErrorCode.TOKEN_EXPIRED); // TTL만료
            if (!savedCode.equals(request.code())) throw new EmailAuthException(EmailAuthErrorCode.INVALID_CODE); // code 불일치

            log.markAsUsed(); // 인증성공처리
            redisTemplate.delete("auth:email:" + request.email()); // 사용된 코드 redis에서 삭제
            return new EmailAuthResponseDTO.VerifyResponseDTO(request.email(), LocalDateTime.now(), "SUCCESS");
        } catch (GeneralException e) { // 예외처리
            // 별도 트랜잭션 서비스를 호출하여 롤백 방지
            emailAuthLogService.recordFailure(log.getId(), e.getCode().getCode());
            throw e;
        }
    }

    // IP를 해싱하는 메서드 (SHA-256 해싱)
    private String hashIp(String ip) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            return "HASH_ERROR";
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