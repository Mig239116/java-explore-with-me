package ru.practicum.mainsvc.event.service;

import ru.practicum.mainsvc.event.dto.*;
import ru.practicum.mainsvc.event.model.EventState;
import ru.practicum.mainsvc.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainsvc.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainsvc.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

    EventFullDto addEvent(NewEventDto newEventDto, Long userId);

    Collection<EventShortDto> getEvents(Long userId);

    Collection<EventFullDto> getEvents(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size
    );

    Collection<EventShortDto> getEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            int from,
            int size
    );

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto getEvent(Long eventId);

    EventFullDto updateEvent(UpdateEventUserRequest requestDto, Long userId, Long eventId);

    EventFullDto updateEvent(UpdateEventAdminRequest requestDto, Long eventId);

    Collection<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequests(
            EventRequestStatusUpdateRequest requestDto,
            Long userId,
            Long eventId
    );
}
