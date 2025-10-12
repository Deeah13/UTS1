package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.VerifikasiRequest;
import com.bps.uts.sipakjabat.model.Pengajuan;
import com.bps.uts.sipakjabat.model.StatusPengajuan;
import com.bps.uts.sipakjabat.model.User; // <-- 1. Tambahkan import untuk User
import com.bps.uts.sipakjabat.repository.PengajuanRepository;
import com.bps.uts.sipakjabat.repository.UserRepository; // <-- 2. Tambahkan import untuk UserRepository
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifikasiService {

    private final PengajuanRepository pengajuanRepository;
    private final UserRepository userRepository; // <-- 3. Inject UserRepository di sini

    public List<Pengajuan> getAllPengajuanMasuk() {
        // Ambil semua pengajuan yang sudah di-submit oleh pegawai
        return pengajuanRepository.findAllByStatus(StatusPengajuan.DIAJUKAN);
    }

    @Transactional
    public Pengajuan verifikasiPengajuan(Long pengajuanId, VerifikasiRequest request) {
        Pengajuan pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new RuntimeException("Pengajuan tidak ditemukan"));

        if (request.getStatus() != StatusPengajuan.DISETUJUI && request.getStatus() != StatusPengajuan.DITOLAK) {
            throw new IllegalArgumentException("Status verifikasi hanya boleh DISETUJUI atau DITOLAK");
        }

        // --- LOGIKA PERBAIKAN DIMULAI DI SINI ---

        // 4. Jika pengajuan disetujui, update data master pegawai
        if (request.getStatus() == StatusPengajuan.DISETUJUI) {
            User userToUpdate = pengajuan.getUser(); // Ambil objek user dari pengajuan

            // Update field pangkat dan jabatan user dengan data dari pengajuan
            userToUpdate.setPangkatGolongan(pengajuan.getPangkatTujuan());
            userToUpdate.setJabatan(pengajuan.getJabatanTujuan());

            userRepository.save(userToUpdate); // 5. Simpan perubahan pada data user ke database
        }

        // --- LOGIKA PERBAIKAN SELESAI ---

        // Lanjutkan proses untuk mengubah status pengajuan itu sendiri
        pengajuan.setStatus(request.getStatus());
        pengajuan.setCatatanVerifikator(request.getCatatan());
        return pengajuanRepository.save(pengajuan);
    }
}