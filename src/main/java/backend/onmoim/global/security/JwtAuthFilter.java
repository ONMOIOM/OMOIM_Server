package backend.onmoim.global.security;

import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserQueryRepository userQueryRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // 토큰 가져오기
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            token = token.replace("Bearer ", "");

            if (jwtUtil.isValid(token)) {
                // JWT에서 id 추출
                Long id = jwtUtil.getId(token);  // JwtUtil 수정 필요

                // DB에서 User 직접 조회
                User user = userQueryRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // SecurityContext에 User 객체 직접 설정
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        user,  // principal에 User 객체 직접!
                        null
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            ApiResponse<Void> errorResponse = ApiResponse.onFailure(
                    GeneralErrorCode.UNAUTHORIZED,
                    null
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), errorResponse);
        }
    }
}
