package backend.onmoim.domain.user.controller;

import backend.onmoim.domain.user.dto.req.LoginRequestDTO;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "사용자 API", description = "사용자 관련 API")
public interface UserControllerDocs {

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    ApiResponse<SignUpResponseDTO.SignUpDTO> signUp(
            @RequestBody @Valid SignUpRequestDTO.SignUpDTO dto
    );

    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
    ApiResponse<LoginResponseDTO.LoginDTO> login(
            @RequestBody @Valid LoginRequestDTO.LoginDTO dto,
            HttpServletResponse response
    );
}
