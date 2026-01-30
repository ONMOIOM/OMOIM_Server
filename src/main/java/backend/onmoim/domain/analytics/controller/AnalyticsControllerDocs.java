package backend.onmoim.domain.analytics.controller;

import backend.onmoim.domain.analytics.dto.res.AnalyticsResDto;
import backend.onmoim.domain.user.dto.req.SignUpRequestDTO;
import backend.onmoim.domain.user.dto.res.SignUpResponseDTO;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
@Tag(name = "Event 통계 API", description = "통계 관련 API")
public interface AnalyticsControllerDocs {
    @Operation(summary = "입장시간 기록 및 카운트", description = "입장 시간을 기록하고 click 수를 카운트합니다.")
    public ApiResponse<AnalyticsResDto.SessionStartResDto> sessionStart(@AuthenticationPrincipal User user, @PathVariable Long eventId);
}
