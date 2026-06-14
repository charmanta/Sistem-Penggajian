-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3307
-- Generation Time: Jun 14, 2026 at 08:18 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_penggajian`
--

-- --------------------------------------------------------

--
-- Table structure for table `jabatan`
--

CREATE TABLE `jabatan` (
  `id_jabatan` varchar(10) NOT NULL,
  `nama_jabatan` varchar(50) NOT NULL,
  `gaji_pokok` int(11) NOT NULL,
  `tunjangan` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `jabatan`
--

INSERT INTO `jabatan` (`id_jabatan`, `nama_jabatan`, `gaji_pokok`, `tunjangan`) VALUES
('JBT-01', 'Manager', 6000000, 1500000),
('JBT-02', 'Staff Admin', 3500000, 500000),
('JBT-03', 'Operator', 3000000, 400000),
('JBT-04', 'Kasir', 3200000, 400000),
('JBT-05', 'Driver', 2800000, 300000),
('JBT-06', 'Staff IT', 3500000, 600000);

-- --------------------------------------------------------

--
-- Table structure for table `pegawai`
--

CREATE TABLE `pegawai` (
  `id_pegawai` varchar(20) NOT NULL,
  `id_jabatan` varchar(10) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  `nama_pegawai` varchar(100) NOT NULL,
  `email` varchar(50) DEFAULT NULL,
  `no_telp` varchar(15) DEFAULT NULL,
  `jenis_kelamin` enum('Laki-laki','Perempuan') DEFAULT NULL,
  `no_hp` varchar(15) DEFAULT NULL,
  `alamat` text DEFAULT NULL,
  `status` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `pegawai`
--

INSERT INTO `pegawai` (`id_pegawai`, `id_jabatan`, `id_user`, `nama_pegawai`, `email`, `no_telp`, `jenis_kelamin`, `no_hp`, `alamat`, `status`) VALUES
('234567', 'JBT-01', NULL, 'marinaaaa', 'mariana@gmail.com', NULL, 'Perempuan', '4567876567800', 'jln. bumi', 'Aktif'),
('2345678', 'JBT-02', NULL, 'marta', 'marta@gmail.com', NULL, 'Perempuan', '456789876', 'jln, sumbar', 'Aktif'),
('2345678987', 'JBT-01', NULL, 'marinajuana', 'marina@gmail.com', NULL, 'Perempuan', '2345', 'jln.pelabuhan', 'Aktif'),
('PEG-004', 'JBT-02', 15, 'Rina Melati', 'rina@mail.com', NULL, NULL, '08444444', 'Jl. D', '');

-- --------------------------------------------------------

--
-- Table structure for table `penggajian`
--

CREATE TABLE `penggajian` (
  `id_penggajian` int(11) NOT NULL,
  `id_presensi` int(11) DEFAULT NULL,
  `periode` varchar(20) NOT NULL,
  `total_kehadiran` int(11) NOT NULL DEFAULT 0,
  `tunjangan` decimal(12,2) NOT NULL DEFAULT 0.00,
  `potongan` decimal(12,2) NOT NULL DEFAULT 0.00,
  `total_gaji` decimal(12,2) NOT NULL DEFAULT 0.00,
  `status` enum('menunggu','diverifikasi') NOT NULL DEFAULT 'menunggu',
  `verified_by` int(11) DEFAULT NULL,
  `verified_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `presensi`
--

CREATE TABLE `presensi` (
  `id_presensi` int(11) NOT NULL,
  `id_pegawai` varchar(20) DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  `jam_masuk` time DEFAULT NULL,
  `jam_keluar` time DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `keterangan` varchar(255) DEFAULT NULL,
  `jam_pulang` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `presensi`
--

INSERT INTO `presensi` (`id_presensi`, `id_pegawai`, `tanggal`, `jam_masuk`, `jam_keluar`, `status`, `keterangan`, `jam_pulang`) VALUES
(4, 'PEG-002', '2026-06-14', '08:00:00', NULL, 'Hadir', 'Masuk kantor', NULL),
(5, 'PEG-003', '2026-06-14', '08:10:00', NULL, 'Hadir', 'Masuk kantor', NULL),
(6, 'PEG-004', '2026-06-14', NULL, NULL, 'Izin', 'Sakit', NULL),
(7, 'PEG-003', '2026-06-14', '08:00:00', NULL, 'Sakit', 'izin pulang sakit', NULL),
(8, 'PEG-004', '2026-06-14', '08:00:00', NULL, 'Sakit', 'sakit', '18:00:00'),
(9, 'PEG-003', '2026-06-14', '09:00:00', NULL, 'Hadir', 'hadirr', '14:00:00'),
(10, '2345678987', '2026-06-15', '08:00:00', NULL, 'Hadir', 'hadir selalu', '12:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `hak_akses` enum('admin','pegawai','pemilik','hrd') NOT NULL DEFAULT 'pegawai'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `username`, `password`, `hak_akses`) VALUES
(11, 'ahmad_santoso', 'pass123', 'pemilik'),
(12, 'budi_prasetyo', 'pass123', 'pegawai'),
(13, 'sari_dewi', 'pass123', 'hrd'),
(14, 'andi_wijaya', 'pass123', 'pegawai'),
(15, 'rina_kusuma', 'pass123', 'pegawai');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `jabatan`
--
ALTER TABLE `jabatan`
  ADD PRIMARY KEY (`id_jabatan`);

--
-- Indexes for table `pegawai`
--
ALTER TABLE `pegawai`
  ADD PRIMARY KEY (`id_pegawai`),
  ADD KEY `id_jabatan` (`id_jabatan`),
  ADD KEY `id_user` (`id_user`);

--
-- Indexes for table `penggajian`
--
ALTER TABLE `penggajian`
  ADD PRIMARY KEY (`id_penggajian`),
  ADD KEY `id_presensi` (`id_presensi`),
  ADD KEY `verified_by` (`verified_by`);

--
-- Indexes for table `presensi`
--
ALTER TABLE `presensi`
  ADD PRIMARY KEY (`id_presensi`),
  ADD KEY `id_pegawai` (`id_pegawai`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `penggajian`
--
ALTER TABLE `penggajian`
  MODIFY `id_penggajian` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `presensi`
--
ALTER TABLE `presensi`
  MODIFY `id_presensi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `pegawai`
--
ALTER TABLE `pegawai`
  ADD CONSTRAINT `pegawai_ibfk_1` FOREIGN KEY (`id_jabatan`) REFERENCES `jabatan` (`id_jabatan`) ON UPDATE CASCADE,
  ADD CONSTRAINT `pegawai_ibfk_2` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON UPDATE CASCADE;

--
-- Constraints for table `penggajian`
--
ALTER TABLE `penggajian`
  ADD CONSTRAINT `penggajian_ibfk_1` FOREIGN KEY (`id_presensi`) REFERENCES `presensi` (`id_presensi`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `penggajian_ibfk_2` FOREIGN KEY (`verified_by`) REFERENCES `user` (`id_user`) ON DELETE SET NULL ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
