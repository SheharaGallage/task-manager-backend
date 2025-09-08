package com.TM.taskmanager.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF because you're building a stateless REST API (no
                // cookies/sessions)
                .csrf(csrf -> csrf.disable())
                // 2. Tell Spring Security not to create sessions; every request must have a JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. Authorization rules:
                .authorizeHttpRequests(auth -> auth
                        // Allow anyone to access endpoints under /api/v1/auth/** (like login, register)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // All other requests require authentication (JWT must be valid)
                        .anyRequest().authenticated())
                // 4. Use your custom AuthenticationProvider (responsible for user details +
                // token validation)
                .authenticationProvider(authenticationProvider)
                // 5. Add your custom JWT filter before the built-in
                // UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
