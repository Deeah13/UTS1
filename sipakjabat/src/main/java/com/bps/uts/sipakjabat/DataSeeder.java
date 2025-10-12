package com.bps.uts.sipakjabat;

import com.bps.uts.sipakjabat.model.JenisDokumen;
import com.bps.uts.sipakjabat.model.MasterDokumenPegawai;
import com.bps.uts.sipakjabat.model.Role;
import com.bps.uts.sipakjabat.model.User;
import com.bps.uts.sipakjabat.repository.MasterDokumenPegawaiRepository;
import com.bps.uts.sipakjabat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MasterDokumenPegawaiRepository dokumenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Hanya jalankan seeder jika tidak ada user sama sekali
        if (userRepository.count() == 0) {
            // User 1
            User andi = User.builder()
                    .namaLengkap("Andi Budiman")
                    .nip("199501012020121001")
                    .email("andi.budiman@bps.go.id")
                    .password(passwordEncoder.encode("password123"))
                    .pangkatGolongan("Penata Muda / IIIa")
                    .jabatan("Statistisi Ahli Pertama")
                    .role(Role.PEGAWAI)
                    .build();
            userRepository.save(andi);

            dokumenRepository.save(MasterDokumenPegawai.builder()
                    .user(andi)
                    .jenisDokumen(JenisDokumen.SK_PANGKAT)
                    .nomorDokumen("123/BPS/SK/IIIa/2022")
                    .tanggalTerbit(LocalDate.of(2022, 4, 1))
                    .deskripsi("SK Kenaikan Pangkat Golongan IIIa")
                    .build());

            dokumenRepository.save(MasterDokumenPegawai.builder()
                    .user(andi)
                    .jenisDokumen(JenisDokumen.IJAZAH)
                    .nomorDokumen("UNIV/STAT/001/2018")
                    .tanggalTerbit(LocalDate.of(2018, 9, 20))
                    .deskripsi("Ijazah S1 Statistik Universitas ABC")
                    .build());

            // User 2
            User citra = User.builder()
                    .namaLengkap("Citra Lestari")
                    .nip("199603152021122002")
                    .email("citra.lestari@bps.go.id")
                    .password(passwordEncoder.encode("password123"))
                    .pangkatGolongan("Pengatur / IIc")
                    .jabatan("Statistisi Pelaksana")
                    .role(Role.PEGAWAI)
                    .build();
            userRepository.save(citra);

            // User 3 - Verifikator
            User verifikator = User.builder()
                    .namaLengkap("Rina Wulandari")
                    .nip("199005102015032001")
                    .email("rina.wulandari@bps.go.id")
                    .password(passwordEncoder.encode("verifikator123"))
                    .pangkatGolongan("Penata / IIIc")
                    .jabatan("Analis Kepegawaian")
                    .role(Role.VERIFIKATOR)
                    .build();
            userRepository.save(verifikator);
        }
    }
}