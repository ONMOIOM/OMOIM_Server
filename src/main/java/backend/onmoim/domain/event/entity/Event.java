package backend.onmoim.domain.event.entity;

import backend.onmoim.domain.event.enums.Status;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "Event")
@EntityListeners(AuditingEntityListener.class)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

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
    private int price;

    @Column(name = "introduction", nullable = true)
    private String introduction;

    @Column(name = "status", nullable = true, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

}
