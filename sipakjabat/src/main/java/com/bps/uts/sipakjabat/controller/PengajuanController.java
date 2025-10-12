package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.CreatePengajuanRequest;
import com.bps.uts.sipakjabat.dto.LampirkanDokumenRequest;
import com.bps.uts.sipakjabat.dto.MessageResponse;
import com.bps.uts.sipakjabat.model.Pengajuan;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.service.PengajuanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pengajuan")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "4. Manajemen Pengajuan", description = "API untuk mengelola pengajuan kenaikan pangkat/jabatan oleh pegawai.")
public class PengajuanController {

    private final PengajuanService pengajuanService;

    // ... (method create, get, update, lampirkan, submit tetap sama) ...
    @Operation(summary = "Membuat draf pengajuan baru",
            description = "Membuat sebuah pengajuan baru dengan status awal 'DRAFT'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pengajuan berhasil dibuat"),
            @ApiResponse(responseCode = "403", description = "Akses ditolak (token tidak valid)")
    })
    @PostMapping
    public ResponseEntity<Pengajuan> create(@AuthenticationPrincipal User user, @RequestBody CreatePengajuanRequest request) {
        return ResponseEntity.ok(pengajuanService.createPengajuan(user, request));
    }

    @Operation(summary = "Mendapatkan daftar pengajuan milik saya",
            description = "Mengambil semua histori pengajuan yang pernah dibuat oleh pengguna yang sedang login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daftar pengajuan berhasil diambil"),
            @ApiResponse(responseCode = "403", description = "Akses ditolak")
    })
    @GetMapping("/my-list")
    public ResponseEntity<List<Pengajuan>> getMyPengajuanList(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(pengajuanService.getMyPengajuan(user.getId()));
    }

    @Operation(summary = "Mendapatkan detail satu pengajuan",
            description = "Mengambil detail lengkap dari satu pengajuan spesifik berdasarkan ID-nya.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detail pengajuan berhasil diambil"),
            @ApiResponse(responseCode = "403", description = "Akses ditolak"),
            @ApiResponse(responseCode = "404", description = "Pengajuan tidak ditemukan atau bukan milik Anda")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pengajuan> getById(
            @Parameter(description = "ID dari pengajuan yang ingin dilihat") @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(pengajuanService.getPengajuanById(user, id));
    }

    @Operation(summary = "Mengubah draf pengajuan",
            description = "Mengubah detail draf pengajuan yang statusnya masih 'DRAFT'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perubahan berhasil disimpan"),
            @ApiResponse(responseCode = "400", description = "Bad Request (bukan DRAFT)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Pengajuan> update(
            @Parameter(description = "ID pengajuan yang akan diubah") @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody CreatePengajuanRequest request) {
        return ResponseEntity.ok(pengajuanService.updatePengajuan(user, id, request));
    }

    @Operation(summary = "Menghapus draf pengajuan",
            description = "Menghapus draf pengajuan yang statusnya masih 'DRAFT'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Draf berhasil dihapus",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request (bukan DRAFT)", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID pengajuan yang akan dihapus") @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        MessageResponse response = pengajuanService.deletePengajuan(user, id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Melampirkan dokumen ke draf",
            description = "Menambahkan satu atau lebih referensi dokumen ke draf pengajuan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dokumen berhasil dilampirkan")
    })
    @PostMapping("/{id}/lampirkan")
    public ResponseEntity<Pengajuan> lampirkanDokumen(
            @Parameter(description = "ID pengajuan yang akan diberi lampiran") @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody LampirkanDokumenRequest request) {
        return ResponseEntity.ok(pengajuanService.lampirkanDokumen(user, id, request));
    }

    @Operation(summary = "Mengirim draf pengajuan untuk verifikasi",
            description = "Mengubah status pengajuan dari 'DRAFT' menjadi 'DIAJUKAN'. Endpoint akan gagal jika belum ada dokumen yang dilampirkan.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pengajuan berhasil di-submit"),
            @ApiResponse(responseCode = "400", description = "Bad Request (misal: belum ada lampiran)"),
            @ApiResponse(responseCode = "404", description = "Pengajuan tidak ditemukan")
    })
    @PostMapping("/{id}/submit")
    public ResponseEntity<Pengajuan> submit(
            @Parameter(description = "ID dari pengajuan yang ingin di-submit") @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(pengajuanService.submitPengajuan(user, id));
    }
}