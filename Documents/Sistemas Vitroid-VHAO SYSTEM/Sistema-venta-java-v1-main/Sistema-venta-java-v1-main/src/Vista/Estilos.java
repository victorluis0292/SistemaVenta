// Archivo: Estilos.java
package Estilos;

import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.table.JTableHeader;

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

    public static void estiloTitulo(JLabel titulo) {
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setForeground(new Color(0, 102, 204));
    }

    public static void estiloCampoTexto(JTextField campo) {
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setForeground(Color.BLACK);
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
}

