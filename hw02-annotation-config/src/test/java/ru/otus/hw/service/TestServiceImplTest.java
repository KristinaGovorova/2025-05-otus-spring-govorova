package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
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
    void executeTestFor_ShouldReturnFullScore_WhenAllAnswersCorrect() {
        // Arrange
        List<Question> testQuestions = List.of(
                createQuestion("Q1", "Correct", true, "Wrong", false),
                createQuestion("Q2", "Right", true, "Wrong", false)
        );

        when(questionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(1);

        var student = new Student("John", "Doe");

        // Act
        TestResult result = testService.executeTestFor(student);

        // Assert
        assertEquals(2, result.getRightAnswersCount());
        assertEquals(2, result.getAnsweredQuestions().size());
        verify(ioService, times(2)).readIntForRangeWithPrompt(1, 2, "Please enter number of correct answer:", 
                "Incorrect input. Number must be between 1 and 2");
    }

    @Test
    void executeTestFor_ShouldReturnZero_WhenAllAnswersWrong() {
        // Arrange
        List<Question> testQuestions = List.of(
                createQuestion("Q1", "Wrong", false, "Correct", true),
                createQuestion("Q2", "Wrong", false, "Right", true)
        );

        when(questionDao.findAll()).thenReturn(testQuestions);
        when(ioService.readIntForRangeWithPrompt(anyInt(), anyInt(), anyString(), anyString()))
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