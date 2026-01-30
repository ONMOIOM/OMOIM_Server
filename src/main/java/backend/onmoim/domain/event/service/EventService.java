package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.entity.Participation;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.domain.event.repository.ParticipationRepository;
import backend.onmoim.domain.user.entity.User;
import backend.onmoim.domain.user.repository.UserRepository;
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
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;

    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("행사를 찾을 수 없습니다."));

        List<Participation> allParticipations = participationRepository.findAllByEvent(event);

        int totalCount = allParticipations.size();


        List<EventDetailResponse.ParticipantDto> participantDtos = allParticipations.stream()
                .limit(4)
                .map(EventDetailResponse.ParticipantDto::from)
                .collect(Collectors.toList());

        return EventDetailResponse.of(event, participantDtos, totalCount);
    }


    @Transactional
    public void castVote(Long eventId, Long userId, VoteRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("행사를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        participationRepository.findByUserAndEvent(user, event)
                .ifPresentOrElse(
                        existingParticipation -> existingParticipation.updateStatus(request.getStatus()),
                        () -> {
                            Participation newParticipation = Participation.builder()
                                    .user(user)
                                    .event(event)
                                    .status(request.getStatus())
                                    .build();
                            participationRepository.save(newParticipation);
                        }
                );
    }
    @Transactional
    public void deleteEvent(Long eventId, Long currentUserId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 행사를 찾을 수 없습니다."));

        //기존 데이터에 host가 null이면 에러가 날 수 있다
        if (event.getHost() == null || !event.getHost().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("행사를 삭제할 권한이 없습니다. (주최자만 삭제 가능)");
        }

        participationRepository.deleteAllByEvent(event);

        eventRepository.delete(event);
    }
}