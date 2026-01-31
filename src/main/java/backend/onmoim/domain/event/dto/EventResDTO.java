package backend.onmoim.domain.event.dto;


import backend.onmoim.domain.event.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResDTO {
    private Long eventId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String streetAddress;
    private String lotNumberAddress;
    private int price;
    private String introduction;
    private Status status;

}
