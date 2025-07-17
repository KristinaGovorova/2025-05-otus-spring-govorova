package ru.otus.hw.service;

public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    public TestRunnerServiceImpl(TestService testService) {
        this.testService = testService;
    }

    @Override
    public void run() {
        testService.executeTest();
    }
}
