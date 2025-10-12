package com.bps.uts.sipakjabat.controller;

import com.bps.uts.sipakjabat.dto.ChangePasswordRequest;
import com.bps.uts.sipakjabat.dto.MessageResponse; // <-- PASTIKAN BARIS INI ADA
import com.bps.uts.sipakjabat.dto.ProfileResponse;
import com.bps.uts.sipakjabat.dto.UpdateProfileRequest;
import com.bps.uts.sipakjabat.model.User;
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

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Manajemen Profil Pengguna", description = "API untuk mengelola data profil milik pengguna yang sedang login.")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Mengganti password saya", description = "Mengganti password pengguna setelah memverifikasi password lama.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password berhasil diganti",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request (misal: password lama salah)", content = @Content)
    })
    @PutMapping("/change-password")
    public ResponseEntity<MessageResponse> changeMyPassword(@AuthenticationPrincipal User currentUser, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(userService.changePassword(currentUser, request));
    }

    @Operation(summary = "Menghapus akun saya", description = "Menghapus akun pengguna yang sedang login secara permanen.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Akun berhasil dihapus",
            content = @Content(schema = @Schema(implementation = MessageResponse.class))))
    @DeleteMapping("/account")
    public ResponseEntity<MessageResponse> deleteMyAccount(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.deleteAccount(currentUser));
    }

    @Operation(summary = "Mendapatkan profil saya", description = "Mengambil data profil lengkap dari pengguna yang sedang login.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Data profil berhasil diambil"))
    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userService.getProfile(currentUser));
    }

    @Operation(summary = "Memperbarui profil saya", description = "Memperbarui data profil seperti nama, email, pangkat, dan jabatan. Akan mengembalikan data profil yang sudah terupdate.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Profil berhasil diperbarui"))
    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateMyProfile(@AuthenticationPrincipal User currentUser, @RequestBody UpdateProfileRequest request) {
        ProfileResponse updatedProfile = userService.updateProfile(currentUser, request);
        return ResponseEntity.ok(updatedProfile);
    }
}