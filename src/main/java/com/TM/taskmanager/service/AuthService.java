package com.TM.taskmanager.service;

import com.TM.taskmanager.dto.AuthenticateRequest;
import com.TM.taskmanager.dto.AuthenticationResponse;
import com.TM.taskmanager.dto.RegisterRequest;
import com.TM.taskmanager.service.JwtService;
import com.TM.taskmanager.entity.Role;
import com.TM.taskmanager.entity.User;
import com.TM.taskmanager.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        // AuthService.register(RegisterRequest request)
        public AuthenticationResponse register(RegisterRequest request) {
                // create user object
                var user = User.builder()
                                .firstname(request.getFirstname())
                                .lastname(request.getLastname())
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword())) // hash the password(encode the
                                                                                         // password)
                                .role(Role.USER)
                                .build();

                userRepository.save(user); // save the user to the database
                var jwtToken = jwtService.generateToken(user); // generate JWT token
                return AuthenticationResponse.builder()// return the token to client
                                .token(jwtToken)
                                .build();
        }

        // AuthService.authenticate(AuthenticateRequest request)
        public AuthenticationResponse authenticate(AuthenticateRequest request) {
                // 1.Authenticate credentials
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));
                // 2. find user in the database
                var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
                // 3.generate token for the user
                var jwtToken = jwtService.generateToken(user);
                // 4. return the token
                return AuthenticationResponse.builder()
                                .token(jwtToken)
                                .build();
        }
}
