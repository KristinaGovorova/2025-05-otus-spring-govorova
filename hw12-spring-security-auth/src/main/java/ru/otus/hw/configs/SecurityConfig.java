package ru.otus.hw.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/login",
                                "/error",
                                "/h2-console/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                        .requestMatchers("/books/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/books/edit/**").hasRole("ADMIN")
                        .requestMatchers("/books/create").hasRole("ADMIN")
                        .requestMatchers("/books/delete/**").hasRole("ADMIN")

                        .requestMatchers("/authors/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/authors/edit/**").hasRole("ADMIN")
                        .requestMatchers("/authors/create").hasRole("ADMIN")

                        .requestMatchers("/genres/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/genres/edit/**").hasRole("ADMIN")
                        .requestMatchers("/genres/create").hasRole("ADMIN")

                        .requestMatchers("/comments/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/comments/delete/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied")
                )

                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}