package Vista;

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

        grid.add(crearTarjetaDispositivo("🖨️", "Impresora de Tickets", false));
        grid.add(crearTarjetaDispositivo("📷", "Lector de Códigos", false));
        grid.add(crearTarjetaDispositivo("💵", "Cajón de Dinero", false));
        grid.add(crearTarjetaDispositivo("⚖️", "Báscula", true));
        grid.add(crearTarjetaDispositivo("💳", "Terminal TPV", false));

        panel.add(grid, BorderLayout.CENTER);
        return panel;
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

    private void volverAGrid() {
        cardLayout.show(contenedor, "GRID");
    }
}