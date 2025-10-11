package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.LocalizedMessagesService;
import ru.otus.hw.service.TestRunnerService;


@ShellComponent
@RequiredArgsConstructor
public class ApplicationCommands {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Run student testing", key = {"run-test", "run", "r"})
    public void runTest() {
        testRunnerService.run();
    }
}