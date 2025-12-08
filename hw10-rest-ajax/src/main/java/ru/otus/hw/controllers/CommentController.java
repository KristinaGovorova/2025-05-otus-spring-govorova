//package ru.otus.hw.controllers;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import ru.otus.hw.models.Comment;
//import ru.otus.hw.services.BookService;
//import ru.otus.hw.services.CommentService;
//
//@Controller
//@RequiredArgsConstructor
//public class CommentController {
//
//    private final CommentService commentService;
//    private final BookService bookService;
//
//    @PostMapping("/books/{bookId}/comments")
//    public String addComment(@PathVariable long bookId,
//                             @RequestParam String text) {
//        commentService.insert(bookId, text);
//        return "redirect:/books/" + bookId;
//    }
//
//    @PostMapping("/comments/{id}/delete")
//    public String deleteComment(@PathVariable long id) {
//        Comment comment = commentService.findById(id)
//                .orElseThrow(() -> new RuntimeException("Comment not found"));
//        long bookId = comment.getBook().getId();
//        commentService.deleteById(id);
//        return "redirect:/books/" + bookId;
//    }
//}