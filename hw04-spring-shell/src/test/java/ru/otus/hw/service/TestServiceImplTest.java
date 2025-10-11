package ru.otus.hw.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = {"spring.shell.interactive.enabled=false"})
class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    @Test
    void executeTestFor_ShouldReturnFullScore_WhenAllAnswersCorrect() {
        // Arrange
        List<Question> testQuestions = List.of(
                createQuestion("Q1", "Correct", true, "Wrong", false),
                createQuestion("Q2", "Right", true, "Wrong", false)
        );

        when(questionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString()))
                .thenReturn(1);

        var student = new Student("John", "Doe");

        // Act
        TestResult result = testService.executeTestFor(student);

        // Assert
        assertEquals(2, result.getRightAnswersCount());
        assertEquals(2, result.getAnsweredQuestions().size());
        verify(ioService, times(2)).readIntForRange(eq(1), eq(2), anyString());
    }

    @Test
    void executeTestFor_ShouldReturnZero_WhenAllAnswersWrong() {
        // Arrange
        List<Question> testQuestions = List.of(
                createQuestion("Q1", "Wrong", false, "Correct", true),
                createQuestion("Q2", "Wrong", false, "Right", true)
        );

        when(questionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString()))
                .thenReturn(1);

        var student = new Student("John", "Doe");

        // Act
        TestResult result = testService.executeTestFor(student);

        // Assert
        assertEquals(0, result.getRightAnswersCount());
        assertEquals(2, result.getAnsweredQuestions().size());
    }

    private Question createQuestion(String text, String answer1Text, boolean answer1Correct,
                                    String answer2Text, boolean answer2Correct) {
        return new Question(text, List.of(
                new Answer(answer1Text, answer1Correct),
                new Answer(answer2Text, answer2Correct)
        ));
    }
}