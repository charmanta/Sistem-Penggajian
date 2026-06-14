/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.sistempenggajian;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.File;
import java.io.FileOutputStream;

/**
 *
 * @author ASUS
 */
public class SlipGaji extends javax.swing.JFrame {

    private int idPegawai;
    private String nama;
    private String jabatan;
    private String inisial;

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SlipGaji.class.getName());

    /**
     * Creates new form SlipGaji
     */
    public SlipGaji(
            int idPegawai,
            String nama,
            String jabatan,
            String inisial
    ) {
        initComponents();

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        this.idPegawai = idPegawai;
        this.nama = nama;
        this.jabatan = jabatan;
        this.inisial = inisial;

        lblHeaderNama.setText(nama);
        lblHeaderJabatan.setText(jabatan);
        lblHeaderInisial.setText(inisial);

        loadDataSlipGaji(idPegawai);

    }

    private void loadDataSlipGaji(int idPegawai) {
        String periodeBulanIni = java.time.LocalDate.now().getYear() + "-"
                + String.format("%02d", java.time.LocalDate.now().getMonthValue());

        String sql = "SELECT pg.tunjangan, pg.potongan, pg.total_gaji, pg.periode, "
                + "j.gaji_pokok, p.nik "
                + "FROM penggajian pg "
                + "JOIN pegawai p ON pg.id_penggajian = p.id_pegawai "
                + "JOIN jabatan j ON p.id_jabatan = j.id_jabatan "
                + "WHERE p.id_pegawai = ? AND pg.periode = ? "
                + "LIMIT 1";

        try (java.sql.Connection conn = DBConnection.getKoneksi(); java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idPegawai);
            ps.setString(2, periodeBulanIni);
            java.sql.ResultSet rs = ps.executeQuery();
            java.text.NumberFormat fmt = new java.text.DecimalFormat("#,###");

            if (rs.next()) {
                double gajiPokok = rs.getDouble("gaji_pokok");
                double tunjangan = rs.getDouble("tunjangan");
                double potongan = rs.getDouble("potongan");
                double totalGaji = rs.getDouble("total_gaji");
                String nik = rs.getString("nik");
                String periode = rs.getString("periode");

                String[] namaBulan = {"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                    "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
                String[] pecah = periode.split("-");
                int bln = Integer.parseInt(pecah[1]);

                jLabel7.setText(nama);
                jLabel9.setText(jabatan);
                jLabel11.setText(nik);
                jLabel5.setText("Periode: " + namaBulan[bln - 1] + " " + pecah[0]);

                lblGajiPokok.setText("Rp " + fmt.format(gajiPokok));
                lblTransport.setText("-");
                lblMakan.setText("-");
                lblJabatan.setText("Rp " + fmt.format(tunjangan));
                lblTotalPendapatan.setText("Rp " + fmt.format(gajiPokok + tunjangan));

                lblBpjsKesehatan.setText("-");
                lblBpjsKetenagakerjaan.setText("-");
                lblPotonganLainnya.setText("- Rp " + fmt.format(potongan));
                lblTotalPotongan.setText("- Rp " + fmt.format(potongan));

                lblGajiBersih.setText("Rp " + fmt.format(totalGaji));
                lblTerbilang.setText("\"" + terbilang((long) totalGaji) + " rupiah\"");

                java.time.LocalDate today = java.time.LocalDate.now();
                jLabel13.setText(today.getDayOfMonth() + " "
                        + namaBulan[today.getMonthValue() - 1] + " " + today.getYear());

            } else {
                jLabel5.setText("Periode: " + periodeBulanIni);
                jLabel7.setText(nama);
                jLabel9.setText(jabatan);
                lblGajiPokok.setText("Belum diproses");
                lblTransport.setText("Rp 0");
                lblMakan.setText("Rp 0");
                lblBpjsKesehatan.setText("Rp 0");
                lblBpjsKetenagakerjaan.setText("Rp 0");
                lblBpjsKetenagakerjaan.setText("-");
                lblPotonganLainnya.setText("-");
                lblTotalPotongan.setText("-");
                lblGajiBersih.setText("Belum diproses");
                lblTerbilang.setText("-");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String terbilang(long angka) {
        String[] satuan = {"", "satu", "dua", "tiga", "empat", "lima",
            "enam", "tujuh", "delapan", "sembilan", "sepuluh",
            "sebelas"};
        if (angka < 12) {
            return satuan[(int) angka];
        }
        if (angka < 20) {
            return satuan[(int) (angka - 10)] + " belas";
        }
        if (angka < 100) {
            return satuan[(int) (angka / 10)] + " puluh"
                    + (angka % 10 != 0 ? " " + terbilang(angka % 10) : "");
        }
        if (angka < 200) {
            return "seratus" + (angka % 100 != 0 ? " " + terbilang(angka % 100) : "");
        }
        if (angka < 1000) {
            return satuan[(int) (angka / 100)] + " ratus"
                    + (angka % 100 != 0 ? " " + terbilang(angka % 100) : "");
        }
        if (angka < 2000) {
            return "seribu" + (angka % 1000 != 0 ? " " + terbilang(angka % 1000) : "");
        }
        if (angka < 1000000) {
            return terbilang(angka / 1000) + " ribu"
                    + (angka % 1000 != 0 ? " " + terbilang(angka % 1000) : "");
        }
        if (angka < 1000000000) {
            return terbilang(angka / 1000000) + " juta"
                    + (angka % 1000000 != 0 ? " " + terbilang(angka % 1000000) : "");
        }
        return angka + "";
    }

    public SlipGaji(int idPegawai) {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        lblTotalPendapatan = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblGajiPokok = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        lblTotalPotongan = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        lblGajiBersih = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lblTerbilang = new javax.swing.JLabel();
        lblTransport = new javax.swing.JLabel();
        lblMakan = new javax.swing.JLabel();
        lblJabatan = new javax.swing.JLabel();
        lblBpjsKesehatan = new javax.swing.JLabel();
        lblBpjsKetenagakerjaan = new javax.swing.JLabel();
        lblPotonganLainnya = new javax.swing.JLabel();
        btnUnduhPdf = new javax.swing.JButton();
        panelSidebar4 = new javax.swing.JPanel();
        lblMenuPegawai4 = new javax.swing.JLabel();
        lblBeranda4 = new javax.swing.JLabel();
        lblPresensi4 = new javax.swing.JLabel();
        lblSlipGaji4 = new javax.swing.JLabel();
        panelHeader = new javax.swing.JPanel();
        lblJudul = new javax.swing.JLabel();
        lblHeaderInisial = new javax.swing.JLabel();
        lblHeaderNama = new javax.swing.JLabel();
        lblHeaderJabatan = new javax.swing.JLabel();
        btnKeluar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(709, 900));

        jPanel3.setBackground(new java.awt.Color(0, 51, 51));
        jPanel3.setForeground(new java.awt.Color(0, 51, 0));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("SLIP GAJI KARYAWAN ");

        jLabel5.setText("Periode: Juni 2026 ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("Nama");

        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Andi Pratama ");

        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("Jabatan ");

        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Staff Admin ");

        jPanel5.setBackground(new java.awt.Color(153, 153, 153));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("Total Pendapatan");

        jLabel21.setForeground(new java.awt.Color(51, 51, 51));
        jLabel21.setText("Potongan");

        jSeparator3.setForeground(new java.awt.Color(102, 102, 102));

        lblTotalPendapatan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalPendapatan.setForeground(new java.awt.Color(0, 0, 0));
        lblTotalPendapatan.setText("Rp 4.500.000");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalPendapatan))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(30, 30, 30))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(lblTotalPendapatan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(153, 153, 153));
        jPanel4.setForeground(new java.awt.Color(153, 153, 153));

        jLabel14.setForeground(new java.awt.Color(51, 51, 51));
        jLabel14.setText("Pendapatan");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel14)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("NIK ");

        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("ALS-010 ");

        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("Tanggal Terbit ");

        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setText("30 Juni 2026 ");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setText("Gaji Pokok");

        jSeparator1.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lblGajiPokok.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblGajiPokok.setForeground(new java.awt.Color(0, 0, 0));
        lblGajiPokok.setText("Rp 3.500.000");

        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setText("Tunjangan Transport");

        jLabel18.setForeground(new java.awt.Color(51, 51, 51));
        jLabel18.setText("Tunjangan Makan");

        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("Tunjangan Jabatan");

        jLabel22.setForeground(new java.awt.Color(51, 51, 51));
        jLabel22.setText("BPJS Kesehatan");

        jLabel23.setForeground(new java.awt.Color(51, 51, 51));
        jLabel23.setText("BPJS Ketenagakerjaan");

        jLabel24.setForeground(new java.awt.Color(51, 51, 51));
        jLabel24.setText("Potongan Lainnya");

        jPanel6.setBackground(new java.awt.Color(153, 153, 153));
        jPanel6.setForeground(new java.awt.Color(153, 153, 153));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("Total Potongan");

        lblTotalPotongan.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalPotongan.setForeground(new java.awt.Color(102, 0, 0));
        lblTotalPotongan.setText("- Rp 150.000");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblTotalPotongan)
                .addGap(28, 28, 28))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(lblTotalPotongan))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(153, 255, 153));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(51, 102, 0));
        jLabel27.setText("GAJI BERSIH DITERIMA");

        lblGajiBersih.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGajiBersih.setForeground(new java.awt.Color(51, 102, 0));
        lblGajiBersih.setText("Rp 4.350.000");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblGajiBersih)
                .addGap(29, 29, 29))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(lblGajiBersih))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(51, 51, 51));
        jLabel25.setText("Terbilang: ");

        lblTerbilang.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        lblTerbilang.setForeground(new java.awt.Color(51, 51, 51));
        lblTerbilang.setText("\"Empat juta tiga ratus lima puluh ribu rupiah\" ");

        lblTransport.setForeground(new java.awt.Color(0, 0, 0));
        lblTransport.setText("Rp 300.000");

        lblMakan.setForeground(new java.awt.Color(0, 0, 0));
        lblMakan.setText("Rp 200.000");

        lblJabatan.setForeground(new java.awt.Color(0, 0, 0));
        lblJabatan.setText("Rp 500.000");

        lblBpjsKesehatan.setForeground(new java.awt.Color(102, 0, 0));
        lblBpjsKesehatan.setText("- Rp 35.000");

        lblBpjsKetenagakerjaan.setForeground(new java.awt.Color(102, 0, 0));
        lblBpjsKetenagakerjaan.setText("- Rp 70.000");

        lblPotonganLainnya.setForeground(new java.awt.Color(102, 0, 0));
        lblPotonganLainnya.setText("- Rp 45.000");

        btnUnduhPdf.setBackground(new java.awt.Color(0, 51, 0));
        btnUnduhPdf.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnUnduhPdf.setText("Unduh PDF");
        btnUnduhPdf.addActionListener(this::btnUnduhPdfActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 226, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(213, 213, 213)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jLabel11)
                    .addComponent(jLabel10)
                    .addComponent(jLabel12))
                .addGap(87, 87, 87))
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblJabatan))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblMakan))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTransport))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblGajiPokok)))
                .addGap(31, 31, 31))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTerbilang)
                            .addComponent(jLabel25))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblPotonganLainnya))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBpjsKetenagakerjaan))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBpjsKesehatan)))
                        .addGap(29, 29, 29))))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnUnduhPdf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel7))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel9)))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(lblGajiPokok))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(lblTransport))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(lblMakan))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(lblJabatan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(lblBpjsKesehatan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(lblBpjsKetenagakerjaan))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(lblPotonganLainnya))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTerbilang)
                .addGap(30, 30, 30)
                .addComponent(btnUnduhPdf, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(232, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel2);

        panelSidebar4.setBackground(new java.awt.Color(255, 255, 255));

        lblMenuPegawai4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblMenuPegawai4.setForeground(new java.awt.Color(0, 0, 0));
        lblMenuPegawai4.setText("Menu Pegawai");

        lblBeranda4.setForeground(new java.awt.Color(0, 0, 0));
        lblBeranda4.setText("🏠 Beranda");
        lblBeranda4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblBeranda4MouseClicked(evt);
            }
        });

        lblPresensi4.setForeground(new java.awt.Color(0, 0, 0));
        lblPresensi4.setText("📅 Presensi");
        lblPresensi4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPresensi4MouseClicked(evt);
            }
        });

        lblSlipGaji4.setForeground(new java.awt.Color(0, 0, 0));
        lblSlipGaji4.setText("📄 Slip Gaji ");

        javax.swing.GroupLayout panelSidebar4Layout = new javax.swing.GroupLayout(panelSidebar4);
        panelSidebar4.setLayout(panelSidebar4Layout);
        panelSidebar4Layout.setHorizontalGroup(
            panelSidebar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSidebar4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSidebar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMenuPegawai4)
                    .addComponent(lblBeranda4)
                    .addComponent(lblPresensi4)
                    .addComponent(lblSlipGaji4))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        panelSidebar4Layout.setVerticalGroup(
            panelSidebar4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSidebar4Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(lblMenuPegawai4)
                .addGap(18, 18, 18)
                .addComponent(lblBeranda4)
                .addGap(18, 18, 18)
                .addComponent(lblPresensi4)
                .addGap(18, 18, 18)
                .addComponent(lblSlipGaji4)
                .addContainerGap(562, Short.MAX_VALUE))
        );

        panelHeader.setBackground(new java.awt.Color(255, 255, 255));

        lblJudul.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblJudul.setForeground(new java.awt.Color(0, 0, 0));
        lblJudul.setText("Sistem Penggajian");
        lblJudul.setMaximumSize(new java.awt.Dimension(100, 100));
        lblJudul.setMinimumSize(new java.awt.Dimension(100, 100));

        lblHeaderInisial.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblHeaderInisial.setForeground(new java.awt.Color(51, 102, 0));
        lblHeaderInisial.setText("AP");

        lblHeaderNama.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHeaderNama.setForeground(new java.awt.Color(0, 0, 0));
        lblHeaderNama.setText("Andi Pratama");

        lblHeaderJabatan.setForeground(new java.awt.Color(51, 51, 51));
        lblHeaderJabatan.setText("Staff Admin");

        btnKeluar.setBackground(new java.awt.Color(204, 204, 204));
        btnKeluar.setForeground(new java.awt.Color(0, 0, 0));
        btnKeluar.setText("Keluar");
        btnKeluar.addActionListener(this::btnKeluarActionPerformed);

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHeaderInisial)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHeaderNama)
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblHeaderJabatan)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnKeluar)
                .addGap(21, 21, 21))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(btnKeluar)
                .addGap(0, 6, Short.MAX_VALUE))
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblJudul, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                                .addComponent(lblHeaderJabatan, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))
                    .addGroup(panelHeaderLayout.createSequentialGroup()
                        .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblHeaderInisial)
                            .addComponent(lblHeaderNama))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Slip Gaji ");

        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("Lihat dan unduh slip gaji kamu ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(panelSidebar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 752, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))))
                .addContainerGap(334, Short.MAX_VALUE))
            .addComponent(panelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelSidebar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(22, 22, 22))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 878, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 1963, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lblBeranda4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBeranda4MouseClicked
        // TODO add your handling code here:
        TampilanUtamaPegawai home = new TampilanUtamaPegawai(
                nama, jabatan, "", "", "", inisial, idPegawai
        );
        home.setVisible(true);
        dispose();
    }//GEN-LAST:event_lblBeranda4MouseClicked

    private void lblPresensi4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPresensi4MouseClicked
        // TODO add your handling code here:
        Presensi p = new Presensi(
                idPegawai,
                nama,
                jabatan,
                inisial
        );

        p.setVisible(true);
        dispose();
    }//GEN-LAST:event_lblPresensi4MouseClicked

    private void btnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKeluarActionPerformed
        // TODO add your handling code here:
        int pilih = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin keluar?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );

        if (pilih == JOptionPane.YES_OPTION) {
            Login login = new Login();
            login.setVisible(true);
            dispose();
        }


    }//GEN-LAST:event_btnKeluarActionPerformed

    private void btnUnduhPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUnduhPdfActionPerformed
        // TODO add your handling code here:
        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Simpan Slip Gaji");
            chooser.setSelectedFile(new File("SlipGaji_" + nama + ".pdf"));
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

            int hasil = chooser.showSaveDialog(this);
            if (hasil != JFileChooser.APPROVE_OPTION) {
                return;
            }

            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".pdf")) {
                path += ".pdf";
            }

            Document document = new Document(PageSize.A4, 30, 30, 30, 30);
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            Font title = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, BaseColor.WHITE);
            Font sub = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font hijau = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(0, 128, 0));

            // HEADER
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("SLIP GAJI KARYAWAN", title));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(0, 70, 70));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(15);
            header.addCell(cell);
            document.add(header);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(jLabel5.getText(), sub));
            document.add(new Paragraph(" "));

            // INFO PEGAWAI (tanpa border)
            PdfPTable info = new PdfPTable(2);
            info.setWidths(new float[]{2, 3});
            info.setSpacingAfter(15f);
            info.setWidthPercentage(100);
            info.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            info.addCell(new Phrase("Nama", bold));
            info.addCell(new Phrase(jLabel7.getText(), sub));
            info.addCell(new Phrase("Jabatan", bold));
            info.addCell(new Phrase(jLabel9.getText(), sub));
            info.addCell(new Phrase("NIK", bold));
            info.addCell(new Phrase(jLabel11.getText(), sub));
            info.addCell(new Phrase("Tanggal Terbit", bold));
            info.addCell(new Phrase(jLabel13.getText(), sub));

            document.add(info);
            document.add(new Paragraph(" "));

            // PENDAPATAN (tanpa border)
            PdfPTable pendapatan = new PdfPTable(2);
            pendapatan.setWidthPercentage(100);
            pendapatan.setWidths(new float[]{2, 2});
            pendapatan.setSpacingAfter(15f);
            pendapatan.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell p1 = new PdfPCell(new Phrase("PENDAPATAN", bold));
            p1.setColspan(2);
            p1.setBackgroundColor(new BaseColor(220, 220, 220));
            p1.setBorder(Rectangle.NO_BORDER);
            p1.setPadding(8);
            pendapatan.addCell(p1);

            pendapatan.addCell(new Phrase("Gaji Pokok", sub));
            pendapatan.addCell(new Phrase(lblGajiPokok.getText(), sub));
            pendapatan.addCell(new Phrase("Transport", sub));
            pendapatan.addCell(new Phrase(lblTransport.getText(), sub));
            pendapatan.addCell(new Phrase("Makan", sub));
            pendapatan.addCell(new Phrase(lblMakan.getText(), sub));
            pendapatan.addCell(new Phrase("Jabatan", sub));
            pendapatan.addCell(new Phrase(lblJabatan.getText(), sub));

            PdfPCell totalLabel1 = new PdfPCell(new Phrase("Total Pendapatan", bold));
            totalLabel1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalLabel1.setBorder(Rectangle.NO_BORDER);
            PdfPCell totalValue1 = new PdfPCell(new Phrase(lblTotalPendapatan.getText(), bold));
            totalValue1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalValue1.setBorder(Rectangle.NO_BORDER);
            totalValue1.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pendapatan.addCell(totalLabel1);
            pendapatan.addCell(totalValue1);

            document.add(pendapatan);
            document.add(new Paragraph(" "));

            // POTONGAN (tanpa border)
            PdfPTable potongan = new PdfPTable(2);
            potongan.setWidthPercentage(100);
            potongan.setWidths(new float[]{2, 2});
            potongan.setSpacingAfter(15f);
            potongan.getDefaultCell().setBorder(Rectangle.NO_BORDER);

            PdfPCell p2 = new PdfPCell(new Phrase("POTONGAN", bold));
            p2.setColspan(2);
            p2.setBackgroundColor(new BaseColor(220, 220, 220));
            p2.setBorder(Rectangle.NO_BORDER);
            p2.setPadding(8);
            potongan.addCell(p2);

            potongan.addCell(new Phrase("BPJS Kesehatan", sub));
            potongan.addCell(new Phrase(lblBpjsKesehatan.getText(), sub));
            potongan.addCell(new Phrase("BPJS Ketenagakerjaan", sub));
            potongan.addCell(new Phrase(lblBpjsKetenagakerjaan.getText(), sub));
            potongan.addCell(new Phrase("Potongan Lain", sub));
            potongan.addCell(new Phrase(lblPotonganLainnya.getText(), sub));

            PdfPCell totalLabel2 = new PdfPCell(new Phrase("Total Potongan", bold));
            totalLabel2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalLabel2.setBorder(Rectangle.NO_BORDER);
            PdfPCell totalValue2 = new PdfPCell(new Phrase(lblTotalPotongan.getText(), bold));
            totalValue2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            totalValue2.setBorder(Rectangle.NO_BORDER);
            totalValue2.setHorizontalAlignment(Element.ALIGN_RIGHT);
            potongan.addCell(totalLabel2);
            potongan.addCell(totalValue2);

            document.add(potongan);
            document.add(new Paragraph(" "));

            // GAJI BERSIH
            PdfPTable gajiBersih = new PdfPTable(1);
            gajiBersih.setWidthPercentage(100);
            gajiBersih.setSpacingAfter(15f);
            PdfPCell gbCell = new PdfPCell(new Phrase(
                    "GAJI BERSIH DITERIMA          " + lblGajiBersih.getText(), hijau));
            gbCell.setBackgroundColor(new BaseColor(220, 245, 220));
            gbCell.setBorder(Rectangle.NO_BORDER);
            gbCell.setPadding(10);
            gajiBersih.addCell(gbCell);
            document.add(gajiBersih);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Terbilang:", sub));
            document.add(new Paragraph(lblTerbilang.getText(), sub));

            document.close();

            JOptionPane.showMessageDialog(this, "PDF berhasil disimpan di:\n" + path);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

    }//GEN-LAST:event_btnUnduhPdfActionPerformed

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        // TODO add your handling code here:
        java.time.LocalDate today = java.time.LocalDate.now();
        jLabel13.setText(today.getDayOfMonth() + " "
                + new String[]{"Januari", "Februari", "Maret", "April", "Mei", "Juni",
                    "Juli", "Agustus", "September", "Oktober", "November", "Desember"}[today.getMonthValue() - 1] + " " + today.getYear());
    }//GEN-LAST:event_jLabel13MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(()
                -> new SlipGaji(
                        0,
                        "",
                        "",
                        ""
                ).setVisible(true)
        );
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnKeluar;
    private javax.swing.JButton btnUnduhPdf;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblBeranda4;
    private javax.swing.JLabel lblBpjsKesehatan;
    private javax.swing.JLabel lblBpjsKetenagakerjaan;
    private javax.swing.JLabel lblGajiBersih;
    private javax.swing.JLabel lblGajiPokok;
    private javax.swing.JLabel lblHeaderInisial;
    private javax.swing.JLabel lblHeaderJabatan;
    private javax.swing.JLabel lblHeaderNama;
    private javax.swing.JLabel lblJabatan;
    private javax.swing.JLabel lblJudul;
    private javax.swing.JLabel lblMakan;
    private javax.swing.JLabel lblMenuPegawai4;
    private javax.swing.JLabel lblPotonganLainnya;
    private javax.swing.JLabel lblPresensi4;
    private javax.swing.JLabel lblSlipGaji4;
    private javax.swing.JLabel lblTerbilang;
    private javax.swing.JLabel lblTotalPendapatan;
    private javax.swing.JLabel lblTotalPotongan;
    private javax.swing.JLabel lblTransport;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelSidebar4;
    // End of variables declaration//GEN-END:variables
}
