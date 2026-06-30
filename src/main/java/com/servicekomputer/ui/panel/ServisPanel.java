package com.servicekomputer.ui.panel;

import com.servicekomputer.dao.ServisDAO;
import com.servicekomputer.model.Servis;
import com.servicekomputer.ui.dialog.ServisDialog;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ServisPanel extends JPanel {

    // Status yang WAJIB sudah tercapai (di Riwayat Servis) sebelum sebuah
    // data servis boleh dihapus dari Data Servis.
    private static final String STATUS_DIAMBIL_PELANGGAN = "Diambil Pelanggan";

    private JTable table;
    private DefaultTableModel tableModel;
    private ServisDAO dao;

    public ServisPanel() {
        dao = new ServisDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_LIGHT);

        add(UIHelper.createPageHeader("Data Servis", "Catat dan kelola pekerjaan servis"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(UIHelper.BG_LIGHT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(16, 28, 12, 28));

        JButton btnTambah = UIHelper.createButton("+ Tambah", UIHelper.PRIMARY);
        JButton btnEdit = UIHelper.createButton("Edit", UIHelper.INFO);
        JButton btnHapus = UIHelper.createButton("Hapus", UIHelper.DANGER);
        JButton btnRefresh = UIHelper.createOutlineButton("Refresh");

        toolbar.add(btnTambah);
        toolbar.add(btnEdit);
        // Tombol Hapus SELALU muncul untuk Admin maupun Teknisi.
        // Validasi status (harus "Diambil Pelanggan" di Riwayat) dilakukan saat diklik.
        toolbar.add(btnHapus);
        toolbar.add(btnRefresh);

        String[] columns = {"ID Servis", "Pelanggan", "Teknisi", "Perangkat", "Merk", "Kerusakan", "Biaya", "Tgl Masuk", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {90, 140, 120, 120, 100, 320, 120, 110, 170};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Wrap text kolom Kerusakan (index 5)
        table.getColumnModel().getColumn(5).setCellRenderer(new WrapCellRenderer());

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

        btnTambah.addActionListener(e -> {
            ServisDialog d = new ServisDialog(null, null);
            d.setVisible(true);
            if (d.isSaved()) loadData();
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!"); return; }
            String id = tableModel.getValueAt(row, 0).toString();
            Servis selected = dao.getById(id);
            if (selected != null) {
                ServisDialog d = new ServisDialog(null, selected);
                d.setVisible(true);
                if (d.isSaved()) loadData();
            }
        });

        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
                return;
            }

            String id = tableModel.getValueAt(row, 0).toString();
            // Status "Diambil Pelanggan" sekarang hanya disetel dari Riwayat Servis.
            // Karena ServisDAO mensinkronkan status ke tabel servis aktif juga saat
            // ditandai dari Riwayat, kita cukup cek kolom Status di tabel servis ini.
            String statusSekarang = tableModel.getValueAt(row, 8).toString();

            if (!STATUS_DIAMBIL_PELANGGAN.equalsIgnoreCase(statusSekarang)) {
                JOptionPane.showMessageDialog(this,
                        "Data servis " + id + " belum bisa dihapus.\n" +
                                "Status saat ini: " + statusSekarang + "\n\n" +
                                "Servis hanya bisa dihapus dari Data Servis jika sudah\n" +
                                "ditandai 'Diambil Pelanggan' di halaman Riwayat Servis.",
                        "Tidak Bisa Dihapus", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int c = JOptionPane.showConfirmDialog(this,
                    "Hapus data servis " + id + " dari Data Servis?\n" +
                            "Data ini akan tetap tersimpan di Riwayat Servis.",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (c != JOptionPane.YES_OPTION) return;

            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(this, "Data servis berhasil dihapus dari Data Servis!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data servis!");
            }
        });

        btnRefresh.addActionListener(e -> loadData());
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<Servis> list = dao.getAll();
        for (Servis s : list) {
            tableModel.addRow(new Object[]{
                    s.getIdServis(),
                    s.getNamaPelanggan() != null ? s.getNamaPelanggan() : s.getIdPelanggan(),
                    s.getNamaTeknisi() != null ? s.getNamaTeknisi() : s.getIdTeknisi(),
                    s.getJenisPerangkat(),
                    s.getMerk(),
                    s.getKerusakan(),
                    String.format("Rp %,.0f", s.getBiaya()),
                    s.getTanggalMasuk(),
                    s.getStatus()
            });
        }
        UIHelper.adjustRowHeightsForWrappedColumn(table, 5);
    }

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
