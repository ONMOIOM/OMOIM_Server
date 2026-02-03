package backend.onmoim.domain.event.dto.res;

import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.entity.EventMember;
import backend.onmoim.domain.event.enums.EventStatus;
import backend.onmoim.domain.event.enums.VoteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class EventDetailResponse {

    private Long event_Id;
    private String title;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private String street_address;
    private String lot_number_address;
    private Integer price;
    private String Introduction;
    private EventStatus status;

    // (ERD에는 없지만 응답에 필요한 정보들)
    private String hostName;
    private List<ParticipantDto> participants;
    private Integer totalParticipantCount;

    public static EventDetailResponse of(Event event, List<ParticipantDto> participants, int totalCount) {
        return EventDetailResponse.builder()
                .event_Id(event.getId())
                .title(event.getTitle())
                .start_time(event.getStartTime())
                .end_time(event.getEndTime())
                .street_address(event.getStreetAddress())
                .lot_number_address(event.getLotNumberAddress())
                .price(event.getPrice())
                .Introduction(event.getIntroduction())
                .status(event.getStatus())
                .hostName(event.getHost().getNickname())
                .participants(participants)
                .totalParticipantCount(totalCount)
                .build();
    }

    @Getter
    @Builder
    public static class ParticipantDto {
        private Long userId;
        private String nickname;
        private VoteStatus status;

        public static ParticipantDto from(EventMember eventMember) {
            return ParticipantDto.builder()
                    .userId(eventMember.getUser().getId())
                    .nickname(eventMember.getUser().getNickname())
                    .status(eventMember.getStatus())
                    .build();
        }
    }
}