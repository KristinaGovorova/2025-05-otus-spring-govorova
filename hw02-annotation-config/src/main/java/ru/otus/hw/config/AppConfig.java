package ru.otus.hw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.dao.dto.AnswerCsvConverter;
import ru.otus.hw.service.*;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public AppProperties appProperties(
            @Value("${test.rightAnswersCountToPass}") int rightAnswersCountToPass,
            @Value("${test.fileName}") String testFileName) {
        return new AppProperties(rightAnswersCountToPass, testFileName);
    }

    @Bean
    public IOService ioService() {
        return new StreamsIOService(System.out, System.in);
    }

    @Bean
    public StudentService studentService(IOService ioService) {
        return new StudentServiceImpl(ioService);
    }

    @Bean
    public AnswerCsvConverter answerCsvConverter() {
        return new AnswerCsvConverter();
    }

    @Bean
    public QuestionDao questionDao(AppProperties appProperties) {
        return new CsvQuestionDao(appProperties);
    }

    @Bean
    public TestService testService(IOService ioService, QuestionDao questionDao) {
        return new TestServiceImpl(ioService, questionDao);
    }

    @Bean
    public ResultService resultService(AppProperties appProperties, IOService ioService) {
        return new ResultServiceImpl(appProperties, ioService);
    }

    @Bean
    public TestRunnerService testRunnerService(TestService testService,
                                               StudentService studentService,
                                               ResultService resultService) {
        return new TestRunnerServiceImpl(testService, studentService, resultService);
    }
}