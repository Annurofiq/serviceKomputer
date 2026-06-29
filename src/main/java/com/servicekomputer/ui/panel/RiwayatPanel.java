package com.servicekomputer.ui.panel;

import com.servicekomputer.dao.RiwayatServisDAO;
import com.servicekomputer.model.RiwayatServis;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Riwayat Servis sekarang independen dari tabel "servis" -- datanya
 * berasal dari tabel "riwayat_servis" yang diisi otomatis saat sebuah
 * data servis dihapus dari Data Servis. Hapus di sini bersifat permanen
 * dan tidak berhubungan dengan tabel servis aktif.
 */
public class RiwayatPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private RiwayatServisDAO dao;

    public RiwayatPanel() {
        dao = new RiwayatServisDAO();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_LIGHT);

        add(UIHelper.createPageHeader("Riwayat Servis", "Histori servis yang telah selesai dan dihapus dari Data Servis"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(UIHelper.BG_LIGHT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(16, 28, 12, 28));

        JButton btnRefresh = UIHelper.createOutlineButton("Refresh");
        JButton btnHapus = UIHelper.createButton("Hapus", UIHelper.DANGER);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(UIHelper.fontPlain(12));
        lblFilter.setForeground(UIHelper.TEXT_MUTED);

        String[] statusList = {"Semua", "Menunggu", "Sedang Diperbaiki", "Menunggu Sparepart", "Selesai", "Diambil Pelanggan"};
        JComboBox<String> cbFilter = new JComboBox<>(statusList);
        UIHelper.styleComboBox(cbFilter);
        cbFilter.setPreferredSize(new Dimension(170, 34));

        toolbar.add(btnRefresh);
        toolbar.add(btnHapus);
        toolbar.add(Box.createHorizontalStrut(12));
        toolbar.add(lblFilter);
        toolbar.add(cbFilter);

        String[] columns = {"ID Servis", "Tgl Masuk", "Pelanggan", "Teknisi", "Perangkat", "Merk", "Kerusakan", "Biaya", "Status", "Tgl Selesai", "Catatan"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UIHelper.styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {90, 100, 130, 110, 120, 100, 250, 120, 180, 120, 360};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Wrap text untuk kolom Kerusakan (6) dan Catatan (10)
        table.getColumnModel().getColumn(6).setCellRenderer(new WrapCellRenderer());
        table.getColumnModel().getColumn(10).setCellRenderer(new WrapCellRenderer());

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

        btnRefresh.addActionListener(e -> {
            cbFilter.setSelectedIndex(0);
            loadData();
        });

        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih data riwayat yang ingin dihapus!");
                return;
            }
            String id = tableModel.getValueAt(row, 0).toString();
            int c = JOptionPane.showConfirmDialog(this,
                    "Hapus riwayat servis " + id + " secara permanen?\nData yang dihapus tidak dapat dikembalikan.",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                if (dao.delete(id)) {
                    JOptionPane.showMessageDialog(this, "Riwayat servis berhasil dihapus!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data!");
                }
            }
        });

        cbFilter.addActionListener(e -> filterData(cbFilter.getSelectedItem().toString()));
    }

    public void loadData() {
        tableModel.setRowCount(0);
        List<RiwayatServis> list = dao.getAll();
        for (RiwayatServis r : list) {
            tableModel.addRow(buildRow(r));
        }
        adjustRowHeights();
    }

    private void filterData(String status) {
        tableModel.setRowCount(0);
        List<RiwayatServis> list = dao.getAll();
        for (RiwayatServis r : list) {
            if (status.equals("Semua") || r.getStatus().equals(status)) {
                tableModel.addRow(buildRow(r));
            }
        }
        adjustRowHeights();
    }

    private void adjustRowHeights() {
        for (int row = 0; row < table.getRowCount(); row++) {
            int maxHeight = 32;
            for (int col : new int[]{6, 10}) {
                Object value = table.getValueAt(row, col);
                JTextArea area = new JTextArea(value == null ? "" : value.toString());
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setFont(table.getFont());
                area.setSize(
                        table.getColumnModel().getColumn(col).getWidth(),
                        Short.MAX_VALUE
                );
                maxHeight = Math.max(maxHeight, area.getPreferredSize().height + 12);
            }
            table.setRowHeight(row, maxHeight);
        }
    }

    private Object[] buildRow(RiwayatServis r) {
        String tglSelesai = r.getTanggalSelesai();
        if (tglSelesai == null || tglSelesai.trim().isEmpty()) {
            tglSelesai = "-";
        }
        return new Object[]{
                r.getIdServis(),
                r.getTanggalMasuk(),
                r.getNamaPelanggan() != null ? r.getNamaPelanggan() : r.getIdPelanggan(),
                r.getNamaTeknisi() != null ? r.getNamaTeknisi() : r.getIdTeknisi(),
                r.getJenisPerangkat(),
                r.getMerk(),
                r.getKerusakan(),
                String.format("Rp %,.0f", r.getBiaya()),
                r.getStatus(),
                tglSelesai,
                r.getCatatan() != null ? r.getCatatan() : "-"
        };
    }

    static class WrapCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

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
                area.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 252));
                area.setForeground(table.getForeground());
            }

            return area;
        }
    }
}
