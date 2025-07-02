// Archivo: Estilos.java
package Estilos;

import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.*;
import java.awt.geom.RoundRectangle2D;
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


    

