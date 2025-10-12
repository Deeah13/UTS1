package com.bps.uts.sipakjabat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pengajuan")
public class Pengajuan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "verifikator_id")
    private User verifikator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JenisPengajuan jenisPengajuan;

    private String pangkatSaatIni;
    private String jabatanSaatIni;
    private String pangkatTujuan;
    private String jabatanTujuan;

    @Enumerated(EnumType.STRING)
    private StatusPengajuan status;

    private String alasanPenolakan;
    private String catatanVerifikator;

    private LocalDateTime tanggalDibuat;
    private LocalDateTime tanggalDiajukan;
    private LocalDateTime tanggalDiputuskan;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "pengajuan_lampiran",
            joinColumns = @JoinColumn(name = "pengajuan_id"),
            inverseJoinColumns = @JoinColumn(name = "dokumen_id")
    )
    private Set<MasterDokumenPegawai> lampiran;
}