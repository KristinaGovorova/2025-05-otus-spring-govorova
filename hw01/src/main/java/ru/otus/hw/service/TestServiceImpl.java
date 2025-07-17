package ru.otus.hw.service;

import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.TestResult;

import java.util.List;

public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    public TestServiceImpl(IOService ioService, QuestionDao questionDao) {
        this.ioService = ioService;
        this.questionDao = questionDao;
    }

    @Override
    public TestResult executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        TestResult testResult = new TestResult(questions.size());

        for (Question question : questions) {
            askQuestion(question, testResult);
        }

        printResults(testResult);
        return testResult;
    }

    private void askQuestion(Question question, TestResult testResult) {
        printQuestion(question);

        int userAnswer = ioService.readIntForRange(
                1,
                question.answers().size(),
                "Please enter your answer: ");

        boolean isCorrect = question.answers().get(userAnswer - 1).isCorrect();
        testResult.applyAnswer(isCorrect);

        ioService.printLine(isCorrect ? "Correct!" : "Incorrect!");
        ioService.printLine("");
    }

    private void printQuestion(Question question) {
        ioService.printFormattedLine("Question: %s", question.text());
        List<Answer> answers = question.answers();
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("Answer %d: %s", i + 1, answers.get(i).text());
        }
    }

    private void printResults(TestResult testResult) {
        ioService.printLine("\nTest completed!");
        ioService.printFormattedLine("Correct answers: %d/%d",
                testResult.getRightAnswersCount(),
                testResult.getTotalQuestions());
    }
}