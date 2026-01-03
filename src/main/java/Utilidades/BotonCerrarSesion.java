package Utilidades;

import Vista.Login;
import javax.swing.*;
import java.awt.*;

public class BotonCerrarSesion {

    /**
     * Crea un botón "Cerrar Sesión" listo para agregar al panel lateral.
     * Se posiciona automáticamente al final del panel y llena todo el ancho del menú lateral,
     * dejando un pequeño espacio debajo del último botón (por ejemplo, BtnCorte).
     * @param parent JFrame padre, usado para cerrar ventana y abrir login
     * @param panel JPanel donde se agregará el botón
     * @return JButton configurado
     */
    public static JButton crearBoton(JFrame parent, JPanel panel) {
        JButton btn = new JButton("Cerrar Sesión");
        btn.setBackground(new Color(220, 53, 69)); // rojo estilo bootstrap
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.BOLD, 17));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    // Acción del botón con confirmación
btn.addActionListener(e -> {
    int opcion = JOptionPane.showConfirmDialog(
        parent, 
        "¿Desea cerrar sesión?", 
        "Confirmar cierre de sesión", 
        JOptionPane.YES_NO_OPTION, 
        JOptionPane.QUESTION_MESSAGE
    );

    if (opcion == JOptionPane.YES_OPTION) {
        System.out.println("🟢 [LOG] Sesión cerrada por usuario");
        parent.dispose();              // cerrar ventana actual
        new Login().setVisible(true);  // abrir ventana login
    } else {
        System.out.println("🟢 [LOG] Usuario canceló cierre de sesión");
        // no hace nada, se queda en la misma ventana
    }
});
        // Layout nulo para posición absoluta
        panel.setLayout(null);

        // Determinar posición vertical debajo del último botón visible
        int y = 10;           // margen superior inicial
        int alto = 40;        // alto del botón
        int espacioEntreBotones = 10; // espacio entre BtnCorte y Cerrar Sesión
        for (Component c : panel.getComponents()) {
            if (c.isVisible()) {
                int bottom = c.getY() + c.getHeight();
                if (bottom + espacioEntreBotones > y) {
                    y = bottom + espacioEntreBotones; // agregar espacio
                }
            }
        }

        // Ancho completo del panel
        int x = 0;
        int ancho = panel.getWidth();

        // Ajustar bounds del botón
        btn.setBounds(x, y, ancho, alto);

        // Agregar al panel
        panel.add(btn);
        panel.setComponentZOrder(btn, 0);
        panel.repaint();

        // Ajustar ancho automáticamente si se redimensiona el panel
        panel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                btn.setBounds(0, btn.getY(), panel.getWidth(), btn.getHeight());
            }
        });

        return btn;
    }
}
