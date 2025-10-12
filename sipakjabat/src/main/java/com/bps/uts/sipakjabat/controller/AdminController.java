package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.*;
import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.model.Pengajuan;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.service.DokumenService;
import com.bps.uts.sipakjabat.service.PengajuanService;
import com.bps.uts.sipakjabat.service.UserService;
import com.bps.uts.sipakjabat.service.VerifikasiService;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Endpoint Admin (Verifikator)", description = "Semua aksi yang dapat dilakukan oleh verifikator.")
@PreAuthorize("hasRole('VERIFIKATOR')")
public class AdminController {

    private final UserService userService;
    private final PengajuanService pengajuanService;
    private final VerifikasiService verifikasiService;
    private final DokumenService dokumenService;

    @Operation(summary = "Membuat pengguna baru (oleh Verifikator)")
    @PostMapping("/users")
    public ResponseEntity<GlobalResponseDTO<User>> createUser(@RequestBody AdminCreateUserRequest request) {
        User newUser = userService.createUserByAdmin(request);
        return ResponseEntity.ok(GlobalResponseDTO.<User>builder().status("success").message("Pengguna baru berhasil dibuat").data(newUser).build());
    }

    @Operation(summary = "Melihat semua daftar pengguna")
    @GetMapping("/users")
    public ResponseEntity<GlobalResponseDTO<List<ProfileResponse>>> getAllUsers() {
        List<ProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(GlobalResponseDTO.<List<ProfileResponse>>builder().status("success").data(users).build());
    }

    @Operation(summary = "Mengubah role seorang pengguna (oleh Verifikator)")
    @PutMapping("/users/{id}/role")
    public ResponseEntity<GlobalResponseDTO<User>> ubahRole(
            @PathVariable Long id, @RequestBody UbahRoleRequestDTO request) {
        User updatedUser = userService.ubahRole(id, request);
        return ResponseEntity.ok(GlobalResponseDTO.<User>builder().status("success").message("Role pengguna berhasil diubah").data(updatedUser).build());
    }

    @Operation(summary = "Menambahkan dokumen baru untuk pengguna tertentu (misal: SK Kenaikan Pangkat)")
    @PostMapping("/users/{userId}/dokumen")
    public ResponseEntity<GlobalResponseDTO<MasterDokumenPegawai>> createDokumenForUser(
            @PathVariable Long userId, @RequestBody PegawaiCreateDokumenRequest request) {

        CreateDokumenRequest adminRequest = new CreateDokumenRequest();
        adminRequest.setUserId(userId);
        adminRequest.setJenisDokumen(request.getJenisDokumen());
        adminRequest.setNomorDokumen(request.getNomorDokumen());
        adminRequest.setTanggalTerbit(request.getTanggalTerbit());
        adminRequest.setDeskripsi(request.getDeskripsi());

        MasterDokumenPegawai newDoc = dokumenService.createDokumenForUser(adminRequest);
        return ResponseEntity.ok(GlobalResponseDTO.<MasterDokumenPegawai>builder().status("success").message("Dokumen baru berhasil ditambahkan untuk pengguna").data(newDoc).build());
    }

    @Operation(summary = "Melihat semua pengajuan dari seluruh pegawai (histori)")
    @GetMapping("/pengajuan/all")
    public ResponseEntity<GlobalResponseDTO<List<Pengajuan>>> getAllPengajuan() {
        List<Pengajuan> allPengajuan = pengajuanService.getAllPengajuan();
        return ResponseEntity.ok(GlobalResponseDTO.<List<Pengajuan>>builder().status("success").data(allPengajuan).build());
    }

    @Operation(summary = "Melihat semua pengajuan yang perlu diverifikasi (status SUBMITTED)")
    @GetMapping("/pengajuan/submitted")
    public ResponseEntity<GlobalResponseDTO<List<Pengajuan>>> getSubmittedPengajuan() {
        List<Pengajuan> submittedPengajuan = verifikasiService.getAllPengajuanMasuk();
        return ResponseEntity.ok(GlobalResponseDTO.<List<Pengajuan>>builder().status("success").data(submittedPengajuan).build());
    }

    @Operation(summary = "Melihat detail satu pengajuan spesifik")
    @GetMapping("/pengajuan/{id}")
    public ResponseEntity<GlobalResponseDTO<Pengajuan>> getDetailPengajuan(@PathVariable Long id) {
        Pengajuan pengajuan = pengajuanService.getDetailPengajuan(id);
        return ResponseEntity.ok(GlobalResponseDTO.<Pengajuan>builder().status("success").data(pengajuan).build());
    }

    @Operation(summary = "Menyetujui sebuah pengajuan")
    @PostMapping("/pengajuan/{id}/approve")
    public ResponseEntity<GlobalResponseDTO<PengajuanVerifikasiResponseDTO>> approve(
            @PathVariable Long id, @RequestBody VerifikasiRequestDTO request, @AuthenticationPrincipal User verifikator) {
        PengajuanVerifikasiResponseDTO responseData = verifikasiService.approvePengajuan(id, request, verifikator);
        return ResponseEntity.ok(GlobalResponseDTO.<PengajuanVerifikasiResponseDTO>builder().status("success").message("Pengajuan berhasil disetujui").data(responseData).build());
    }

    @Operation(summary = "Menolak sebuah pengajuan")
    @PostMapping("/pengajuan/{id}/reject")
    public ResponseEntity<GlobalResponseDTO<PengajuanVerifikasiResponseDTO>> reject(
            @PathVariable Long id, @RequestBody VerifikasiRequestDTO request, @AuthenticationPrincipal User verifikator) {
        PengajuanVerifikasiResponseDTO responseData = verifikasiService.rejectPengajuan(id, request, verifikator);
        return ResponseEntity.ok(GlobalResponseDTO.<PengajuanVerifikasiResponseDTO>builder().status("success").message("Pengajuan ditolak").data(responseData).build());
    }

    @Operation(summary = "Mengembalikan pengajuan ke pegawai untuk direvisi")
    @PostMapping("/pengajuan/{id}/revisi")
    public ResponseEntity<GlobalResponseDTO<Pengajuan>> kembalikanUntukRevisi(
            @PathVariable Long id, @RequestBody RevisiRequestDTO request, @AuthenticationPrincipal User verifikator) {
        Pengajuan pengajuan = verifikasiService.kembalikanUntukRevisi(id, request, verifikator);
        return ResponseEntity.ok(GlobalResponseDTO.<Pengajuan>builder().status("success").message("Pengajuan telah dikembalikan untuk revisi").data(pengajuan).build());
    }

    @Operation(summary = "Mendapatkan ringkasan/summary data pengajuan")
    @GetMapping("/pengajuan/summary")
    public ResponseEntity<GlobalResponseDTO<SummaryResponseDTO>> getSummary() {
        SummaryResponseDTO summary = pengajuanService.getSummary();
        return ResponseEntity.ok(GlobalResponseDTO.<SummaryResponseDTO>builder().status("success").data(summary).build());
    }
}