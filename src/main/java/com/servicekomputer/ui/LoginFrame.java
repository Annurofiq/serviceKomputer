package com.servicekomputer.ui;

import com.servicekomputer.dao.UserDAO;
import com.servicekomputer.model.User;
import com.servicekomputer.util.SessionManager;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblStatus;

    public LoginFrame() {
        initUI();
    }

    private void initUI() {
        setTitle("Login - Sistem Service Komputer & HP");
        setSize(420, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // ===== Header dengan Logo =====
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIHelper.SIDEBAR_BG);
        headerPanel.setPreferredSize(new Dimension(420, 210));
        headerPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 4, 0);

        // Logo image
        URL logoUrl = getClass().getResource("/images/logo_agungplertech.png");
        if (logoUrl != null) {
            ImageIcon rawIcon = new ImageIcon(logoUrl);
            Image scaled = rawIcon.getImage().getScaledInstance(98, 98, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
            headerPanel.add(lblLogo, gbc);
            gbc.gridy = 1;
            gbc.insets = new Insets(2, 0, 3, 0);
        }

        JLabel lblTitle = new JLabel("AgungPlerTech");
        lblTitle.setFont(UIHelper.fontBold(22));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblTitle, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel lblSub = new JLabel("Sistem Manajemen Servis Komputer & HP");
        lblSub.setFont(UIHelper.fontPlain(11));
        lblSub.setForeground(UIHelper.SIDEBAR_SUBTEXT);
        lblSub.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblSub, gbc);

        // ===== Form =====
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(null);

        JLabel lblUser = new JLabel("Username");
        lblUser.setBounds(40, 30, 200, 20);
        lblUser.setFont(UIHelper.fontBold(12));
        lblUser.setForeground(UIHelper.TEXT_DARK);

        txtUsername = new JTextField();
        txtUsername.setBounds(40, 53, 330, 40);
        UIHelper.styleTextField(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setBounds(40, 105, 200, 20);
        lblPass.setFont(UIHelper.fontBold(12));
        lblPass.setForeground(UIHelper.TEXT_DARK);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(40, 128, 330, 40);
        UIHelper.styleTextField(txtPassword);

        btnLogin = UIHelper.createButton("MASUK", UIHelper.PRIMARY);
        btnLogin.setBounds(40, 190, 330, 44);
        btnLogin.setFont(UIHelper.fontBold(14));

        lblStatus = new JLabel("", SwingConstants.CENTER);
        lblStatus.setBounds(40, 242, 330, 25);
        lblStatus.setFont(UIHelper.fontPlain(12));
        lblStatus.setForeground(UIHelper.DANGER);

        JLabel lblHint = new JLabel("Admin: admin / admin123   |   Teknisi: budi / 12345", SwingConstants.CENTER);
        lblHint.setBounds(20, 275, 380, 20);
        lblHint.setFont(UIHelper.fontItalic(11));
        lblHint.setForeground(UIHelper.TEXT_MUTED);

        formPanel.add(lblUser);
        formPanel.add(txtUsername);
        formPanel.add(lblPass);
        formPanel.add(txtPassword);
        formPanel.add(btnLogin);
        formPanel.add(lblStatus);
        formPanel.add(lblHint);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Username dan password tidak boleh kosong!");
            return;
        }

        UserDAO dao = new UserDAO();
        User user = dao.login(username, password);

        if (user != null) {
            SessionManager.setSession(user.getId(), user.getUsername(), user.getRole());
            dispose();
            new MainFrame().setVisible(true);
        } else {
            lblStatus.setText("Username atau password salah!");
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}