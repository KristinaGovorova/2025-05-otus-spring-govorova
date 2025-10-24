package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;
    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            ioService.printLine(question.text());
            printAnswers(question);

            int userAnswer = ioService.readIntForRange(1, question.answers().size(),
                    "Please enter answer number (1-" + question.answers().size() + "): ");

            boolean isAnswerValid = question.answers().get(userAnswer - 1).isCorrect();
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printAnswers(Question question) {
        for (int i = 0; i < question.answers().size(); i++) {
            String answerText = question.answers().get(i).text();
            ioService.printFormattedLine("%d) %s", i + 1, answerText);
        }
    }
}