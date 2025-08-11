package ru.practicum.mainsvc.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.errors.BadRequestException;
import ru.practicum.mainsvc.event.dto.EventFullDto;
import ru.practicum.mainsvc.event.dto.UpdateEventAdminRequest;
import ru.practicum.mainsvc.event.model.EventState;
import ru.practicum.mainsvc.event.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@Validated
public class EventAdminController {
    private final EventService eventService;

    @Autowired
    public EventAdminController(@Qualifier("eventServiceImpl") EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size
    ) {
        log.debug("GET admin/events - getting events with admin criterias");
        if (rangeEnd != null && parseDateTime(rangeEnd).isBefore(parseDateTime(rangeStart))) {
            throw new BadRequestException("The end date is before start");
        }
        return eventService.getEvents(
                users,
                parseStates(states),
                categories,
                parseDateTime(rangeStart),
                parseDateTime(rangeEnd),
                from,
                size
        );
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @RequestBody @Valid UpdateEventAdminRequest eventDto,
            @PathVariable Long eventId
    ) {
        log.debug("PATCH admin/events/id - updating event {}", eventId);
        EventValidator.validateExistingEvent(eventDto);
        return eventService.updateEvent(eventDto, eventId);
    }

    private List<EventState> parseStates(List<String> states) {
        if (states == null) return null;
        return states.stream()
                .map(EventState::valueOf)
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDateTime(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format. Expected format: yyyy-MM-dd HH:mm:ss");
        }
    }
}
