package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;
    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comments by book id", key = "cbbid")
    public String findCommentsByBookId(String bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Insert comment for a book", key = "cins")
    public String insertComment(String bookId, String text) {
        var savedComment = commentService.insert(bookId, text);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Update a comment", key = "cupd")
    public String updateComment(String bookId, String commentId, String text) {
        var savedComment = commentService.update(bookId, commentId, text);
        return commentConverter.commentToString(savedComment);
    }

    @ShellMethod(value = "Delete a comment by its id", key = "cdel")
    public void deleteComment(String bookId, String commentId) {
        commentService.deleteById(bookId, commentId);
    }
}