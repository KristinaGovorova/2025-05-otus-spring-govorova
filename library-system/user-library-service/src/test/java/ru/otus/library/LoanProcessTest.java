package ru.otus.library;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.library.dtos.BookDto;
import ru.otus.library.feign.BookServiceClient;
import ru.otus.library.repositories.LoanRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class LoanProcessTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoanRepository loanRepository;

    @MockitoBean
    private BookServiceClient bookServiceClient;

    @Test
    void shouldCreateLoan_WhenBookIsAvailable() throws Exception {
        Long bookId = 1L;
        String userId = "user1";

        BookDto mockBook = new BookDto();
        mockBook.setId(bookId);
        mockBook.setAvailableCopies(5);
        when(bookServiceClient.getBookById(bookId)).thenReturn(mockBook);

        when(bookServiceClient.borrowBook(bookId)).thenReturn(mockBook);

        mockMvc.perform(post("/api/loans")
                        .param("userId", userId)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isCreated()); // 201 Created
    }

    @Test
    void shouldAllowWaitlist_WhenBookIsNotAvailable() throws Exception {
        Long bookId = 2L;
        String userId = "user2";

        BookDto mockBook = new BookDto();
        mockBook.setId(bookId);
        mockBook.setAvailableCopies(0);
        when(bookServiceClient.getBookById(bookId)).thenReturn(mockBook);

        // попытка взять книгу -> 409 Conflict

        mockMvc.perform(post("/api/waitlist")
                        .param("userId", userId)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDenyWaitlist_WhenBookIsAvailable() throws Exception {
        Long bookId = 3L;
        String userId = "user3";

        BookDto mockBook = new BookDto();
        mockBook.setId(bookId);
        mockBook.setAvailableCopies(1);
        when(bookServiceClient.getBookById(bookId)).thenReturn(mockBook);

        // попытка встать в очередь -> 400
        mockMvc.perform(post("/api/waitlist")
                        .param("userId", userId)
                        .param("bookId", String.valueOf(bookId)))
                .andExpect(status().isBadRequest());
    }
}