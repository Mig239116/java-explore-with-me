package ru.practicum.statsclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class StatsClientConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public StatsClient statsClient() {
        return new StatsClientImpl("http://stats-server:9090", restTemplate());
    }
}
