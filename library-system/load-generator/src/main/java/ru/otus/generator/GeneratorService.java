package ru.otus.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GeneratorService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Random random = new Random();

    @Value("${LOAD_TARGET_URL:http://localhost:8080}")
    private String gatewayUrl;

    private String jwtToken = null;

    @Scheduled(fixedDelay = 3600000)
    public void login() {
        try {
            var request = Map.of("username", "admin", "password", "password");
            System.out.println(">>> LoadGenerator: Attempting to login...");
            String token = restTemplate.postForObject(gatewayUrl + "/auth/token", request, String.class);
            this.jwtToken = token;
            System.out.println(">>> LoadGenerator: Logged in successfully. Token length: " + (token != null ? token.length() : 0));
        } catch (Exception e) {
            System.err.println(">>> LoadGenerator: Login failed: " + e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 500)
    public void generateLoad() {
        if (jwtToken == null) {
            System.out.println(">>> Skip load generation: No token");
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwtToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            int action = random.nextInt(100);
            String url;
            HttpMethod method;

            if (action < 50) {
                // 50% - GET Books
                url = gatewayUrl + "/api/books";
                method = HttpMethod.GET;
                restTemplate.exchange(url, method, entity, String.class);
                System.out.print(".");

            } else if (action < 80) {
                // 30% - GET Loans
                url = gatewayUrl + "/api/loans";
                method = HttpMethod.GET;
                restTemplate.exchange(url, method, entity, String.class);
                System.out.print(".");

            } else {
                // 20% - POST Loan
                long bookId = random.nextInt(5) + 1;
                url = gatewayUrl + "/api/loans?bookId=" + bookId;
                method = HttpMethod.POST;
                restTemplate.exchange(url, method, entity, String.class);
                System.out.print("+");
            }

        } catch (Exception e) {
            System.out.print("E");
        }
    }
}