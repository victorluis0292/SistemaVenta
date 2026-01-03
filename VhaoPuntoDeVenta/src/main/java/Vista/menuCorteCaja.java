package Vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import Controlador.MovimientosCajaController;

public class menuCorteCaja {

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

    public static void agregarBotonesAJPanel(JPanel panel, String usuario) {
        // Crear botones
        JButton btn1 = new JButton("Ventas del Turno");
        ImageIcon iconoVentas = new ImageIcon(menuCorteCaja.class.getResource("/Img/icon-money inflow.png"));
        btn1.setIcon(iconoVentas);
        btn1.setHorizontalTextPosition(JButton.CENTER);
        btn1.setVerticalTextPosition(JButton.BOTTOM);

        JButton btn2 = new JButton("Operaciones");
        JButton btn3 = new JButton("Reporte General");
        JButton btn4 = new JButton("Arqueo De Caja");
        JButton btn5 = new JButton("Corte Cajero");
        JButton btn6 = new JButton("Movimientos de caja");

        // Aplicar estilos
        aplicarEstilo(btn1);
        aplicarEstilo(btn2);
        aplicarEstilo(btn3);
        aplicarEstilo(btn4);
        aplicarEstilo(btn5);
         aplicarEstilo(btn6);

        // Asignar acciones a los botones
        btn1.addActionListener(e -> {
            if (OpcVentasDelTurno.instance == null || !OpcVentasDelTurno.instance.isVisible()) {
                OpcVentasDelTurno.instance = new OpcVentasDelTurno();
                OpcVentasDelTurno.instance.setVisible(true);
            } else {
                OpcVentasDelTurno.instance.toFront();
            }
        });

        btn2.addActionListener(e -> {
            if (verMovimientosCaja.instance == null || !verMovimientosCaja.instance.isVisible()) {
                verMovimientosCaja.instance = new verMovimientosCaja();
                verMovimientosCaja.instance.setVisible(true);
            } else {
                verMovimientosCaja.instance.toFront();
            }
        });

        btn3.addActionListener(e -> {
            if (ReporteGeneral.instance == null || !ReporteGeneral.instance.isVisible()) {
                ReporteGeneral.instance = new ReporteGeneral();
                ReporteGeneral.instance.setVisible(true);
            } else {
                ReporteGeneral.instance.toFront();
            }
        });

        btn4.addActionListener(e -> {
            if (ArchedBox.instance == null || !ArchedBox.instance.isVisible()) {
                ArchedBox.instance = new ArchedBox();
                ArchedBox.instance.setVisible(true);
            } else {
                ArchedBox.instance.toFront();
            }
        });

  btn5.addActionListener(e -> {
  CorteCajeroView.getInstance(usuario, Sistema.getInstancia().getLoginView()).setVisible(true); // ✅ BIEN

});
  
 btn6.addActionListener(e -> {
    if (MovimientosCaja.instance == null || !MovimientosCaja.instance.isVisible()) {
        MovimientosCaja.instance = new MovimientosCaja();
        new MovimientosCajaController(MovimientosCaja.instance); // << AÑADE ESTA LÍNEA
        MovimientosCaja.instance.setVisible(true);
    } else {
        MovimientosCaja.instance.toFront();
    }
});
 

        // Layout sugerido
        panel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columnas, separación de 10px

        // Agregar botones al panel
        panel.add(btn1);
        panel.add(btn2);
        panel.add(btn3);
        panel.add(btn4);
        panel.add(btn5);
        panel.add(btn6); //Movimientos de caja

        // Refrescar el panel
        panel.revalidate();
        panel.repaint();
    }

}
