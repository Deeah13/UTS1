package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.AuthResponse;
import com.bps.uts.sipakjabat.dto.LoginRequest;
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {

        // 1. Cek dulu apakah user dengan NIP tersebut ada di database.
        var userOptional = repository.findByNip(request.getNip());

        if (userOptional.isEmpty()) {
            // 2. Jika tidak ada, langsung lemparkan UsernameNotFoundException.
            // Exception ini akan ditangkap oleh GlobalExceptionHandler dan diubah menjadi 403 Forbidden.
            throw new UsernameNotFoundException("Akun tidak ditemukan, tidak aktif, atau telah diblokir.");
        }

        // 3. Jika user ADA, baru lanjutkan ke proses otentikasi password.
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getNip(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            // 4. Jika password salah, AuthenticationManager akan melempar error ini.
            // Kita tangkap dan lemparkan lagi agar ditangani sebagai 401 Unauthorized.
            throw new BadCredentialsException("NIP atau password salah.");
        }

        // Jika semua berhasil, generate token
        var user = userOptional.get();
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}