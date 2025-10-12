package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.CreateDokumenRequest;
import com.bps.uts.sipakjabat.dto.ProfileResponse;
import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.service.DokumenService;
import com.bps.uts.sipakjabat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Manajemen Admin", description = "API yang hanya bisa diakses oleh role dengan hak akses tinggi.")
public class AdminController {

    private final UserService userService;
    private final DokumenService dokumenService; // Tambahkan service baru

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('VERIFIKATOR', 'KEPALA_BAGIAN')")
    @Operation(summary = "Mendapatkan semua daftar pengguna",
            description = "Mengambil daftar lengkap semua pengguna yang terdaftar di sistem. Hanya bisa diakses oleh Verifikator atau Kepala Bagian.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar pengguna berhasil diambil"),
            @ApiResponse(responseCode = "403", description = "Akses ditolak (role tidak sesuai)")
    })
    public ResponseEntity<List<ProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // --- ENDPOINT BARU DI SINI ---
    @PostMapping("/dokumen")
    @PreAuthorize("hasAnyRole('VERIFIKATOR', 'KEPALA_BAGIAN')")
    @Operation(summary = "Menambahkan dokumen baru untuk pengguna",
            description = "Membuat data master dokumen baru (SK, Ijazah, dll) untuk pengguna tertentu berdasarkan User ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dokumen baru berhasil dibuat"),
            @ApiResponse(responseCode = "403", description = "Akses ditolak (role tidak sesuai)"),
            @ApiResponse(responseCode = "404", description = "User dengan ID yang diberikan tidak ditemukan")
    })
    public ResponseEntity<MasterDokumenPegawai> createDokumen(@RequestBody CreateDokumenRequest request) {
        return ResponseEntity.ok(dokumenService.createDokumenForUser(request));
    }
}