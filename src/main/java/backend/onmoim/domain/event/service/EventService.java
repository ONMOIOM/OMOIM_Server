package backend.onmoim.domain.event.service;

import backend.onmoim.domain.event.dto.req.VoteRequest;
import backend.onmoim.domain.event.dto.res.EventDetailResponse;
import backend.onmoim.domain.event.dto.res.EventListResponse;
import backend.onmoim.domain.event.dto.res.EventResDTO;
import backend.onmoim.domain.event.dto.res.EventUpdateDTO;
import backend.onmoim.domain.user.entity.User;

import java.util.List;

public interface EventService {
    EventResDTO createDraftEvent();
    EventResDTO patchEvent(Long eventID, EventUpdateDTO updateDTO);
    EventResDTO publishEvent(Long eventID);

    EventDetailResponse getEventDetail(Long eventId);
    List<EventListResponse> getEvents();
    void deleteEvent(Long eventId, User user);
    void castVote(Long eventId, User user, VoteRequest request);
}
