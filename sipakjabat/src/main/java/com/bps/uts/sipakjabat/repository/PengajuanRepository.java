package com.bps.uts.sipakjabat.repository;

import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.model.StatusPengajuan;
import com.bps.uts.sipakjabat.model.Pengajuan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PengajuanRepository extends JpaRepository<Pengajuan, Long> {
    List<Pengajuan> findByUserId(Long userId);
    List<Pengajuan> findAllByStatus(StatusPengajuan status);
    long countByStatus(StatusPengajuan status);
    long countByLampiranContains(MasterDokumenPegawai dokumen);
}