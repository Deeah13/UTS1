package com.bps.uts.sipakjabat.dto;

import com.bps.uts.sipakjabat.model.StatusPengajuan;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PengajuanVerifikasiResponseDTO {
    private Long id;
    private StatusPengajuan status;
    private UserSimpleDTO verifikator;
    private String alasanPenolakan;
    private String catatanVerifikator;
    private LocalDateTime tanggalDiputuskan;

    @Data
    @Builder
    public static class UserSimpleDTO {
        private Long id;
        private String namaLengkap;
    }
}