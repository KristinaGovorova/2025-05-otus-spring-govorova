package ru.otus.library.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.library.dtos.BookDto;

@FeignClient(name = "book-service")
public interface BookServiceClient {

    @GetMapping("/api/books/{id}")
    BookDto getBookById(@PathVariable("id") Long id);

    @PostMapping("/api/books/{id}/borrow")
    BookDto borrowBook(@PathVariable("id") Long id);

    @PostMapping("/api/books/{id}/return")
    void returnBook(@PathVariable("id") Long id);
}
