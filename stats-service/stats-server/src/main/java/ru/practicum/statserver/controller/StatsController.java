package ru.practicum.statserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.dto.NewEndpointHitDto;
import ru.practicum.statsdto.dto.ViewStatsDto;
import ru.practicum.statsdto.dto.EndpointHitDto;
import ru.practicum.statserver.service.StatsService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody NewEndpointHitDto hitDto) {
        log.debug("POST/hit - saving new hit for app {} from ip {} for uri {}",
                hitDto.getApp(),
                hitDto.getIp(),
                hitDto.getUri()
        );
        EndpointHitDto hitDtoSaved = statsService.saveHit(hitDto);
        log.debug("POST/hit - saved new hit {} for app {} from ip {} for uri {}",
                hitDtoSaved.getId(),
                hitDtoSaved.getApp(),
                hitDtoSaved.getIp(),
                hitDtoSaved.getUri()
        );
    }

    @GetMapping("/stats")
    public Collection<ViewStatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique
            ) {
        return statsService.getStats(start, end, uris, unique);
    }
}
