package backend.onmoim.domain.auth.controller;

import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.service.command.EmailAuthCommandService;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthCommandService emailAuthCommandService;

    @PostMapping("/verification")
    @Operation(summary = "인증 메일 발송 API")
    public ApiResponse<String> sendCode(@RequestBody EmailAuthRequestDTO.SendCodeDTO request) {
        emailAuthCommandService.sendCode(request);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, "인증 코드가 발송되었습니다.");
    }

}
