package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.entity.EventMember;
import backend.onmoim.domain.event.exception.EventException;
import backend.onmoim.domain.event.repository.EventMemberRepository;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.global.common.code.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventMemberRepository eventMemberRepository;

    // 1. 행사 상세 조회
    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(GeneralErrorCode.BAD_REQUEST));

        List<EventMember> allMembers = eventMemberRepository.findAllByEvent(event);
        int totalCount = allMembers.size();

        List<EventDetailResponse.ParticipantDto> participantDtos = allMembers.stream()
                .limit(4)
                .map(EventDetailResponse.ParticipantDto::from)
                .collect(Collectors.toList());

        return EventDetailResponse.of(event, participantDtos, totalCount);
    }

    // 2. 투표하기
    @Transactional
    public void castVote(Long eventId, User user, VoteRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(GeneralErrorCode.BAD_REQUEST));

        eventMemberRepository.findByUserAndEvent(user, event)
                .ifPresentOrElse(
                        existingMember -> existingMember.updateStatus(request.getStatus()),
                        () -> {
                            EventMember newMember = EventMember.builder()
                                    .user(user)
                                    .event(event)
                                    .status(request.getStatus())
                                    .build();
                            eventMemberRepository.save(newMember);
                        }
                );
    }

    // 3. 행사 삭제
    @Transactional
    public void deleteEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventException(GeneralErrorCode.BAD_REQUEST));

        if (!event.getHost().getId().equals(user.getId())) {
            throw new EventException(GeneralErrorCode.UNAUTHORIZED);
        }

        eventMemberRepository.deleteAllByEvent(event);
        eventRepository.delete(event);
    }
}