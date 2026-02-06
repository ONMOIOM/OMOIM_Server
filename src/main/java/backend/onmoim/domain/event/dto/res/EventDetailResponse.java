package backend.onmoim.domain.event.dto.res;

import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.enums.EventStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventDetailResponse {


    private Long event_Id;
    private Long user_id;
    private String title;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String street_address;
    private String lot_number_address;
    private Integer price;
    private String Introduction;
    private EventStatus status;
    private String playlist;
    private Integer capacity;


    public static EventDetailResponse from(Event event) {
        return EventDetailResponse.builder()
                .user_id(event.getUser().getId())
                .event_Id(event.getId())
                .title(event.getTitle())
                .start_time(event.getStartTime())
                .end_time(event.getEndTime())
                .street_address(event.getStreetAddress())
                .lot_number_address(event.getLotNumberAddress())
                .price(event.getPrice())
                .Introduction(event.getIntroduction())
                .status(event.getStatus())
                .playlist(event.getPlaylistUrl())
                .capacity(event.getCapacity())
                .build();
    }
}