package backend.onmoim.domain.analytics.controller;

import backend.onmoim.domain.analytics.code.AnalyticsSuccessCode;
import backend.onmoim.domain.analytics.converter.AnalyticsConverter;
import backend.onmoim.domain.analytics.dto.res.AnalyticsResDto;
import backend.onmoim.domain.analytics.service.AnalyticsCommandService;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsCommandService analyticsCommendService;

    @PostMapping("/{eventId}/session")
    public ApiResponse<AnalyticsResDto.SessionStartResDto> sessionStart(@AuthenticationPrincipal User user, @PathVariable Long eventId)
    {
        Long userId=user.getId();
        String sessionId =analyticsCommendService.sessionEnter(userId,eventId);
        analyticsCommendService.enterCount(eventId);

        return ApiResponse.onSuccess(
                AnalyticsSuccessCode.REQUEST_OK,
                AnalyticsConverter.toSessionStartDTO(sessionId)
        );
    }
}



