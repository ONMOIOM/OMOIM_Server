package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.converter.EventConverter;
import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.dto.res.EventUpdateDTO;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.enums.Status;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public EventResDTO createDraftEvent() {
        Event eventEntity = Event.builder()
                .status(Status.DRAFTED)
                .build();

        Event saved = eventRepository.save(eventEntity);
        return EventConverter.toResDTO(saved);
    }

    @Override
    @Transactional
    public EventResDTO patchEvent(Long eventId, EventUpdateDTO updateDTO) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));

        Event updatedEvent = event.update(
                updateDTO.getTitle(),
                updateDTO.getStartTime(),
                updateDTO.getEndTime(),
                updateDTO.getStreetAddress(),
                updateDTO.getLotNumberAddress(),
                updateDTO.getPrice(),
                updateDTO.getPlaylistUrl(),
                updateDTO.getCapacity(),
                updateDTO.getIntroduction()
        );

        Event saved = eventRepository.save(updatedEvent);
        return EventConverter.toResDTO(saved);
    }

    @Override
    @Transactional
    public EventResDTO publishEvent(Long eventID) {
        Event event = eventRepository.findById(eventID)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.EVENT_NOT_FOUND));
        Event publishedEvent = event.publish();
        Event saved = eventRepository.save(publishedEvent);

        return EventConverter.toResDTO(saved);
    }
}
