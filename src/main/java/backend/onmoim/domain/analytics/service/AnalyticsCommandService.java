package backend.onmoim.domain.analytics.service;

import backend.onmoim.domain.analytics.code.AnalyticsErrorCode;
import backend.onmoim.domain.analytics.entity.Analytics;
import backend.onmoim.domain.analytics.repository.AnalyticsRespository;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.global.common.exception.GeneralException;
import backend.onmoim.global.common.session.RedisSessionTracker;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static backend.onmoim.domain.analytics.code.AnalyticsErrorCode.BAD_EVENT_ID;

@Transactional
@Service
@RequiredArgsConstructor
public class AnalyticsCommandService {

    private final RedisSessionTracker redisSessionTracker;
    private final AnalyticsRespository analyticsRepository;
    private final EventRepository eventRepository;

    public String sessionEnter(Long userId,Long eventId){
        String sessionId=redisSessionTracker.enter(userId,eventId);
        return sessionId;
    }


    public void exitCount(Long eventId){
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        int updated = analyticsRepository.incrementClickCount(eventId, today);
        if (updated == 0) {
            throw new GeneralException(BAD_EVENT_ID);
        }
    }

    public void sessionExit(String sessionId,Long eventId){

        RedisSessionTracker.SessionData data=redisSessionTracker.exit(sessionId);
        if(data==null){
            throw new GeneralException(AnalyticsErrorCode.REDIS_NOT_FOUND);
        }
        if (!data.getEventId().equals(eventId)) {
            throw new GeneralException(BAD_EVENT_ID);
        }
        LocalDateTime enterTime = data.getEnterTime();
        LocalDateTime exitTime= LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        Duration duration = Duration.between(enterTime,exitTime);
        long seconds = duration.getSeconds();

        analyticsRepository.updateAverageDuration(data.getEventId(),LocalDate.now(),seconds);
    }

    public void createDailyAnalyticsForAllEvents() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<Event> events = eventRepository.findAll();

        for(Event event : events){
            if(analyticsRepository.existsByEventAndDate(event,today)){
                continue;
            }

            Analytics analytics = Analytics.builder().
                                    event(event).
                                    date(today).
                                    clickCount(0).
                                    avgSessionTimeSec(0).
                                    build();
            analyticsRepository.save(analytics);
        }
    }

    public void createTodayAnalyticsTable(Event event){

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if(event==null){
            throw new GeneralException(BAD_EVENT_ID);
        }

        Analytics analytics = Analytics.builder().
                event(event).
                date(today).
                clickCount(0).
                avgSessionTimeSec(0).
                build();
        analyticsRepository.save(analytics);
    }
}
