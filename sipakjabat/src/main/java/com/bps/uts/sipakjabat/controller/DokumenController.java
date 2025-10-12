package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.MasterDokumenPegawaiRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dokumen")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Manajemen Dokumen", description = "API untuk melihat dokumen kepegawaian.")
public class DokumenController {

    private final MasterDokumenPegawaiRepository dokumenRepository;

    @Operation(summary = "Mendapatkan daftar dokumen milik saya",
            description = "Mengambil daftar semua dokumen (SK, Ijazah, dll) yang terdaftar atas nama pengguna yang sedang login.")
    @ApiResponse(responseCode = "200", description = "Daftar dokumen berhasil diambil")
    @GetMapping("/my-documents")
    public ResponseEntity<List<MasterDokumenPegawai>> getMyAvailableDocuments(@AuthenticationPrincipal User currentUser) {
        List<MasterDokumenPegawai> documents = dokumenRepository.findByUserId(currentUser.getId());
        return ResponseEntity.ok(documents);
    }
}