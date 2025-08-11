package ru.practicum.mainsvc.common.stat;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.dto.NewEndpointHitDto;
import ru.practicum.statsdto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatClientService {
    @Value("${spring.application.name}")
    private String appName;

    private static final String EVENT_URI = "/events/";

    private final StatsClient statsClient;

    public void saveHit(HttpServletRequest request) {
        NewEndpointHitDto hitDto = new NewEndpointHitDto(
                appName,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        log.debug("Saving hit {}", hitDto);
        statsClient.hit(hitDto);
    }

    public Collection<ViewStatsDto> getEventStats(List<Long> eventIds, LocalDateTime start, LocalDateTime end) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }
        List<String> uris = eventIds.stream()
                .map(id -> EVENT_URI + id)
                .collect(Collectors.toList());
        return statsClient.getStats(start, end, uris, true);
    }

    public void saveStatsForEvents(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        List<String> uris = eventIds.stream()
                .map(id -> EVENT_URI + id)
                .collect(Collectors.toList());
        statsClient.getStats(now.minusHours(1), now, uris, true);
    }
}
