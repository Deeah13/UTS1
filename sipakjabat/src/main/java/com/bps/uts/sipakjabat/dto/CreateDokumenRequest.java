package com.bps.uts.sipakjabat.dto;

import com.bps.uts.sipakjabat.model.JenisDokumen;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateDokumenRequest {
    private Long userId; // ID Pengguna yang akan diberi dokumen
    private JenisDokumen jenisDokumen;
    private String nomorDokumen;
    private LocalDate tanggalTerbit;
    private String deskripsi;
}