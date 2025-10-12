package com.bps.uts.sipakjabat.dto;

import com.bps.uts.sipakjabat.model.StatusPengajuan;
import lombok.Data;

@Data
public class VerifikasiRequest {
    private StatusPengajuan status; // Harus DISETUJUI atau DITOLAK
    private String catatan;
}