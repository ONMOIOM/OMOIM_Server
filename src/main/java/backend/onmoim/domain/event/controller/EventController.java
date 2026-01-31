package backend.onmoim.domain.event.controller;

import backend.onmoim.domain.event.converter.EventConverter;
import backend.onmoim.domain.event.dto.EventResDTO;
import backend.onmoim.domain.event.service.EventService;
import backend.onmoim.domain.event.service.EventServiceImpl;
import backend.onmoim.global.common.ApiResponse;
import backend.onmoim.global.common.code.BaseSuccessCode;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/events")
    public ApiResponse<EventResDTO> createDraft() {
        EventResDTO eventResDTO = eventService.createDraftEvent();
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, eventResDTO);
    }

    @PatchMapping("/events/{eventId}")
        public ApiResponse<EventResDTO> patchEvent
            (@PathVariable Long eventId, @RequestBody Map <String, Object> updates){
            EventResDTO eventResDTO = eventService.patchEvent(eventId, updates);
            return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, eventResDTO);
        }

    @PostMapping("/events/{eventId}/published")
    public ApiResponse<EventResDTO> publishEvent(@PathVariable Long eventId){
        EventResDTO eventResDTO = eventService.publishEvent(eventId);
        return ApiResponse.onSuccess(GeneralSuccessCode.REQUEST_OK, eventResDTO);
    }
}
