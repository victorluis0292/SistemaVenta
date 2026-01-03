package Vista;

import javax.swing.*;
import java.awt.*;

public class CashInBoxForm extends JFrame {
    public CurrencyField txtAmount;
    public JButton btnRegistrar;

    public CashInBoxForm() {
        initComponents();
        setLocationRelativeTo(null); // Centrar ventana
        setVisible(true); // Mostrar al iniciar
    }

    private void initComponents() {
        txtAmount = new CurrencyField();
        btnRegistrar = new JButton("Registrar dinero inicial en la caja");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLayout(new GridBagLayout());
        setResizable(false);

        btnRegistrar.setPreferredSize(new Dimension(250, 40));
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Importante para que se expanda horizontalmente
        add(txtAmount, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 20, 20);
        gbc.fill = GridBagConstraints.NONE; // El botón no necesita expandirse
        gbc.weightx = 0;
        add(btnRegistrar, gbc);

        btnRegistrar.addActionListener(e -> {
            double valor = txtAmount.getDoubleValue();
            JOptionPane.showMessageDialog(this, "Registrado: $" + String.format("%.2f", valor));
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CashInBoxForm::new);
    }
}
