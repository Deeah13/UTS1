package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.AuthResponse;
import com.bps.uts.sipakjabat.dto.LoginRequest;
import com.bps.uts.sipakjabat.dto.RegisterRequest;
import com.bps.uts.sipakjabat.model.Role;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (repository.findByNip(request.getNip()).isPresent()) {
            throw new IllegalStateException("NIP sudah terdaftar.");
        }
        var user = User.builder()
                .namaLengkap(request.getNamaLengkap())
                .nip(request.getNip())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .pangkatGolongan(request.getPangkatGolongan())
                .jabatan(request.getJabatan())
                .tmtPangkatTerakhir(request.getTmtPangkatTerakhir())
                .role(Role.PEGAWAI)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getNip(), request.getPassword())
        );
        var user = repository.findByNip(request.getNip())
                .orElseThrow(() -> new RuntimeException("NIP atau password salah."));
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).build();
    }
}