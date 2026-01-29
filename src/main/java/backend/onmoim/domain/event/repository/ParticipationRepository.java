package backend.onmoim.domain.event.repository;

import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.entity.Participation;
import backend.onmoim.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    // 특정 유저가 특정 행사에 남긴 투표 기록 찾기
    Optional<Participation> findByUserAndEvent(User user, Event event);

    // 특정 행사의 모든 참여자 목록 가져오기
    java.util.List<Participation> findAllByEvent(Event event);
}
//DB에서 "내가 이 행사에 투표한 적이 있나?"를 확인