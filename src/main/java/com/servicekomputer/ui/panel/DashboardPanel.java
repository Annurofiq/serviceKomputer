package com.servicekomputer.ui.panel;

import com.servicekomputer.dao.PelangganDAO;
import com.servicekomputer.dao.ServisDAO;
import com.servicekomputer.dao.TeknisiDAO;
import com.servicekomputer.util.SessionManager;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        initUI();
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

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBackground(new Color(245, 245, 255));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        ServisDAO servisDAO = new ServisDAO();
        PelangganDAO pelangganDAO = new PelangganDAO();
        TeknisiDAO teknisiDAO = new TeknisiDAO();

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

        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel lblInfo = new JLabel("📌  Teknisi terdaftar: " + teknisiDAO.getAll().size() +
                "  |  Diambil Pelanggan: " + servisDAO.countByStatus("Diambil Pelanggan"));
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInfo.setForeground(new Color(80, 80, 100));
        infoPanel.add(lblInfo);

        add(header, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
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
