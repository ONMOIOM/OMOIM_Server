package backend.onmoim.domain.comment.converter;

import backend.onmoim.domain.comment.dto.response.CommentResponseDTO;
import backend.onmoim.domain.comment.entity.Comment;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.user.entity.User;

public class CommentConverter {
    public static Comment toComment(String content, User user, Event event) {
        return Comment.builder()
                .content(content)
                .user(user)
                .event(event)
                .build();
    }

    public static CommentResponseDTO.CommentResultDTO toCommentResultDTO(Comment comment) {
        User user = comment.getUser();
        // 프로필 이미지가 없을 경우를 대비한 Null 체크
        String profileUrl = (user.getProfileImage() != null) ? user.getProfileImage().getImageUrl() : null;

        return new CommentResponseDTO.CommentResultDTO(
                comment.getId(),
                user.getNickname(),
                profileUrl,
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
