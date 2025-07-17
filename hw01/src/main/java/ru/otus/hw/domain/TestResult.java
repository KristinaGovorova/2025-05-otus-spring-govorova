package ru.otus.hw.domain;

public class TestResult {
    private final int totalQuestions;

    private int rightAnswersCount;

    public TestResult(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public void applyAnswer(boolean isCorrect) {
        if (isCorrect) {
            rightAnswersCount++;
        }
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getRightAnswersCount() {
        return rightAnswersCount;
    }
}
