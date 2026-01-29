package backend.onmoim.domain.event.controller;

import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.service.EventService;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
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
                                        @RequestBody VoteRequest request) {
        Long userId = 1L;
        eventService.castVote(eventId, userId, request);


        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, "투표가 완료되었습니다.");
    }
}