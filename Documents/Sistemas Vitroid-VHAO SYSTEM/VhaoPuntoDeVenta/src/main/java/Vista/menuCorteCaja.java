package Vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

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
   public static void agregarBotonesAJPanel(JPanel panel) {
    // Crear botones
    JButton btn1 = new JButton("Ventas del día");
ImageIcon iconoVentas = new ImageIcon(menuCorteCaja.class.getResource("/Img/icon-money inflow.png"));
btn1.setIcon(iconoVentas);
btn1.setHorizontalTextPosition(JButton.CENTER);
btn1.setVerticalTextPosition(JButton.BOTTOM);
    JButton btn2 = new JButton("Operaciones");
    JButton btn3 = new JButton("Reporte General");
    JButton btn4 = new JButton("Arqueo De Caja");

    // Aplicar estilos
    aplicarEstilo(btn1);
    aplicarEstilo(btn2);
    aplicarEstilo(btn3);
    aplicarEstilo(btn4);

    // Asignar acciones a los botones
   btn1.addActionListener(e -> {
    if (OpcVentasDelDia.instance == null || !OpcVentasDelDia.instance.isVisible()) {
        OpcVentasDelDia.instance = new OpcVentasDelDia();
        OpcVentasDelDia.instance.setVisible(true);
    } else {
        OpcVentasDelDia.instance.toFront(); // Lleva la ventana al frente si ya está abierta
    }
});
    
       btn2.addActionListener(e -> {
    if (verMovimientosCaja.instance == null || !verMovimientosCaja.instance.isVisible()) {
        verMovimientosCaja.instance = new verMovimientosCaja();
        verMovimientosCaja.instance.setVisible(true);
    } else {
        verMovimientosCaja.instance.toFront(); // Lleva la ventana al frente si ya está abierta
    }
});


   
       btn3.addActionListener(e -> {
    if (ReporteGeneral.instance == null || !ReporteGeneral.instance.isVisible()) {
        ReporteGeneral.instance = new ReporteGeneral();
        ReporteGeneral.instance.setVisible(true);
    } else {
        ReporteGeneral.instance.toFront(); // Lleva la ventana al frente si ya está abierta
    }
});

  
    
       btn4.addActionListener(e -> {
    if (ArchedBox.instance == null || !ArchedBox.instance.isVisible()) {
        ArchedBox.instance = new ArchedBox();
        ArchedBox.instance.setVisible(true);
    } else {
        ArchedBox.instance.toFront(); // Lleva la ventana al frente si ya está abierta
    }
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
