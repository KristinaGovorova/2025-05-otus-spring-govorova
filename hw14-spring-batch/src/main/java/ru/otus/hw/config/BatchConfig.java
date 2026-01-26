package ru.otus.hw.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.entity.relational.BookEntity;
import ru.otus.hw.entity.nosql.BookDocument;
import ru.otus.hw.processor.BookItemProcessor;
import ru.otus.hw.repository.relational.BookRepository;

import java.util.Collections;

@Configuration
public class BatchConfig {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public RepositoryItemReader<BookEntity> reader() {
        return new RepositoryItemReaderBuilder<BookEntity>()
                .name("bookReader")
                .repository(bookRepository)
                .methodName("findAll")
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .pageSize(10)
                .build();
    }

    @Bean
    public BookItemProcessor processor() {
        return new BookItemProcessor();
    }

    @Bean
    public MongoItemWriter<BookDocument> writer() {
        MongoItemWriter<BookDocument> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection("books");
        return writer;
    }

    @Bean
    public Step migrationStep(JobRepository jobRepository) {
        return new StepBuilder("migrationStep", jobRepository)
                .<BookEntity, BookDocument>chunk(10, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job migrationJob(JobRepository jobRepository,
                            Step migrationStep) {
        return new JobBuilder("migrationJob", jobRepository)
                .start(migrationStep)
                .build();
    }
}