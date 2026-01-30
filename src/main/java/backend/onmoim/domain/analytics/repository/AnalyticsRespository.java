package backend.onmoim.domain.analytics.repository;

import backend.onmoim.domain.analytics.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AnalyticsRespository extends JpaRepository<Analytics,Long> {
    @Query("SELECT a FROM Analytics a WHERE a.event.id = :eventId AND a.date = :date")
    Optional<Analytics> findByEventIdAndDate(@Param("eventId") Long eventId,
                                             @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE Analytics a SET a.clickCount = a.clickCount + 1 WHERE a.event.id = :eventId AND a.date = :date")
    int incrementClickCount(@Param("eventId") Long eventId, @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE Analytics a " +
            "SET a.avgSessionTimeSec = (a.avgSessionTimeSec * a.clickCount + :sessionTime) / (a.clickCount + 1), " +
            "    a.clickCount = a.clickCount + 1 " +
            "WHERE a.event.id = :eventId AND a.date = :date")
    int updateAverageDuration(@Param("eventId") Long eventId,
                              @Param("date") LocalDate date,
                              @Param("sessionTime") long sessionTime);
}
