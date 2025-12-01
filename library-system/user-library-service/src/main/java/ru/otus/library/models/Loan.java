package ru.otus.library.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;

    private String userId;

    private LocalDate issueDate;

    private LocalDate returnDeadline;

    private LocalDate actualReturnDate;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;
}