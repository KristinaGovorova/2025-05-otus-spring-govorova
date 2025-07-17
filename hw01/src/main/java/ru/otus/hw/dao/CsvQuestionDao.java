package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    public CsvQuestionDao(TestFileNameProvider fileNameProvider) {
        this.fileNameProvider = fileNameProvider;
    }

    @Override
    public List<Question> findAll() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileNameProvider.getTestFileName())) {
            if (is == null) {
                throw new QuestionReadException("File not found: " + fileNameProvider.getTestFileName());
            }

            List<QuestionDto> questionDtos = new CsvToBeanBuilder<QuestionDto>(new InputStreamReader(is))
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();

            return questionDtos.stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();

        } catch (Exception e) {
            throw new QuestionReadException("Error when reading file: " + fileNameProvider.getTestFileName(), e);
        }
    }
}
