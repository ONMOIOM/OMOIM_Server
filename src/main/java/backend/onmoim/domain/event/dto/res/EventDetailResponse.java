package backend.onmoim.domain.event.dto.res;

import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.entity.Participation;
import backend.onmoim.domain.event.enums.VoteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class EventDetailResponse {
    private Long eventId;
    private String title;
//    private LocalDateTime eventDate;
//    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String introduction;
    private String streetAddress;
    private String lotNumberAddress;
    private Integer price;
    private String playlistUrl;
    private String content;
    private List<ParticipantDto> participants;
    private int totalParticipantCount;

    @Getter
    @Builder
    public static class ParticipantDto {
        private String userName;
        private VoteStatus status;

        public static ParticipantDto from(Participation participation) {
            return ParticipantDto.builder()
                    .userName(participation.getUser().getNickname()) //User에 getNickName이라고 되어있음
                    .status(participation.getStatus())
                    .build();
        }
    }


    public static EventDetailResponse of(Event event, List<ParticipantDto> participants, int totalCount) {
        return EventDetailResponse.builder()
                .eventId(event.getId())
                .title(event.getTitle())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
//                .eventDate(event.getEventDate())
//                .location(event.getLocation())
                .lotNumberAddress(event.getLotNumberAddress())
                .streetAddress(event.getStreetAddress())
                .price(event.getPrice())
                .playlistUrl(event.getPlaylistUrl())
                .introduction(event.getIntroduction())
                .participants(participants)
                .totalParticipantCount(totalCount)
                .build();
    }
}