package backend.onmoim.domain.analytics.entity;

import backend.onmoim.domain.event.entity.Event;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Analytics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analyticsId;

    @Column(nullable=false)
    private LocalDate date;
    
    @Column(nullable = false)
    private int clickCount;

    @Column(nullable = false)
    private long avgSessionTimeSec;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public void incrementClickCount() {
        this.clickCount += 1;
    }
}
