package Utilidades;

import javax.swing.*;
import java.awt.*;

/**
 * LoaderUpdateSystemPDV
 * Ventana modal que muestra un mensaje y una barra de progreso indeterminada
 * durante el proceso de actualización o verificación del sistema.
 */
public class LoaderUpdateSystemPDV extends JDialog {

    private final JLabel lblMensaje;
    private final JProgressBar barra;

    /**
     * Constructor principal del loader
     *
     * @param parent  Ventana padre (puede ser null si no se necesita bloquear una)
     * @param mensaje Mensaje inicial que se mostrará en el loader
     */
    public LoaderUpdateSystemPDV(Frame parent, String mensaje) {
        super(parent, "Actualizando sistema", true);

        // 🔹 Configuración básica del loader
        setUndecorated(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // 🔹 Etiqueta del mensaje
        lblMensaje = new JLabel(mensaje, SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblMensaje.setForeground(new Color(60, 60, 60));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        // 🔹 Barra de carga animada
        barra = new JProgressBar();
        barra.setIndeterminate(true);
        barra.setBorderPainted(false);
        barra.setBackground(Color.WHITE);
        barra.setPreferredSize(new Dimension(300, 10));

        // 🔹 Panel contenedor para sombra y estilo limpio
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));
        panel.add(lblMensaje, BorderLayout.CENTER);
        panel.add(barra, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);

        // 🔹 Ajuste de tamaño y posición
        pack();
        setSize(320, 130);
        setLocationRelativeTo(parent);
    }

    public LoaderUpdateSystemPDV() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Actualiza el mensaje mostrado en el loader.
     */
    public void setMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> lblMensaje.setText(mensaje));
    }

    /**
     * Muestra el loader de forma segura desde el hilo de Swing.
     */
    public void mostrar() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    /**
     * Cierra el loader de forma segura desde el hilo de Swing.
     */
    public void cerrar() {
        SwingUtilities.invokeLater(this::dispose);
    }
}
