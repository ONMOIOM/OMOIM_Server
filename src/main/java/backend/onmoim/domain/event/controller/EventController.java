package backend.onmoim.domain.event.controller;

import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.dto.res.EventListResponse;
import backend.onmoim.domain.event.service.EventService;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    // 1. 행사 목록 전체 조회 (중첩 객체 DTO 반환)
    @GetMapping
    public ApiResponse<List<EventListResponse>> getEventList() {
        List<EventListResponse> responses = eventService.getEventList();
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, responses);
    }

    // 2. 행사 상세 조회
    @GetMapping("/{eventId}")
    public ApiResponse<EventDetailResponse> getEventDetail(@PathVariable Long eventId) {
        EventDetailResponse response = eventService.getEventDetail(eventId);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, response);
    }

    // 3. 투표하기
    @PostMapping("/{eventId}/vote")
    public ApiResponse<String> castVote(@PathVariable Long eventId,
                                        @AuthenticationPrincipal User user,
                                        @RequestBody VoteRequest request) {
        eventService.castVote(eventId, user, request);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, "투표가 완료되었습니다.");
    }

    // 4. 행사 삭제
    @DeleteMapping("/{eventId}")
    public ApiResponse<String> deleteEvent(@PathVariable Long eventId,
                                           @AuthenticationPrincipal User user) {
        eventService.deleteEvent(eventId, user);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, "행사가 삭제되었습니다.");
    }
}