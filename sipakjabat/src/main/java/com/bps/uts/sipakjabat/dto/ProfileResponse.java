package com.bps.uts.sipakjabat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
    private Long id;
    private String namaLengkap;
    private String nip;
    private String email;
    private String pangkatGolongan;
    private String jabatan;
    private String role;
}