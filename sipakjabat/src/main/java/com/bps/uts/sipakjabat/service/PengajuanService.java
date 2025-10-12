package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.CreatePengajuanRequest;
import com.bps.uts.sipakjabat.dto.LampirkanDokumenRequest;
import com.bps.uts.sipakjabat.dto.MessageResponse;
import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.model.Pengajuan;
import com.bps.uts.sipakjabat.model.StatusPengajuan;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.MasterDokumenPegawaiRepository;
import com.bps.uts.sipakjabat.repository.PengajuanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PengajuanService {

    private final PengajuanRepository pengajuanRepository;
    private final MasterDokumenPegawaiRepository dokumenRepository;

    // ... (method create, get, update, lampirkan, submit tetap sama) ...
    public List<Pengajuan> getMyPengajuan(Long userId) {
        return pengajuanRepository.findByUserId(userId);
    }

    public Pengajuan getPengajuanById(User user, Long pengajuanId) {
        return pengajuanRepository.findById(pengajuanId)
                .filter(p -> p.getUser().getId().equals(user.getId())) // Pastikan pemiliknya benar
                .orElseThrow(() -> new RuntimeException("Pengajuan tidak ditemukan atau bukan milik Anda"));
    }

    public Pengajuan createPengajuan(User user, CreatePengajuanRequest request) {
        Pengajuan pengajuan = Pengajuan.builder()
                .user(user)
                .pangkatTujuan(request.getPangkatTujuan())
                .jabatanTujuan(request.getJabatanTujuan())
                .status(StatusPengajuan.DRAFT)
                .tanggalPengajuan(LocalDateTime.now())
                .build();
        return pengajuanRepository.save(pengajuan);
    }

    @Transactional
    public Pengajuan updatePengajuan(User user, Long pengajuanId, CreatePengajuanRequest request) {
        Pengajuan pengajuan = getPengajuanById(user, pengajuanId);
        if (pengajuan.getStatus() != StatusPengajuan.DRAFT) {
            throw new IllegalStateException("Hanya pengajuan dengan status DRAFT yang bisa diubah");
        }
        pengajuan.setPangkatTujuan(request.getPangkatTujuan());
        pengajuan.setJabatanTujuan(request.getJabatanTujuan());
        return pengajuanRepository.save(pengajuan);
    }

    @Transactional
    public MessageResponse deletePengajuan(User user, Long pengajuanId) {
        Pengajuan pengajuan = getPengajuanById(user, pengajuanId);
        if (pengajuan.getStatus() != StatusPengajuan.DRAFT) {
            throw new IllegalStateException("Hanya pengajuan dengan status DRAFT yang bisa dihapus");
        }
        pengajuanRepository.delete(pengajuan);
        return new MessageResponse("Draf pengajuan berhasil dihapus.");
    }

    @Transactional
    public Pengajuan lampirkanDokumen(User user, Long pengajuanId, LampirkanDokumenRequest request) {
        Pengajuan pengajuan = getPengajuanById(user, pengajuanId);
        List<MasterDokumenPegawai> dokumenList = dokumenRepository.findAllById(request.getDokumenIds());

        for (MasterDokumenPegawai doc : dokumenList) {
            if (!doc.getUser().getId().equals(user.getId())) {
                throw new SecurityException("Anda mencoba melampirkan dokumen milik orang lain");
            }
        }

        pengajuan.setLampiran(new HashSet<>(dokumenList));
        return pengajuanRepository.save(pengajuan);
    }

    @Transactional
    public Pengajuan submitPengajuan(User user, Long pengajuanId) {
        Pengajuan pengajuan = getPengajuanById(user, pengajuanId);
        if (pengajuan.getStatus() != StatusPengajuan.DRAFT) {
            throw new IllegalStateException("Hanya pengajuan dengan status DRAFT yang bisa diajukan");
        }
        if (pengajuan.getLampiran() == null || pengajuan.getLampiran().isEmpty()) {
            throw new IllegalStateException("Pengajuan harus memiliki setidaknya satu dokumen lampiran");
        }
        pengajuan.setStatus(StatusPengajuan.DIAJUKAN);
        return pengajuanRepository.save(pengajuan);
    }
}