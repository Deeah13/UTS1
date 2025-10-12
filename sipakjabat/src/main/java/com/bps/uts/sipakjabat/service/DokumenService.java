package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.CreateDokumenRequest;
import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.MasterDokumenPegawaiRepository;
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DokumenService {

    private final MasterDokumenPegawaiRepository dokumenRepository;
    private final UserRepository userRepository;

    public MasterDokumenPegawai createDokumenForUser(CreateDokumenRequest request) {
        // 1. Cari pengguna berdasarkan userId dari request
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User dengan ID " + request.getUserId() + " tidak ditemukan"));

        // 2. Buat objek dokumen baru
        MasterDokumenPegawai newDokumen = MasterDokumenPegawai.builder()
                .user(user)
                .jenisDokumen(request.getJenisDokumen())
                .nomorDokumen(request.getNomorDokumen())
                .tanggalTerbit(request.getTanggalTerbit())
                .deskripsi(request.getDeskripsi())
                .build();

        // 3. Simpan ke database dan kembalikan hasilnya
        return dokumenRepository.save(newDokumen);
    }
}