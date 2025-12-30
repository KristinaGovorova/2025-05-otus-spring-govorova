package ru.otus.hw.services;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Slf4j
@Service
public class ExternalDataService {

    private final RestTemplate restTemplate;

    public ExternalDataService(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(2))
                .build();
    }

    @CircuitBreaker(name = "externalService", fallbackMethod = "fallback")
    public String getAdditionalInfo(long id) {
        String url = "https://jsonplaceholder.typicode.com/posts/" + id;

        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        if (response != null && response.has("title")) {
            return "External Review: " + response.get("title").asText();
        }
        return "No external info";
    }

    public String fallback(long id, Throwable t) {
        log.warn("External service unavailable for id: {}. Reason: {}", id, t.getMessage());
        return "Внешний сервис временно недоступен";
    }
}
