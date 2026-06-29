package com.servicekomputer.ui;

import com.servicekomputer.ui.panel.*;
import com.servicekomputer.util.SessionManager;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton activeMenuButton;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Sistem Manajemen Service Komputer & HP");
        setSize(1180, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(960, 620));
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIHelper.BG_LIGHT);

        contentPanel.add(new DashboardPanel(), "dashboard");
        contentPanel.add(new PelangganPanel(), "pelanggan");
        if (SessionManager.isAdmin()) {
            contentPanel.add(new TeknisiPanel(), "teknisi");
        }
        contentPanel.add(new ServisPanel(), "servis");
        contentPanel.add(new StatusPanel(), "status");
        contentPanel.add(new RiwayatPanel(), "riwayat");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "dashboard");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(UIHelper.SIDEBAR_BG);
        sidebar.setLayout(new BorderLayout());

        // ===== Bagian atas: logo + judul + info user =====
        JPanel topPanel = new JPanel();
        topPanel.setBackground(UIHelper.SIDEBAR_BG);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Logo image — besar & tengah
        URL logoUrl = getClass().getResource("/images/logo_agungplertech.png");
        if (logoUrl != null) {
            ImageIcon rawIcon = new ImageIcon(logoUrl);
            Image scaled = rawIcon.getImage().getScaledInstance(98, 98, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            topPanel.add(lblLogo);
            topPanel.add(Box.createVerticalStrut(4));
        }

        JLabel lblApp = new JLabel("AgungPlerTech");
        lblApp.setFont(UIHelper.fontBold(16));
        lblApp.setForeground(Color.WHITE);
        lblApp.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Manajemen Servis");
        lblSub.setFont(UIHelper.fontPlain(11));
        lblSub.setForeground(UIHelper.SIDEBAR_SUBTEXT);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(50, 53, 75));
        sep.setBackground(UIHelper.SIDEBAR_BG);
        sep.setMaximumSize(new Dimension(190, 1));
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel userBadge = new JPanel();
        userBadge.setLayout(new BoxLayout(userBadge, BoxLayout.Y_AXIS));
        userBadge.setBackground(new Color(38, 40, 58));
        userBadge.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        userBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        userBadge.setMaximumSize(new Dimension(190, 60));

        JLabel lblUserName = new JLabel(SessionManager.getUsername());
        lblUserName.setFont(UIHelper.fontBold(13));
        lblUserName.setForeground(Color.WHITE);
        lblUserName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUserName.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblUserRole = new JLabel(SessionManager.getRole());
        lblUserRole.setFont(UIHelper.fontPlain(11));
        lblUserRole.setForeground(UIHelper.SIDEBAR_SUBTEXT);
        lblUserRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUserRole.setHorizontalAlignment(SwingConstants.CENTER);

        userBadge.add(lblUserName);
        userBadge.add(Box.createVerticalStrut(2));
        userBadge.add(lblUserRole);

        topPanel.add(lblApp);
        topPanel.add(Box.createVerticalStrut(1));
        topPanel.add(lblSub);
        topPanel.add(Box.createVerticalStrut(18));
        topPanel.add(sep);
        topPanel.add(Box.createVerticalStrut(14));
        topPanel.add(userBadge);

        // ===== Menu =====
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(UIHelper.SIDEBAR_BG);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JButton btnDashboard = addMenuItem(menuPanel, "Dashboard", "dashboard");
        addMenuItem(menuPanel, "Data Pelanggan", "pelanggan");
        if (SessionManager.isAdmin()) {
            addMenuItem(menuPanel, "Data Teknisi", "teknisi");
        }
        addMenuItem(menuPanel, "Data Servis", "servis");
        addMenuItem(menuPanel, "Status Perbaikan", "status");
        addMenuItem(menuPanel, "Riwayat Servis", "riwayat");

        setActiveButton(btnDashboard);

        // ===== Bawah: Logout =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(UIHelper.SIDEBAR_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 22, 12));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(UIHelper.fontBold(13));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(UIHelper.DANGER);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(206, 42));
        btnLogout.setHorizontalAlignment(SwingConstants.CENTER);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogout.setBackground(UIHelper.DANGER.brighter()); }
            public void mouseExited(MouseEvent e) { btnLogout.setBackground(UIHelper.DANGER); }
        });
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin logout?", "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                SessionManager.clearSession();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        bottomPanel.add(btnLogout);

        sidebar.add(topPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton addMenuItem(JPanel panel, String text, String card) {
        JButton btn = createMenuButton(text);
        btn.addActionListener(e -> {
            cardLayout.show(contentPanel, card);
            setActiveButton(btn);
            refreshPanel(card);
        });
        panel.add(btn);
        panel.add(Box.createVerticalStrut(4));
        return btn;
    }

    private void refreshPanel(String card) {
        for (Component c : contentPanel.getComponents()) {
            if (!c.isVisible()) continue;
            if (c instanceof PelangganPanel) ((PelangganPanel) c).loadData();
            else if (c instanceof TeknisiPanel) ((TeknisiPanel) c).loadData();
            else if (c instanceof ServisPanel) ((ServisPanel) c).loadData();
            else if (c instanceof StatusPanel) ((StatusPanel) c).loadData();
            else if (c instanceof RiwayatPanel) ((RiwayatPanel) c).loadData();
        }
    }

    private void setActiveButton(JButton btn) {
        if (activeMenuButton != null) {
            activeMenuButton.setBackground(UIHelper.SIDEBAR_BG);
            activeMenuButton.setForeground(UIHelper.SIDEBAR_TEXT);
        }
        btn.setBackground(UIHelper.SIDEBAR_HOVER);
        btn.setForeground(Color.WHITE);
        activeMenuButton = btn;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UIHelper.fontPlain(13));
        btn.setForeground(UIHelper.SIDEBAR_TEXT);
        btn.setBackground(UIHelper.SIDEBAR_BG);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(206, 42));
        btn.setPreferredSize(new Dimension(206, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeMenuButton) {
                    btn.setBackground(new Color(38, 40, 58));
                    btn.setForeground(Color.WHITE);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeMenuButton) {
                    btn.setBackground(UIHelper.SIDEBAR_BG);
                    btn.setForeground(UIHelper.SIDEBAR_TEXT);
                }
            }
        });

        return btn;
    }
}