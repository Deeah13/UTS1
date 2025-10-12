package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.ChangePasswordRequest;
import com.bps.uts.sipakjabat.dto.MessageResponse;
import com.bps.uts.sipakjabat.dto.ProfileResponse;
import com.bps.uts.sipakjabat.dto.UpdateProfileRequest;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        currentUser.setPangkatGolongan(request.getPangkatGolongan());
        currentUser.setJabatan(request.getJabatan());
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

    // Method helper privat untuk mengubah User menjadi ProfileResponse
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