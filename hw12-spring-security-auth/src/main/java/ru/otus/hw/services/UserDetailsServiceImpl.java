package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.repositories.UserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getAuthorities().stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList()))
                        .disabled(!user.isEnabled())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}