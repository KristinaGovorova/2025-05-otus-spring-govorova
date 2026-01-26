package ru.otus.hw;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.otus.hw.repository.nosql.BookMongoRepository;
import ru.otus.hw.repository.relational.BookRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@SpringBatchTest
@Testcontainers
class MigrationJobTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired(required = false)
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMongoRepository bookMongoRepository;

    @MockitoBean
    private JobExecutionListener jobExecutionListener;

    @BeforeEach
    void clearTarget() {
        bookMongoRepository.deleteAll();
    }

    @Test
    void testMigrationJob() throws Exception {
        long countBefore = bookRepository.count();
        assertThat(countBefore).isGreaterThan(0);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        long countAfter = bookMongoRepository.count();
        assertThat(countAfter).isEqualTo(countBefore);

        var books = bookMongoRepository.findAll();
        assertThat(books).allMatch(book -> book.getAuthor() != null);
        assertThat(books).allMatch(book -> book.getGenre() != null);
    }

    @Test
    void shouldMigrateCorrectDataIntegrity() throws Exception {
        jobLauncherTestUtils.launchJob();

        var expectedBook = bookRepository.findAll().get(0);
        var actualBook = bookMongoRepository.findAll().stream()
                .filter(b -> b.getTitle().equals(expectedBook.getTitle()))
                .findFirst()
                .orElseThrow();

        assertThat(actualBook.getAuthor().getName())
                .isEqualTo(expectedBook.getAuthor().getName());
    }

    @Test
    void shouldInvokeListenerDuringJobExecution() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        verify(jobExecutionListener, atLeastOnce()).beforeJob(any(JobExecution.class));
        verify(jobExecutionListener, atLeastOnce()).afterJob(any(JobExecution.class));

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
    }
}