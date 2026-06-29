package com.servicekomputer.ui.dialog;

import com.servicekomputer.dao.TeknisiDAO;
import com.servicekomputer.model.Teknisi;
import com.servicekomputer.util.UIHelper;

import javax.swing.*;
import java.awt.*;

public class TeknisiDialog extends JDialog {

    private JTextField txtId, txtNama, txtKeahlian, txtNoHp;
    private boolean saved = false;
    private Teknisi teknisi;
    private TeknisiDAO dao;

    public TeknisiDialog(Frame parent, Teknisi teknisi) {
        super(parent, teknisi == null ? "Tambah Teknisi" : "Edit Teknisi", true);
        this.teknisi = teknisi;
        this.dao = new TeknisiDAO();
        initUI();
    }

    private void initUI() {
        setSize(440, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Color.WHITE);
        setLayout(null);

        JLabel lblTitle = new JLabel(teknisi == null ? "Tambah Teknisi Baru" : "Edit Data Teknisi");
        lblTitle.setBounds(24, 20, 380, 26);
        lblTitle.setFont(UIHelper.fontBold(16));
        lblTitle.setForeground(UIHelper.TEXT_DARK);

        int y = 65;

        JLabel lblId = new JLabel("ID Teknisi");
        lblId.setBounds(24, y, 200, 20);
        lblId.setFont(UIHelper.fontBold(12));
        lblId.setForeground(UIHelper.TEXT_DARK);
        txtId = new JTextField();
        txtId.setBounds(24, y + 24, 380, 38);
        UIHelper.styleTextField(txtId);
        txtId.setText(teknisi == null ? dao.generateId() : teknisi.getIdTeknisi());
        txtId.setEditable(false);
        txtId.setBackground(UIHelper.BG_LIGHT);

        y += 78;
        JLabel lblNama = new JLabel("Nama Teknisi *");
        lblNama.setBounds(24, y, 250, 20);
        lblNama.setFont(UIHelper.fontBold(12));
        lblNama.setForeground(UIHelper.TEXT_DARK);
        txtNama = new JTextField();
        txtNama.setBounds(24, y + 24, 380, 38);
        UIHelper.styleTextField(txtNama);
        if (teknisi != null) txtNama.setText(teknisi.getNamaTeknisi());

        y += 78;
        JLabel lblKeahlian = new JLabel("Keahlian *");
        lblKeahlian.setBounds(24, y, 250, 20);
        lblKeahlian.setFont(UIHelper.fontBold(12));
        lblKeahlian.setForeground(UIHelper.TEXT_DARK);
        txtKeahlian = new JTextField();
        txtKeahlian.setBounds(24, y + 24, 380, 38);
        UIHelper.styleTextField(txtKeahlian);
        if (teknisi != null) txtKeahlian.setText(teknisi.getKeahlian());

        y += 78;
        JLabel lblHp = new JLabel("No HP * (contoh: 08123456789 / +6281234567890)");
        lblHp.setBounds(24, y, 380, 20);
        lblHp.setFont(UIHelper.fontBold(12));
        lblHp.setForeground(UIHelper.TEXT_DARK);
        txtNoHp = new JTextField();
        txtNoHp.setBounds(24, y + 24, 380, 38);
        UIHelper.styleTextField(txtNoHp);
        UIHelper.restrictToPhoneChars(txtNoHp);
        if (teknisi != null) txtNoHp.setText(teknisi.getNoHp());

        y += 78;
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
        add(lblKeahlian); add(txtKeahlian);
        add(lblHp); add(txtNoHp);
        add(lblKeterangan);
        add(btnSimpan); add(btnBatal);

        setSize(440, y + 90);

        btnSimpan.addActionListener(e -> simpan());
        btnBatal.addActionListener(e -> dispose());
    }

    private void simpan() {
        String nama = txtNama.getText().trim();
        String keahlian = txtKeahlian.getText().trim();
        String noHp = txtNoHp.getText().trim();

        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNama.requestFocus();
            return;
        }
        if (keahlian.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keahlian tidak boleh kosong!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtKeahlian.requestFocus();
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

        Teknisi t = new Teknisi(txtId.getText(), nama, keahlian, noHp);
        boolean result = teknisi == null ? dao.insert(t) : dao.update(t);

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