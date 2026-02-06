package backend.onmoim.domain.event.entity;

import backend.onmoim.domain.event.enums.EventStatus;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Event extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_Id")
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "lot_number_address")
    private String lotNumberAddress;

    private Integer price;

    @Column(name = "Introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "playlist_url", columnDefinition = "TEXT")
    private String playlistUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private EventStatus status;
}