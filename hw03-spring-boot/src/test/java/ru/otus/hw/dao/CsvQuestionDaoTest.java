package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "test.locale=en-US"
})
class CsvQuestionDaoTest {

    @Autowired
    private QuestionDao questionDao;

    @Test
    void testFindAll() {
        var questions = questionDao.findAll();
        assertThat(questions).isNotNull().hasSize(3);

        var question1 = questions.get(0);
        assertThat(question1.text()).isEqualTo("Is there life on Mars?");
        assertThat(question1.answers()).hasSize(3);

        var question2 = questions.get(1);
        assertThat(question2.text()).isEqualTo("How should resources be loaded form jar in Java?");
        assertThat(question2.answers()).hasSize(3);

        var question3 = questions.get(2);
        assertThat(question3.text()).isEqualTo("Which option is a good way to handle the exception?");
        assertThat(question3.answers()).hasSize(4);
    }
}