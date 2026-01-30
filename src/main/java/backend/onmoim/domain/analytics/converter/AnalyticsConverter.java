package backend.onmoim.domain.analytics.converter;

import backend.onmoim.domain.analytics.dto.res.AnalyticsResDto;

public class AnalyticsConverter {

    public static AnalyticsResDto.SessionStartResDto toSessionStartDTO(
            String sessionId
    ){
        return AnalyticsResDto.SessionStartResDto.builder()
                .sessionId(sessionId)
                .build();
    }
}
