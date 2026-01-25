package backend.onmoim.domain.auth.service;

import backend.onmoim.domain.auth.dto.RotateTokenResponseDTO;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserQueryRepository userQueryRepository;
    private final RedisTemplate<String, String> redisTemplate;  // 블랙리스트용

    @Transactional(readOnly = true)
    public RotateTokenResponseDTO rotateAccessToken(String refreshToken) {
        // 기존 검증 로직 (유효성, 블랙리스트, 사용자 확인)
        if (!jwtUtil.isValid(refreshToken)) {
            throw new GeneralException(GeneralErrorCode.INVALID_REFRESH_TOKEN);
        }
        if (redisTemplate.opsForValue().get("blacklist:" + refreshToken) != null) {
            throw new GeneralException(GeneralErrorCode.EXPIRED_REFRESH_TOKEN);
        }
        Long userId = jwtUtil.getId(refreshToken);
        User user = userQueryRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        // 기존 refresh 블랙리스트 추가
        Long ttl = getTokenExpiry(refreshToken);
        redisTemplate.opsForValue().set("blacklist:" + refreshToken, "true", ttl, TimeUnit.MILLISECONDS);

        // 새 access + 새 refresh 생성
        String newAccessToken = jwtUtil.createAccessToken(user);
        String newRefreshToken = jwtUtil.createRefreshToken(user);

        return RotateTokenResponseDTO.builder()
                .newAccessToken(newAccessToken)
                .newRefreshToken(newRefreshToken)
                .build();
    }

    // 로그아웃
    public void logout(String refreshToken, HttpServletResponse response) {
        // Refresh 블랙리스트 추가 (만료까지)
        Long ttl = getTokenExpiry(refreshToken);
        redisTemplate.opsForValue().set("blacklist:" + refreshToken, "true", ttl, TimeUnit.MILLISECONDS);

        jwtUtil.deleteRefreshTokenCookie(response);
    }

    private Long getTokenExpiry(String token) {
        Claims claims = jwtUtil.getClaims(token).getPayload();
        Date expiry = claims.getExpiration();
        return expiry.getTime() - System.currentTimeMillis();
    }

}
