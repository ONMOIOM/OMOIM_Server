package backend.onmoim.domain.analytics.service;

import backend.onmoim.domain.analytics.code.AnalyticsErrorCode;
import backend.onmoim.domain.analytics.entity.Analytics;
import backend.onmoim.domain.analytics.repository.AnalyticsRespository;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.common.session.RedisSessionTracker;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Transactional
@Service
@RequiredArgsConstructor
public class AnalyticsCommandService {

    private final RedisSessionTracker redisSessionTracker;
    private final AnalyticsRespository analyticsRespoitory;

    public String sessionEnter(Long userId,Long eventId){
        String sessionId=redisSessionTracker.enter(userId,eventId);

        return sessionId;
    }


    public void enterCount(Long eventId){
        LocalDate today = LocalDate.now();
        int retries = 5;

        while(retries>0) {
            try {
                Analytics analytics = analyticsRespoitory.findByEventIdAndDate(eventId, today)
                        .orElseThrow(() -> new GeneralException(AnalyticsErrorCode.BAD_EVENT_ID));

                analytics.incrementClickCount();

                analyticsRespoitory.save(analytics);
                break;
            }
            catch(OptimisticLockException e){
                retries--;
                if(retries == 0) throw e;
            }
        }
    }
}
