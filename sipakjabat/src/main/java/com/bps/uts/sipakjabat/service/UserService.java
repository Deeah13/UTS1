package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.*;
import com.bps.uts.sipakjabat.model.User;
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

    // ==================== OPERASI UNTUK USER SENDIRI ====================

    public ProfileResponse getProfile(User currentUser) {
        return mapToProfileResponse(currentUser);
    }

    @Transactional
    public ProfileResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        // User hanya bisa update nama dan email
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
        // Validasi password lama
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalStateException("Password lama salah");
        }

        // Validasi password baru tidak sama dengan password lama
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new IllegalStateException("Password baru tidak boleh sama dengan password lama");
        }

        // Validasi panjang password baru (minimal 6 karakter)
        if (request.getNewPassword().length() < 6) {
            throw new IllegalStateException("Password baru minimal 6 karakter");
        }

        // Update password
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
        // Validasi input
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

        // Cek duplikasi NIP
        if (userRepository.findByNip(request.getNip()).isPresent()) {
            throw new IllegalStateException("NIP sudah terdaftar");
        }

        // Buat user baru
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

        // Update field yang dikirim (jika tidak null)
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

    @Transactional
    public MessageResponse deleteUserByAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));

        // Cek apakah user ini sedang memiliki pengajuan aktif
        // (Optional: bisa ditambahkan validasi business logic di sini)

        userRepository.delete(user);
        return new MessageResponse("Akun pengguna dengan NIP " + user.getNip() + " berhasil dihapus");
    }

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