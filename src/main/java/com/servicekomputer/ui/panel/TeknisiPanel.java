package com.servicekomputer.ui.panel;

import com.servicekomputer.dao.TeknisiDAO;
import com.servicekomputer.model.Teknisi;
import com.servicekomputer.ui.dialog.TeknisiDialog;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TeknisiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private TeknisiDAO dao;

    public TeknisiPanel() {
        dao = new TeknisiDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_LIGHT);

        add(UIHelper.createPageHeader("Data Teknisi", "Kelola data teknisi yang bekerja"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(UIHelper.BG_LIGHT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(16, 28, 12, 28));

        JButton btnTambah = UIHelper.createButton("+ Tambah", UIHelper.PRIMARY);
        JButton btnEdit = UIHelper.createButton("Edit", UIHelper.INFO);
        JButton btnHapus = UIHelper.createButton("Hapus", UIHelper.DANGER);
        JButton btnRefresh = UIHelper.createOutlineButton("Refresh");

        toolbar.add(btnTambah);
        toolbar.add(btnEdit);
        toolbar.add(btnHapus);
        toolbar.add(btnRefresh);

        String[] columns = {"ID Teknisi", "Nama Teknisi", "Keahlian", "No HP"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR, 1, true));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIHelper.BG_LIGHT);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 28, 28, 28));
        tablePanel.add(scroll, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UIHelper.BG_LIGHT);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(tablePanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        btnTambah.addActionListener(e -> {
            TeknisiDialog d = new TeknisiDialog(null, null);
            d.setVisible(true);
            if (d.isSaved()) loadData();
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!"); return; }
            Teknisi t = new Teknisi(
                    tableModel.getValueAt(row, 0).toString(),
                    tableModel.getValueAt(row, 1).toString(),
                    tableModel.getValueAt(row, 2).toString(),
                    tableModel.getValueAt(row, 3).toString()
            );
            TeknisiDialog d = new TeknisiDialog(null, t);
            d.setVisible(true);
            if (d.isSaved()) loadData();
        });

        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!"); return; }
            String id = tableModel.getValueAt(row, 0).toString();
            String nama = tableModel.getValueAt(row, 1).toString();
            int c = JOptionPane.showConfirmDialog(this, "Hapus teknisi " + nama + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                if (dao.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus! Mungkin ada data servis terkait.");
                }
            }
        });

        btnRefresh.addActionListener(e -> loadData());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        for (Teknisi t : dao.getAll()) {
            tableModel.addRow(new Object[]{
                    t.getIdTeknisi(), t.getNamaTeknisi(), t.getKeahlian(), t.getNoHp()
            });
        }
    }
}
