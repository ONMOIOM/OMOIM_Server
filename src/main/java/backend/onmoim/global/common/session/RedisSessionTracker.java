package backend.onmoim.global.common.session;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class RedisSessionTracker {
   private static final Duration TTL = Duration.ofMinutes(50);
   private static final String KEY_PREFIX = "session:enter:";
   private final ObjectMapper objectMapper;
   private final RedisTemplate<String,String> redisTemplate;

   @Data
   @AllArgsConstructor
   static class SessionData{
       private Long userId;
       private Long eventId;
       private LocalDateTime enterTime;
   }

   public String enter(Long userId,Long eventId){
       String sessionId = UUID.randomUUID().toString();
       SessionData data = new SessionData(userId,eventId,LocalDateTime.now());

       try {
           String json = objectMapper.writeValueAsString(data);
           redisTemplate.opsForValue().set(KEY_PREFIX + sessionId, json, TTL);
       } catch (JsonProcessingException e) {
           throw new RuntimeException("Redis 저장 실패", e);
       }

       return sessionId;
   }
}
