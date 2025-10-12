package com.bps.uts.sipakjabat.dto;

import com.bps.uts.sipakjabat.model.Role;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AdminCreateUserRequest {
    private String namaLengkap;
    private String nip;
    private String email;
    private String password;
    private String pangkatGolongan;
    private String jabatan;
    private LocalDate tmtPangkatTerakhir;
    private Role role;
}