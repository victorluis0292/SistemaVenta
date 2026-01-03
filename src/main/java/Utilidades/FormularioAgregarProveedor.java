package Utilidades;

import Estilos.Estilos;
import Modelo.Proveedor;
import Modelo.ProveedorDao;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FormularioAgregarProveedor extends JDialog {

    private JTextField txtRuc;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private final int idEmpresa;

    public FormularioAgregarProveedor(Frame parent, int idEmpresa) {
        super(parent, "Agregar Proveedor", true);
        this.idEmpresa = idEmpresa;
        initComponents();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        // Panel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Campos
        txtRuc = new JTextField(18);
          Estilos.estiloCampo(txtRuc);
        txtNombre = new JTextField(18);
          Estilos.estiloCampo(txtNombre);
        txtTelefono = new JTextField(18);
          Estilos.estiloCampo(txtTelefono);
        txtDireccion = new JTextField(18);
            Estilos.estiloCampo(txtDireccion);
        // Botones
        btnGuardar = new JButton("Guardar");
              
        btnCancelar = new JButton("Cancelar");

        // 🔹 Igualar tamaño de botones
        Dimension btnSize = new Dimension(120, 35);
        btnGuardar.setPreferredSize(btnSize);
        btnCancelar.setPreferredSize(btnSize);

        // 🔹 Estilo visual
        btnGuardar.setBackground(new Color(0, 153, 51));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);

        btnCancelar.setBackground(new Color(204, 0, 0));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancelar.setFocusPainted(false);

        // --- Layout de campos ---
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("RFC:"), gbc);
        gbc.gridx = 1;
        panel.add(txtRuc, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        panel.add(txtTelefono, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        panel.add(txtDireccion, gbc);

        // --- Panel de botones alineados ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(panelBotones, gbc);

        // --- Eventos ---
        btnGuardar.addActionListener(e -> guardarProveedor());
        btnCancelar.addActionListener(e -> dispose());

        add(panel);
        pack();
    }

    private void guardarProveedor() {
        String ruc = txtRuc.getText().trim();
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (ruc.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El campo RUC y Nombre son obligatorios.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Proveedor pr = new Proveedor();
        pr.setRuc(ruc);
        pr.setNombre(nombre);
        pr.setTelefono(telefono);
        pr.setDireccion(direccion);
        pr.setIdEmpresa(idEmpresa);

        ProveedorDao dao = new ProveedorDao();
        if (dao.RegistrarProveedor(pr)) {
            JOptionPane.showMessageDialog(this,
                    "Proveedor registrado correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar el proveedor.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
