package Vista;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class FormRangoFechasDelCorte extends JDialog {

    private final JDateChooser fechaInicioChooser;
    private final JDateChooser fechaFinChooser;

    public interface RangoFechasListener {
        void onFechasSeleccionadas(Date inicio, Date fin);
    }

    public FormRangoFechasDelCorte(JFrame parent, RangoFechasListener listener) {
        super(parent, "Seleccionar Rango de Fechas", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());

        // Fecha choosers
        fechaInicioChooser = new JDateChooser();
        fechaFinChooser = new JDateChooser();

        // Fuente y color
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Color labelColor = new Color(50, 50, 50);

        // Panel central con campos
        JPanel panelFechas = new JPanel();
        panelFechas.setLayout(new BoxLayout(panelFechas, BoxLayout.Y_AXIS));
        panelFechas.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        panelFechas.setBackground(new Color(245, 245, 245));

        JLabel lblInicio = new JLabel("📅 Fecha inicio:");
        lblInicio.setFont(labelFont);
        lblInicio.setForeground(labelColor);
        lblInicio.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFin = new JLabel("📅 Fecha fin:");
        lblFin.setFont(labelFont);
        lblFin.setForeground(labelColor);
        lblFin.setAlignmentX(Component.LEFT_ALIGNMENT);

        fechaInicioChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
        fechaFinChooser.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelFechas.add(lblInicio);
        panelFechas.add(Box.createVerticalStrut(5));
        panelFechas.add(fechaInicioChooser);
        panelFechas.add(Box.createVerticalStrut(15));
        panelFechas.add(lblFin);
        panelFechas.add(Box.createVerticalStrut(5));
        panelFechas.add(fechaFinChooser);

        // Botón Aceptar
        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAceptar.setBackground(new Color(33, 150, 243));
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setFocusPainted(false);
        btnAceptar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAceptar.setPreferredSize(new Dimension(120, 35));

        btnAceptar.addActionListener(e -> {
            Date inicio = fechaInicioChooser.getDate();
            Date fin = fechaFinChooser.getDate();

            if (inicio == null || fin == null) {
                JOptionPane.showMessageDialog(this, "Selecciona ambas fechas", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (inicio.after(fin)) {
                JOptionPane.showMessageDialog(this, "La fecha de inicio debe ser antes de la final.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            listener.onFechasSeleccionadas(inicio, fin);
            dispose();
        });

        // Panel inferior con botón centrado
        JPanel panelBoton = new JPanel();
        panelBoton.setBackground(new Color(245, 245, 245));
        panelBoton.add(btnAceptar);

        // Agregar todo al frame
        add(panelFechas, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }
}
