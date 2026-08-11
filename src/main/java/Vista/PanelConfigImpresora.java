package Vista;

import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class PanelConfigImpresora extends JPanel {

    private final Runnable volverAGrid;

    public PanelConfigImpresora(Runnable volverAGrid) {
        this.volverAGrid = volverAGrid;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // fondo gris claro

        // Encabezado compacto
        JLabel titulo = new JLabel("🖨️ Impresora de Tickets", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(50, 50, 50));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Lista de impresoras conectadas
        String[] nombres = obtenerImpresorasDisponibles();

        JComboBox<String> comboImpresoras = new JComboBox<>(nombres);
        comboImpresoras.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboImpresoras.setBackground(new Color(250, 250, 250));
        comboImpresoras.setPreferredSize(new Dimension(200, 25)); // reducido

        if (nombres.length == 0) {
            comboImpresoras.addItem("⚠️ No se detectaron impresoras conectadas");
            comboImpresoras.setEnabled(false);
        }

        // Panel central compacto
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setPreferredSize(new Dimension(280, 100));

        card.add(new JLabel("Selecciona la impresora:", SwingConstants.CENTER));

        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        comboPanel.setBackground(Color.WHITE);
        comboPanel.add(comboImpresoras);
        card.add(comboPanel);

        // Botones pequeños
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setBackground(new Color(245, 247, 250));

        JButton btnVolver = new JButton("← Volver");
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVolver.setBackground(new Color(230, 230, 230));
        btnVolver.setFocusPainted(false);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnGuardar.setBackground(new Color(0, 153, 76));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);

        btnVolver.addActionListener((ActionEvent e) -> volverAGrid.run());
        btnGuardar.addActionListener((ActionEvent e) -> {
            if (nombres.length > 0) {
                String seleccion = (String) comboImpresoras.getSelectedItem();
                JOptionPane.showMessageDialog(this, "✅ Impresora seleccionada: " + seleccion);
                // Aquí puedes guardar la impresora en BD o archivo de configuración
            }
        });

        botones.add(btnVolver);
        botones.add(btnGuardar);

        // Contenedor centrado
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(new Color(245, 247, 250));
        centro.add(card, BorderLayout.CENTER);
        centro.add(botones, BorderLayout.SOUTH);

        add(centro, BorderLayout.CENTER);
    }

    // 🔑 Método para filtrar impresoras conectadas
    private String[] obtenerImpresorasDisponibles() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        List<String> disponibles = new ArrayList<>();

        for (PrintService service : services) {
            String nombre = service.getName();

            // Filtrar impresoras virtuales conocidas
            if (nombre.toLowerCase().contains("pdf") ||
                nombre.toLowerCase().contains("xps") ||
                nombre.toLowerCase().contains("onenote")) {
                continue;
            }

            // Probar si se puede crear un trabajo de impresión
            try {
                DocPrintJob job = service.createPrintJob();
                if (job != null) {
                    disponibles.add(nombre);
                }
            } catch (Exception e) {
                System.out.println("⚠️ Impresora no disponible: " + nombre);
            }
        }

        return disponibles.toArray(new String[0]);
    }
}
