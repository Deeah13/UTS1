package com.bps.uts.sipakjabat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserByAdminRequest {
    private String namaLengkap;
    private String email;
    private String pangkatGolongan;
    private String jabatan;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tmtPangkatTerakhir;
}