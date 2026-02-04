package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.converter.EventConverter;
import backend.onmoim.domain.event.dto.EventResDTO;
import backend.onmoim.domain.event.entity.Event;
import backend.onmoim.domain.event.enums.Status;
import backend.onmoim.domain.event.repository.EventRepository;
import backend.onmoim.global.common.code.GeneralErrorCode;
import backend.onmoim.global.common.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private  final EventRepository eventRepository;

    @Override
    public EventResDTO createDraftEvent() {
        Event eventEntity = Event.builder()
                .status(Status.DRAFTED)
                .build();

        Event saved =  eventRepository.save(eventEntity);
        return EventConverter.toResDTO(saved);
    }

    @Override
    @Transactional
    public EventResDTO patchEvent(Long eventId, Map<String, Object> updates) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.BAD_REQUEST));
        if(updates.containsKey("title")){
            event.setTitle((String) updates.get("title"));
        }

        if(updates.containsKey("startTime")){
            Object startTimeValue = updates.get("startTime");
            if (startTimeValue instanceof String) {
                event.setStartTime(LocalDateTime.parse((String) startTimeValue));
            } else if (startTimeValue instanceof LocalDateTime) {
                event.setStartTime((LocalDateTime) startTimeValue);
            }
        }

        if(updates.containsKey("endTime")){
            Object endTimeValue = updates.get("endTime");
            if(endTimeValue instanceof String) {
                event.setEndTime(LocalDateTime.parse((String) endTimeValue));
            }else if (endTimeValue instanceof LocalDateTime) {
                event.setEndTime((LocalDateTime) endTimeValue);
            }
        }

        if(updates.containsKey("streetAddress")){
            event.setStreetAddress((String) updates.get("streetAddress"));
        }

        if(updates.containsKey("lotNumberAddress")){
            event.setLotNumberAddress((String) updates.get("lotNumberAddress"));
        }

        if(updates.containsKey("price")){
            event.setPrice((Integer) updates.get("price"));
        }

        if(updates.containsKey("introduction")){
            event.setIntroduction((String) updates.get("introduction"));
        }

        return EventConverter.toResDTO(event);
    }

    @Override
    @Transactional
    public EventResDTO publishEvent(Long eventID) {
        Event event = eventRepository.findById(eventID).orElseThrow();
        event.setStatus(Status.PUBLISHED);

        return EventConverter.toResDTO(event);
    }
}
