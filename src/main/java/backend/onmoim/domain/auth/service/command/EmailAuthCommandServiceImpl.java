package backend.onmoim.domain.auth.service.command;

import backend.onmoim.domain.auth.converter.EmailAuthConverter;
import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.dto.response.EmailAuthResponseDTO;
import backend.onmoim.domain.auth.entity.EmailAuth;
import backend.onmoim.domain.auth.repository.EmailAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Duration;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
class EmailAuthCommandServiceImpl implements EmailAuthCommandService {

    private final StringRedisTemplate redisTemplate;
    private final JavaMailSender mailSender;
    private final EmailAuthRepository emailAuthRepository;

    private static final String AUTH_PREFIX = "auth:email:";
    private static final long EXPIRE_DURATION = 300L; // 만료시간 300초(5분)

    @Override
    public EmailAuthResponseDTO.VerificationResultDTO sendCode(EmailAuthRequestDTO.SendCodeDTO request) {
        String code = String.format("%06d", new Random().nextInt(1000000)); // 6자리 무작위 난수

        // Redis 저장 ,TTL 5분
        redisTemplate.opsForValue().set(AUTH_PREFIX + request.email(), code, Duration.ofSeconds(EXPIRE_DURATION));

        // DB 로깅
        EmailAuth emailAuth = EmailAuthConverter.toEmailAuth(request.email(), code);
        emailAuthRepository.save(emailAuth);

        // 메일 발송
        sendMail(request.email(), code);

        return EmailAuthConverter.toResultDTO(request.email(), EXPIRE_DURATION);
    }

    private void sendMail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[ONMOIM] 인증 코드 안내");
        message.setText("인증 코드: [" + code + "] 입니다. 5분 이내에 입력해 주세요.");
        mailSender.send(message);
    } // 메일 메시지 내용 설정
}
