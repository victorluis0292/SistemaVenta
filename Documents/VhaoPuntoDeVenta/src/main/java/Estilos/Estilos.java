// Archivo: Estilos.java
package Estilos;

import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.table.DefaultTableCellRenderer;
public class Estilos {

    public static void estiloBoton(JButton boton) {
        boton.setBackground(new Color(0, 153, 255));
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Arial", Font.BOLD, 14));
        boton.setFocusPainted(false);
    }

    public static void estiloTabla(JTable tabla) {
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(25);
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(0, 102, 204));
        header.setForeground(Color.WHITE);
    }
public static void estiloTablas(JTable tabla) {
    tabla.setFont(new Font("Arial", Font.PLAIN, 20));
    tabla.setRowHeight(25);

    JTableHeader header = tabla.getTableHeader();
    header.setFont(new Font("Arial", Font.BOLD, 17));
    header.setBackground(new Color(0, 102, 204));
    header.setForeground(Color.WHITE);

    // Usa renderizador respetando completamente la selección
    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            } else {
                c.setBackground((row % 2 == 0) ? Color.WHITE : new Color(230, 244, 255));
                c.setForeground(Color.BLACK);
            }

            return c;
        }
    };

    // Aplica el renderizador a cada columna explícitamente (evita problemas con nuevos modelos)
    for (int i = 0; i < tabla.getColumnModel().getColumnCount(); i++) {
        tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }

    tabla.setShowGrid(false);
    tabla.setIntercellSpacing(new java.awt.Dimension(0, 0));
    tabla.setFocusable(true);
    tabla.setRowSelectionAllowed(true);

    Component parent = tabla.getParent();
    if (parent != null && parent.getParent() instanceof JScrollPane) {
        JScrollPane scroll = (JScrollPane) parent.getParent();
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
    }
}


    public static void estiloEtiqueta(JLabel etiqueta) {
        etiqueta.setFont(new Font("Arial", Font.PLAIN, 14));
        etiqueta.setForeground(Color.DARK_GRAY);
    }
     public static void estiloEtiquetaGreen(JLabel etiqueta) {
           etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 18));
    etiqueta.setForeground(new Color(34, 139, 34)); // Verde moderno
    }
      public static void estiloEtiquetaRed(JLabel etiqueta) {
          etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 18));
          etiqueta.setForeground(new Color(220, 53, 69)); // Rojo moderno (tipo Bootstrap Danger)
    }
      
      public static void estiloEtiquetaBlue(JLabel etiqueta) {
    etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 18));
    etiqueta.setForeground(new Color(30, 144, 255)); // Azul moderno (DodgerBlue)
}

    public static void estiloTitulo(JLabel titulo) {
  //se implementa asi  ejemplo ->  Estilos.estiloEtiquetaGreen(lblVentas);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(0, 102, 204));
    }

    public static void estiloCampoTexto(JTextField campo) {
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setForeground(Color.BLACK);
    }
    
    
   public static void estiloBotonVerdeLima(JButton boton) {
    boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
    boton.setForeground(Color.WHITE); // Letra blanca
    boton.setBackground(new Color(144, 199, 78)); // Verde lima
    boton.setFocusPainted(false);
    boton.setBorderPainted(false);
}

     public static void estiloCalendario(JDateChooser calendario) {
    // Fuente general
    Font fuente = new Font("Segoe UI", Font.PLAIN, 14);
    calendario.setFont(fuente);

    // Editor de texto
    JTextField editor = (JTextField) calendario.getDateEditor().getUiComponent();
    editor.setFont(fuente);
    editor.setForeground(new Color(33, 37, 41));
    editor.setBackground(new Color(250, 250, 250));
    editor.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true), // borde redondeado
        BorderFactory.createEmptyBorder(5, 8, 5, 8) // padding interno
    ));

    // Fondo del calendario (por si afecta el botón o área)
    calendario.setBackground(new Color(245, 245, 245));
    calendario.setOpaque(false);

     
     
     }
         // Panel reutilizable con fondo blanco, curvatura y sombra suave
    public static class PanelConEstilo extends JPanel {

        public PanelConEstilo() {
            setOpaque(false); // Para permitir transparencia en la sombra
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 5, y = 5;
            int width = getWidth() - 10;
            int height = getHeight() - 10;

            // Sombra suave
            g2.setColor(new Color(0, 0, 0, 30)); // Sombra semi-transparente
            g2.fillRoundRect(x + 3, y + 3, width, height, 20, 20);

            // Fondo blanco con bordes redondeados
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, width, height, 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }
    }

}


    

