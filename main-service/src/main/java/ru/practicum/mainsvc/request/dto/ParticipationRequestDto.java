package ru.practicum.mainsvc.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.mainsvc.request.model.RequestStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private RequestStatus status;
}
