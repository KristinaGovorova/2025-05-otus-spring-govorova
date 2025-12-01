package ru.otus.auth.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.auth.dtos.AuthRequest;
import ru.otus.auth.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        return authService.register(request.getUsername(), request.getPassword());
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest request) {
        return authService.login(request.getUsername(), request.getPassword());
    }
}
