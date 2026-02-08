package backend.onmoim.domain.event.entity;

import backend.onmoim.domain.analytics.entity.Analytics;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Event extends BaseEntity {
    @Column(name = "event_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime eventDate;

    private String location;

    private Integer price;

    private String playlistUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "event")
    private List<Analytics> analytics = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User host;
}