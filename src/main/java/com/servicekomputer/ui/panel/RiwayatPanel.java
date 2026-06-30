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
 * Riwayat Servis independen dari Data Servis: setiap servis yang ditambah,
 * diedit, atau status-nya diupdate di Data Servis OTOMATIS tersinkron di sini
 * secara real-time (lihat ServisDAO.syncToRiwayat). Riwayat tidak perlu
 * menunggu data dihapus dari Data Servis untuk muncul di sini.
 *
 * Menandai "Diambil Pelanggan" dilakukan DI SINI (bukan di Status Perbaikan),
 * dan hapus di sini bersifat permanen serta tidak memengaruhi Data Servis.
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

        add(UIHelper.createPageHeader("Riwayat Servis", "Semua histori servis, tersinkron otomatis dari Data Servis"), BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(UIHelper.BG_LIGHT);
        toolbar.setBorder(BorderFactory.createEmptyBorder(16, 28, 12, 28));

        JButton btnRefresh = UIHelper.createOutlineButton("Refresh");
        JButton btnSelesai = UIHelper.createButton("Tandai Diambil Pelanggan", UIHelper.SUCCESS);
        JButton btnHapus = UIHelper.createButton("Hapus", UIHelper.DANGER);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(UIHelper.fontPlain(12));
        lblFilter.setForeground(UIHelper.TEXT_MUTED);

        String[] statusList = {"Semua", "Menunggu", "Sedang Diperbaiki", "Menunggu Sparepart", "Selesai", "Diambil Pelanggan"};
        JComboBox<String> cbFilter = new JComboBox<>(statusList);
        UIHelper.styleComboBox(cbFilter);
        cbFilter.setPreferredSize(new Dimension(170, 34));

        toolbar.add(btnRefresh);
        toolbar.add(btnSelesai);
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

        btnSelesai.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih servis yang ingin ditandai diambil!");
                return;
            }
            String id = tableModel.getValueAt(row, 0).toString();
            String statusSekarang = tableModel.getValueAt(row, 8).toString();

            if ("Diambil Pelanggan".equalsIgnoreCase(statusSekarang)) {
                JOptionPane.showMessageDialog(this, "Servis " + id + " sudah berstatus 'Diambil Pelanggan'.");
                return;
            }
            if (!"Selesai".equalsIgnoreCase(statusSekarang)) {
                JOptionPane.showMessageDialog(this,
                        "Servis ini belum bisa ditandai 'Diambil Pelanggan'.\n" +
                                "Status saat ini: " + statusSekarang + "\n\n" +
                                "Perbaikan harus berstatus 'Selesai' terlebih dahulu\n" +
                                "(ubah lewat halaman Status Perbaikan).",
                        "Belum Bisa Diambil", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int c = JOptionPane.showConfirmDialog(this,
                    "Tandai servis " + id + " sebagai 'Diambil Pelanggan'?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                if (dao.updateStatusDiambil(id, "Perangkat telah diambil oleh pelanggan")) {
                    JOptionPane.showMessageDialog(this, "Status berhasil diupdate!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal update status!");
                }
            }
        });

        btnHapus.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Pilih data riwayat yang ingin dihapus!");
                return;
            }
            String id = tableModel.getValueAt(row, 0).toString();
            String statusSekarang = tableModel.getValueAt(row, 8).toString();

            if (!"Diambil Pelanggan".equalsIgnoreCase(statusSekarang)) {
                JOptionPane.showMessageDialog(this,
                        "Riwayat servis " + id + " belum bisa dihapus.\n" +
                                "Status saat ini: " + statusSekarang + "\n\n" +
                                "Riwayat hanya bisa dihapus jika servis sudah berstatus\n" +
                                "'Diambil Pelanggan' (selesai sepenuhnya).",
                        "Belum Bisa Dihapus", JOptionPane.WARNING_MESSAGE);
                return;
            }

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
        terapkanUkuranKolomDanBaris();
    }

    private void filterData(String status) {
        tableModel.setRowCount(0);
        List<RiwayatServis> list = dao.getAll();
        for (RiwayatServis r : list) {
            if (status.equals("Semua") || r.getStatus().equals(status)) {
                tableModel.addRow(buildRow(r));
            }
        }
        terapkanUkuranKolomDanBaris();
    }

    /**
     * Lebar kolom seperti Biaya/Status/Tgl menyesuaikan isi data (pendek = sempit).
     * Kolom Kerusakan dan Catatan dibatasi maksimal lalu sisanya wrap ke bawah.
     */
    private void terapkanUkuranKolomDanBaris() {
        int[] minWidths = {80, 95, 110, 95, 95, 85, 150, 90, 130, 95, 180};
        int[] maxWidths = {100, 110, 180, 150, 160, 140, 320, 130, 180, 110, 380};
        UIHelper.autoFitColumnWidths(table, minWidths, maxWidths);
        UIHelper.adjustRowHeightsForWrappedColumns(table, new int[]{6, 10});
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
