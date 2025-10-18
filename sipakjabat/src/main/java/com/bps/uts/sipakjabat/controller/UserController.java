package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.*;
import com.bps.uts.sipakjabat.model.Pengajuan;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.service.PengajuanService;
import com.bps.uts.sipakjabat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Manajemen Profil Pengguna", description = "API untuk mengelola data profil milik pengguna yang sedang login")
public class UserController {

    private final UserService userService;
    private final PengajuanService pengajuanService;

    @Operation(
            summary = "Mendapatkan profil saya",
            description = "Mengambil data profil lengkap dari pengguna yang sedang login"
    )
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Data profil berhasil diambil"
    ))
    @GetMapping("/profile")
    public ResponseEntity<GlobalResponseDTO<ProfileResponse>> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        ProfileResponse profile = userService.getProfile(currentUser);
        return ResponseEntity.ok(GlobalResponseDTO.<ProfileResponse>builder()
                .status("success")
                .data(profile)
                .build());
    }

    @Operation(
            summary = "Memperbarui profil saya",
            description = "Memperbarui data profil personal (nama lengkap dan email). " +
                    "Data kepegawaian seperti NIP, pangkat, jabatan, dan TMT hanya dapat diubah oleh Verifikator."
    )
    @ApiResponses(@ApiResponse(
            responseCode = "200",
            description = "Profil berhasil diperbarui"
    ))
    @PutMapping("/profile")
    public ResponseEntity<GlobalResponseDTO<ProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateProfileRequest request) {
        ProfileResponse updatedProfile = userService.updateProfile(currentUser, request);
        return ResponseEntity.ok(GlobalResponseDTO.<ProfileResponse>builder()
                .status("success")
                .message("Profil berhasil diperbarui")
                .data(updatedProfile)
                .build());
    }

    @Operation(
            summary = "Mengganti password saya",
            description = "Mengganti password pengguna setelah memverifikasi password lama. " +
                    "Password baru harus berbeda dari password lama."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password berhasil diganti",
                    // Skema diubah agar konsisten menggunakan GlobalResponseDTO
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    // Deskripsi diperjelas
                    description = "Bad Request - Password lama salah, password baru sama, atau password baru < 6 karakter",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    @PutMapping("/change-password")
    public ResponseEntity<GlobalResponseDTO<String>> changeMyPassword(
            @AuthenticationPrincipal User currentUser,
            @RequestBody ChangePasswordRequest request) {
        MessageResponse message = userService.changePassword(currentUser, request);
        return ResponseEntity.ok(GlobalResponseDTO.<String>builder()
                .status("success")
                .message(message.getMessage())
                .build());
    }

    @Operation(
            summary = "Melihat semua pengajuan saya",
            description = "Mendapatkan daftar semua pengajuan yang pernah dibuat oleh pengguna yang sedang login"
    )
    @GetMapping("/pengajuan")
    public ResponseEntity<GlobalResponseDTO<List<Pengajuan>>> getMyPengajuan(
            @AuthenticationPrincipal User currentUser) {
        List<Pengajuan> pengajuanList = pengajuanService.getMyPengajuan(currentUser.getId());
        return ResponseEntity.ok(GlobalResponseDTO.<List<Pengajuan>>builder()
                .status("success")
                .data(pengajuanList)
                .build());
    }

    @Operation(
            summary = "Melihat detail pengajuan saya",
            description = "Mendapatkan detail lengkap dari satu pengajuan milik pengguna yang sedang login"
    )
    // Anotasi Ditambahkan
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Data detail pengajuan berhasil diambil"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Akses ditolak (mencoba melihat pengajuan milik orang lain)",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pengajuan tidak ditemukan",
                    content = @Content(schema = @Schema(implementation = GlobalResponseDTO.class))
            )
    })
    // Akhir Anotasi
    @GetMapping("/pengajuan/{id}")
    public ResponseEntity<GlobalResponseDTO<Pengajuan>> getMyPengajuanDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        Pengajuan pengajuan = pengajuanService.getDetailPengajuan(id);

        // Validasi bahwa pengajuan milik user yang sedang login
        if (!pengajuan.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Anda tidak memiliki akses ke pengajuan ini");
        }

        return ResponseEntity.ok(GlobalResponseDTO.<Pengajuan>builder()
                .status("success")
                .data(pengajuan)
                .build());
    }
}