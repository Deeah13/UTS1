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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String pangkatTujuan;
    private String jabatanTujuan;

    @Enumerated(EnumType.STRING)
    private StatusPengajuan status;

    private String catatanVerifikator;
    private LocalDateTime tanggalPengajuan;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "pengajuan_lampiran",
            joinColumns = @JoinColumn(name = "pengajuan_id"),
            inverseJoinColumns = @JoinColumn(name = "dokumen_id")
    )
    private Set<MasterDokumenPegawai> lampiran;
}