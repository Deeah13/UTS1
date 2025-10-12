package com.bps.uts.sipakjabat.dto;

import com.bps.uts.sipakjabat.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String namaLengkap;
    private String nip;
    private String email;
    private String password;
    private String pangkatGolongan;
    private String jabatan;
    private Role role;
}