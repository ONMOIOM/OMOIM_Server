package backend.onmoim.domain.comment.dto.request;

import lombok.Builder;

public class CommentRequestDTO {
    @Builder
    public record CreateCommentDTO(
            String content
    ) {}
}
