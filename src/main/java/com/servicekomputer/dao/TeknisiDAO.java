package com.servicekomputer.dao;

import com.servicekomputer.database.DatabaseManager;
import com.servicekomputer.model.Teknisi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeknisiDAO {

    public List<Teknisi> getAll() {
        List<Teknisi> list = new ArrayList<>();
        String sql = "SELECT * FROM teknisi ORDER BY id_teknisi";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Teknisi(
                        rs.getString("id_teknisi"),
                        rs.getString("nama_teknisi"),
                        rs.getString("keahlian"),
                        rs.getString("no_hp")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error getAll teknisi: " + e.getMessage());
        }
        return list;
    }

    public boolean insert(Teknisi t) {
        String sql = "INSERT INTO teknisi VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, t.getIdTeknisi());
            ps.setString(2, t.getNamaTeknisi());
            ps.setString(3, t.getKeahlian());
            ps.setString(4, t.getNoHp());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error insert teknisi: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Teknisi t) {
        String sql = "UPDATE teknisi SET nama_teknisi=?, keahlian=?, no_hp=? WHERE id_teknisi=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNamaTeknisi());
            ps.setString(2, t.getKeahlian());
            ps.setString(3, t.getNoHp());
            ps.setString(4, t.getIdTeknisi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error update teknisi: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String idTeknisi) {
        String sql = "DELETE FROM teknisi WHERE id_teknisi=?";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idTeknisi);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error delete teknisi: " + e.getMessage());
            return false;
        }
    }

    public String generateId() {
        String sql = "SELECT id_teknisi FROM teknisi ORDER BY id_teknisi DESC LIMIT 1";
        try {
            Connection conn = DatabaseManager.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String lastId = rs.getString("id_teknisi");
                int num = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("T%03d", num);
            }
        } catch (SQLException e) {
            System.out.println("Error generate ID: " + e.getMessage());
        }
        return "T001";
    }
}
