package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.AuthResponse;
import com.bps.uts.sipakjabat.dto.LoginRequest;
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getNip(), request.getPassword())
            );

            // Get user details
            var user = repository.findByNip(request.getNip())
                    .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

            // Generate JWT token
            var jwtToken = jwtService.generateToken(user);

            return AuthResponse.builder()
                    .token(jwtToken)
                    .build();

        } catch (AuthenticationException e) {
            throw new RuntimeException("NIP atau password salah");
        }
    }
}