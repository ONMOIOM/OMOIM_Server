package backend.onmoim.domain.auth.service.command;

import backend.onmoim.domain.auth.converter.EmailAuthConverter;
import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.dto.response.EmailAuthResponseDTO;
import backend.onmoim.domain.auth.entity.EmailAuth;
import backend.onmoim.domain.auth.exception.EmailAuthErrorCode;
import backend.onmoim.domain.auth.exception.EmailAuthException;
import backend.onmoim.domain.auth.repository.EmailAuthRepository;
import backend.onmoim.global.common.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 실패한 계정을 뒤로 보내는 이메일 서비스
 */
@Slf4j
@Service
@Transactional
@SuppressWarnings("unchecked")
class EmailAuthCommandServiceImpl implements EmailAuthCommandService {

    private final StringRedisTemplate redisTemplate;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailAuthLogService emailAuthLogService;

    // 멀티스레드 환경에서 안전하게 리스트를 수정하기 위해 CopyOnWriteArrayList를 사용
    private final List<JavaMailSender> javaMailSenders;

    private final RestTemplate restTemplate = new RestTemplate();
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${spring.cloudflare.turnstile.secret-key}")
    private String turnstileSecret;

    @Value("${spring.cloudflare.turnstile.verify-url}")
    private String turnstileVerifyUrl;

    // 생성자에서 주입받은 원본 리스트를 스레드 안전한 가변 리스트로 변환
    public EmailAuthCommandServiceImpl(
            StringRedisTemplate redisTemplate,
            EmailAuthRepository emailAuthRepository,
            EmailAuthLogService emailAuthLogService,
            List<JavaMailSender> javaMailSenders) {
        this.redisTemplate = redisTemplate;
        this.emailAuthRepository = emailAuthRepository;
        this.emailAuthLogService = emailAuthLogService;
        // 실패 시 순서를 바꿔야 하므로 수정 가능한 CopyOnWriteArrayList로 관리
        this.javaMailSenders = new CopyOnWriteArrayList<>(javaMailSenders);
    }

    @Override
    public EmailAuthResponseDTO.VerificationResultDTO sendCode(EmailAuthRequestDTO.SendCodeDTO request, String ip) {
        // 1 중복 요청 쿨타임 체크
        String cooldownKey = "auth:cooldown:" + request.email();
        Boolean isSet = redisTemplate.opsForValue().setIfAbsent(cooldownKey, "true", Duration.ofMinutes(1));
        if (Boolean.FALSE.equals(isSet)) {
            throw new EmailAuthException(EmailAuthErrorCode.RATE_LIMITED);
        }

        //  봇 방지 검증
        verifyTurnstile(request.turnstileToken());

        // 인증 코드 및 DB 로그 미리 생성
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        String authKey = "auth:email:" + request.email();
        EmailAuth emailAuth = emailAuthRepository.save(EmailAuth.builder()
                .email(request.email())
                .code(code)
                .hashedIp(hashIp(ip))
                .isUsed(false)
                .build());

        // 재정렬 전략이 적용된 발송 로직
        boolean isSent = false;

        // javaMailSenders는 현재 가장 성공 가능성이 높은 계정 순으로 정렬
        for (JavaMailSender sender : javaMailSenders) {
            JavaMailSenderImpl impl = (JavaMailSenderImpl) sender;
            String username = impl.getUsername();
            String usageKey = "auth:mail:usage:" + username;

            // 계정별 사용량 체크 (500건)
            String usageCount = redisTemplate.opsForValue().get(usageKey);
            if (usageCount != null && Integer.parseInt(usageCount) >= 500) {
                continue;
            }

            try {
                // 발송 시도
                sendMail(sender, request.email(), code);

                // 성공 시 Redis 저장 및 사용량 갱신
                redisTemplate.opsForValue().set(authKey, code, Duration.ofMinutes(5));
                if (redisTemplate.opsForValue().increment(usageKey) == 1) {
                    redisTemplate.expire(usageKey, Duration.ofDays(1));
                } // 사용량의 TTL은 하루

                isSent = true;
                break; // 성공했으므로 루프 종료

            } catch (Exception e) {
                // 문제 발생 시 해당 계정을 리스트의 맨 뒤로 이동
                // CopyOnWriteArrayList는 순회 중에 수정해도 ConcurrentModificationException이 발생하지 않음.
                log.error("메일 발송 실패 - 계정: {}, 사유: {}. 해당 계정을 후순위로 이동합니다.", username, e.getMessage());

                javaMailSenders.remove(sender); // 현재 위치에서 제거
                javaMailSenders.add(sender);    // 리스트의 맨 끝으로 추가

                // 다음 루프에서 자동으로 다음계정 - 2순위였던 계정을 시도
            }
        }

        // 모든 시도 실패 시 롤백
        if (!isSent) {
            redisTemplate.delete(cooldownKey);
            emailAuthRepository.delete(emailAuth);
            throw new EmailAuthException(EmailAuthErrorCode.ALL_MAIL_ACCOUNTS_EXHAUSTED);
        }

        return EmailAuthConverter.toResultDTO(request.email(), 300L);
    }


    // 메일 전송
    private void sendMail(JavaMailSender mailSender, String to, String code) {
        JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(impl.getUsername());
        message.setTo(to);
        message.setSubject("[ONMOIM] 인증 코드 안내");
        message.setText("인증 코드: [" + code + "] 입니다. 5분 이내에 입력해 주세요.");
        mailSender.send(message);
    }


    @Override
    public EmailAuthResponseDTO.VerifyResponseDTO verifyCode(EmailAuthRequestDTO.VerifyCodeDTO request) {
        EmailAuth log = emailAuthRepository.findTopByEmailOrderByCreatedAtDesc(request.email())
                .orElseThrow(() -> new EmailAuthException(EmailAuthErrorCode.DATA_NOT_FOUND));
        try {// 정상 흐름
            if (log.getIsUsed()) throw new EmailAuthException(EmailAuthErrorCode.TOKEN_ALREADY_USED);

            String savedCode = redisTemplate.opsForValue().get("auth:email:" + request.email());

            if (savedCode == null) throw new EmailAuthException(EmailAuthErrorCode.TOKEN_EXPIRED);
            if (!savedCode.equals(request.code())) throw new EmailAuthException(EmailAuthErrorCode.INVALID_CODE);

            log.markAsUsed();  // 인증 성공 처리
            redisTemplate.delete("auth:email:" + request.email());

            return new EmailAuthResponseDTO.VerifyResponseDTO(request.email(), LocalDateTime.now(), "SUCCESS");
        } catch (GeneralException e) {// 예외 처리
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
        // Map.of()는 null 값을 허용하지 않으므로 사전에 null인지 검증
        if (token == null || token.isBlank()) {
            throw new EmailAuthException(EmailAuthErrorCode.BOT_DETECTED);
        }
        Map<String, String> body = Map.of("secret", turnstileSecret, "response", token);
        Map<String, Object> response = restTemplate.postForObject(turnstileVerifyUrl, body, Map.class);

        if (response == null || !Boolean.TRUE.equals(response.get("success"))) {
            throw new GeneralException(EmailAuthErrorCode.BOT_DETECTED);
        }
    }
}