package com.servicekomputer.dao;

import com.servicekomputer.database.DatabaseManager;
import com.servicekomputer.model.RiwayatServis;
import com.servicekomputer.model.Servis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RiwayatServisDAO {

    public boolean insertFromServis(Servis s) {
        String sql = "INSERT INTO riwayat_servis " +
                "(id_servis, id_pelanggan, nama_pelanggan, id_teknisi, nama_teknisi, " +
                "jenis_perangkat, merk, kerusakan, biaya, tanggal_masuk, tanggal_selesai, status, catatan) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "nama_pelanggan=VALUES(nama_pelanggan), nama_teknisi=VALUES(nama_teknisi), " +
                "jenis_perangkat=VALUES(jenis_perangkat), merk=VALUES(merk), kerusakan=VALUES(kerusakan), " +
                "biaya=VALUES(biaya), tanggal_masuk=VALUES(tanggal_masuk), tanggal_selesai=VALUES(tanggal_selesai), " +
                "status=VALUES(status), catatan=VALUES(catatan)";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, s.getIdServis());
            ps.setString(2, s.getIdPelanggan());
            ps.setString(3, s.getNamaPelanggan() != null ? s.getNamaPelanggan() : s.getIdPelanggan());
            ps.setString(4, s.getIdTeknisi());
            ps.setString(5, s.getNamaTeknisi() != null ? s.getNamaTeknisi() : s.getIdTeknisi());
            ps.setString(6, s.getJenisPerangkat());
            ps.setString(7, s.getMerk());
            ps.setString(8, s.getKerusakan());
            ps.setDouble(9, s.getBiaya());
            ps.setString(10, s.getTanggalMasuk());
            ps.setString(11, s.getTanggalSelesai());
            ps.setString(12, s.getStatus());
            ps.setString(13, s.getCatatan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insert riwayat_servis: " + e.getMessage());
            return false;
        }
    }

    public List<RiwayatServis> getAll() {
        List<RiwayatServis> list = new ArrayList<>();
        String sql = "SELECT * FROM riwayat_servis ORDER BY tanggal_masuk ASC, dihapus_pada ASC";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                RiwayatServis r = new RiwayatServis();
                r.setIdServis(rs.getString("id_servis"));
                r.setIdPelanggan(rs.getString("id_pelanggan"));
                r.setNamaPelanggan(rs.getString("nama_pelanggan"));
                r.setIdTeknisi(rs.getString("id_teknisi"));
                r.setNamaTeknisi(rs.getString("nama_teknisi"));
                r.setJenisPerangkat(rs.getString("jenis_perangkat"));
                r.setMerk(rs.getString("merk"));
                r.setKerusakan(rs.getString("kerusakan"));
                r.setBiaya(rs.getDouble("biaya"));
                r.setTanggalMasuk(rs.getString("tanggal_masuk"));
                r.setTanggalSelesai(rs.getString("tanggal_selesai"));
                r.setStatus(rs.getString("status"));
                r.setCatatan(rs.getString("catatan"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Error getAll riwayat_servis: " + e.getMessage());
        }
        return list;
    }

    public boolean delete(String idServis) {
        String sql = "DELETE FROM riwayat_servis WHERE id_servis=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idServis);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error delete riwayat_servis: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tandai sebuah servis sebagai "Diambil Pelanggan" dari halaman Riwayat.
     * Mengupdate tabel riwayat_servis, dan -- jika baris yang sama masih ada
     * di tabel servis aktif (belum dihapus dari Data Servis) -- ikut diupdate
     * juga di sana supaya kedua tabel tetap konsisten.
     */
    public boolean updateStatusDiambil(String idServis, String catatan) {
        String tanggalSekarang = java.time.LocalDate.now().toString();

        String sqlRiwayat = "UPDATE riwayat_servis SET status=?, catatan=?, " +
                "tanggal_selesai = COALESCE(tanggal_selesai, ?) WHERE id_servis=?";
        String sqlServisAktif = "UPDATE servis SET status=?, catatan=?, " +
                "tanggal_selesai = COALESCE(tanggal_selesai, ?) WHERE id_servis=?";
        try {
            Connection conn = DatabaseManager.getConnection();

            PreparedStatement psRiwayat = conn.prepareStatement(sqlRiwayat);
            psRiwayat.setString(1, "Diambil Pelanggan");
            psRiwayat.setString(2, catatan);
            psRiwayat.setString(3, tanggalSekarang);
            psRiwayat.setString(4, idServis);
            boolean ok = psRiwayat.executeUpdate() > 0;

            // Sinkron ke tabel servis aktif juga, kalau barisnya masih ada di sana.
            PreparedStatement psServis = conn.prepareStatement(sqlServisAktif);
            psServis.setString(1, "Diambil Pelanggan");
            psServis.setString(2, catatan);
            psServis.setString(3, tanggalSekarang);
            psServis.setString(4, idServis);
            psServis.executeUpdate(); // tidak masalah jika 0 baris (servis sudah dihapus dari Data Servis)

            return ok;
        } catch (SQLException e) {
            System.out.println("Error updateStatusDiambil: " + e.getMessage());
            return false;
        }
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM riwayat_servis";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error countAll riwayat_servis: " + e.getMessage());
        }
        return 0;
    }
}