package com.bps.uts.sipakjabat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponseDTO {
    private long totalPengajuan;
    private long jumlahDraft;
    private long jumlahSubmitted;
    private long jumlahPerluRevisi;
    private long jumlahApproved;
    private long jumlahRejected;
}