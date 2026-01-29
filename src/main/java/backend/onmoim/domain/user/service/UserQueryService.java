package backend.onmoim.domain.user.service;

import backend.onmoim.domain.user.dto.req.LoginRequestDTO;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

public interface UserQueryService {
    LoginResponseDTO.LoginDTO login(@Valid LoginRequestDTO.LoginDTO dto, HttpServletResponse response);

    // 회원가입
    @Transactional
    SignUpResponseDTO.SignUpDTO signup(SignUpRequestDTO.SignUpDTO dto);
}
