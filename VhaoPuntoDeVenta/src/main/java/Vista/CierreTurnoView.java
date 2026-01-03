package Vista;

import javax.swing.*;
import java.awt.*;

public class CierreTurnoView extends JDialog {
    public JLabel lblEsperado = new JLabel("$0.00");
    //public JComboBox<String> cmbCantidad = new JComboBox<>();
    public JTextField txtCantidad = new JTextField(); // ← aquí
    public JLabel lblDiferencia = new JLabel("$0.00");
    public JLabel lblMensaje = new JLabel("");
    public JButton btnCerrarTurno = new JButton("Cerrar Turno");
    public JButton btnCancelar = new JButton("Cancelar");

    public CierreTurnoView(JFrame parent) {
        super(parent, "Cierre de turno", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panel.add(new JLabel("Efectivo esperado en caja:"));
        panel.add(lblEsperado);

        panel.add(new JLabel("¿Cuánto efectivo hay en Caja?"));
        
        panel.add(txtCantidad);

        panel.add(new JLabel("Diferencia:"));
        panel.add(lblDiferencia);

        panel.add(new JLabel(""));
        panel.add(lblMensaje);

        JPanel botones = new JPanel();
        botones.add(btnCerrarTurno);
        botones.add(btnCancelar);

        add(panel, BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }
}
