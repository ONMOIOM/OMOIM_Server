package backend.onmoim.domain.event.repository;

import backend.onmoim.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
