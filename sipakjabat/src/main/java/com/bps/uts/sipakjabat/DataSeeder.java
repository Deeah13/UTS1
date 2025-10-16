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
            System.out.println("========================================");
            System.out.println("SEEDING DATABASE...");
            System.out.println("========================================");

            // User 1 - Pegawai
            User andi = User.builder()
                    .namaLengkap("Andi Budiman")
                    .nip("199501012020121001")
                    .email("andi.budiman@bps.go.id")
                    .password(passwordEncoder.encode("password123"))
                    .pangkatGolongan("Penata Muda (III/a)")
                    .jabatan("Statistisi Ahli Pertama")
                    .tmtPangkatTerakhir(LocalDate.of(2022, 4, 1))
                    .role(Role.PEGAWAI)
                    .build();
            userRepository.save(andi);
            System.out.println("✓ User PEGAWAI created: " + andi.getNip());

            // Buat dokumen SK Pangkat untuk Andi
            dokumenRepository.save(MasterDokumenPegawai.builder()
                    .user(andi)
                    .jenisDokumen(JenisDokumen.SK_PANGKAT)
                    .nomorDokumen("123/BPS/SK/IIIa/2022")
                    .tanggalTerbit(LocalDate.of(2022, 4, 1))
                    .deskripsi("SK Kenaikan Pangkat Golongan IIIa")
                    .build());
            System.out.println("  ✓ Dokumen SK_PANGKAT added for " + andi.getNamaLengkap());

            // Buat dokumen SKP untuk Andi
            dokumenRepository.save(MasterDokumenPegawai.builder()
                    .user(andi)
                    .jenisDokumen(JenisDokumen.SKP)
                    .nomorDokumen("SKP/BPS/2024")
                    .tanggalTerbit(LocalDate.of(2024, 12, 31))
                    .deskripsi("SKP 2 Tahun Terakhir")
                    .build());
            System.out.println("  ✓ Dokumen SKP added for " + andi.getNamaLengkap());

            // User 2 - Verifikator
            User rina = User.builder()
                    .namaLengkap("Rina Wulandari")
                    .nip("199005102015032001")
                    .email("rina.wulandari@bps.go.id")
                    .password(passwordEncoder.encode("verifikator123"))
                    .pangkatGolongan("Eselon II (IV/d)")
                    .jabatan("Analis Kepegawaian")
                    .tmtPangkatTerakhir(LocalDate.of(2020, 1, 1))
                    .role(Role.VERIFIKATOR)
                    .build();
            userRepository.save(rina);
            System.out.println("✓ User VERIFIKATOR created: " + rina.getNip());

            // User 3 - Pegawai tambahan untuk testing
            User budi = User.builder()
                    .namaLengkap("Budi Santoso")
                    .nip("199601012021121002")
                    .email("budi.santoso@bps.go.id")
                    .password(passwordEncoder.encode("password123"))
                    .pangkatGolongan("Penata Muda Tingkat I (III/b)")
                    .jabatan("Statistisi Ahli Pertama")
                    .tmtPangkatTerakhir(LocalDate.of(2021, 6, 1))
                    .role(Role.PEGAWAI)
                    .build();
            userRepository.save(budi);
            System.out.println("✓ User PEGAWAI created: " + budi.getNip());

            // Dokumen untuk Budi
            dokumenRepository.save(MasterDokumenPegawai.builder()
                    .user(budi)
                    .jenisDokumen(JenisDokumen.SK_PANGKAT)
                    .nomorDokumen("456/BPS/SK/IIIb/2021")
                    .tanggalTerbit(LocalDate.of(2021, 6, 1))
                    .deskripsi("SK Kenaikan Pangkat Golongan IIIb")
                    .build());
            System.out.println("  ✓ Dokumen SK_PANGKAT added for " + budi.getNamaLengkap());

            dokumenRepository.save(MasterDokumenPegawai.builder()
                    .user(budi)
                    .jenisDokumen(JenisDokumen.SKP)
                    .nomorDokumen("SKP/BPS/2023")
                    .tanggalTerbit(LocalDate.of(2023, 12, 31))
                    .deskripsi("SKP 2 Tahun Terakhir")
                    .build());
            System.out.println("  ✓ Dokumen SKP added for " + budi.getNamaLengkap());

            System.out.println("========================================");
            System.out.println("DATABASE SEEDING COMPLETED!");
            System.out.println("========================================");
            System.out.println("\nDEFAULT CREDENTIALS:");
            System.out.println("--------------------");
            System.out.println("PEGAWAI 1:");
            System.out.println("  NIP: " + andi.getNip());
            System.out.println("  Password: password123");
            System.out.println("\nPEGAWAI 2:");
            System.out.println("  NIP: " + budi.getNip());
            System.out.println("  Password: password123");
            System.out.println("\nVERIFIKATOR:");
            System.out.println("  NIP: " + rina.getNip());
            System.out.println("  Password: verifikator123");
            System.out.println("========================================\n");
        }
    }
}