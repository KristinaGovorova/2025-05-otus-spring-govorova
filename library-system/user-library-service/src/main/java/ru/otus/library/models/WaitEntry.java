package ru.otus.library.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "wait_list")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WaitEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bookId;

    @Column(nullable = false)
    private String userId;

    private LocalDateTime requestDate;

    private boolean notified;
}