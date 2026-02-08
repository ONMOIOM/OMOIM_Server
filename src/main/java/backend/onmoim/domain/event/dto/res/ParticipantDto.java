package backend.onmoim.domain.event.dto.res;

import backend.onmoim.domain.event.entity.EventMember;
import backend.onmoim.domain.event.enums.VoteStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantDto {
    private Long userId;
    private String nickname;
    private VoteStatus status;

    public static ParticipantDto from(EventMember member) {
        return ParticipantDto.builder()
                .userId(member.getUser().getId())
                .nickname(member.getUser().getNickname())
                .status(member.getStatus())
                .build();
    }
}