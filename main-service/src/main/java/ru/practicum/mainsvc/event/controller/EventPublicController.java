package ru.practicum.mainsvc.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.common.stat.StatClientService;
import ru.practicum.mainsvc.errors.BadRequestException;
import ru.practicum.mainsvc.event.dto.EventFullDto;
import ru.practicum.mainsvc.event.dto.EventShortDto;
import ru.practicum.mainsvc.event.service.EventService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@Validated
public class EventPublicController {
    private final EventService eventService;
    private final StatClientService statClientService;

    @Autowired
    public EventPublicController(
            @Qualifier("eventServiceImpl") EventService eventService,
            StatClientService statClientService
            ) {
        this.eventService = eventService;
        this.statClientService = statClientService;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable Long eventId, HttpServletRequest request) {
        log.debug("GET events/id - getting event {}", eventId);
        EventFullDto eventDto = eventService.getEvent(eventId);
        statClientService.saveHit(request);
        return eventDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size,
            HttpServletRequest request
    ) {
        log.debug("GET /events - getting events for public endpoint");
        if (rangeEnd != null && parseDateTime(rangeEnd).isBefore(parseDateTime(rangeStart))) {
            throw new BadRequestException("The end date is before start");
        }
        Collection<EventShortDto> events = eventService.getEvents(
                text,
                categories,
                paid,
                parseDateTime(rangeStart),
                parseDateTime(rangeEnd),
                onlyAvailable,
                sort,
                from,
                size);
        statClientService.saveHit(request);
        return events;
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
