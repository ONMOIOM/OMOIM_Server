package backend.onmoim.domain.event.entity;

import backend.onmoim.domain.event.enums.Status;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "Event")
@EntityListeners(AuditingEntityListener.class)
public class Event {
    @Column(name = "event_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 50, nullable = true)
    private String title;

    @Column(name = "start_time", nullable = true)
    private LocalDateTime startTime;

    @Column(nullable = true)
    private LocalDateTime endTime;

    @Column(name = "street_address", nullable = true, length = 255)
    private String streetAddress;

    @Column(name = "lot_number_address", nullable = true, length = 255)
    private String lotNumberAddress;

    @Column(name = "price", nullable = true)
    private Integer price;

    @Column(name = "introduction", nullable = true)
    private String introduction;

    @Column(name = "status", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "playlist_url", nullable = true)
    private  String playlistUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
