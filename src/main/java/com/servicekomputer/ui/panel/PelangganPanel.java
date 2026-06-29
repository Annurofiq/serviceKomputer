package com.servicekomputer.ui.panel;

import com.servicekomputer.dao.PelangganDAO;
import com.servicekomputer.model.Pelanggan;
import com.servicekomputer.ui.dialog.PelangganDialog;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PelangganPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private PelangganDAO dao;
    private JTextField txtCari;

    public PelangganPanel() {
        dao = new PelangganDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_LIGHT);

        add(UIHelper.createPageHeader("Data Pelanggan", "Kelola informasi pelanggan yang terdaftar"), BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(UIHelper.BG_LIGHT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(16, 28, 12, 28));

        JPanel leftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftTools.setBackground(UIHelper.BG_LIGHT);

        JButton btnTambah = UIHelper.createButton("+ Tambah", UIHelper.PRIMARY);
        JButton btnEdit = UIHelper.createButton("Edit", UIHelper.INFO);
        JButton btnHapus = UIHelper.createButton("Hapus", UIHelper.DANGER);
        JButton btnRefresh = UIHelper.createOutlineButton("Refresh");

        leftTools.add(btnTambah);
        leftTools.add(btnEdit);
        leftTools.add(btnHapus);
        leftTools.add(btnRefresh);

        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightTools.setBackground(UIHelper.BG_LIGHT);
        txtCari = new JTextField(16);
        UIHelper.styleTextField(txtCari);
        JButton btnCari = UIHelper.createOutlineButton("Cari");
        rightTools.add(txtCari);
        rightTools.add(btnCari);

        toolbar.add(leftTools, BorderLayout.WEST);
        toolbar.add(rightTools, BorderLayout.EAST);

        // Table
        String[] columns = {"ID Pelanggan", "Nama", "No HP", "Alamat"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR, 1, true));
        scroll.setBackground(Color.WHITE);
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIHelper.BG_LIGHT);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 28, 28, 28));
        tablePanel.add(scroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(UIHelper.BG_LIGHT);
        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Events
        btnTambah.addActionListener(e -> {
            PelangganDialog dialog = new PelangganDialog(null, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) loadData();
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!"); return; }
            Pelanggan p = new Pelanggan(
                    tableModel.getValueAt(row, 0).toString(),
                    tableModel.getValueAt(row, 1).toString(),
                    tableModel.getValueAt(row, 2).toString(),
                    tableModel.getValueAt(row, 3).toString()
            );
            PelangganDialog dialog = new PelangganDialog(null, p);
            dialog.setVisible(true);
            if (dialog.isSaved()) loadData();
        });

        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!"); return; }
            String id = tableModel.getValueAt(row, 0).toString();
            String nama = tableModel.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Hapus pelanggan " + nama + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus! Mungkin ada data servis terkait.");
                }
            }
        });

        btnRefresh.addActionListener(e -> { txtCari.setText(""); loadData(); });

        btnCari.addActionListener(e -> cariData());
        txtCari.addActionListener(e -> cariData());
    }

    private void cariData() {
        String keyword = txtCari.getText().trim().toLowerCase();
        if (keyword.isEmpty()) { loadData(); return; }
        tableModel.setRowCount(0);
        List<Pelanggan> list = dao.getAll();
        for (Pelanggan p : list) {
            if (p.getNama().toLowerCase().contains(keyword) ||
                    p.getNoHp().toLowerCase().contains(keyword) ||
                    p.getIdPelanggan().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[]{
                        p.getIdPelanggan(), p.getNama(), p.getNoHp(), p.getAlamat()
                });
            }
        }
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Pelanggan> list = dao.getAll();
        for (Pelanggan p : list) {
            tableModel.addRow(new Object[]{
                    p.getIdPelanggan(), p.getNama(), p.getNoHp(), p.getAlamat()
            });
        }
    }
}
