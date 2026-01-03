package Vista;

import Modelo.TecnicoDAO;
import Modelo.Conexion;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class VentanaValidarTecnico extends JDialog {

    private JTextField txtCodigo;
    private JButton btnValidar;
    private int idLicenciaActiva; // ID de la licencia que se va a activar

    public VentanaValidarTecnico(JFrame parent, int idLicenciaActiva) {
        super(parent, "Validación de Técnico", true); // Modal
        this.idLicenciaActiva = idLicenciaActiva;

        setSize(350, 150);
        setLocationRelativeTo(parent);
        setLayout(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("VentanaValidarTecnico cerrada por X");
            }
        });

        JLabel lbl = new JLabel("Ingrese código de técnico:");
        lbl.setBounds(20, 20, 200, 25);
        add(lbl);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(20, 50, 200, 25);
        add(txtCodigo);

        btnValidar = new JButton("Validar");
        btnValidar.setBounds(230, 50, 90, 25);
        add(btnValidar);

        btnValidar.addActionListener(e -> validar(parent));
    }

    private void validar(JFrame parent) {
        String codigo = txtCodigo.getText().trim();
        TecnicoDAO dao = new TecnicoDAO();

        if (dao.validarNumControl(codigo)) {
            // Obtener el nombre del técnico
            String nombreTecnico = dao.obtenerNombrePorNumControl(codigo);

            if (nombreTecnico != null) {
                try (Connection con = Conexion.getConnection();
                     PreparedStatement ps = con.prepareStatement(
                             "UPDATE licencias_autorizadaslocales SET tecnico = ? WHERE id = ?")) {

                    ps.setString(1, nombreTecnico);
                    ps.setInt(2, idLicenciaActiva); // licencia que se está activando
                    ps.executeUpdate();

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "❌ Error al registrar el técnico.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "✅ Técnico válido: " + nombreTecnico);

            } else {
                JOptionPane.showMessageDialog(this,
                        "❌ No se encontró el nombre del técnico.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            dispose(); // Cierra la ventana actual

            // 🔹 Pasamos el nombre del técnico al constructor de VentanaActivacion
            VentanaActivacion ventanaActivacion = new VentanaActivacion(parent, nombreTecnico);
            ventanaActivacion.setVisible(true);

        } else {
            JOptionPane.showMessageDialog(this,
                    "❌ Código de técnico inválido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
