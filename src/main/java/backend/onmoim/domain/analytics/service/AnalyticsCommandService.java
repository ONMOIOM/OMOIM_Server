package backend.onmoim.domain.analytics.service;

import backend.onmoim.domain.analytics.code.AnalyticsErrorCode;
import backend.onmoim.domain.analytics.entity.Analytics;
import backend.onmoim.domain.analytics.repository.AnalyticsRespository;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.common.session.RedisSessionTracker;
import jakarta.persistence.OptimisticLockException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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


    public void enterCount(Long eventId){
        LocalDate today = LocalDate.now();
        int updated = analyticsRepository.incrementClickCount(eventId, today);
        if (updated == 0) {
            throw new GeneralException(AnalyticsErrorCode.BAD_EVENT_ID);
        }
    }
}
