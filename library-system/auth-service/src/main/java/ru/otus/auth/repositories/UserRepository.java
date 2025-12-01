package ru.otus.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.auth.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
