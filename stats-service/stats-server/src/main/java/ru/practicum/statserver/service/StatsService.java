package ru.practicum.statserver.service;

import ru.practicum.statsdto.dto.NewEndpointHitDto;
import ru.practicum.statsdto.dto.ViewStatsDto;
import ru.practicum.statsdto.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatsService {

    EndpointHitDto saveHit(NewEndpointHitDto hitDto);

    Collection<ViewStatsDto> getStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      boolean unique
    );

}
