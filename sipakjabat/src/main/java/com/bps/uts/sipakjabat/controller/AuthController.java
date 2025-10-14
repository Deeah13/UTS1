package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.AuthResponse;
import com.bps.uts.sipakjabat.dto.LoginRequest;
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
@Tag(name = "Otentikasi", description = "API untuk proses login pengguna. Pembuatan akun baru hanya dapat dilakukan oleh Verifikator melalui endpoint /api/admin/users")
public class AuthController {

    private final AuthService service;

    @Operation(
            summary = "Login pengguna",
            description = "Mengotentikasi pengguna dengan NIP dan password, lalu mengembalikan token JWT jika berhasil. " +
                    "Token ini harus disertakan di header Authorization untuk mengakses endpoint yang dilindungi."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login berhasil dan mengembalikan token JWT",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - NIP atau password salah"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Akun tidak aktif atau diblokir"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }
}