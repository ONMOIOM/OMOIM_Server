package backend.onmoim.domain.auth.entity;

import backend.onmoim.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmailAuth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private Boolean isUsed;

    private LocalDateTime verifiedAt;

    // 업데이트 메서드
    // 인증된 시간 + 사용상태 갱신
    public void markAsUsed() {
        this.isUsed = true;
        this.verifiedAt = LocalDateTime.now();
    }
}
