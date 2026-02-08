package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.dto.res.EventUpdateDTO;


public interface EventService {
    EventResDTO createDraftEvent();
    EventResDTO patchEvent(Long eventID, EventUpdateDTO updateDTO);

    EventResDTO publishEvent(Long eventID);


}
