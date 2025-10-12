package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.AuthResponse;
import com.bps.uts.sipakjabat.dto.LoginRequest;
import com.bps.uts.sipakjabat.dto.RegisterRequest;
import com.bps.uts.sipakjabat.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Otentikasi & Registrasi", description = "API untuk proses registrasi dan login pengguna.")
public class AuthController {

    private final AuthService service;

    @Operation(summary = "Registrasi pengguna baru",
            description = "Membuat akun pengguna baru. Jika role tidak dispesifikasikan, akan default menjadi 'PEGAWAI'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registrasi berhasil dan mengembalikan token JWT",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request (misal: NIP atau email sudah terdaftar)")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Login pengguna",
            description = "Mengotentikasi pengguna dengan NIP dan password, lalu mengembalikan token JWT jika berhasil.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login berhasil dan mengembalikan token JWT",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden (NIP atau password salah)")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }
}