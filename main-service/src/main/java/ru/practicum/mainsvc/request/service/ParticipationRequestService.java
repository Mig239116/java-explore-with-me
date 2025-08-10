package ru.practicum.mainsvc.request.service;

import ru.practicum.mainsvc.request.dto.ParticipationRequestDto;

import java.util.Collection;

public interface ParticipationRequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    Collection<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}
