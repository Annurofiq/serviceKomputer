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
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new StripedTableCellRenderer());
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
}