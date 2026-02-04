package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.dto.EventResDTO;

import java.util.Map;

public interface EventService {
    EventResDTO createDraftEvent();
    EventResDTO patchEvent(Long eventID, Map<String, Object> updates);

    EventResDTO publishEvent(Long eventID);


}
