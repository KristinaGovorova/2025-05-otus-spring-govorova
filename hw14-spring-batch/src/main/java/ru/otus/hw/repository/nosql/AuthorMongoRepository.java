package ru.otus.hw.repository.nosql;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.hw.entity.nosql.AuthorDocument;

@Repository
public interface AuthorMongoRepository extends MongoRepository<AuthorDocument, String> {
}
