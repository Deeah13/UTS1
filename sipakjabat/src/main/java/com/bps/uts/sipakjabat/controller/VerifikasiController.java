package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.VerifikasiRequest;
import com.bps.uts.sipakjabat.model.Pengajuan;
import com.bps.uts.sipakjabat.service.VerifikasiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/verifikasi")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Proses Verifikasi (Khusus Verifikator)", description = "API yang hanya bisa diakses oleh Verifikator/Kepala Bagian untuk memproses pengajuan.")
public class VerifikasiController {

    private final VerifikasiService verifikasiService;

    @Operation(summary = "Mendapatkan daftar pengajuan masuk",
            description = "Mengambil semua pengajuan dari seluruh pegawai yang statusnya 'DIAJUKAN' dan siap untuk diproses.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar pengajuan berhasil diambil"),
            @ApiResponse(responseCode = "403", description = "Akses ditolak (bukan Verifikator/Kepala Bagian)")
    })
    @GetMapping("/pengajuan")
    @PreAuthorize("hasAnyRole('VERIFIKATOR', 'KEPALA_BAGIAN')")
    public ResponseEntity<List<Pengajuan>> getDaftarPengajuan() {
        return ResponseEntity.ok(verifikasiService.getAllPengajuanMasuk());
    }

    @Operation(summary = "Memproses dan memberi keputusan verifikasi",
            description = "Memberikan keputusan (DISETUJUI/DITOLAK) dan catatan pada sebuah pengajuan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verifikasi berhasil disimpan"),
            @ApiResponse(responseCode = "403", description = "Akses ditolak (bukan Verifikator/Kepala Bagian)"),
            @ApiResponse(responseCode = "404", description = "Pengajuan dengan ID tersebut tidak ditemukan")
    })
    @PostMapping("/pengajuan/{id}")
    @PreAuthorize("hasAnyRole('VERIFIKATOR', 'KEPALA_BAGIAN')")
    public ResponseEntity<Pengajuan> prosesVerifikasi(
            @Parameter(description = "ID dari pengajuan yang akan diproses") @PathVariable Long id,
            @RequestBody VerifikasiRequest request) {
        return ResponseEntity.ok(verifikasiService.verifikasiPengajuan(id, request));
    }
}