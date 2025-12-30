package ru.otus.hw.actuators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

@Component
@RequiredArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    @Override
    public Health health() {
        try {
            long booksCount = bookRepository.count();
            if (booksCount > 0) {
                return Health.up()
                        .withDetail("message", "Library is working fine")
                        .withDetail("booksCount", booksCount)
                        .build();
            } else {
                return Health.down()
                        .withDetail("message", "Library is empty!")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("message", "Database error")
                    .build();
        }
    }
}

