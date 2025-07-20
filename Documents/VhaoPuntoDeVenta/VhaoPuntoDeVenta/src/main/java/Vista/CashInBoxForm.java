package Vista;

import Modelo.CashInBox;
import Modelo.CashInBoxDAO;
import Modelo.TurnoDAO;
import Modelo.TurnoModel;
import Modelo.login;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class CashInBoxForm extends JFrame {
    private final login usuarioLogeado;
    public CurrencyField txtAmount;
    public JButton btnRegistrar;

    public CashInBoxForm(login usuarioLogeado) {
        this.usuarioLogeado = usuarioLogeado;

        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        txtAmount = new CurrencyField();
        btnRegistrar = new JButton("Registrar saldo inicial");

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
        gbc.weightx = 1.0;
        add(txtAmount, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 20, 20);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        add(btnRegistrar, gbc);
        // Escuchar la tecla Enter en el campo de monto
txtAmount.addKeyListener(new java.awt.event.KeyAdapter() {
    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnRegistrar.doClick(); // Ejecuta el botón
        }
    }
});
// 🔻 Elimina todo esto de initComponents()
btnRegistrar.addActionListener(e -> {
    double valor = getAmount();
    if (valor > 0) {
        // ❌ TODO esto se elimina
    } else {
        JOptionPane.showMessageDialog(this, "Ingrese un valor válido mayor a 0.");
    }
});
       
    }

    public double getAmount() {
        return txtAmount.getDoubleValue();
    }

  
}
