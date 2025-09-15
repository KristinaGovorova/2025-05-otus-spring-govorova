package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;


@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            if (question == null || question.answers() == null) {
                continue;
            }
            var isAnswerValid = askQuestion(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean askQuestion(Question question) {
        ioService.printLine(question.text());
        var answers = question.answers();

        if (answers == null || answers.isEmpty()) {
            ioService.printLine("No answers available for this question.");
            return false;
        }
        
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("%d. %s", i + 1, answers.get(i).text());
        }

        int answerNo = ioService.readIntForRangeWithPrompt(1, answers.size(),
                "Please enter number of correct answer:",
                "Incorrect input. Number must be between 1 and " + answers.size());

        return answers.get(answerNo - 1).isCorrect();
    }
}