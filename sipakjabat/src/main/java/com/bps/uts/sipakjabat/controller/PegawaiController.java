package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.*;
import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.model.Pengajuan;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.MasterDokumenPegawaiRepository;
import com.bps.uts.sipakjabat.service.DokumenService;
import com.bps.uts.sipakjabat.service.PengajuanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pegawai")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Endpoint Pegawai", description = "Semua aksi yang dapat dilakukan oleh pegawai.")
@PreAuthorize("hasRole('PEGAWAI')")
public class PegawaiController {

    private final PengajuanService pengajuanService;
    private final DokumenService dokumenService;
    private final MasterDokumenPegawaiRepository dokumenRepository;

    @Operation(summary = "Membuat draf pengajuan baru")
    @PostMapping("/pengajuan")
    public ResponseEntity<GlobalResponseDTO<PengajuanCreateResponseDTO>> createPengajuan(
            @AuthenticationPrincipal User user, @RequestBody PengajuanCreateRequestDTO request) {

        PengajuanCreateResponseDTO responseData = pengajuanService.createPengajuan(user, request);

        GlobalResponseDTO<PengajuanCreateResponseDTO> response = GlobalResponseDTO.<PengajuanCreateResponseDTO>builder()
                .status("success")
                .message("Pengajuan berhasil dibuat")
                .data(responseData)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Submit draf pengajuan untuk verifikasi")
    @PostMapping("/pengajuan/{id}/submit")
    public ResponseEntity<GlobalResponseDTO<Pengajuan>> submitPengajuan(
            @PathVariable Long id, @AuthenticationPrincipal User user) {

        Pengajuan pengajuanData = pengajuanService.submitPengajuan(user, id);

        GlobalResponseDTO<Pengajuan> response = GlobalResponseDTO.<Pengajuan>builder()
                .status("success")
                .message("Pengajuan berhasil disubmit untuk diverifikasi")
                .data(pengajuanData)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Melampirkan dokumen ke draf")
    @PostMapping("/pengajuan/{id}/lampirkan")
    public ResponseEntity<GlobalResponseDTO<Pengajuan>> lampirkanDokumen(
            @PathVariable Long id, @AuthenticationPrincipal User user, @RequestBody LampirkanDokumenRequest request) {

        Pengajuan pengajuanData = pengajuanService.lampirkanDokumen(user, id, request);

        GlobalResponseDTO<Pengajuan> response = GlobalResponseDTO.<Pengajuan>builder()
                .status("success")
                .data(pengajuanData)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Menghapus draf pengajuan")
    @DeleteMapping("/pengajuan/{id}")
    public ResponseEntity<GlobalResponseDTO<String>> deletePengajuan(
            @PathVariable Long id, @AuthenticationPrincipal User currentUser) {

        MessageResponse message = pengajuanService.deletePengajuan(currentUser, id);
        return ResponseEntity.ok(GlobalResponseDTO.<String>builder().status("success").message(message.getMessage()).build());
    }

    @Operation(summary = "Melihat semua dokumen milik saya")
    @GetMapping("/dokumen")
    public ResponseEntity<GlobalResponseDTO<List<MasterDokumenPegawai>>> getMyDocuments(@AuthenticationPrincipal User currentUser) {
        List<MasterDokumenPegawai> documents = dokumenRepository.findByUserId(currentUser.getId());
        return ResponseEntity.ok(GlobalResponseDTO.<List<MasterDokumenPegawai>>builder().status("success").data(documents).build());
    }

    @Operation(summary = "Menambah dokumen baru milik saya")
    @PostMapping("/dokumen")
    public ResponseEntity<GlobalResponseDTO<MasterDokumenPegawai>> createMyDokumen(
            @RequestBody PegawaiCreateDokumenRequest request, @AuthenticationPrincipal User currentUser) {

        MasterDokumenPegawai dokumen = dokumenService.createMyDokumen(request, currentUser);
        return ResponseEntity.ok(GlobalResponseDTO.<MasterDokumenPegawai>builder().status("success").message("Dokumen berhasil ditambahkan").data(dokumen).build());
    }

    @Operation(summary = "Menghapus dokumen milik saya")
    @DeleteMapping("/dokumen/{id}")
    public ResponseEntity<GlobalResponseDTO<String>> deleteMyDokumen(
            @PathVariable Long id, @AuthenticationPrincipal User currentUser) {

        MessageResponse message = dokumenService.deleteMyDokumen(id, currentUser);
        return ResponseEntity.ok(GlobalResponseDTO.<String>builder().status("success").message(message.getMessage()).build());
    }
}