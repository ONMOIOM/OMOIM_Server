package backend.onmoim.domain.event.repository;

import backend.onmoim.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    // 기본적으로 findById, save, delete 등이 자동으로 포함되어 있습니다.
    // 추가적인 쿼리 메소드가 필요하면 여기에 작성하면 됩니다.
}