package com.bps.uts.sipakjabat.dto;

import com.bps.uts.sipakjabat.model.JenisPengajuan;
import lombok.Data;

@Data
public class PengajuanCreateRequestDTO {
    private JenisPengajuan jenisPengajuan;
    private String pangkatTujuan;
    private String jabatanTujuan;
}