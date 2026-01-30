package backend.onmoim.domain.user.service;

public interface UserCommandService {
    void withdraw(Long userId);  // 비밀번호 확인 후 탈퇴
}
