package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStreamReader;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try {
            var resource = new ClassPathResource(fileNameProvider.getTestFileName());

            if (!resource.exists()) {
                throw new QuestionReadException("File not found: " + fileNameProvider.getTestFileName());
            }

            var reader = new InputStreamReader(resource.getInputStream());

            List<QuestionDto> questionDtos = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();

            return questionDtos.stream()
                    .map(QuestionDto::toDomainObject)
                    .filter(question -> question.answers() != null && !question.answers().isEmpty())
                    .toList();

        } catch (Exception e) {
            throw new QuestionReadException("Error reading questions file: " + fileNameProvider.getTestFileName(), e);
        }
    }
}