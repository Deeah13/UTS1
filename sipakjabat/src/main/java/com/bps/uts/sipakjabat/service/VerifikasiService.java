package com.bps.uts.sipakjabat.service;

import com.bps.uts.sipakjabat.dto.PengajuanVerifikasiResponseDTO;
import com.bps.uts.sipakjabat.dto.RevisiRequestDTO;
import com.bps.uts.sipakjabat.dto.VerifikasiRequestDTO;
import com.bps.uts.sipakjabat.model.Pengajuan;
import com.bps.uts.sipakjabat.model.StatusPengajuan;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.PengajuanRepository;
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifikasiService {

    private final PengajuanRepository pengajuanRepository;
    private final UserRepository userRepository;

    public List<Pengajuan> getAllPengajuanMasuk() {
        return pengajuanRepository.findAllByStatus(StatusPengajuan.SUBMITTED);
    }

    @Transactional
    public PengajuanVerifikasiResponseDTO approvePengajuan(Long pengajuanId, VerifikasiRequestDTO request, User verifikator) {
        Pengajuan pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new RuntimeException("Pengajuan dengan ID " + pengajuanId + " tidak ditemukan"));

        if (pengajuan.getStatus() != StatusPengajuan.SUBMITTED) {
            throw new IllegalStateException("Hanya pengajuan dengan status SUBMITTED yang bisa disetujui.");
        }

        pengajuan.setStatus(StatusPengajuan.APPROVED);
        pengajuan.setCatatanVerifikator(request.getCatatan());
        pengajuan.setVerifikator(verifikator);
        pengajuan.setTanggalDiputuskan(LocalDateTime.now());

        // Update data user yang mengajukan
        User userToUpdate = pengajuan.getUser();
        userToUpdate.setPangkatGolongan(pengajuan.getPangkatTujuan());
        userToUpdate.setJabatan(pengajuan.getJabatanTujuan());
        userToUpdate.setTmtPangkatTerakhir(LocalDate.now());
        userRepository.save(userToUpdate);

        pengajuanRepository.save(pengajuan);

        return PengajuanVerifikasiResponseDTO.builder()
                .id(pengajuan.getId())
                .status(pengajuan.getStatus())
                .verifikator(PengajuanVerifikasiResponseDTO.UserSimpleDTO.builder()
                        .id(verifikator.getId())
                        .namaLengkap(verifikator.getNamaLengkap())
                        .build())
                .catatanVerifikator(pengajuan.getCatatanVerifikator())
                .tanggalDiputuskan(pengajuan.getTanggalDiputuskan())
                .build();
    }

    @Transactional
    public PengajuanVerifikasiResponseDTO rejectPengajuan(Long pengajuanId, VerifikasiRequestDTO request, User verifikator) {
        Pengajuan pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new RuntimeException("Pengajuan dengan ID " + pengajuanId + " tidak ditemukan"));

        if (pengajuan.getStatus() != StatusPengajuan.SUBMITTED) {
            throw new IllegalStateException("Hanya pengajuan dengan status SUBMITTED yang bisa ditolak.");
        }

        pengajuan.setStatus(StatusPengajuan.REJECTED);
        pengajuan.setAlasanPenolakan(request.getAlasan());
        pengajuan.setCatatanVerifikator(request.getCatatan());
        pengajuan.setVerifikator(verifikator);
        pengajuan.setTanggalDiputuskan(LocalDateTime.now());

        pengajuanRepository.save(pengajuan);

        return PengajuanVerifikasiResponseDTO.builder()
                .id(pengajuan.getId())
                .status(pengajuan.getStatus())
                .verifikator(PengajuanVerifikasiResponseDTO.UserSimpleDTO.builder()
                        .id(verifikator.getId())
                        .namaLengkap(verifikator.getNamaLengkap())
                        .build())
                .alasanPenolakan(pengajuan.getAlasanPenolakan())
                .catatanVerifikator(pengajuan.getCatatanVerifikator())
                .tanggalDiputuskan(pengajuan.getTanggalDiputuskan())
                .build();
    }

    @Transactional
    public Pengajuan kembalikanUntukRevisi(Long pengajuanId, RevisiRequestDTO request, User verifikator) {
        Pengajuan pengajuan = pengajuanRepository.findById(pengajuanId)
                .orElseThrow(() -> new RuntimeException("Pengajuan dengan ID " + pengajuanId + " tidak ditemukan"));

        if (pengajuan.getStatus() != StatusPengajuan.SUBMITTED) {
            throw new IllegalStateException("Hanya pengajuan dengan status SUBMITTED yang bisa dikembalikan untuk revisi.");
        }

        pengajuan.setStatus(StatusPengajuan.PERLU_REVISI);
        pengajuan.setCatatanVerifikator(request.getCatatan());
        pengajuan.setVerifikator(verifikator);

        return pengajuanRepository.save(pengajuan);
    }
}