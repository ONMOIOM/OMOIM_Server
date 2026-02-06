package backend.onmoim.domain.comment.dto.response;

import java.time.LocalDateTime;

public class CommentResponseDTO {
    public record CommentResultDTO(
            Long commentId,
            String nickname,      // 닉네임
            String profileImageUrl, // 프로필 이미지 URL
            String content,       // 내용
            LocalDateTime createdAt // 작성일자
    ) {}
}
