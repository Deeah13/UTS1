package com.bps.uts.sipakjabat.repository;

import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MasterDokumenPegawaiRepository extends JpaRepository<MasterDokumenPegawai, Long> {
    List<MasterDokumenPegawai> findByUserId(Long userId);
}