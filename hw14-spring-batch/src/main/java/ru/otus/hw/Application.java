package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("ru.otus.hw.repository.nosql")
@EnableJpaRepositories("ru.otus.hw.repository.relational")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
