package com.servicekomputer.ui.panel;

import com.servicekomputer.dao.ServisDAO;
import com.servicekomputer.model.Servis;
import com.servicekomputer.util.SessionManager;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StatusPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private ServisDAO dao;

    public StatusPanel() {
        dao = new ServisDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_LIGHT);

        add(UIHelper.createPageHeader("Status Perbaikan", "Pantau dan update progres setiap servis"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(UIHelper.BG_LIGHT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(16, 28, 12, 28));

        // Hanya teknisi yang bisa update status
        if (!SessionManager.isAdmin()) {
            JButton btnUpdate = UIHelper.createButton("Update Status", UIHelper.PRIMARY);
            toolbar.add(btnUpdate);
            btnUpdate.addActionListener(e -> updateStatus());
        }

        JButton btnRefresh = UIHelper.createOutlineButton("Refresh");
        toolbar.add(btnRefresh);

        // Tambah label info hak akses
        if (SessionManager.isAdmin()) {
            JLabel lblInfo = new JLabel("  ℹ️  Mode Admin: hanya dapat melihat status");
            lblInfo.setFont(UIHelper.fontItalic(11));
            lblInfo.setForeground(UIHelper.TEXT_MUTED);
            toolbar.add(lblInfo);
        }

        String[] columns = {"ID Servis", "Pelanggan", "Teknisi", "Perangkat", "Kerusakan", "Status", "Catatan"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);

        // Wrap text untuk kolom Kerusakan (4) dan Catatan (6)
        table.getColumnModel().getColumn(4).setCellRenderer(new WrapCellRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new WrapCellRenderer());

        // Lebar kolom
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {90, 130, 110, 130, 220, 170, 340};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR, 1, true));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIHelper.BG_LIGHT);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 28, 28, 28));
        tablePanel.add(scroll, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UIHelper.BG_LIGHT);
        center.add(toolbar, BorderLayout.NORTH);
        center.add(tablePanel, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> loadData());
    }

    private void updateStatus() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data servis yang ingin diupdate!");
            return;
        }

        String idServis = tableModel.getValueAt(row, 0).toString();
        String statusSaat = tableModel.getValueAt(row, 5).toString();
        String catatanSaat = tableModel.getValueAt(row, 6) != null ?
                tableModel.getValueAt(row, 6).toString() : "";

        JDialog dialog = new JDialog((Frame) null, "Update Status - " + idServis, true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(Color.WHITE);

        JLabel lblStatus = new JLabel("Status Perbaikan");
        lblStatus.setBounds(24, 22, 200, 22);
        lblStatus.setFont(UIHelper.fontBold(12));
        lblStatus.setForeground(UIHelper.TEXT_DARK);

        String[] statusOptions = {"Menunggu", "Sedang Diperbaiki", "Menunggu Sparepart", "Selesai", "Diambil Pelanggan"};
        JComboBox<String> cbStatus = new JComboBox<>(statusOptions);
        cbStatus.setBounds(24, 48, 370, 38);
        UIHelper.styleComboBox(cbStatus);
        cbStatus.setSelectedItem(statusSaat);

        JLabel lblCatatan = new JLabel("Catatan Perbaikan");
        lblCatatan.setBounds(24, 100, 200, 22);
        lblCatatan.setFont(UIHelper.fontBold(12));
        lblCatatan.setForeground(UIHelper.TEXT_DARK);

        JTextArea txtCatatan = new JTextArea(catatanSaat);
        UIHelper.styleTextArea(txtCatatan);
        JScrollPane scrollCatatan = new JScrollPane(txtCatatan);
        scrollCatatan.setBounds(24, 126, 370, 80);
        scrollCatatan.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR, 1, true));

        JButton btnSave = UIHelper.createButton("Simpan", UIHelper.PRIMARY);
        btnSave.setBounds(24, 225, 178, 42);

        JButton btnBatal = UIHelper.createOutlineButton("Batal");
        btnBatal.setBounds(216, 225, 178, 42);

        dialog.add(lblStatus);
        dialog.add(cbStatus);
        dialog.add(lblCatatan);
        dialog.add(scrollCatatan);
        dialog.add(btnSave);
        dialog.add(btnBatal);

        btnSave.addActionListener(e -> {
            String newStatus = cbStatus.getSelectedItem().toString();
            String newCatatan = txtCatatan.getText().trim();
            if (dao.updateStatus(idServis, newStatus, newCatatan)) {
                JOptionPane.showMessageDialog(dialog, "Status berhasil diupdate!");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Gagal update status!");
            }
        });

        btnBatal.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Servis> list = dao.getAll();
        for (Servis s : list) {
            tableModel.addRow(new Object[]{
                    s.getIdServis(),
                    s.getNamaPelanggan() != null ? s.getNamaPelanggan() : s.getIdPelanggan(),
                    s.getNamaTeknisi() != null ? s.getNamaTeknisi() : s.getIdTeknisi(),
                    s.getJenisPerangkat() + " " + s.getMerk(),
                    s.getKerusakan(),
                    s.getStatus(),
                    s.getCatatan() != null ? s.getCatatan() : "-"
            });
        }

        // Sesuaikan tinggi baris agar teks tidak terpotong
        for (int row = 0; row < table.getRowCount(); row++) {

            int maxHeight = 32;

            for (int col : new int[]{4, 6}) {

                Object value = table.getValueAt(row, col);

                JTextArea area = new JTextArea(value == null ? "" : value.toString());

                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setFont(table.getFont());

                // WAJIB agar tinggi bisa dihitung dengan benar
                area.setSize(
                        table.getColumnModel().getColumn(col).getWidth(),
                        Short.MAX_VALUE
                );

                maxHeight = Math.max(
                        maxHeight,
                        area.getPreferredSize().height + 12
                );
            }

            table.setRowHeight(row, maxHeight);
        }
    }

    // Renderer untuk wrap text di sel tabel
    static class WrapCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JTextArea area = new JTextArea();

            area.setText(value == null ? "" : value.toString());

            area.setLineWrap(true);
            area.setWrapStyleWord(true);

            area.setFont(table.getFont());

            // Penting supaya Swing mengetahui batas lebar kolom
            area.setSize(
                    table.getColumnModel().getColumn(column).getWidth(),
                    Short.MAX_VALUE
            );

            area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            area.setOpaque(true);

            if (isSelected) {
                area.setBackground(table.getSelectionBackground());
                area.setForeground(table.getSelectionForeground());
            } else {
                area.setBackground(
                        row % 2 == 0
                                ? Color.WHITE
                                : new Color(248, 249, 252)
                );
                area.setForeground(table.getForeground());
            }

            return area;
        }
    }
}