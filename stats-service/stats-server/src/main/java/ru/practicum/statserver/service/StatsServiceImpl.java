package ru.practicum.statserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsdto.dto.NewEndpointHitDto;
import ru.practicum.statsdto.dto.ViewStatsDto;
import ru.practicum.statsdto.dto.EndpointHitDto;
import ru.practicum.statserver.mapper.StatsMapper;
import ru.practicum.statserver.model.EndpointHit;
import ru.practicum.statserver.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService{

    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDto saveHit(NewEndpointHitDto hitDto) {
        EndpointHit endpointHit = StatsMapper.toEndPointHit(hitDto);
        return StatsMapper.toFullDto(statsRepository.save(endpointHit));
    }

    @Override
    public List<ViewStatsDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique
            ) {
        if (uris == null || uris.isEmpty()) {
            return unique ?
                    statsRepository.getUniqueStats(start, end) :
                    statsRepository.getStats(start, end);
        } else {
            return unique ?
                    statsRepository.getUniqueStatsByUris(start, end, uris) :
                    statsRepository.getStatsByUris(start, end, uris);
        }
    }
}
