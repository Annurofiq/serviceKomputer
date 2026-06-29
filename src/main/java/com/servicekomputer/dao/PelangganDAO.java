package com.servicekomputer.dao;

import com.servicekomputer.database.DatabaseManager;
import com.servicekomputer.model.Pelanggan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PelangganDAO {

    public List<Pelanggan> getAll() {
        List<Pelanggan> list = new ArrayList<>();
        String sql = "SELECT * FROM pelanggan ORDER BY id_pelanggan";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Pelanggan(
                        rs.getString("id_pelanggan"),
                        rs.getString("nama"),
                        rs.getString("no_hp"),
                        rs.getString("alamat")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error getAll pelanggan: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Pelanggan p) {
        String sql = "INSERT INTO pelanggan VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getIdPelanggan());
            ps.setString(2, p.getNama());
            ps.setString(3, p.getNoHp());
            ps.setString(4, p.getAlamat());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insert pelanggan: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Pelanggan p) {
        String sql = "UPDATE pelanggan SET nama=?, no_hp=?, alamat=? WHERE id_pelanggan=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getNama());
            ps.setString(2, p.getNoHp());
            ps.setString(3, p.getAlamat());
            ps.setString(4, p.getIdPelanggan());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error update pelanggan: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String idPelanggan) {
        String sql = "DELETE FROM pelanggan WHERE id_pelanggan=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idPelanggan);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error delete pelanggan: " + e.getMessage());
            return false;
        }
    }

    public String generateId() {
        String sql = "SELECT id_pelanggan FROM pelanggan ORDER BY id_pelanggan DESC LIMIT 1";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String lastId = rs.getString("id_pelanggan");
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("P%03d", num);
            }
        } catch (SQLException e) {
            System.out.println("Error generate ID: " + e.getMessage());
        }
        return "P001";
    }
}
