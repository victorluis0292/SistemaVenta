package Vista;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyField extends JTextField {
    private StringBuilder digits = new StringBuilder("0");
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
    private boolean isUpdating = false;

    public CurrencyField() {
        super();
        setColumns(10); // Ayuda a Swing a estimar el tamaño
        setHorizontalAlignment(JTextField.LEFT);
        setFont(new Font("Segoe UI", Font.BOLD, 24));
        setPreferredSize(new Dimension(250, 65));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        updateTextDirectly();

        SwingUtilities.invokeLater(() -> {
            ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    if (string != null && string.matches("\\d+")) {
                        digits.append(string);
                        updateText(fb);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (text != null && text.matches("\\d*")) {
                        int start = Math.max(0, digits.length() - length);
                        if (start + length > digits.length()) {
                            length = digits.length() - start;
                        }
                        digits.replace(start, start + length, text);
                        updateText(fb);
                    }
                }

                @Override
                public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                    if (digits.length() > 0) {
                        int removeStart = Math.max(0, digits.length() - length);
                        int removeEnd = digits.length();
                        digits.delete(removeStart, removeEnd);
                        if (digits.length() == 0) {
                            digits.append("0");
                        }
                        updateText(fb);
                    }
                }

                private void updateText(FilterBypass fb) throws BadLocationException {
                    if (isUpdating) return;
                    isUpdating = true;
                    try {
                        double val = Double.parseDouble(digits.toString()) / 100.0;
                        String formatted = currencyFormat.format(val);
                        fb.replace(0, fb.getDocument().getLength(), formatted, null);
                    } finally {
                        isUpdating = false;
                    }
                }
            });
        });
    }

    private void updateTextDirectly() {
        double val = Double.parseDouble(digits.toString()) / 100.0;
        String formatted = currencyFormat.format(val);
        setText(formatted);
    }

    public double getDoubleValue() {
        if (digits.length() == 0) return 0.0;
        return Double.parseDouble(digits.toString()) / 100.0;
    }
}
