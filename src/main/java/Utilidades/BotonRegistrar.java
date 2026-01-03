package Utilidades;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class BotonRegistrar {

    public static JButton crearBoton() {
        JButton btnRegistrar = new JButton("Registrar");

        // Estilos
        btnRegistrar.setBackground(new Color(0, 153, 0)); // Verde
        btnRegistrar.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Acción (ejemplo)
        btnRegistrar.addActionListener(e -> {
           // JOptionPane.showMessageDialog(null, "Aquí abrimos la ventana de registro.");
            // new VentanaRegistro().setVisible(true);  <-- si luego quieres crearla
        });

        return btnRegistrar;
    }
}
