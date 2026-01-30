package backend.onmoim.domain.user.service;

import backend.onmoim.domain.auth.dto.request.EmailAuthRequestDTO;
import backend.onmoim.domain.auth.exception.EmailAuthErrorCode;
import backend.onmoim.domain.auth.exception.EmailAuthException;
import backend.onmoim.domain.auth.service.command.EmailAuthCommandService;
import backend.onmoim.domain.user.converter.UserConverter;
import backend.onmoim.domain.user.dto.req.LoginRequestDTO;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.res.LoginResponseDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.enums.Status;
import backend.onmoim.domain.user.repository.UserQueryRepository;
import backend.onmoim.domain.user.repository.UserRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.utils.JwtUtil;
import backend.onmoim.global.utils.RandomNicknameGenerator;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService{

    private final UserQueryRepository userQueryRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final RandomNicknameGenerator randomNicknameGenerator;
    private final EmailAuthCommandService emailAuthCommandService;

    @Override
    public LoginResponseDTO.LoginDTO login(
            LoginRequestDTO.@Valid LoginDTO dto,
            HttpServletResponse response
    ) {

        // User 조회
        User user = userQueryRepository.findByEmail(dto.email())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.MEMBER_NOT_FOUND));

        if (user.getStatus() != Status.ACTIVE) {
            throw new GeneralException(GeneralErrorCode.USER_INACTIVE);
        }

        // 이메일 인증코드 검증
        emailAuthCommandService.verifyCode(
                new EmailAuthRequestDTO.VerifyCodeDTO(dto.email(), dto.authCode())
        );

        // 엑세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        jwtUtil.setRefreshTokenCookie(response, refreshToken);
        String refreshKey = "refresh:token:" + user.getId();
        jwtUtil.storeRefreshToken(refreshKey, refreshToken);

        // DTO 조립
        return UserConverter.toLoginDTO(user, accessToken);
    }

    // 회원가입
    @Transactional
    @Override
    public SignUpResponseDTO.SignUpDTO signup(SignUpRequestDTO.SignUpDTO dto) {

        // 먼저 이메일 인증 검증 (존재 여부 노출 방지)
        try {
            emailAuthCommandService.verifyCode(
                    new EmailAuthRequestDTO.VerifyCodeDTO(dto.email(), dto.authCode())
            );
        } catch (EmailAuthException e) {
            throw new EmailAuthException(EmailAuthErrorCode.DATA_NOT_FOUND);
        }

        String randomNickname = randomNicknameGenerator.generateUniqueNickname();

        User user = User.builder()
                .email(dto.email())
                .nickname(randomNickname)
                .status(Status.ACTIVE)
                .build();

        try {
            userRepository.save(user);
            return UserConverter.toSignUpDTO(user);
        } catch (DataIntegrityViolationException e) {
            throw new GeneralException(GeneralErrorCode.DUPLICATE_MEMBER);
        }
    }

}