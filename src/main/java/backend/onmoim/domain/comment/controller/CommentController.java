package backend.onmoim.domain.comment.controller;

import backend.onmoim.domain.comment.converter.CommentConverter;
import backend.onmoim.domain.comment.dto.request.CommentRequestDTO;
import backend.onmoim.domain.comment.dto.response.CommentResponseDTO;
import backend.onmoim.domain.comment.entity.Comment;
import backend.onmoim.domain.comment.service.command.CommentCommandService;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Comment API", description = "댓글 관련 API입니다.")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController { // PascalCase 클래스 네이밍 준수

    private final CommentCommandService commentCommandService;

    @Operation(summary = "댓글 작성 API", description = "특정 이벤트에 댓글을 작성합니다.")
    @PostMapping("/events/{eventId}/comments")
    public ApiResponse<CommentResponseDTO.CommentResultDTO> createComment(
            @PathVariable(name = "eventId") Long eventId,
            @AuthenticationPrincipal User user,
            @RequestBody CommentRequestDTO.CreateCommentDTO request) {

        Comment comment = commentCommandService.createComment(eventId, user, request);

        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK,CommentConverter.toCommentResultDTO(comment));
    }

    @Operation(summary = "댓글 삭제 API", description = "본인이 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    public ApiResponse<String> deleteComment(
            @PathVariable(name = "eventId") Long eventId,
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal User user) {

        commentCommandService.deleteComment(commentId, user);

        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK,"댓글이 성공적으로 삭제되었습니다.");
    }
}
