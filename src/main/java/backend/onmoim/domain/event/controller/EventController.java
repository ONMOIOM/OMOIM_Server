package backend.onmoim.domain.event.controller;

import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.service.EventService;
import backend.onmoim.domain.user.entity.User; // 👈 User 엔티티 import 확인!
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;


    @GetMapping("/{eventId}")
    public ApiResponse<EventDetailResponse> getEventDetail(@PathVariable Long eventId) {
        EventDetailResponse response = eventService.getEventDetail(eventId);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, response);
    }


    @PostMapping("/{eventId}/vote")
    public ApiResponse<String> castVote(@PathVariable Long eventId,
                                        @AuthenticationPrincipal User user,
                                        @RequestBody VoteRequest request) {

        eventService.castVote(eventId, user, request);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, "투표가 완료되었습니다.");
    }


    @DeleteMapping("/{eventId}")
    public ApiResponse<String> deleteEvent(@PathVariable Long eventId,
                                           @AuthenticationPrincipal User user) {
        eventService.deleteEvent(eventId, user);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, "행사가 삭제되었습니다.");
    }
}