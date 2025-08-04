package ru.practicum.statserver.mapper;

import ru.practicum.statsdto.dto.NewEndpointHitDto;
import ru.practicum.statsdto.dto.EndpointHitDto;
import ru.practicum.statserver.model.EndpointHit;

public class StatsMapper {
    public static EndpointHit toEndPointHit(NewEndpointHitDto hitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(hitDto.getApp());
        endpointHit.setIp(hitDto.getIp());
        endpointHit.setUri(hitDto.getUri());
        endpointHit.setTimestamp(hitDto.getTimestamp());
        return endpointHit;
    }

    public static EndpointHitDto toFullDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp()
        );
    }
}
