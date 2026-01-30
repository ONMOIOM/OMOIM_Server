package backend.onmoim.domain.analytics.dto.res;

import lombok.Builder;

public class AnalyticsResDto {
    @Builder
    public record SessionStartResDto(String sessionId){}

    @Builder
    public record SessionEndResDto(String sessionId,long seconds){}
}
