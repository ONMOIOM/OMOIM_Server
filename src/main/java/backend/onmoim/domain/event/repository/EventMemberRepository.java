package backend.onmoim.domain.event.repository;

import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.entity.EventMember;
import backend.onmoim.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventMemberRepository extends JpaRepository<EventMember, Long> {

    Optional<EventMember> findByUserAndEvent(User user, Event event);

    List<EventMember> findAllByEvent(Event event);

    void deleteAllByEvent(Event event);
}