package ru.practicum.mainsvc.event.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.event.dto.EventFullDto;
import ru.practicum.mainsvc.event.dto.EventShortDto;
import ru.practicum.mainsvc.event.dto.NewEventDto;
import ru.practicum.mainsvc.event.dto.UpdateEventUserRequest;
import ru.practicum.mainsvc.event.service.EventService;
import ru.practicum.mainsvc.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainsvc.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainsvc.request.dto.ParticipationRequestDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@Validated
public class EventPrivateController {
    private final EventService eventService;

    @Autowired
    public EventPrivateController(@Qualifier("eventServiceImpl") EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@RequestBody @Valid NewEventDto eventDto, @PathVariable long userId) {
        log.debug("POST users/id/event - posting new event");
        //EventValidator.validateNewEvent(eventDto);
        if (eventDto.getRequestModeration() == null) {
            eventDto.setRequestModeration(true);
        }
        if (eventDto.getParticipantLimit() == null) {
            eventDto.setParticipantLimit(0);
        }
        if (eventDto.getPaid() == null) {
            eventDto.setPaid(false);
        }
        EventFullDto eventFullDto = eventService.addEvent(eventDto, userId);
        log.debug("POST users/id/event - event posted with id {}", eventFullDto.getId());
        return eventFullDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getEvents(@PathVariable Long userId) {
        log.debug("GET users/id/events getting events for user {}", userId);
        return eventService.getEvents(userId);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.debug("GET users/id/events/id getting event {} for user {}", eventId, userId);
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @RequestBody @Valid UpdateEventUserRequest eventDto,
            @PathVariable Long userId,
            @PathVariable Long eventId
            ) {
        log.debug("PATCH users/id/events/id updating event {} by user {}", eventId, userId);
        EventValidator.validateExistingEvent(eventDto);
        return eventService.updateEvent(eventDto, userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getEventRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.debug("GET users/id/events/id/requests getting requests for event {} by user {}", eventId, userId);
        return eventService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequests(
            @RequestBody @Valid EventRequestStatusUpdateRequest requestDto,
            @PathVariable Long userId,
            @PathVariable Long eventId
            ) {
        EventValidator.validateUpdateRequests(requestDto);
        return eventService.updateEventRequests(requestDto, userId, eventId);
    }

}
