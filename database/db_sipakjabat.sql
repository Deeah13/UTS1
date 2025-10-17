-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Oct 16, 2025 at 01:05 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_sipakjabat`
--

-- --------------------------------------------------------

--
-- Table structure for table `master_dokumen_pegawai`
--

CREATE TABLE `master_dokumen_pegawai` (
  `id` bigint(20) NOT NULL,
  `deskripsi` varchar(255) DEFAULT NULL,
  `jenis_dokumen` enum('PAK','SKP','SK_JABATAN','SK_PANGKAT','SK_PELANTIKAN','SPMT') DEFAULT NULL,
  `nomor_dokumen` varchar(255) DEFAULT NULL,
  `tanggal_terbit` date DEFAULT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `master_dokumen_pegawai`
--

INSERT INTO `master_dokumen_pegawai` (`id`, `deskripsi`, `jenis_dokumen`, `nomor_dokumen`, `tanggal_terbit`, `user_id`) VALUES
(1, 'SK Kenaikan Pangkat Golongan IIIa', 'SK_PANGKAT', '123/BPS/SK/IIIa/2022', '2022-04-01', 1),
(2, 'SKP 2 Tahun Terakhir', 'SKP', 'SKP/BPS/2024', '2024-12-31', 1),
(5, 'SK Pangkat Deeah', 'SK_PANGKAT', 'SK/DEEAH/01', '2021-10-01', 5),
(6, 'SKP Deeah', 'SKP', 'SKP/DEEAH/2024', '2024-12-31', 5),
(7, 'SKP Viera', 'SKP', 'SKP/VIERA/2023', '2023-10-13', 4),
(8, 'SK Pangkat Viera', 'SK_PANGKAT', 'SK/VIERA/2023', '2023-12-31', 4),
(9, 'SK Kenaikan Jabatan ke Statistisi Ahli Madya', 'SK_JABATAN', 'SK/JAB/DEEAH/001/2025', '2025-10-15', 5),
(10, 'SKP Andi Versi Jelas', 'SKP', 'SKP/BPS/2024', '2024-12-31', 1);

-- --------------------------------------------------------

--
-- Table structure for table `pengajuan`
--

CREATE TABLE `pengajuan` (
  `id` bigint(20) NOT NULL,
  `alasan_penolakan` varchar(255) DEFAULT NULL,
  `catatan_verifikator` varchar(255) DEFAULT NULL,
  `jabatan_saat_ini` varchar(255) DEFAULT NULL,
  `jabatan_tujuan` varchar(255) DEFAULT NULL,
  `jenis_pengajuan` enum('FUNGSIONAL','REGULER','STRUKTURAL') NOT NULL,
  `pangkat_saat_ini` varchar(255) DEFAULT NULL,
  `pangkat_tujuan` varchar(255) DEFAULT NULL,
  `status` enum('APPROVED','DRAFT','PERLU_REVISI','REJECTED','SUBMITTED') DEFAULT NULL,
  `tanggal_diajukan` datetime(6) DEFAULT NULL,
  `tanggal_dibuat` datetime(6) DEFAULT NULL,
  `tanggal_diputuskan` datetime(6) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `verifikator_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pengajuan`
--

INSERT INTO `pengajuan` (`id`, `alasan_penolakan`, `catatan_verifikator`, `jabatan_saat_ini`, `jabatan_tujuan`, `jenis_pengajuan`, `pangkat_saat_ini`, `pangkat_tujuan`, `status`, `tanggal_diajukan`, `tanggal_dibuat`, `tanggal_diputuskan`, `user_id`, `verifikator_id`) VALUES
(1, NULL, 'Dokumen sudah jelas, syarat semua terpenuhi. Disetujui.', 'Statistisi Ahli Muda', 'Statistisi Ahli Madya', 'REGULER', 'Penata (III/c)', 'Penata Tk. I (III/d)', 'APPROVED', '2025-10-15 20:35:07.000000', '2025-10-15 20:28:51.000000', '2025-10-16 16:34:59.000000', 5, 2),
(3, 'Masa Kerja Belum Memenuhi Syarat', 'Pengajuan ditolak karena masa kerja pada pangkat terakhir belum mencapai syarat minimal 2 tahun.', 'Statistisi Ahli Pertama', 'Statistisi Ahli Muda', 'REGULER', 'Penata Muda (III/a)', 'Penata Tk.I (III/c)', 'REJECTED', '2025-10-16 14:44:46.000000', '2025-10-16 14:42:10.000000', '2025-10-16 17:49:45.000000', 1, 2),
(4, 'Masa Kerja Belum Memenuhi Syarat', 'Pengajuan ditolak karena masa kerja pada pangkat terakhir belum mencapai syarat minimal 2 tahun.', 'Statistisi Ahli Pertama', 'Statistisi Ahli Muda', 'REGULER', 'Penata Muda (III/a)', 'Penata Muda (III/b)', 'REJECTED', '2025-10-16 16:31:31.000000', '2025-10-16 16:25:35.000000', '2025-10-16 16:40:02.000000', 4, 2),
(5, NULL, 'Dokumen SKP yang dilampirkan buram. Mohon unggah ulang dengan kualitas yang lebih baik.', 'Statistisi Ahli Pertama', 'Statistisi Ahli Muda', 'REGULER', 'Penata Muda (III/a)', 'Penata Muda (III/b)', 'PERLU_REVISI', '2025-10-16 16:43:54.000000', '2025-10-16 16:42:04.000000', NULL, 1, 2),
(6, NULL, NULL, 'Statistisi Ahli Pertama', 'Statistisi Ahli Muda', 'REGULER', 'Penata Muda (III/a)', 'Penata Muda (III/b)', 'SUBMITTED', '2025-10-16 17:57:26.000000', '2025-10-16 17:56:13.000000', NULL, 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `pengajuan_lampiran`
--

CREATE TABLE `pengajuan_lampiran` (
  `pengajuan_id` bigint(20) NOT NULL,
  `dokumen_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pengajuan_lampiran`
--

INSERT INTO `pengajuan_lampiran` (`pengajuan_id`, `dokumen_id`) VALUES
(1, 5),
(1, 6),
(3, 1),
(3, 2),
(4, 7),
(4, 8),
(5, 1),
(5, 2),
(6, 1),
(6, 10);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `email` varchar(255) NOT NULL,
  `jabatan` varchar(255) DEFAULT NULL,
  `nama_lengkap` varchar(255) NOT NULL,
  `nip` varchar(255) NOT NULL,
  `pangkat_golongan` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('PEGAWAI','VERIFIKATOR') DEFAULT NULL,
  `tmt_pangkat_terakhir` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `jabatan`, `nama_lengkap`, `nip`, `pangkat_golongan`, `password`, `role`, `tmt_pangkat_terakhir`) VALUES
(1, 'andi.budiman@bps.go.id', 'Statistisi Ahli Pertama', 'Andi Budiman', '199501012020121001', 'Penata Muda (III/a)', '$2a$10$Fo1qK.pUFpueeFu54L8H.e9XPDnJq.DgmKqk1BoKS.6jbTAsvYMbe', 'PEGAWAI', '2022-04-01'),
(2, 'rina.wulandari@bps.go.id', 'Analis Kepegawaian', 'Rina Wulandari', '199005102015032001', 'Eselon II (IV/d)', '$2a$10$4D3AihMWUFY2gEijYMrC.u.YgmlscxLShxjwbtqwlPy2Ievpa2/sK', 'VERIFIKATOR', '2020-01-01'),
(4, 'viera.ananda@bps.go.id', 'Statistisi Ahli Pertama', 'Viera Ananda', '199701012022032001', 'Penata Muda (III/a)', '$2a$10$ZHmP0g.pGTygShx1SUs1duRRvKUjsHphlV9R.ZqDno0Ae/MkZwOPW', 'PEGAWAI', '2024-04-01'),
(5, 'deeah.ayu.new@bps.go.id', 'Statistisi Ahli Madya', 'Deeah Ayu Raditio Updated', '199508172020122003', 'Penata Tk. I (III/d)', '$2a$10$vqeFjZu4CBxpL43Tdu4zYeAtQqk/czHcIcPdNBtLRs6GMdipAPy3O', 'PEGAWAI', '2025-10-01'),
(6, 'yubin.kim@bps.go.id', 'Statistisi Pelaksana', 'Yubin Kim', '199911252023011005', 'Pengatur (II/c)', '$2a$10$UicdqkTVeOTHyjgkqref6e1wP0cc3bmQzqO9apmyH2NHp2nKLjSDS', 'VERIFIKATOR', '2023-01-01');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `master_dokumen_pegawai`
--
ALTER TABLE `master_dokumen_pegawai`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKovmq1tswr6e5mfjc0o5kscgnf` (`user_id`);

--
-- Indexes for table `pengajuan`
--
ALTER TABLE `pengajuan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKvioqp27sga4dys1xaxubrkx` (`user_id`),
  ADD KEY `FKso20619tjaqao0nni2r81ol5v` (`verifikator_id`);

--
-- Indexes for table `pengajuan_lampiran`
--
ALTER TABLE `pengajuan_lampiran`
  ADD PRIMARY KEY (`pengajuan_id`,`dokumen_id`),
  ADD KEY `FK1lgcdxnb1nhtps88qdwhmrffp` (`dokumen_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  ADD UNIQUE KEY `UKe72fwutcg2xou2qg41w9bn5ed` (`nip`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `master_dokumen_pegawai`
--
ALTER TABLE `master_dokumen_pegawai`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `pengajuan`
--
ALTER TABLE `pengajuan`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `master_dokumen_pegawai`
--
ALTER TABLE `master_dokumen_pegawai`
  ADD CONSTRAINT `FKovmq1tswr6e5mfjc0o5kscgnf` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `pengajuan`
--
ALTER TABLE `pengajuan`
  ADD CONSTRAINT `FKso20619tjaqao0nni2r81ol5v` FOREIGN KEY (`verifikator_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKvioqp27sga4dys1xaxubrkx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `pengajuan_lampiran`
--
ALTER TABLE `pengajuan_lampiran`
  ADD CONSTRAINT `FK1lgcdxnb1nhtps88qdwhmrffp` FOREIGN KEY (`dokumen_id`) REFERENCES `master_dokumen_pegawai` (`id`),
  ADD CONSTRAINT `FKj06g0asfwkvucdg0hx4i3lo5q` FOREIGN KEY (`pengajuan_id`) REFERENCES `pengajuan` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
