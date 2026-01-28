package backend.onmoim.domain.user.controller;

import backend.onmoim.domain.user.dto.req.LoginRequestDTO;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.domain.user.service.UserQueryService;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {

    private final UserQueryService userQueryService;


    @Override
    @PostMapping("/signup")
    public ApiResponse<SignUpResponseDTO.SignUpDTO> signUp(@RequestBody @Valid SignUpRequestDTO.SignUpDTO dto) {
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK,userQueryService.signup(dto));
    }

    @Override
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO.LoginDTO> login(
            @RequestBody @Valid LoginRequestDTO.LoginDTO dto,
            HttpServletResponse response
    ){
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, userQueryService.login(dto,response));
    }
}
