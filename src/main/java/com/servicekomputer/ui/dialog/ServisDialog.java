package com.servicekomputer.ui.dialog;

import com.servicekomputer.dao.PelangganDAO;
import com.servicekomputer.dao.ServisDAO;
import com.servicekomputer.dao.TeknisiDAO;
import com.servicekomputer.model.Pelanggan;
import com.servicekomputer.model.Servis;
import com.servicekomputer.model.Teknisi;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ServisDialog extends JDialog {

    private JTextField txtId, txtMerk, txtBiaya, txtTanggalMasuk;
    private JTextArea txtKerusakan, txtCatatan;
    private JComboBox<Pelanggan> cbPelanggan;
    private JComboBox<Teknisi> cbTeknisi;
    private JComboBox<String> cbJenisPerangkat;
    private boolean saved = false;
    private Servis servis;
    private ServisDAO dao;

    public ServisDialog(Frame parent, Servis servis) {
        super(parent, servis == null ? "Tambah Servis Baru" : "Edit Data Servis", true);
        this.servis = servis;
        this.dao = new ServisDAO();
        initUI();
    }

    private void initUI() {
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel(servis == null ? "Tambah Servis Baru" : "Edit Data Servis");
        lblTitle.setBounds(24, 18, 440, 26);
        lblTitle.setFont(UIHelper.fontBold(16));
        lblTitle.setForeground(UIHelper.TEXT_DARK);

        int y = 58;

        addLabel("ID Servis", 24, y);
        addLabel("Tanggal Masuk", 260, y);
        y += 22;
        txtId = new JTextField();
        txtId.setBounds(24, y, 215, 38);
        UIHelper.styleTextField(txtId);
        txtId.setText(servis == null ? dao.generateId() : servis.getIdServis());
        txtId.setEditable(false);
        txtId.setBackground(UIHelper.BG_LIGHT);

        txtTanggalMasuk = new JTextField();
        txtTanggalMasuk.setBounds(260, y, 215, 38);
        UIHelper.styleTextField(txtTanggalMasuk);
        txtTanggalMasuk.setText(servis == null ? LocalDate.now().toString() : servis.getTanggalMasuk());

        y += 56;
        addLabel("Pelanggan", 24, y);
        addLabel("Teknisi", 260, y);
        y += 22;
        cbPelanggan = new JComboBox<>();
        cbPelanggan.setBounds(24, y, 215, 38);
        UIHelper.styleComboBox(cbPelanggan);
        loadPelanggan();

        cbTeknisi = new JComboBox<>();
        cbTeknisi.setBounds(260, y, 215, 38);
        UIHelper.styleComboBox(cbTeknisi);
        loadTeknisi();

        y += 56;
        addLabel("Jenis Perangkat", 24, y);
        addLabel("Merk *", 260, y);
        y += 22;
        String[] jenis = {"Laptop", "Komputer/PC", "Handphone", "Tablet", "Printer", "Lainnya"};
        cbJenisPerangkat = new JComboBox<>(jenis);
        cbJenisPerangkat.setBounds(24, y, 215, 38);
        UIHelper.styleComboBox(cbJenisPerangkat);
        if (servis != null) cbJenisPerangkat.setSelectedItem(servis.getJenisPerangkat());

        txtMerk = new JTextField();
        txtMerk.setBounds(260, y, 215, 38);
        UIHelper.styleTextField(txtMerk);
        if (servis != null) txtMerk.setText(servis.getMerk());

        y += 56;
        addLabel("Deskripsi Kerusakan *", 24, y);
        y += 22;
        txtKerusakan = new JTextArea();
        UIHelper.styleTextArea(txtKerusakan);
        JScrollPane scrollK = new JScrollPane(txtKerusakan);
        scrollK.setBounds(24, y, 451, 65);
        scrollK.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR, 1, true));
        if (servis != null) txtKerusakan.setText(servis.getKerusakan());

        y += 83;
        addLabel("Estimasi Biaya (Rp)", 24, y);
        if (servis != null) {
            addLabel("Status Saat Ini: " + servis.getStatus(), 260, y);
        }
        y += 22;
        txtBiaya = new JTextField();
        txtBiaya.setBounds(24, y, 215, 38);
        UIHelper.styleTextField(txtBiaya);
        txtBiaya.setText(servis != null ? String.valueOf((int) servis.getBiaya()) : "0");

        if (servis != null) {
            JLabel lblHintStatus = new JLabel("Ubah status lewat halaman Status Perbaikan");
            lblHintStatus.setBounds(260, y, 215, 38);
            lblHintStatus.setFont(UIHelper.fontItalic(11));
            lblHintStatus.setForeground(UIHelper.TEXT_MUTED);
            add(lblHintStatus);
        }

        y += 56;
        addLabel("Catatan Tambahan", 24, y);
        y += 22;
        txtCatatan = new JTextArea();
        UIHelper.styleTextArea(txtCatatan);
        JScrollPane scrollC = new JScrollPane(txtCatatan);
        scrollC.setBounds(24, y, 451, 60);
        scrollC.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR, 1, true));
        if (servis != null && servis.getCatatan() != null) txtCatatan.setText(servis.getCatatan());

        y += 78;
        JButton btnSimpan = UIHelper.createButton("Simpan", UIHelper.PRIMARY);
        btnSimpan.setBounds(24, y, 215, 44);
        btnSimpan.setFont(UIHelper.fontBold(13));

        JButton btnBatal = UIHelper.createOutlineButton("Batal");
        btnBatal.setBounds(260, y, 215, 44);

        add(lblTitle);
        add(txtId); add(txtTanggalMasuk);
        add(cbPelanggan); add(cbTeknisi);
        add(cbJenisPerangkat); add(txtMerk);
        add(scrollK); add(txtBiaya);
        add(scrollC);
        add(btnSimpan); add(btnBatal);

        setSize(500, y + 90);

        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
    }

    private void loadPelanggan() {
        PelangganDAO pDao = new PelangganDAO();
        List<Pelanggan> list = pDao.getAll();
        for (Pelanggan p : list) cbPelanggan.addItem(p);
        if (servis != null) {
            for (int i = 0; i < cbPelanggan.getItemCount(); i++) {
                if (cbPelanggan.getItemAt(i).getIdPelanggan().equals(servis.getIdPelanggan())) {
                    cbPelanggan.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void loadTeknisi() {
        TeknisiDAO tDao = new TeknisiDAO();
        List<Teknisi> list = tDao.getAll();
        for (Teknisi t : list) cbTeknisi.addItem(t);
        if (servis != null) {
            for (int i = 0; i < cbTeknisi.getItemCount(); i++) {
                if (cbTeknisi.getItemAt(i).getIdTeknisi().equals(servis.getIdTeknisi())) {
                    cbTeknisi.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void simpan() {
        if (cbPelanggan.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data pelanggan! Tambah pelanggan dulu.");
            return;
        }
        if (cbTeknisi.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data teknisi! Tambah teknisi dulu.");
            return;
        }
        if (txtMerk.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Merk tidak boleh kosong!");
            return;
        }
        if (txtKerusakan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Deskripsi kerusakan tidak boleh kosong!");
            return;
        }

        Pelanggan p = (Pelanggan) cbPelanggan.getSelectedItem();
        Teknisi t = (Teknisi) cbTeknisi.getSelectedItem();

        double biaya = 0;
        try { biaya = Double.parseDouble(txtBiaya.getText().trim()); }
        catch (Exception ignored) {}

        // Status TIDAK bisa diubah dari dialog ini.
        // Tambah baru -> selalu "Menunggu". Edit -> pertahankan status yang sudah ada.
        String status = (servis == null) ? "Menunggu" : servis.getStatus();
        String tanggalSelesai = (servis == null) ? null : servis.getTanggalSelesai();

        Servis s = new Servis(
                txtId.getText(),
                p.getIdPelanggan(),
                t.getIdTeknisi(),
                cbJenisPerangkat.getSelectedItem().toString(),
                txtMerk.getText().trim(),
                txtKerusakan.getText().trim(),
                biaya,
                txtTanggalMasuk.getText().trim(),
                tanggalSelesai,
                status,
                txtCatatan.getText().trim()
        );

        boolean result = servis == null ? dao.insert(s) : dao.update(s);

        if (result) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Data servis berhasil disimpan!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data servis!");
        }
    }

    private void addLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text);
        lbl.setBounds(x, y, 215, 20);
        lbl.setFont(UIHelper.fontBold(12));
        lbl.setForeground(UIHelper.TEXT_DARK);
        add(lbl);
    }

    public boolean isSaved() { return saved; }
}