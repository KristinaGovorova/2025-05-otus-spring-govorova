package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.otus.hw.config.LocaleConfig;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "test.locale=en-US"
})
class LocalizedMessagesServiceImplTest {

    @Autowired
    private LocalizedMessagesServiceImpl messagesService;

    @Autowired
    private LocaleConfig localeConfig;

    @Test
    void getMessage_ShouldReturnLocalizedMessage() {
        String message = messagesService.getMessage("TestService.answer.the.questions");
        assertThat(message).isNotNull().isNotEmpty();
    }

    @Test
    void localeConfig_ShouldReturnCorrectLocale() {
        assertThat(localeConfig.getLocale()).isEqualTo(Locale.forLanguageTag("en-US"));
    }
}