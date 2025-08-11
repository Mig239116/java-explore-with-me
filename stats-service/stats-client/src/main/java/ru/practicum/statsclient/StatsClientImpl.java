package ru.practicum.statsclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsdto.dto.NewEndpointHitDto;
import ru.practicum.statsdto.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class StatsClientImpl implements StatsClient {
    private final String serverUrl;
    private final RestTemplate restTemplate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClientImpl(@Value("${stats.server.url}") String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public void hit(NewEndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NewEndpointHitDto> request = new HttpEntity<>(endpointHitDto, headers);

        restTemplate.exchange(
                serverUrl + "/hit",
                HttpMethod.POST,
                request,
                Void.class
        );
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        String startEncoded = encodeDateTime(start);
        String endEncoded = encodeDateTime(end);

        String url = String.format(
                "%s/stats?start=%s&end=%s&unique=%s",
                serverUrl,
                start.format(FORMATTER),
                end.format(FORMATTER),
                unique
        );

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                url += "&uris=" + uri;
            }
        }

        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                url,
                ViewStatsDto[].class
        );

        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    private String encodeDateTime(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }
}
