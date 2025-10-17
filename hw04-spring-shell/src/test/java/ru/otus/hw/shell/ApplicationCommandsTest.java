package ru.otus.hw.shell;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.service.TestRunnerService;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ApplicationCommandsTest {
    @MockitoBean
    private TestRunnerService testRunnerService;

    @Autowired
    private ApplicationCommands testCommand;

    @Test
    void runTest_ShouldInvokeTestRunner() {
        // Act
        testCommand.runTest();

        // Assert
        verify(testRunnerService, times(1)).run();
    }
}

