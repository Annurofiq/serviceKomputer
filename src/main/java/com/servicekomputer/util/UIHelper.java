package com.servicekomputer.util;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * Helper terpusat untuk warna, font, dan styling komponen.
 * Tujuannya supaya semua panel & dialog punya tampilan yang konsisten.
 */
public class UIHelper {

    // ===== PALET WARNA =====
    public static final Color SIDEBAR_BG      = new Color(24, 26, 40);
    public static final Color SIDEBAR_HOVER    = new Color(79, 70, 229);
    public static final Color SIDEBAR_TEXT     = new Color(196, 199, 220);
    public static final Color SIDEBAR_SUBTEXT  = new Color(130, 134, 165);

    public static final Color BG_LIGHT         = new Color(244, 245, 250);
    public static final Color CARD_BG          = Color.WHITE;
    public static final Color BORDER_COLOR     = new Color(226, 228, 238);

    public static final Color PRIMARY          = new Color(79, 70, 229);
    public static final Color PRIMARY_HOVER    = new Color(99, 91, 245);
    public static final Color SUCCESS          = new Color(22, 163, 74);
    public static final Color DANGER           = new Color(220, 38, 38);
    public static final Color WARNING          = new Color(217, 119, 6);
    public static final Color INFO             = new Color(37, 99, 235);
    public static final Color NEUTRAL          = new Color(107, 114, 128);

    public static final Color TEXT_DARK        = new Color(24, 26, 40);
    public static final Color TEXT_MUTED       = new Color(107, 114, 128);

    public static final Color TABLE_HEADER_BG  = new Color(79, 70, 229);
    public static final Color TABLE_HEADER_FG  = Color.WHITE;
    public static final Color TABLE_ROW_ALT    = new Color(248, 248, 252);
    public static final Color TABLE_SELECTION  = new Color(224, 222, 252);
    public static final Color TABLE_GRID       = new Color(234, 235, 242);

    // ===== FONTS =====
    public static final String FONT_FAMILY = "Segoe UI";

    public static Font fontBold(int size) { return new Font(FONT_FAMILY, Font.BOLD, size); }
    public static Font fontPlain(int size) { return new Font(FONT_FAMILY, Font.PLAIN, size); }
    public static Font fontItalic(int size) { return new Font(FONT_FAMILY, Font.ITALIC, size); }

    // ===== BUTTON STYLING =====
    public static JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(fontBold(12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        Color hover = bgColor.brighter();
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(fontBold(12));
        btn.setBackground(Color.WHITE);
        btn.setForeground(TEXT_DARK);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(BG_LIGHT); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    // ===== TEXT FIELD STYLING =====
    public static void styleTextField(JTextField field) {
        field.setFont(fontPlain(13));
        field.setForeground(TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void styleTextArea(JTextArea area) {
        area.setFont(fontPlain(13));
        area.setForeground(TEXT_DARK);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(fontPlain(13));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
    }

    // ===== TABLE STYLING =====
    public static void styleTable(JTable table) {
        table.setFont(fontPlain(13));
        table.setRowHeight(34);
        table.setForeground(TEXT_DARK);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(TABLE_SELECTION);
        table.setSelectionForeground(TEXT_DARK);
        table.setGridColor(TABLE_GRID);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(fontBold(13));
        header.setDefaultRenderer(new HeaderCellRenderer());
        header.setPreferredSize(new Dimension(0, 40));
        header.setOpaque(true);
        // Background komponen header dibiarkan PUTIH (bukan ungu) -- warna ungu
        // hanya dirender per-kolom lewat HeaderCellRenderer. Ini mencegah sisa
        // ruang kosong di kanan tabel (saat total lebar kolom < lebar viewport)
        // ikut terlihat ungu seperti "kolom hantu".
        header.setBackground(Color.WHITE);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Latar belakang viewport tabel (area di luar baris data) dibuat putih,
        // supaya tidak ada warna aneh muncul jika tabel lebih pendek dari scroll pane.
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Object.class, new StripedTableCellRenderer());
    }

    /**
     * Pasang setelah mengatur scroll pane: memastikan area kosong di sebelah
     * kanan/bawah tabel (jika kolom data tidak memenuhi lebar viewport) berwarna
     * putih polos, bukan ikut menampilkan sisa garis/header ungu.
     */
    public static void fixEmptyViewportArea(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(Color.WHITE);
        if (scrollPane.getColumnHeader() != null) {
            scrollPane.getColumnHeader().setBackground(Color.WHITE);
        }
    }

    /** Renderer header tabel — dipaksa sama di setiap kolom agar tidak ada yang kosong/putih. */
    static class HeaderCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        public HeaderCellRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            lbl.setBackground(TABLE_HEADER_BG);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(fontBold(13));
            lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, TABLE_HEADER_BG.darker()),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)
            ));
            return lbl;
        }
    }

    /** Renderer agar baris tabel berseling warna dan teks selalu gelap & terbaca. */
    static class StripedTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setForeground(TEXT_DARK);
            if (isSelected) {
                c.setBackground(TABLE_SELECTION);
            } else {
                c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALT);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return c;
        }
    }

    // ===== PANEL HEADER (judul halaman) =====
    public static JPanel createPageHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(22, 28, 18, 28)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(fontBold(22));
        lblTitle.setForeground(TEXT_DARK);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(lblTitle);

        if (subtitle != null && !subtitle.isEmpty()) {
            header.add(Box.createVerticalStrut(4));
            JLabel lblSub = new JLabel(subtitle);
            lblSub.setFont(fontPlain(13));
            lblSub.setForeground(TEXT_MUTED);
            lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
            header.add(lblSub);
        }
        return header;
    }

    // ===== VALIDASI NO HP =====
    /**
     * Validasi format nomor HP Indonesia & internasional.
     * Diterima: hanya digit, spasi, tanda "+", "-", "(", ")".
     * Format valid contoh:
     *   08123456789       (lokal Indonesia, awalan 08)
     *   +6281234567890    (format internasional Indonesia)
     *   62812345678       (tanpa +)
     *   +1 415 555 0132   (nomor luar negeri)
     * Ditolak jika mengandung huruf atau simbol lain, atau terlalu pendek/panjang.
     */
    public static boolean isValidPhoneNumber(String input) {
        if (input == null) return false;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return false;

        // Hanya boleh digit, spasi, +, -, (, )
        if (!trimmed.matches("^[0-9+\\-() ]+$")) return false;

        // Hitung jumlah digit saja (tanpa simbol) untuk cek panjang wajar
        String digitsOnly = trimmed.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 8 || digitsOnly.length() > 15) return false;

        // Tanda "+" jika ada harus di awal saja
        int plusCount = 0;
        for (char c : trimmed.toCharArray()) if (c == '+') plusCount++;
        if (plusCount > 1) return false;
        if (plusCount == 1 && trimmed.charAt(0) != '+') return false;

        return true;
    }

    /** Filter realtime supaya field No HP hanya menerima karakter angka/+/-/spasi/kurung. */
    public static void restrictToPhoneChars(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            private String filter(String text) {
                return text == null ? "" : text.replaceAll("[^0-9+\\-() ]", "");
            }
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                super.insertString(fb, offset, filter(string), attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                super.replace(fb, offset, length, filter(text), attrs);
            }
        });
    }

    // ===== PENYESUAIAN TINGGI BARIS TABEL UNTUK KOLOM WRAP-TEXT =====
    /**
     * Hitung & terapkan tinggi baris yang pas untuk SATU kolom yang memakai wrap text
     * (misalnya kolom "Kerusakan"), supaya teks panjang tidak terpotong dan teks
     * pendek tidak membuat baris terlalu tinggi.
     *
     * Dipanggil setelah data tabel selesai diisi (akhir loadData()).
     * Menggunakan invokeLater supaya lebar kolom yang dipakai untuk menghitung
     * sudah benar-benar final (mengatasi race condition saat tabel baru dirender).
     */
    public static void adjustRowHeightsForWrappedColumn(JTable table, int wrapColumn) {
        adjustRowHeightsForWrappedColumns(table, new int[]{wrapColumn});
    }

    /**
     * Sama seperti di atas, tapi untuk BEBERAPA kolom wrap-text sekaligus
     * (misalnya kolom "Kerusakan" dan "Catatan" bersamaan) -- tinggi baris
     * akan mengikuti kolom yang butuh ruang paling tinggi.
     */
    public static void adjustRowHeightsForWrappedColumns(JTable table, int[] wrapColumns) {
        Runnable resize = () -> {
            int rowCount = table.getRowCount();
            int colCount = table.getColumnCount();
            for (int row = 0; row < rowCount; row++) {
                int maxHeight = 32;
                for (int col : wrapColumns) {
                    if (col < 0 || col >= colCount) continue;
                    Object value = table.getValueAt(row, col);
                    String text = value == null ? "" : value.toString();

                    int colWidth = table.getColumnModel().getColumn(col).getWidth();
                    if (colWidth <= 0) colWidth = table.getColumnModel().getColumn(col).getPreferredWidth();

                    JTextArea measurer = new JTextArea(text);
                    measurer.setLineWrap(true);
                    measurer.setWrapStyleWord(true);
                    measurer.setFont(table.getFont());
                    // Kurangi sedikit untuk padding kiri-kanan renderer (lihat WrapCellRenderer: 8+8)
                    measurer.setSize(Math.max(colWidth - 16, 10), Short.MAX_VALUE);

                    int height = measurer.getPreferredSize().height + 12; // + padding atas-bawah
                    maxHeight = Math.max(maxHeight, height);
                }
                if (row < table.getRowCount()) {
                    table.setRowHeight(row, maxHeight);
                }
            }
        };

        // Jalankan sekarang untuk estimasi awal, lalu sekali lagi setelah layout final.
        resize.run();
        SwingUtilities.invokeLater(resize);
    }

    // ===== LEBAR KOLOM OTOMATIS SESUAI ISI DATA =====
    /**
     * Hitung & terapkan lebar tiap kolom berdasarkan teks terlebar di kolom itu
     * (header maupun isi baris) -- mirip "AutoFit Column Width" di Excel/Sheets.
     * Kolom dengan teks pendek (misal "Biaya": "Rp 20.000.000") jadi sempit pas,
     * kolom dengan teks panjang melebar secukupnya sampai batas maxWidth, lalu
     * sisanya di-wrap (gunakan bersama WrapCellRenderer + adjustRowHeights...).
     *
     * @param table       tabel yang akan diatur
     * @param minWidths   lebar minimum tiap kolom (px), array sepanjang jumlah kolom
     * @param maxWidths   lebar maksimum tiap kolom (px); isi 0 / negatif berarti tak terbatas
     */
    public static void autoFitColumnWidths(JTable table, int[] minWidths, int[] maxWidths) {
        int colCount = table.getColumnCount();
        FontMetrics headerMetrics = table.getFontMetrics(fontBold(13));
        FontMetrics cellMetrics = table.getFontMetrics(table.getFont());

        for (int col = 0; col < colCount; col++) {
            String header = table.getColumnModel().getColumn(col).getHeaderValue() != null
                    ? table.getColumnModel().getColumn(col).getHeaderValue().toString() : "";
            int widest = headerMetrics.stringWidth(header) + 24; // padding header

            for (int row = 0; row < table.getRowCount(); row++) {
                Object value = table.getValueAt(row, col);
                String text = value == null ? "" : value.toString();
                int textWidth = cellMetrics.stringWidth(text) + 20; // padding sel kiri-kanan
                widest = Math.max(widest, textWidth);
            }

            int min = (minWidths != null && col < minWidths.length) ? minWidths[col] : 60;
            int max = (maxWidths != null && col < maxWidths.length) ? maxWidths[col] : 0;

            int finalWidth = Math.max(widest, min);
            if (max > 0) finalWidth = Math.min(finalWidth, max);

            table.getColumnModel().getColumn(col).setPreferredWidth(finalWidth);
        }
    }

    /**
     * Renderer wrap-text yang bisa dipakai ulang di panel mana pun (mis. kolom
     * "Alamat" di Data Pelanggan, "Kerusakan"/"Catatan" di Data Servis & Riwayat).
     * Sel akan menampilkan teks penuh dengan baris baru otomatis, bukan kepotong "...".
     */
    public static class WrapCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

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
                area.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ROW_ALT);
                area.setForeground(table.getForeground());
            }
            return area;
        }
    }
}