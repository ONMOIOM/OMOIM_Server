package backend.onmoim.domain.event.entity;

import backend.onmoim.domain.event.enums.VoteStatus;
import backend.onmoim.domain.user.entity.User; // User 엔티티 import (패키지 경로 확인 필요)
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Participation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가 투표했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 어떤 행사에 투표했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    // 투표 상태 (참여/고민/불참)
    @Enumerated(EnumType.STRING)
    private VoteStatus status;

    // [중요] 상태 변경 메소드 (Service에서 사용)
    public void updateStatus(VoteStatus newStatus) {
        this.status = newStatus;
    }
}
//유저와 행사를 연결해준다. 누가 어떤 행사에 투표 했는지 db에 저징