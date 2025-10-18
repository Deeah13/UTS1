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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Endpoint Admin (Verifikator)", description = "Semua aksi yang dapat dilakukan oleh verifikator")
@PreAuthorize("hasRole('VERIFIKATOR')")
public class AdminController {

    private final UserService userService;
    private final PengajuanService pengajuanService;
    private final VerifikasiService verifikasiService;
    private final DokumenService dokumenService;

    // ==================== MANAJEMEN USER ====================

    @Operation(
            summary = "Membuat pengguna baru",
            description = "Ini adalah satu-satunya cara untuk membuat akun baru di sistem. " +
                    "Self-registration tidak diperbolehkan untuk menjaga keamanan dan integritas data."
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pengguna baru berhasil dibuat"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Input tidak valid (misal: NIP/Email/Role wajib diisi, NIP sudah terdaftar)",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @PostMapping("/users")
    public ResponseEntity<GlobalResponseDTO<User>> createUser(@RequestBody AdminCreateUserRequest request) {
        User newUser = userService.createUserByAdmin(request);
        return ResponseEntity.ok(GlobalResponseDTO.<User>builder()
                .status("success")
                .message("Pengguna baru berhasil dibuat")
                .data(newUser)
                .build());
    }

    @Operation(summary = "Melihat semua daftar pengguna")
    @GetMapping("/users")
    public ResponseEntity<GlobalResponseDTO<List<ProfileResponse>>> getAllUsers() {
        List<ProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(GlobalResponseDTO.<List<ProfileResponse>>builder()
                .status("success")
                .data(users)
                .build());
    }

    @Operation(summary = "Melihat detail pengguna tertentu")
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data pengguna berhasil diambil"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @GetMapping("/users/{id}")
    public ResponseEntity<GlobalResponseDTO<User>> getUserDetail(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(GlobalResponseDTO.<User>builder()
                .status("success")
                .data(user)
                .build());
    }

    @Operation(
            summary = "Memperbarui data kepegawaian pengguna",
            description = "Mengubah data kepegawaian seperti pangkat, golongan, jabatan, dan TMT pangkat terakhir"
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data pengguna berhasil diperbarui"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @PutMapping("/users/{id}")
    public ResponseEntity<GlobalResponseDTO<User>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserByAdminRequest request) {
        User updatedUser = userService.updateUserByAdmin(id, request);
        return ResponseEntity.ok(GlobalResponseDTO.<User>builder()
                .status("success")
                .message("Data pengguna berhasil diperbarui")
                .data(updatedUser)
                .build());
    }

    @Operation(
            summary = "Mengubah role seorang pengguna",
            description = "Mengubah role pengguna antara PEGAWAI dan VERIFIKATOR"
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Role pengguna berhasil diubah"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Role baru wajib diisi",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @PutMapping("/users/{id}/role")
    public ResponseEntity<GlobalResponseDTO<User>> ubahRole(
            @PathVariable Long id,
            @RequestBody UbahRoleRequestDTO request) {
        User updatedUser = userService.ubahRole(id, request);
        return ResponseEntity.ok(GlobalResponseDTO.<User>builder()
                .status("success")
                .message("Role pengguna berhasil diubah menjadi " + request.getNewRole())
                .data(updatedUser)
                .build());
    }

    @Operation(
            summary = "Menghapus akun pengguna",
            description = "Menghapus akun pengguna secara permanen. " +
                    "Pengguna tidak dapat menghapus akunnya sendiri untuk menjaga integritas data."
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Akun pengguna berhasil dihapus"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Gagal menghapus (pengguna masih memiliki pengajuan aktif)",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @DeleteMapping("/users/{id}")
    public ResponseEntity<GlobalResponseDTO<String>> deleteUser(@PathVariable Long id) {
        MessageResponse message = userService.deleteUserByAdmin(id);
        return ResponseEntity.ok(GlobalResponseDTO.<String>builder()
                .status("success")
                .message(message.getMessage())
                .build());
    }

    // ==================== MANAJEMEN DOKUMEN ====================

    @Operation(
            summary = "Menambahkan dokumen untuk pengguna tertentu",
            description = "Menambahkan dokumen kepegawaian seperti SK Pangkat, SKP, dll untuk pengguna tertentu"
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dokumen berhasil ditambahkan"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @PostMapping("/users/{userId}/dokumen")
    public ResponseEntity<GlobalResponseDTO<MasterDokumenPegawai>> createDokumenForUser(
            @PathVariable Long userId,
            @RequestBody PegawaiCreateDokumenRequest request) {

        CreateDokumenRequest adminRequest = new CreateDokumenRequest();
        adminRequest.setUserId(userId);
        adminRequest.setJenisDokumen(request.getJenisDokumen());
        adminRequest.setNomorDokumen(request.getNomorDokumen());
        adminRequest.setTanggalTerbit(request.getTanggalTerbit());
        adminRequest.setDeskripsi(request.getDeskripsi());

        MasterDokumenPegawai newDoc = dokumenService.createDokumenForUser(adminRequest);
        return ResponseEntity.ok(GlobalResponseDTO.<MasterDokumenPegawai>builder()
                .status("success")
                .message("Dokumen berhasil ditambahkan untuk pengguna")
                .data(newDoc)
                .build());
    }

    // ==================== MANAJEMEN PENGAJUAN ====================

    @Operation(summary = "Melihat semua pengajuan dari seluruh pegawai")
    @GetMapping("/pengajuan/all")
    public ResponseEntity<GlobalResponseDTO<List<Pengajuan>>> getAllPengajuan() {
        List<Pengajuan> allPengajuan = pengajuanService.getAllPengajuan();
        return ResponseEntity.ok(GlobalResponseDTO.<List<Pengajuan>>builder()
                .status("success")
                .data(allPengajuan)
                .build());
    }

    @Operation(
            summary = "Melihat pengajuan yang perlu diverifikasi",
            description = "Mendapatkan daftar pengajuan dengan status SUBMITTED yang menunggu verifikasi"
    )
    @GetMapping("/pengajuan/submitted")
    public ResponseEntity<GlobalResponseDTO<List<Pengajuan>>> getSubmittedPengajuan() {
        List<Pengajuan> submittedPengajuan = verifikasiService.getAllPengajuanMasuk();
        return ResponseEntity.ok(GlobalResponseDTO.<List<Pengajuan>>builder()
                .status("success")
                .data(submittedPengajuan)
                .build());
    }

    @Operation(summary = "Melihat detail satu pengajuan spesifik")
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data pengajuan berhasil diambil"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pengajuan tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @GetMapping("/pengajuan/{id}")
    public ResponseEntity<GlobalResponseDTO<Pengajuan>> getDetailPengajuan(@PathVariable Long id) {
        Pengajuan pengajuan = pengajuanService.getDetailPengajuan(id);
        return ResponseEntity.ok(GlobalResponseDTO.<Pengajuan>builder()
                .status("success")
                .data(pengajuan)
                .build());
    }

    @Operation(
            summary = "Menyetujui sebuah pengajuan",
            description = "Menyetujui pengajuan kenaikan pangkat/jabatan. " +
                    "Data kepegawaian pegawai akan otomatis diupdate sesuai dengan pangkat/jabatan tujuan."
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pengajuan berhasil disetujui"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Hanya pengajuan dengan status SUBMITTED yang bisa disetujui",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pengajuan tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @PostMapping("/pengajuan/{id}/approve")
    public ResponseEntity<GlobalResponseDTO<PengajuanVerifikasiResponseDTO>> approve(
            @PathVariable Long id,
            @RequestBody VerifikasiRequestDTO request,
            @AuthenticationPrincipal User verifikator) {
        PengajuanVerifikasiResponseDTO responseData = verifikasiService.approvePengajuan(id, request, verifikator);
        return ResponseEntity.ok(GlobalResponseDTO.<PengajuanVerifikasiResponseDTO>builder()
                .status("success")
                .message("Pengajuan berhasil disetujui")
                .data(responseData)
                .build());
    }

    @Operation(
            summary = "Menolak sebuah pengajuan",
            description = "Menolak pengajuan dengan memberikan alasan penolakan"
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pengajuan berhasil ditolak"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Hanya pengajuan dengan status SUBMITTED yang bisa ditolak",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pengajuan tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @PostMapping("/pengajuan/{id}/reject")
    public ResponseEntity<GlobalResponseDTO<PengajuanVerifikasiResponseDTO>> reject(
            @PathVariable Long id,
            @RequestBody VerifikasiRequestDTO request,
            @AuthenticationPrincipal User verifikator) {
        PengajuanVerifikasiResponseDTO responseData = verifikasiService.rejectPengajuan(id, request, verifikator);
        return ResponseEntity.ok(GlobalResponseDTO.<PengajuanVerifikasiResponseDTO>builder()
                .status("success")
                .message("Pengajuan ditolak")
                .data(responseData)
                .build());
    }

    @Operation(
            summary = "Mengembalikan pengajuan untuk direvisi",
            description = "Mengembalikan pengajuan ke pegawai untuk diperbaiki dengan memberikan catatan revisi"
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pengajuan telah dikembalikan untuk revisi"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Hanya pengajuan dengan status SUBMITTED yang bisa dikembalikan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pengajuan tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @PostMapping("/pengajuan/{id}/revisi")
    public ResponseEntity<GlobalResponseDTO<Pengajuan>> kembalikanUntukRevisi(
            @PathVariable Long id,
            @RequestBody RevisiRequestDTO request,
            @AuthenticationPrincipal User verifikator) {
        Pengajuan pengajuan = verifikasiService.kembalikanUntukRevisi(id, request, verifikator);
        return ResponseEntity.ok(GlobalResponseDTO.<Pengajuan>builder()
                .status("success")
                .message("Pengajuan telah dikembalikan untuk revisi")
                .data(pengajuan)
                .build());
    }

    @Operation(summary = "Mendapatkan ringkasan/summary data pengajuan")
    @GetMapping("/pengajuan/summary")
    public ResponseEntity<GlobalResponseDTO<SummaryResponseDTO>> getSummary() {
        SummaryResponseDTO summary = pengajuanService.getSummary();
        return ResponseEntity.ok(GlobalResponseDTO.<SummaryResponseDTO>builder()
                .status("success")
                .data(summary)
                .build());
    }
}