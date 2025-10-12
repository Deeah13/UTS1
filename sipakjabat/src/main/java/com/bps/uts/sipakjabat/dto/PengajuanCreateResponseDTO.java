package com.bps.uts.sipakjabat.dto;

import com.bps.uts.sipakjabat.model.StatusPengajuan;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PengajuanCreateResponseDTO {
    private Long id;
    private String pangkatSaatIni;
    private String pangkatTujuan;
    private String jabatanSaatIni;
    private String jabatanTujuan;
    private StatusPengajuan status;
    private Integer masaKerjaGolonganTahun;
    private Integer masaKerjaGolonganBulan;
    private boolean memenuhiSyaratMasaKerja;
    private boolean dokumenLengkap;
    private LocalDateTime tanggalDibuat;
}