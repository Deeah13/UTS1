package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.CreateDokumenRequest;
import com.bps.uts.sipakjabat.dto.MessageResponse;
import com.bps.uts.sipakjabat.dto.PegawaiCreateDokumenRequest;
import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.MasterDokumenPegawaiRepository;
import com.bps.uts.sipakjabat.repository.PengajuanRepository; // <-- 1. Import tambahan
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DokumenService {

    private final MasterDokumenPegawaiRepository dokumenRepository;
    private final UserRepository userRepository;
    private final PengajuanRepository pengajuanRepository; // <-- 2. Injeksi Repository Pengajuan

    @Transactional
    public MasterDokumenPegawai createMyDokumen(PegawaiCreateDokumenRequest request, User currentUser) {
        MasterDokumenPegawai newDokumen = MasterDokumenPegawai.builder()
                .user(currentUser)
                .jenisDokumen(request.getJenisDokumen())
                .nomorDokumen(request.getNomorDokumen())
                .tanggalTerbit(request.getTanggalTerbit())
                .deskripsi(request.getDeskripsi())
                .build();
        return dokumenRepository.save(newDokumen);
    }

    // --- METHOD INI YANG DIPERBAIKI ---
    @Transactional
    public MessageResponse deleteMyDokumen(Long dokumenId, User currentUser) {
        MasterDokumenPegawai dokumen = dokumenRepository.findById(dokumenId)
                .orElseThrow(() -> new RuntimeException("Dokumen dengan ID " + dokumenId + " tidak ditemukan"));

        // Validasi kepemilikan
        if (!dokumen.getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Akses ditolak: Anda tidak memiliki izin untuk menghapus dokumen ini.");
        }

        // 3. LOGIKA VALIDASI BARU: Cek apakah dokumen ini terlampir di pengajuan manapun
        long countUsage = pengajuanRepository.countByLampiranContains(dokumen);
        if (countUsage > 0) {
            // 4. Jika terpakai, tolak penghapusan dan beri pesan error yang jelas
            throw new IllegalStateException(
                    "Tidak dapat menghapus dokumen. Dokumen ini sedang terlampir pada " + countUsage + " pengajuan."
            );
        }

        // 5. Jika tidak terpakai (aman untuk dihapus), baru jalankan proses hapus
        dokumenRepository.delete(dokumen);
        return new MessageResponse("Dokumen berhasil dihapus.");
    }
    // --- AKHIR DARI PERBAIKAN ---

    @Transactional
    public MasterDokumenPegawai createDokumenForUser(CreateDokumenRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User dengan ID " + request.getUserId() + " tidak ditemukan"));

        MasterDokumenPegawai newDokumen = MasterDokumenPegawai.builder()
                .user(user)
                .jenisDokumen(request.getJenisDokumen())
                .nomorDokumen(request.getNomorDokumen())
                .tanggalTerbit(request.getTanggalTerbit())
                .deskripsi(request.getDeskripsi())
                .build();

        return dokumenRepository.save(newDokumen);
    }
}