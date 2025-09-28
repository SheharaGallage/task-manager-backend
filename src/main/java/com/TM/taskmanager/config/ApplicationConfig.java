package com.TM.taskmanager.config;

import com.TM.taskmanager.repo.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    // Component that actually performs authentication (check username, check
    // password).
    // data acceess object which is responsible for fetch user details and also
    // decodes password s
    // came from spring framework package
    @Bean
    public AuthenticationProvider authenticationProvider() {

        // DaoAuthenticationProvider → Default implementation that uses a
        // UserDetailsService to fetch user data
        // from the database and a PasswordEncoder to verify the password.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // userDetailsService() → Your custom service that tells Spring how to find a
        // user by username/email.
        // It returns a UserDetails object.
        authProvider.setUserDetailsService(userDetailsService());

        // passwordEncoder() → Defines the algorithm to compare passwords (e.g.,
        // BCrypt).
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // when User registers PasswordEncoder encodes (hashed) the password and saving
    // to DB.
    // when User logs in → PasswordEncoder encodes the provided password and
    // compares it with the stored one.
    @Bean
    public PasswordEncoder passwordEncoder() {

        // BCryptPasswordEncoder → Most commonly used, adaptive hashing, secure.
        return new BCryptPasswordEncoder();

    }
}
