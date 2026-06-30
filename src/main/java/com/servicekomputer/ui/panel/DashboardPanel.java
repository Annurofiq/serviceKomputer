package com.servicekomputer.ui.panel;

import com.servicekomputer.dao.PelangganDAO;
import com.servicekomputer.dao.RiwayatServisDAO;
import com.servicekomputer.dao.ServisDAO;
import com.servicekomputer.dao.TeknisiDAO;
import com.servicekomputer.util.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard menghitung ulang semua statistik setiap kali dibuka (lihat
 * loadData(), dipanggil dari MainFrame.refreshPanel) -- supaya data yang
 * ditampilkan selalu realtime sesuai kondisi database saat ini, bukan
 * snapshot dari saat MainFrame pertama kali dibuka.
 */
public class DashboardPanel extends JPanel {

    private JPanel statsPanel;
    private JLabel lblInfo;

    private final ServisDAO servisDAO = new ServisDAO();
    private final PelangganDAO pelangganDAO = new PelangganDAO();
    private final TeknisiDAO teknisiDAO = new TeknisiDAO();
    private final RiwayatServisDAO riwayatDAO = new RiwayatServisDAO();

    public DashboardPanel() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 255));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 30, 47));

        JLabel lblWelcome = new JLabel("Selamat datang, " + SessionManager.getUsername() +
                " | Role: " + SessionManager.getRole());
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblWelcome.setForeground(new Color(100, 100, 130));

        header.add(lblTitle, BorderLayout.NORTH);
        header.add(lblWelcome, BorderLayout.SOUTH);

        // 7 kartu statistik -> grid 3x3 (baris terakhir tidak penuh, tidak masalah)
        statsPanel = new JPanel(new GridLayout(3, 3, 15, 15));
        statsPanel.setBackground(new Color(245, 245, 255));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        lblInfo = new JLabel();
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(new Color(80, 80, 100));
        infoPanel.add(lblInfo);

        add(header, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    /** Hitung ulang semua statistik dari database dan render ulang kartu-kartunya. */
    public void loadData() {
        statsPanel.removeAll();

        statsPanel.add(createCard("Total Servis", String.valueOf(servisDAO.countAll()),
                "🔧", new Color(79, 70, 229)));
        statsPanel.add(createCard("Menunggu", String.valueOf(servisDAO.countByStatus("Menunggu")),
                "⏳", new Color(234, 179, 8)));
        statsPanel.add(createCard("Sedang Diperbaiki", String.valueOf(servisDAO.countByStatus("Sedang Diperbaiki")),
                "🛠", new Color(59, 130, 246)));
        statsPanel.add(createCard("Menunggu Sparepart", String.valueOf(servisDAO.countByStatus("Menunggu Sparepart")),
                "📦", new Color(249, 115, 22)));
        statsPanel.add(createCard("Selesai", String.valueOf(servisDAO.countByStatus("Selesai")),
                "✅", new Color(34, 197, 94)));
        statsPanel.add(createCard("Total Pelanggan", String.valueOf(pelangganDAO.getAll().size()),
                "👥", new Color(168, 85, 247)));
        statsPanel.add(createCard("Total Riwayat Servis", String.valueOf(riwayatDAO.countAll()),
                "📜", new Color(20, 184, 166)));

        statsPanel.revalidate();
        statsPanel.repaint();

        lblInfo.setText("📌  Teknisi terdaftar: " + teknisiDAO.getAll().size() +
                "  |  Diambil Pelanggan: " + servisDAO.countByStatus("Diambil Pelanggan"));
    }

    private JPanel createCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 240), 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(color);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(new Color(120, 120, 150));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setBackground(Color.WHITE);
        left.add(lblValue);
        left.add(lblTitle);

        card.add(lblIcon, BorderLayout.EAST);
        card.add(left, BorderLayout.CENTER);

        return card;
    }
}
