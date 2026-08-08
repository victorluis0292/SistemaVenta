package Vista;

import Modelo.ConfiguracionDispositivos;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class PanelConfigBascula extends JPanel {

    private JCheckBox chkHabilitada;
    private JComboBox<String> cbxModelo;
    private JComboBox<String> cbxPuerto;
    private JButton btnRefrescarPuertos;

    private final Runnable alVolver;

    public PanelConfigBascula(Runnable alVolver) {
        this.alVolver = alVolver;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirFormulario(), BorderLayout.CENTER);
        add(construirBotones(), BorderLayout.SOUTH);

        cargarConfigActual();
    }

    private JPanel construirEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        JLabel lbl = new JLabel("BÁSCULA CONECTADA A LA COMPUTADORA");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(new Color(30, 60, 150));
        panel.add(lbl, BorderLayout.WEST);
        return panel;
    }

    private JPanel construirFormulario() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        chkHabilitada = new JCheckBox("Tengo conectada una báscula a mi computadora");
        chkHabilitada.setBackground(Color.WHITE);
        chkHabilitada.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        form.add(chkHabilitada, gbc);

        JLabel lblModelo = new JLabel("Por favor elige el modelo de la báscula:");
        lblModelo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(lblModelo, gbc);

        cbxModelo = new JComboBox<>(ConfiguracionDispositivos.listarModelosBascula().toArray(new String[0]));
        gbc.gridx = 1; gbc.gridy = 1;
        form.add(cbxModelo, gbc);

        JLabel lblPuerto = new JLabel("Puerto:");
        lblPuerto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(lblPuerto, gbc);

        JPanel panelPuerto = new JPanel(new BorderLayout(6, 0));
        panelPuerto.setBackground(Color.WHITE);
        cbxPuerto = new JComboBox<>();
        btnRefrescarPuertos = new JButton("🔄");
        btnRefrescarPuertos.setToolTipText("Actualizar lista de puertos");
        btnRefrescarPuertos.addActionListener(e -> refrescarPuertos());
        panelPuerto.add(cbxPuerto, BorderLayout.CENTER);
        panelPuerto.add(btnRefrescarPuertos, BorderLayout.EAST);
        gbc.gridx = 1; gbc.gridy = 2;
        form.add(panelPuerto, gbc);

        JTextArea txtAyuda = new JTextArea(
            "Si tu báscula no aparece en la lista de modelos soportados, contáctanos " +
            "para revisar la posibilidad de agregar su protocolo al sistema."
        );
        txtAyuda.setEditable(false);
        txtAyuda.setLineWrap(true);
        txtAyuda.setWrapStyleWord(true);
        txtAyuda.setBackground(Color.WHITE);
        txtAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        txtAyuda.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(txtAyuda, gbc);

        refrescarPuertos();
        chkHabilitada.addActionListener(e -> actualizarHabilitacionCampos());

        return form;
    }

    private void actualizarHabilitacionCampos() {
        boolean habilitada = chkHabilitada.isSelected();
        cbxModelo.setEnabled(habilitada);
        cbxPuerto.setEnabled(habilitada);
        btnRefrescarPuertos.setEnabled(habilitada);
    }

    private void refrescarPuertos() {
        String seleccionActual = (String) cbxPuerto.getSelectedItem();
        cbxPuerto.removeAllItems();
        List<String> puertos = ConfiguracionDispositivos.listarPuertosDisponibles();
        if (puertos.isEmpty()) {
            cbxPuerto.addItem("(sin puertos detectados)");
        } else {
            for (String p : puertos) cbxPuerto.addItem(p);
        }
        if (seleccionActual != null) {
            cbxPuerto.setSelectedItem(seleccionActual);
        }
    }

    private JPanel construirBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);

        JButton btnVolver = new JButton("← Volver");
        btnVolver.addActionListener(e -> { if (alVolver != null) alVolver.run(); });

        JButton btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setBackground(new Color(46, 125, 50));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.addActionListener(e -> guardarConfig());

        panel.add(btnVolver);
        panel.add(btnGuardar);
        return panel;
    }

    private void cargarConfigActual() {
        ConfiguracionDispositivos.ConfigBascula cfg = ConfiguracionDispositivos.cargarConfigBascula();
        chkHabilitada.setSelected(cfg.habilitada);
        if (cfg.modelo != null && !cfg.modelo.isEmpty()) {
            cbxModelo.setSelectedItem(cfg.modelo);
        }
        if (cfg.puerto != null && !cfg.puerto.isEmpty()) {
            cbxPuerto.setSelectedItem(cfg.puerto);
        }
        actualizarHabilitacionCampos();
    }

    private void guardarConfig() {
        boolean habilitada = chkHabilitada.isSelected();
        String modelo = (String) cbxModelo.getSelectedItem();
        String puerto = (String) cbxPuerto.getSelectedItem();

        if (habilitada && (puerto == null || puerto.startsWith("(sin puertos"))) {
            JOptionPane.showMessageDialog(this, "No hay un puerto válido seleccionado.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ConfiguracionDispositivos.guardarConfigBascula(habilitada, modelo, puerto);
        JOptionPane.showMessageDialog(this, "Configuración de báscula guardada con éxito.");
    }
}