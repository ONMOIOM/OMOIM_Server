package backend.onmoim.domain.event.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime eventDate;

    private String location;

    private Integer price;

    private String playlistUrl;

    @Column(columnDefinition = "TEXT")
    private String content;
}