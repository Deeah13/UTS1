package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.LampirkanDokumenRequest;
import com.bps.uts.sipakjabat.dto.MessageResponse;
import com.bps.uts.sipakjabat.dto.PengajuanCreateRequestDTO;
import com.bps.uts.sipakjabat.dto.PengajuanCreateResponseDTO;
import com.bps.uts.sipakjabat.dto.SummaryResponseDTO;
import com.bps.uts.sipakjabat.model.*;
import com.bps.uts.sipakjabat.repository.MasterDokumenPegawaiRepository;
import com.bps.uts.sipakjabat.repository.PengajuanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PengajuanService {

    private final PengajuanRepository pengajuanRepository;
    private final MasterDokumenPegawaiRepository dokumenRepository;

    @Transactional
    public PengajuanCreateResponseDTO createPengajuan(User user, PengajuanCreateRequestDTO request) {
        if (request.getJenisPengajuan() == null) {
            throw new IllegalArgumentException("Jenis pengajuan wajib diisi.");
        }

        Pengajuan pengajuan = Pengajuan.builder()
                .user(user)
                .pangkatSaatIni(user.getPangkatGolongan())
                .jabatanSaatIni(user.getJabatan())
                .jenisPengajuan(request.getJenisPengajuan())
                .pangkatTujuan(request.getPangkatTujuan())
                .jabatanTujuan(request.getJabatanTujuan())
                .status(StatusPengajuan.DRAFT)
                .tanggalDibuat(LocalDateTime.now())
                .build();

        Pengajuan savedPengajuan = pengajuanRepository.save(pengajuan);

        Period masaKerja = Period.ZERO;
        if (user.getTmtPangkatTerakhir() != null) {
            masaKerja = Period.between(user.getTmtPangkatTerakhir(), LocalDate.now());
        }

        return PengajuanCreateResponseDTO.builder()
                .id(savedPengajuan.getId())
                .pangkatSaatIni(savedPengajuan.getPangkatSaatIni())
                .pangkatTujuan(savedPengajuan.getPangkatTujuan())
                .jabatanSaatIni(savedPengajuan.getJabatanSaatIni())
                .jabatanTujuan(savedPengajuan.getJabatanTujuan())
                .status(savedPengajuan.getStatus())
                .masaKerjaGolonganTahun(masaKerja.getYears())
                .masaKerjaGolonganBulan(masaKerja.getMonths())
                .memenuhiSyaratMasaKerja(masaKerja.getYears() >= 2) // Asumsi syarat minimal 2 tahun
                .dokumenLengkap(false)
                .tanggalDibuat(savedPengajuan.getTanggalDibuat())
                .build();
    }

    @Transactional
    public Pengajuan submitPengajuan(User user, Long pengajuanId) {
        Pengajuan pengajuan = pengajuanRepository.findById(pengajuanId)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Pengajuan tidak ditemukan atau bukan milik Anda"));

        if (pengajuan.getStatus() != StatusPengajuan.DRAFT && pengajuan.getStatus() != StatusPengajuan.PERLU_REVISI) {
            throw new IllegalStateException("Hanya pengajuan dengan status DRAFT atau PERLU_REVISI yang bisa di-submit");
        }

        validateRequiredDocuments(pengajuan);

        pengajuan.setStatus(StatusPengajuan.SUBMITTED);
        pengajuan.setTanggalDiajukan(LocalDateTime.now());
        pengajuan.setCatatanVerifikator(null);

        return pengajuanRepository.save(pengajuan);
    }

    @Transactional
    public Pengajuan lampirkanDokumen(User user, Long pengajuanId, LampirkanDokumenRequest request) {
        Pengajuan pengajuan = pengajuanRepository.findById(pengajuanId)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Pengajuan tidak ditemukan atau bukan milik Anda"));

        if (pengajuan.getStatus() != StatusPengajuan.DRAFT && pengajuan.getStatus() != StatusPengajuan.PERLU_REVISI) {
            throw new IllegalStateException("Dokumen hanya bisa dilampirkan pada pengajuan berstatus DRAFT atau PERLU_REVISI");
        }

        List<MasterDokumenPegawai> dokumenList = dokumenRepository.findAllById(request.getDokumenIds());
        for (MasterDokumenPegawai doc : dokumenList) {
            if (!doc.getUser().getId().equals(user.getId())) {
                throw new SecurityException("Akses ditolak: Anda mencoba melampirkan dokumen milik orang lain.");
            }
        }
        pengajuan.setLampiran(new HashSet<>(dokumenList));
        return pengajuanRepository.save(pengajuan);
    }

    @Transactional
    public MessageResponse deletePengajuan(User user, Long pengajuanId) {
        Pengajuan pengajuan = pengajuanRepository.findById(pengajuanId)
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Pengajuan tidak ditemukan atau bukan milik Anda"));

        if (pengajuan.getStatus() != StatusPengajuan.DRAFT) {
            throw new IllegalStateException("Hanya pengajuan dengan status DRAFT yang bisa dihapus");
        }

        pengajuanRepository.delete(pengajuan);
        return new MessageResponse("Draf pengajuan berhasil dihapus.");
    }

    public List<Pengajuan> getAllPengajuan() {
        return pengajuanRepository.findAll();
    }

    public List<Pengajuan> getMyPengajuan(Long userId) {
        return pengajuanRepository.findByUserId(userId);
    }

    public Pengajuan getDetailPengajuan(Long id) {
        return pengajuanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengajuan dengan ID " + id + " tidak ditemukan"));
    }

    public SummaryResponseDTO getSummary() {
        long total = pengajuanRepository.count();
        long draft = pengajuanRepository.countByStatus(StatusPengajuan.DRAFT);
        long submitted = pengajuanRepository.countByStatus(StatusPengajuan.SUBMITTED);
        long revisi = pengajuanRepository.countByStatus(StatusPengajuan.PERLU_REVISI);
        long approved = pengajuanRepository.countByStatus(StatusPengajuan.APPROVED);
        long rejected = pengajuanRepository.countByStatus(StatusPengajuan.REJECTED);

        return new SummaryResponseDTO(total, draft, submitted, revisi, approved, rejected);
    }

    private void validateRequiredDocuments(Pengajuan pengajuan) {
        if (pengajuan.getLampiran() == null || pengajuan.getLampiran().isEmpty()) {
            throw new IllegalStateException("Pengajuan harus memiliki setidaknya satu dokumen lampiran untuk di-submit.");
        }

        Set<JenisDokumen> attachedDocuments = pengajuan.getLampiran().stream()
                .map(MasterDokumenPegawai::getJenisDokumen)
                .collect(Collectors.toSet());

        switch (pengajuan.getJenisPengajuan()) {
            case REGULER:
                if (!attachedDocuments.containsAll(Set.of(JenisDokumen.SK_PANGKAT, JenisDokumen.SKP))) {
                    throw new IllegalStateException("Untuk pengajuan REGULER, wajib melampirkan SK Pangkat dan SKP.");
                }
                break;
            case FUNGSIONAL:
                if (!attachedDocuments.containsAll(Set.of(JenisDokumen.SK_PANGKAT, JenisDokumen.SK_JABATAN, JenisDokumen.SKP, JenisDokumen.PAK))) {
                    throw new IllegalStateException("Untuk pengajuan FUNGSIONAL, wajib melampirkan: SK Pangkat, SK Jabatan, SKP, dan PAK.");
                }
                break;
            case STRUKTURAL:
                if (!attachedDocuments.containsAll(Set.of(JenisDokumen.SK_PANGKAT, JenisDokumen.SK_JABATAN, JenisDokumen.SK_PELANTIKAN, JenisDokumen.SPMT, JenisDokumen.SKP))) {
                    throw new IllegalStateException("Untuk pengajuan STRUKTURAL, wajib melampirkan: SK Pangkat, SK Jabatan, SK Pelantikan, SPMT, dan SKP.");
                }
                break;
        }
    }
}