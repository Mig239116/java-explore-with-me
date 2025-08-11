package ru.practicum.mainsvc.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainsvc.request.dto.ParticipationRequestDto;
import ru.practicum.mainsvc.request.service.ParticipationRequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class ParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    @Autowired
    public ParticipationRequestController(@Qualifier("participationRequestServiceImpl") ParticipationRequestService participationRequestService) {
        this.participationRequestService = participationRequestService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        log.debug("GET user/id/requests - getting user {} requests", userId);
        return participationRequestService.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable Long userId,
            @RequestParam Long eventId) {
        log.debug("POST users/id/requests - creating user {} request for event {}", userId, eventId);
        ParticipationRequestDto requestDtoStored = participationRequestService.createRequest(userId, eventId);
        log.debug("POST users/id/requests - created request {}", requestDtoStored.getId());
        return requestDtoStored;
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.debug("PATCH users/ud/requests/id/cancel - cancelling request {}", requestId);
        return participationRequestService.cancelRequest(userId, requestId);
    }
}
