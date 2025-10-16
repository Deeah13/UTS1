package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.*;
import com.bps.uts.sipakjabat.model.Pengajuan; // <-- Import tambahan
import com.bps.uts.sipakjabat.model.StatusPengajuan; // <-- Import tambahan
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.MasterDokumenPegawaiRepository; // <-- Import tambahan
import com.bps.uts.sipakjabat.repository.PengajuanRepository; // <-- Import tambahan
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PengajuanRepository pengajuanRepository; // <-- Injeksi Repository
    private final MasterDokumenPegawaiRepository dokumenRepository; // <-- Injeksi Repository

    // ==================== OPERASI UNTUK USER SENDIRI ====================

    public ProfileResponse getProfile(User currentUser) {
        return mapToProfileResponse(currentUser);
    }

    @Transactional
    public ProfileResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        if (request.getNamaLengkap() != null) {
            currentUser.setNamaLengkap(request.getNamaLengkap());
        }
        if (request.getEmail() != null) {
            currentUser.setEmail(request.getEmail());
        }
        User updatedUser = userRepository.save(currentUser);
        return mapToProfileResponse(updatedUser);
    }

    @Transactional
    public MessageResponse changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalStateException("Password lama salah");
        }
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new IllegalStateException("Password baru tidak boleh sama dengan password lama");
        }
        if (request.getNewPassword().length() < 6) {
            throw new IllegalStateException("Password baru minimal 6 karakter");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        return new MessageResponse("Password berhasil diperbarui");
    }

    // ==================== OPERASI UNTUK ADMIN/VERIFIKATOR ====================

    public List<ProfileResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));
    }

    @Transactional
    public User createUserByAdmin(AdminCreateUserRequest request) {
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role wajib diisi saat membuat user baru");
        }
        if (request.getNip() == null || request.getNip().trim().isEmpty()) {
            throw new IllegalArgumentException("NIP wajib diisi");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email wajib diisi");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password minimal 6 karakter");
        }
        if (userRepository.findByNip(request.getNip()).isPresent()) {
            throw new IllegalStateException("NIP sudah terdaftar");
        }
        var user = User.builder()
                .namaLengkap(request.getNamaLengkap())
                .nip(request.getNip())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .pangkatGolongan(request.getPangkatGolongan())
                .jabatan(request.getJabatan())
                .tmtPangkatTerakhir(request.getTmtPangkatTerakhir())
                .role(request.getRole())
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserByAdmin(Long userId, UpdateUserByAdminRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));
        if (request.getNamaLengkap() != null && !request.getNamaLengkap().trim().isEmpty()) {
            user.setNamaLengkap(request.getNamaLengkap());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getPangkatGolongan() != null) {
            user.setPangkatGolongan(request.getPangkatGolongan());
        }
        if (request.getJabatan() != null) {
            user.setJabatan(request.getJabatan());
        }
        if (request.getTmtPangkatTerakhir() != null) {
            user.setTmtPangkatTerakhir(request.getTmtPangkatTerakhir());
        }
        return userRepository.save(user);
    }

    // --- METHOD INI YANG DIPERBAIKI ---
    @Transactional
    public MessageResponse deleteUserByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));

        // 1. Dapatkan semua pengajuan milik user ini
        List<Pengajuan> pengajuanList = pengajuanRepository.findByUserId(user.getId());

        // 2. Cek apakah ada pengajuan yang statusnya masih aktif (menunggu proses)
        boolean hasActivePengajuan = pengajuanList.stream()
                .anyMatch(p -> p.getStatus() == StatusPengajuan.SUBMITTED || p.getStatus() == StatusPengajuan.PERLU_REVISI);

        if (hasActivePengajuan) {
            // 3. Jika ada, tolak penghapusan dan beri pesan error
            throw new IllegalStateException(
                    "Tidak dapat menghapus akun. Pengguna masih memiliki pengajuan yang sedang diproses (status SUBMITTED atau PERLU_REVISI)."
            );
        }

        // 4. Jika tidak ada pengajuan aktif, hapus semua data terkait SEBELUM menghapus user
        // Hapus semua pengajuan (yang statusnya DRAFT, APPROVED, atau REJECTED)
        pengajuanRepository.deleteAll(pengajuanList);

        // Hapus semua dokumen
        dokumenRepository.deleteAll(dokumenRepository.findByUserId(user.getId()));

        // 5. Setelah semua data anak dihapus, baru hapus data induk (user)
        userRepository.delete(user);

        return new MessageResponse("Akun pengguna dengan NIP " + user.getNip() + " beserta seluruh data terkait berhasil dihapus.");
    }
    // --- AKHIR DARI PERBAIKAN ---

    @Transactional
    public User ubahRole(Long userId, UbahRoleRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));
        if (request.getNewRole() == null) {
            throw new IllegalArgumentException("Role baru wajib diisi");
        }
        user.setRole(request.getNewRole());
        return userRepository.save(user);
    }

    // ==================== HELPER METHOD ====================

    private ProfileResponse mapToProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .namaLengkap(user.getNamaLengkap())
                .nip(user.getNip())
                .email(user.getEmail())
                .pangkatGolongan(user.getPangkatGolongan())
                .jabatan(user.getJabatan())
                .role(user.getRole().name())
                .build();
    }
}