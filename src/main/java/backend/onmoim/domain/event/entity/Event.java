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

    @Column(name = "startTime", nullable = true)
    private LocalDateTime startTime;

    @Column(name = "endTime", nullable = true)
    private LocalDateTime endTime;

    @Column(name = "streetAddress", nullable = true, length = 255)
    private String streetAddress;

    @Column(name = "lotNumberAddress", nullable = true, length = 255)
    private String lotNumberAddress;

    @Column(name = "price", nullable = true)
    private int price;

    @Column(name = "introduction", nullable = true)
    private String introduction;

    @Column(name = "status", nullable = true)
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    @Column(name = "createdAt", nullable = true)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updatedAt", nullable = true)
    private LocalDateTime updatedAt;

}
