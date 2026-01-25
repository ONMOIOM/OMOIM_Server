package backend.onmoim.domain.auth.exception;

import backend.onmoim.global.common.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EmailAuthErrorCode implements BaseErrorCode {

    BOT_DETECTED(HttpStatus.BAD_REQUEST, "AUTH_4001", "비정상적인 접근이 감지되었습니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH_4002", "인증 시간이 만료되었습니다."),
    TOKEN_ALREADY_USED(HttpStatus.BAD_REQUEST, "AUTH_4003", "이미 사용된 인증 코드입니다."),
    INVALID_CODE(HttpStatus.BAD_REQUEST, "AUTH_4005", "인증 코드가 일치하지 않습니다."),
    RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "AUTH_4029", "요청이 너무 잦습니다. 잠시 후 시도해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
