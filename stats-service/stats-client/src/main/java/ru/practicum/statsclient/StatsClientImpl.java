package ru.practicum.statsclient;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsdto.dto.NewEndpointHitDto;
import ru.practicum.statsdto.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StatsClientImpl implements StatsClient{
    private final String serverUrl;
    private final RestTemplate restTemplate;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StatsClientImpl(String serverUrl, RestTemplate restTemplate) {
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

        String uriParams = (uris != null && !uris.isEmpty())
                ? "&uris=" + String.join("&uris=", uris)
                : "";

        String url = String.format(
                "%s/stats?start=%s&end=%s%s&unique=%s",
                serverUrl,
                startEncoded,
                endEncoded,
                uriParams,
                unique
        );

        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                url,
                ViewStatsDto[].class
        );

        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    private String encodeDateTime(LocalDateTime dateTime) {
        return URLEncoder.encode(dateTime.format(FORMATTER), StandardCharsets.UTF_8);
    }
}
