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

    public ProfileResponse getProfile(User currentUser) {
        return mapToProfileResponse(currentUser);
    }

    public ProfileResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        currentUser.setNamaLengkap(request.getNamaLengkap());
        currentUser.setEmail(request.getEmail());
        User updatedUser = userRepository.save(currentUser);
        return mapToProfileResponse(updatedUser);
    }

    public MessageResponse changePassword(User currentUser, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalStateException("Password lama salah");
        }
        if (request.getNewPassword().equals(request.getCurrentPassword())) {
            throw new IllegalStateException("Password baru tidak boleh sama dengan password lama");
        }
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        return new MessageResponse("Password berhasil diperbarui.");
    }

    public MessageResponse deleteAccount(User currentUser) {
        userRepository.delete(currentUser);
        return new MessageResponse("Akun berhasil dihapus secara permanen.");
    }

    public List<ProfileResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public User createUserByAdmin(AdminCreateUserRequest request) {
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role wajib diisi saat membuat user baru oleh admin.");
        }
        if (userRepository.findByNip(request.getNip()).isPresent()) {
            throw new IllegalStateException("NIP sudah terdaftar.");
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
    public User ubahRole(Long userId, UbahRoleRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User dengan ID " + userId + " tidak ditemukan"));

        if (request.getNewRole() == null) {
            throw new IllegalArgumentException("Role baru wajib diisi.");
        }

        user.setRole(request.getNewRole());
        return userRepository.save(user);
    }

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