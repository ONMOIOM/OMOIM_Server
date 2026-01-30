package backend.onmoim.domain.analytics.service;

import backend.onmoim.domain.analytics.code.AnalyticsErrorCode;
import backend.onmoim.domain.analytics.converter.AnalyticsConverter;
import backend.onmoim.domain.analytics.dto.res.AnalyticsResDto;
import backend.onmoim.domain.analytics.repository.AnalyticsRespository;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.common.session.RedisSessionTracker;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class AnalyticsCommandService {

    private final RedisSessionTracker redisSessionTracker;
    private final AnalyticsRespository analyticsRepository;

    public String sessionEnter(Long userId,Long eventId){
        String sessionId=redisSessionTracker.enter(userId,eventId);
        return sessionId;
    }


    public void exitCount(Long eventId){
        LocalDate today = LocalDate.now();
        int updated = analyticsRepository.incrementClickCount(eventId, today);
        if (updated == 0) {
            throw new GeneralException(AnalyticsErrorCode.BAD_EVENT_ID);
        }
    }

    public AnalyticsResDto.SessionEndResDto sessionExit(Long eventId, String sessionId){

        RedisSessionTracker.SessionData data=redisSessionTracker.exit(sessionId);

        LocalDateTime enterTime = data.getEnterTime();
        LocalDateTime exitTime= LocalDateTime.now();

        Duration duration = Duration.between(enterTime,exitTime);
        long seconds = duration.getSeconds();

        analyticsRepository.updateAverageDuration(data.getEventId(),LocalDate.now(),seconds);

        return AnalyticsConverter.toSessionEndDTO(sessionId,seconds);
    }
}
