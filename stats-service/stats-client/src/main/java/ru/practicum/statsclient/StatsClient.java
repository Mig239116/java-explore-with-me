package ru.practicum.statsclient;

import ru.practicum.statsdto.dto.NewEndpointHitDto;
import ru.practicum.statsdto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {

    void hit(NewEndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
