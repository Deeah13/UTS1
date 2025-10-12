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
        // Hanya jalankan seeder jika database kosong
        if (userRepository.count() == 0) {
            // User 1 - Pegawai
            User andi = User.builder()
                    .namaLengkap("Andi Budiman")
                    .nip("199501012020121001")
                    .email("andi.budiman@bps.go.id")
                    .password(passwordEncoder.encode("password123"))
                    .pangkatGolongan("Penata Muda (III/a)")
                    .jabatan("Statistisi Ahli Pertama")
                    // --- PERBAIKAN FINAL ADA DI SINI ---
                    .tmtPangkatTerakhir(LocalDate.of(2022, 4, 1)) // Gunakan method builder yang benar
                    .role(Role.PEGAWAI)
                    .build();
            userRepository.save(andi);

            // Buat dokumen SK Pangkat untuk Andi
            dokumenRepository.save(MasterDokumenPegawai.builder()
                    .user(andi)
                    .jenisDokumen(JenisDokumen.SK_PANGKAT)
                    .nomorDokumen("123/BPS/SK/IIIa/2022")
                    .tanggalTerbit(LocalDate.of(2022, 4, 1))
                    .deskripsi("SK Kenaikan Pangkat Golongan IIIa")
                    .build());

            // Buat dokumen SKP untuk Andi
            dokumenRepository.save(MasterDokumenPegawai.builder()
                    .user(andi)
                    .jenisDokumen(JenisDokumen.SKP)
                    .nomorDokumen("SKP/BPS/2024")
                    .tanggalTerbit(LocalDate.of(2024, 12, 31))
                    .deskripsi("SKP 2 Tahun Terakhir")
                    .build());

            // User 2 - Verifikator
            User rina = User.builder()
                    .namaLengkap("Rina Wulandari")
                    .nip("199005102015032001")
                    .email("rina.wulandari@bps.go.id")
                    .password(passwordEncoder.encode("verifikator123"))
                    .pangkatGolongan("Penata (III/c)")
                    .jabatan("Analis Kepegawaian")
                    .role(Role.VERIFIKATOR)
                    .build();
            userRepository.save(rina);
        }
    }
}