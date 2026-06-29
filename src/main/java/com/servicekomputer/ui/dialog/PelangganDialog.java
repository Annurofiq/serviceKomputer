package com.servicekomputer.ui.dialog;

import com.servicekomputer.dao.PelangganDAO;
import com.servicekomputer.model.Pelanggan;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import java.awt.*;

public class PelangganDialog extends JDialog {

    private JTextField txtId, txtNama, txtNoHp;
    private JTextArea txtAlamat;
    private boolean saved = false;
    private Pelanggan pelanggan;
    private PelangganDAO dao;

    public PelangganDialog(Frame parent, Pelanggan pelanggan) {
        super(parent, pelanggan == null ? "Tambah Pelanggan" : "Edit Pelanggan", true);
        this.pelanggan = pelanggan;
        this.dao = new PelangganDAO();
        initUI();
    }

    private void initUI() {
        setSize(440, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel(pelanggan == null ? "Tambah Pelanggan Baru" : "Edit Data Pelanggan");
        lblTitle.setBounds(24, 20, 380, 26);
        lblTitle.setFont(UIHelper.fontBold(16));
        lblTitle.setForeground(UIHelper.TEXT_DARK);

        int y = 65;

        JLabel lblId = new JLabel("ID Pelanggan");
        lblId.setBounds(24, y, 200, 20);
        lblId.setFont(UIHelper.fontBold(12));
        lblId.setForeground(UIHelper.TEXT_DARK);
        txtId = new JTextField();
        txtId.setBounds(24, y + 24, 380, 38);
        UIHelper.styleTextField(txtId);
        txtId.setText(pelanggan == null ? dao.generateId() : pelanggan.getIdPelanggan());
        txtId.setEditable(false);
        txtId.setBackground(UIHelper.BG_LIGHT);

        y += 78;
        JLabel lblNama = new JLabel("Nama Pelanggan *");
        lblNama.setBounds(24, y, 250, 20);
        lblNama.setFont(UIHelper.fontBold(12));
        lblNama.setForeground(UIHelper.TEXT_DARK);
        txtNama = new JTextField();
        txtNama.setBounds(24, y + 24, 380, 38);
        UIHelper.styleTextField(txtNama);
        if (pelanggan != null) txtNama.setText(pelanggan.getNama());

        y += 78;
        JLabel lblHp = new JLabel("No HP * (contoh: 08123456789 / +6281234567890)");
        lblHp.setBounds(24, y, 380, 20);
        lblHp.setFont(UIHelper.fontBold(12));
        lblHp.setForeground(UIHelper.TEXT_DARK);
        txtNoHp = new JTextField();
        txtNoHp.setBounds(24, y + 24, 380, 38);
        UIHelper.styleTextField(txtNoHp);
        UIHelper.restrictToPhoneChars(txtNoHp);
        if (pelanggan != null) txtNoHp.setText(pelanggan.getNoHp());

        y += 78;
        JLabel lblAlamat = new JLabel("Alamat *");
        lblAlamat.setBounds(24, y, 250, 20);
        lblAlamat.setFont(UIHelper.fontBold(12));
        lblAlamat.setForeground(UIHelper.TEXT_DARK);
        txtAlamat = new JTextArea();
        UIHelper.styleTextArea(txtAlamat);
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        scrollAlamat.setBounds(24, y + 24, 380, 70);
        scrollAlamat.setBorder(BorderFactory.createLineBorder(UIHelper.BORDER_COLOR, 1, true));
        if (pelanggan != null && pelanggan.getAlamat() != null) txtAlamat.setText(pelanggan.getAlamat());

        y += 110;
        JLabel lblKeterangan = new JLabel("* Wajib diisi");
        lblKeterangan.setBounds(24, y, 380, 18);
        lblKeterangan.setFont(UIHelper.fontItalic(11));
        lblKeterangan.setForeground(UIHelper.TEXT_MUTED);

        y += 24;
        JButton btnSimpan = UIHelper.createButton("Simpan", UIHelper.PRIMARY);
        btnSimpan.setBounds(24, y, 184, 44);
        btnSimpan.setFont(UIHelper.fontBold(13));

        JButton btnBatal = UIHelper.createOutlineButton("Batal");
        btnBatal.setBounds(220, y, 184, 44);

        add(lblTitle); add(lblId); add(txtId);
        add(lblNama); add(txtNama);
        add(lblHp); add(txtNoHp);
        add(lblAlamat); add(scrollAlamat);
        add(lblKeterangan);
        add(btnSimpan); add(btnBatal);

        setSize(440, y + 90);

        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
    }

    private void simpan() {
        String nama = txtNama.getText().trim();
        String noHp = txtNoHp.getText().trim();
        String alamat = txtAlamat.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocus();
            return;
        }
        if (noHp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No HP tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNoHp.requestFocus();
            return;
        }
        if (!UIHelper.isValidPhoneNumber(noHp)) {
            JOptionPane.showMessageDialog(this,
                    "Format No HP tidak valid!\n\n" +
                            "Gunakan format:\n" +
                            "- 08123456789 (lokal Indonesia)\n" +
                            "- +6281234567890 (internasional)\n" +
                            "- +1 415 555 0132 (nomor luar negeri)\n\n" +
                            "Hanya boleh angka, +, -, spasi, dan tanda kurung.",
                    "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNoHp.requestFocus();
            return;
        }
        if (alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alamat tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtAlamat.requestFocus();
            return;
        }

        Pelanggan p = new Pelanggan(txtId.getText(), nama, noHp, alamat);
        boolean result = pelanggan == null ? dao.insert(p) : dao.update(p);

        if (result) {
            saved = true;
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data!");
        }
    }

    public boolean isSaved() { return saved; }
}