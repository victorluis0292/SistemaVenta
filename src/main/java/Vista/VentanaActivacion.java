package Vista;

import LicenciasLocales.LicenciaValidadorLocalSerie;
import Modelo.TecnicoDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaActivacion extends JDialog {

    private JButton btnAltaNegocio;
    private JTextField txtClaveActivacion;
    private JLabel lblMensaje;

    public VentanaActivacion(JFrame parent, String nombreTecnico) {
        super(parent, "Activación del Sistema", true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(null);

        // Título
        JLabel lblTitulo = new JLabel("Activación del Sistema", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBounds(50, 20, 300, 30);
        add(lblTitulo);

        // Botón Alta negocio
        btnAltaNegocio = new JButton("Alta negocio");
        btnAltaNegocio.setBounds(120, 70, 150, 30);
        add(btnAltaNegocio);

        // Etiqueta y campo clave
        JLabel lblClave = new JLabel("Clave de activación:");
        lblClave.setBounds(50, 120, 150, 25);
        add(lblClave);

        txtClaveActivacion = new JTextField();
        txtClaveActivacion.setBounds(180, 120, 150, 25);
        add(txtClaveActivacion);

        // Botón validar
        JButton btnValidarClave = new JButton("Validar");
        btnValidarClave.setBounds(120, 160, 150, 30);
        add(btnValidarClave);

        // Mensaje de error
        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setBounds(50, 200, 300, 25);
        lblMensaje.setForeground(Color.RED);
        add(lblMensaje);

        // ===============================
        // Acción del botón Alta negocio
        // ===============================
        btnAltaNegocio.addActionListener((ActionEvent e) -> {
            String nombreNegocio = LicenciaValidadorLocalSerie.pedirNombreNegocio();
            if (nombreNegocio != null && !nombreNegocio.isEmpty()) {
                String inputId = JOptionPane.showInputDialog(this, "Ingresa el ID de la empresa:");
                if (inputId == null || inputId.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El ID de empresa es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    int idEmpresa = Integer.parseInt(inputId.trim());

                    // 🔹 CORREGIDO: ahora pasamos también el nombre del técnico
boolean registrado = LicenciaValidadorLocalSerie.registrarLicenciaEnBase(nombreNegocio, idEmpresa, nombreTecnico);
                    LicenciaValidadorLocalSerie.exportarInfoLicenciaEnDescargas(nombreNegocio, idEmpresa, nombreTecnico);

                    if (registrado) {
                        JOptionPane.showMessageDialog(this,
                                "✅ Licencia registrada correctamente en la base de datos y archivo generado \n en carpeta Descargas.",
                                "Licencia Activada", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "⚠ La licencia ya existía en la base de datos, se actualizó el archivo.",
                                "Licencia Existente", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "ID de empresa inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "❌ Error al registrar la licencia en la base de datos:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // ===============================
        // Acción para validar clave de activación (UUID)
        // ===============================
        btnValidarClave.addActionListener((ActionEvent e) -> {
            String claveIngresada = txtClaveActivacion.getText().trim();
            String uuidSistema = LicenciaValidadorLocalSerie.obtenerUUID();

            if (claveIngresada.equalsIgnoreCase(uuidSistema)) {
                try {
                    boolean actualizado = LicenciaValidadorLocalSerie.activarLicencia();

                    if (actualizado) {
                        boolean empresaActiva = LicenciaValidadorLocalSerie.activarEmpresaPorLicencia();

                        if (empresaActiva) {
                            JOptionPane.showMessageDialog(this,
                                    "✅ Licencia y empresa activadas correctamente.\nAhora puedes acceder al sistema.",
                                    "Activación completa", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "⚠ Licencia activada, pero no se pudo actualizar el estado de la empresa.",
                                    "Aviso", JOptionPane.WARNING_MESSAGE);
                        }

                        dispose();
                    } else {
                        lblMensaje.setText("⚠ No se encontró licencia registrada para este equipo.");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    lblMensaje.setText("❌ Error al activar licencia: " + ex.getMessage());
                }
            } else {
    lblMensaje.setText("❌ Clave incorrecta. Verifica la UUID.");
    lblMensaje.setForeground(Color.RED);
    lblMensaje.setVisible(true);

    // 🔹 Forzar actualización visual inmediata
    lblMensaje.revalidate();
    lblMensaje.repaint();

    // 🔹 Además mostrar alerta visual clara
    JOptionPane.showMessageDialog(this,
            "❌ Clave incorrecta.\n.",
            "Error de activación", JOptionPane.ERROR_MESSAGE);
}

        });
    }
}
