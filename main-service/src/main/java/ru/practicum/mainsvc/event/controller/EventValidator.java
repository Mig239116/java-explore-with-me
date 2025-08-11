package ru.practicum.mainsvc.event.controller;

import org.springframework.stereotype.Component;
import ru.practicum.mainsvc.errors.ForbiddenException;
import ru.practicum.mainsvc.event.dto.NewEventDto;
import ru.practicum.mainsvc.event.dto.UpdateEventAdminRequest;
import ru.practicum.mainsvc.event.dto.UpdateEventUserRequest;
import ru.practicum.mainsvc.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.mainsvc.request.model.RequestStatus;

import java.time.LocalDateTime;

@Component
public class EventValidator {
    public static void validateNewEvent(NewEventDto newEventDto) {
        if (newEventDto.getEventDate() == null) {
            throw new ForbiddenException("Event date must be provided");
        }

        if (!newEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Event must be at least two hours in the future");
        }
    }

    public static void validateExistingEvent(UpdateEventUserRequest updateEventDto) {
        if (updateEventDto.getEventDate() == null) {
            return;
        }
        if (!updateEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Event must be at least two hours in the future");
        }
    }

    public static void validateExistingEvent(UpdateEventAdminRequest updateEventDto) {
        if (updateEventDto.getEventDate() == null) {
            return;
        }
        if (!updateEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenException("Event must be at least one hour in the future");
        }
    }

    public static void validateUpdateRequests(EventRequestStatusUpdateRequest requestDto) {
        if (requestDto.getStatus() != RequestStatus.CONFIRMED
                && requestDto.getStatus() != RequestStatus.REJECTED) {
            throw new ForbiddenException("New status should be either CONFIRMED or REJECTED");
        }
    }
}