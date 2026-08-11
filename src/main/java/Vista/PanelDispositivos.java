package Vista;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelDispositivos extends JPanel {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contenedor = new JPanel(cardLayout);

    public PanelDispositivos() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        contenedor.add(construirGridDispositivos(), "GRID");
        contenedor.add(new PanelConfigBascula(this::volverAGrid), "BASCULA");
        contenedor.add(new PanelConfigImpresora(this::volverAGrid), "IMPRESORA");

        add(contenedor, BorderLayout.CENTER);
        cardLayout.show(contenedor, "GRID");
    }

    private JPanel construirGridDispositivos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Dispositivos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 5, 15, 15));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(20, 0, 0, 0));

        grid.add(crearTarjetaImpresora("🖨️", "Impresora de Tickets"));
        grid.add(crearTarjetaDispositivo("📷", "Lector de Códigos", false));
        grid.add(crearTarjetaDispositivo("💵", "Cajón de Dinero", false));
        grid.add(crearTarjetaDispositivo("⚖️", "Báscula", true));
        grid.add(crearTarjetaDispositivo("💳", "Terminal TPV", false));

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    // Tarjeta especial para impresoras
    private JPanel crearTarjetaImpresora(String icono, String nombre) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        card.setPreferredSize(new Dimension(140, 120));

        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        card.add(lblIcono, BorderLayout.CENTER);

        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card.add(lblNombre, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
       card.addMouseListener(new MouseAdapter() {
    @Override public void mouseClicked(MouseEvent e) { 
        cardLayout.show(contenedor, "IMPRESORA"); 
    }
    @Override public void mouseEntered(MouseEvent e) { card.setBackground(new Color(235, 245, 255)); }
    @Override public void mouseExited(MouseEvent e) { card.setBackground(Color.WHITE); }
});

        return card;
    }

    private JPanel crearTarjetaDispositivo(String icono, String nombre, boolean disponible) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        card.setPreferredSize(new Dimension(140, 120));

        JLabel lblIcono = new JLabel(icono, SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        card.add(lblIcono, BorderLayout.CENTER);

        JLabel lblNombre = new JLabel(nombre, SwingConstants.CENTER);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 12));
        card.add(lblNombre, BorderLayout.SOUTH);

        if (disponible) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { cardLayout.show(contenedor, "BASCULA"); }
                @Override public void mouseEntered(MouseEvent e) { card.setBackground(new Color(235, 245, 255)); }
                @Override public void mouseExited(MouseEvent e) { card.setBackground(Color.WHITE); }
            });
        } else {
            lblIcono.setForeground(Color.LIGHT_GRAY);
            lblNombre.setForeground(Color.LIGHT_GRAY);
            card.setToolTipText("Próximamente");
        }

        return card;
    }

    private void listarImpresoras() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        if (services.length == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ No se detectaron impresoras.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] nombres = new String[services.length];
        for (int i = 0; i < services.length; i++) {
            nombres[i] = services[i].getName();
        }

        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Selecciona una impresora:",
                "Impresoras disponibles",
                JOptionPane.PLAIN_MESSAGE,
                null,
                nombres,
                nombres[0]
        );

        if (seleccion != null) {
            // Aquí puedes guardar la impresora seleccionada en tus preferencias
            JOptionPane.showMessageDialog(this, "✅ Impresora seleccionada: " + seleccion);
        }
    }

    private void volverAGrid() {
        cardLayout.show(contenedor, "GRID");
    }
}
