package com.servicekomputer.dao;

import com.servicekomputer.database.DatabaseManager;
import com.servicekomputer.model.Servis;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServisDAO {

    // DAO riwayat dipakai untuk menyalin/sinkronkan data servis secara real-time.
    // Setiap kali servis ditambah, diedit, atau status-nya diupdate, baris yang
    // sama otomatis disalin/diperbarui juga di tabel riwayat_servis -- supaya
    // Riwayat Servis tidak perlu menunggu data dihapus dari Data Servis dulu.
    private final RiwayatServisDAO riwayatDao = new RiwayatServisDAO();

    private Servis mapServis(ResultSet rs) throws SQLException {
        Servis s = new Servis();
        s.setIdServis(rs.getString("id_servis"));
        s.setIdPelanggan(rs.getString("id_pelanggan"));
        s.setIdTeknisi(rs.getString("id_teknisi"));
        s.setJenisPerangkat(rs.getString("jenis_perangkat"));
        s.setMerk(rs.getString("merk"));
        s.setKerusakan(rs.getString("kerusakan"));
        s.setBiaya(rs.getDouble("biaya"));
        s.setTanggalMasuk(rs.getString("tanggal_masuk"));
        s.setTanggalSelesai(rs.getString("tanggal_selesai"));
        s.setStatus(rs.getString("status"));
        s.setCatatan(rs.getString("catatan"));
        try { s.setNamaPelanggan(rs.getString("nama_pelanggan")); } catch (Exception ignored) {}
        try { s.setNamaTeknisi(rs.getString("nama_teknisi")); } catch (Exception ignored) {}
        return s;
    }

    public List<Servis> getAll() {
        List<Servis> list = new ArrayList<>();
        // ASC: data terlama di atas, terbaru di bawah
        String sql = "SELECT s.*, p.nama AS nama_pelanggan, t.nama_teknisi " +
                "FROM servis s " +
                "LEFT JOIN pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                "LEFT JOIN teknisi t ON s.id_teknisi = t.id_teknisi " +
                "ORDER BY s.tanggal_masuk ASC, s.id_servis ASC";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapServis(rs));
        } catch (SQLException e) {
            System.out.println("Error getAll servis: " + e.getMessage());
        }
        return list;
    }

    public List<Servis> getByTeknisi(String idTeknisi) {
        List<Servis> list = new ArrayList<>();
        String sql = "SELECT s.*, p.nama AS nama_pelanggan, t.nama_teknisi " +
                "FROM servis s " +
                "LEFT JOIN pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                "LEFT JOIN teknisi t ON s.id_teknisi = t.id_teknisi " +
                "WHERE s.id_teknisi = ? ORDER BY s.tanggal_masuk ASC, s.id_servis ASC";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idTeknisi);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapServis(rs));
        } catch (SQLException e) {
            System.out.println("Error getByTeknisi: " + e.getMessage());
        }
        return list;
    }

    /** Ambil satu data servis lengkap (termasuk nama pelanggan/teknisi hasil join) berdasarkan ID. */
    public Servis getById(String idServis) {
        String sql = "SELECT s.*, p.nama AS nama_pelanggan, t.nama_teknisi " +
                "FROM servis s " +
                "LEFT JOIN pelanggan p ON s.id_pelanggan = p.id_pelanggan " +
                "LEFT JOIN teknisi t ON s.id_teknisi = t.id_teknisi " +
                "WHERE s.id_servis = ?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idServis);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapServis(rs);
        } catch (SQLException e) {
            System.out.println("Error getById servis: " + e.getMessage());
        }
        return null;
    }

    /**
     * Sinkronkan satu baris servis ke tabel riwayat_servis.
     * Dipanggil otomatis setiap kali insert/update/updateStatus berhasil,
     * supaya Riwayat Servis selalu mencerminkan data Data Servis secara real-time.
     */
    private void syncToRiwayat(String idServis) {
        Servis lengkap = getById(idServis);
        if (lengkap != null) {
            riwayatDao.insertFromServis(lengkap);
        }
    }

    public boolean insert(Servis s) {
        String sql = "INSERT INTO servis VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, s.getIdServis());
            ps.setString(2, s.getIdPelanggan());
            ps.setString(3, s.getIdTeknisi());
            ps.setString(4, s.getJenisPerangkat());
            ps.setString(5, s.getMerk());
            ps.setString(6, s.getKerusakan());
            ps.setDouble(7, s.getBiaya());
            ps.setString(8, s.getTanggalMasuk());
            ps.setString(9, s.getTanggalSelesai());
            ps.setString(10, s.getStatus());
            ps.setString(11, s.getCatatan());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) syncToRiwayat(s.getIdServis());
            return ok;
        } catch (SQLException e) {
            System.out.println("Error insert servis: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Servis s) {
        String sql = "UPDATE servis SET id_pelanggan=?, id_teknisi=?, jenis_perangkat=?, " +
                "merk=?, kerusakan=?, biaya=?, tanggal_masuk=?, tanggal_selesai=?, " +
                "status=?, catatan=? WHERE id_servis=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, s.getIdPelanggan());
            ps.setString(2, s.getIdTeknisi());
            ps.setString(3, s.getJenisPerangkat());
            ps.setString(4, s.getMerk());
            ps.setString(5, s.getKerusakan());
            ps.setDouble(6, s.getBiaya());
            ps.setString(7, s.getTanggalMasuk());
            ps.setString(8, s.getTanggalSelesai());
            ps.setString(9, s.getStatus());
            ps.setString(10, s.getCatatan());
            ps.setString(11, s.getIdServis());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) syncToRiwayat(s.getIdServis());
            return ok;
        } catch (SQLException e) {
            System.out.println("Error update servis: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatus(String idServis, String status, String catatan) {
        // Otomatis isi tanggal_selesai saat status jadi "Selesai" atau "Diambil Pelanggan"
        String tanggalSelesai = null;
        if ("Selesai".equals(status) || "Diambil Pelanggan".equals(status)) {
            // Cek apakah sudah ada tanggal_selesai sebelumnya
            String cekSql = "SELECT tanggal_selesai FROM servis WHERE id_servis=?";
            try {
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(cekSql);
                ps.setString(1, idServis);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String existing = rs.getString("tanggal_selesai");
                    // Isi hanya jika belum ada
                    if (existing == null || existing.trim().isEmpty()) {
                        tanggalSelesai = LocalDate.now().toString();
                    } else {
                        tanggalSelesai = existing;
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error cek tanggal_selesai: " + e.getMessage());
                tanggalSelesai = LocalDate.now().toString();
            }
        }

        String sql = tanggalSelesai != null
                ? "UPDATE servis SET status=?, catatan=?, tanggal_selesai=? WHERE id_servis=?"
                : "UPDATE servis SET status=?, catatan=? WHERE id_servis=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, catatan);
            if (tanggalSelesai != null) {
                ps.setString(3, tanggalSelesai);
                ps.setString(4, idServis);
            } else {
                ps.setString(3, idServis);
            }
            boolean ok = ps.executeUpdate() > 0;
            if (ok) syncToRiwayat(idServis);
            return ok;
        } catch (SQLException e) {
            System.out.println("Error updateStatus: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hapus servis dari Data Servis SAJA. Baris yang sama di riwayat_servis
     * TIDAK terpengaruh -- akan tetap ada di Riwayat Servis secara independen,
     * karena Riwayat sudah disinkronkan sejak servis pertama dibuat.
     */
    public boolean delete(String idServis) {
        String sql = "DELETE FROM servis WHERE id_servis=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idServis);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error delete servis: " + e.getMessage());
            return false;
        }
    }

    public String generateId() {
        String sql = "SELECT id_servis FROM servis ORDER BY id_servis DESC LIMIT 1";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String lastId = rs.getString("id_servis");
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("S%03d", num);
            }
        } catch (SQLException e) {
            System.out.println("Error generate ID: " + e.getMessage());
        }
        return "S001";
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM servis WHERE status=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error countByStatus: " + e.getMessage());
        }
        return 0;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM servis";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error countAll: " + e.getMessage());
        }
        return 0;
    }
}
