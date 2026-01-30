package backend.onmoim.domain.analytics.repository;

import backend.onmoim.domain.analytics.entity.Analytics;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
