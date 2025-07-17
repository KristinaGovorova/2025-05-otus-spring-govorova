package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executeTest_ShouldReturnFullScore_WhenAllAnswersCorrect() {
        // Arrange
        List<Question> testQuestions = List.of(
                createQuestion("Q1", "Correct", true, "Wrong", false),
                createQuestion("Q2", "Right", true, "Wrong", false)
        );

        when(questionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString())).thenReturn(1);

        // Act
        TestResult result = testService.executeTest();

        // Assert
        assertEquals(2, result.getRightAnswersCount());
        assertEquals(2, result.getTotalQuestions());
        verify(ioService, times(2)).printLine("Correct!");
    }

    @Test
    void executeTest_ShouldReturnZero_WhenAllAnswersWrong() {
        // Arrange
        List<Question> testQuestions = List.of(
                createQuestion("Q1", "Correct", true, "Wrong", false),
                createQuestion("Q2", "Right", true, "Wrong", false)
        );

        when(questionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString())).thenReturn(2);

        // Act
        TestResult result = testService.executeTest();

        // Assert
        assertEquals(0, result.getRightAnswersCount());
        assertEquals(2, result.getTotalQuestions());
        verify(ioService, times(2)).printLine("Incorrect!");
    }

    private Question createQuestion(String text, String answer1Text, boolean answer1Correct,
                                    String answer2Text, boolean answer2Correct) {
        return new Question(text, List.of(
                new Answer(answer1Text, answer1Correct),
                new Answer(answer2Text, answer2Correct)
        ));
    }
}
