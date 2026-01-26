package backend.onmoim.domain.test;

import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    private final JwtUtil jwtUtil;

    public TestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "Controller is working!";
    }

    // 현재 로그인된 사용자의 정보를 받아오는 컨트롤러
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(@AuthenticationPrincipal User user,
                                         HttpServletRequest request) {
        return ResponseEntity.ok(String.format("""
            현재 로그인 사용자
            ┌─────────────────────────────┐
            │ ID: %d
            │ Email: %s
            │ Nickname: %s
            │ Status: %s
            │ Cookie: %s
            └─────────────────────────────┘
            """,
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getStatus(),
                jwtUtil.getRefreshTokenCookie(request)));
    }
}