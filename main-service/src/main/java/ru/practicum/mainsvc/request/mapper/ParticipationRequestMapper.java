package ru.practicum.mainsvc.request.mapper;

import ru.practicum.mainsvc.request.dto.ParticipationRequestDto;
import ru.practicum.mainsvc.request.model.ParticipationRequest;

import java.time.temporal.ChronoUnit;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getCreated().truncatedTo(ChronoUnit.MICROS).minusNanos(1000),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }
}
