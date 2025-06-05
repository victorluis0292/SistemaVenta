package Vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class EstilosBotones {

    public static void aplicarEstilo(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(45, 137, 239));
        boton.setOpaque(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(30, 110, 220));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(45, 137, 239));
            }
        });
    }
   public static void agregarBotonesAJPanel(JPanel panel) {
    // Crear botones
    JButton btn1 = new JButton("Ventas del día");
    JButton btn2 = new JButton("Movimientos de caja  Ingreso | Egreso");
    JButton btn3 = new JButton("Reporte General");
    JButton btn4 = new JButton("Opción 4");

    // Aplicar estilos
    aplicarEstilo(btn1);
    aplicarEstilo(btn2);
    aplicarEstilo(btn3);
    aplicarEstilo(btn4);

    // Asignar acciones a los botones
    btn1.addActionListener(e -> {
        // 👉 Abrir formulario de ventas del día
        VentasDiaForm ventana = new VentasDiaForm();
        ventana.setVisible(true);
    });

    btn2.addActionListener(e -> {
        // 👉 Abrir formulario de retiro de efectivo
       verMovimientosCaja ventana = new verMovimientosCaja();
        ventana.setVisible(true);
     
    });

    btn3.addActionListener(e -> {
         // 👉 Abrir formulario de retiro de efectivo
       ReporteGeneral ventana = new ReporteGeneral();
        ventana.setVisible(true);
    });

    btn4.addActionListener(e -> {
        JOptionPane.showMessageDialog(panel, "Opción 4 aún no disponible.");
    });

    // Layout sugerido
    panel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columnas, separación de 10px

    // Agregar botones al panel
    panel.add(btn1);
    panel.add(btn2);
    panel.add(btn3);
    panel.add(btn4);

    // Refrescar
    panel.revalidate();
    panel.repaint();
}

}
