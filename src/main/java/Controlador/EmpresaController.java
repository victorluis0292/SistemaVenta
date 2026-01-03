package Controlador;

import Modelo.Empresa;
import Modelo.EmpresaDao;
import Modelo.Usuario;
import Modelo.UsuarioDao;
import Utilidades.ConfigApp;
import Utilidades.generarPdfRegistro;
import javax.swing.*;
import Estilos.Estilos;

/**
 * Controlador encargado del registro completo de empresa + usuario administrador.
 * El registro se realiza en dos pasos y genera un PDF al finalizar.
 */
public class EmpresaController {

    // Guardamos temporalmente la empresa hasta finalizar
    private Empresa empresaTemporal;

    /**
     * Abre el formulario de registro de empresa.
     */
    public void abrirFormularioRegistro() {
        JFrame frameEmpresa = new JFrame("Registrar Empresa");
        frameEmpresa.setSize(400, 400);
        frameEmpresa.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        frameEmpresa.setLocationRelativeTo(null);

        // Campos
        JTextField txtRuc = new JTextField();
        Estilos.estiloCampo(txtRuc);
        JTextField txtNombre = new JTextField();
        Estilos.estiloCampo(txtNombre);
        JTextField txtTelefono = new JTextField();
        Estilos.estiloCampo(txtTelefono);
        JTextField txtDireccion = new JTextField();
        Estilos.estiloCampo(txtDireccion);
        JTextField txtMensaje = new JTextField();
        Estilos.estiloCampo(txtMensaje);

        // Labels
        JLabel lblRuc = new JLabel("RFC:");
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblTelefono = new JLabel("Teléfono:");
        JLabel lblDireccion = new JLabel("Dirección:");
        JLabel lblMensaje = new JLabel("Mensaje:");

        // Botón siguiente
        JButton btnSiguiente = new JButton("Siguiente");
        Estilos.estiloBotonAzul(btnSiguiente);

        // Agregar componentes
        frameEmpresa.add(lblRuc, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 70, 25));
        frameEmpresa.add(txtRuc, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 200, 30));
        frameEmpresa.add(lblNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 70, 25));
        frameEmpresa.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 200, 30));
        frameEmpresa.add(lblTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 70, 25));
        frameEmpresa.add(txtTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 110, 200, 30));
        frameEmpresa.add(lblDireccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, 70, 25));
        frameEmpresa.add(txtDireccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 150, 200, 30));
        frameEmpresa.add(lblMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, 70, 25));
        frameEmpresa.add(txtMensaje, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 190, 200, 30));
        frameEmpresa.add(btnSiguiente, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 260, 100, 35));

        // Acción siguiente
        btnSiguiente.addActionListener(e -> {
            if (txtRuc.getText().trim().isEmpty() ||
                txtNombre.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty() ||
                txtDireccion.getText().trim().isEmpty() ||
                txtMensaje.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(frameEmpresa,
                        "Por favor completa todos los campos antes de continuar.",
                        "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Guardamos los datos temporalmente
            empresaTemporal = new Empresa();
            empresaTemporal.setRuc(txtRuc.getText().trim());
            empresaTemporal.setNombre(txtNombre.getText().trim());
            empresaTemporal.setTelefono(txtTelefono.getText().trim());
            empresaTemporal.setDireccion(txtDireccion.getText().trim());
            empresaTemporal.setMensaje(txtMensaje.getText().trim());
            empresaTemporal.setEstado(0);

            // Abrimos la siguiente pantalla
            abrirFormularioUsuario(frameEmpresa);
        });

        frameEmpresa.setVisible(true);
    }

    /**
     * Abre el formulario para registrar el usuario administrador.
     */
    private void abrirFormularioUsuario(JFrame frameEmpresa) {
        JFrame frameUsuario = new JFrame("Registrar Usuario Administrador");
        frameUsuario.setSize(400, 300);
        frameUsuario.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        frameUsuario.setLocationRelativeTo(null);

        JTextField txtNombreU = new JTextField();
        Estilos.estiloCampo(txtNombreU);
        JTextField txtCorreoU = new JTextField();
        Estilos.estiloCampo(txtCorreoU);
        JPasswordField txtPassU = new JPasswordField();
        Estilos.estiloCampo(txtPassU);

        JLabel lblNombreU = new JLabel("Nombre:");
        JLabel lblCorreoU = new JLabel("Correo:");
        JLabel lblPassU = new JLabel("Password:");

        JButton btnFinalizar = new JButton("Finalizar Registro");
        Estilos.estiloBotonAzul(btnFinalizar);

        frameUsuario.add(lblNombreU, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 70, 25));
        frameUsuario.add(txtNombreU, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 30, 200, 30));
        frameUsuario.add(lblCorreoU, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 70, 25));
        frameUsuario.add(txtCorreoU, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 70, 200, 30));
        frameUsuario.add(lblPassU, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 70, 25));
        frameUsuario.add(txtPassU, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 110, 200, 30));
        frameUsuario.add(btnFinalizar, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 170, 160, 35));

        // Acción finalizar
        btnFinalizar.addActionListener(ev -> {
            if (txtNombreU.getText().trim().isEmpty() ||
                txtCorreoU.getText().trim().isEmpty() ||
                new String(txtPassU.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(frameUsuario,
                        "Completa todos los campos antes de finalizar.",
                        "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            EmpresaDao empresaDao = new EmpresaDao();
            String correoUsuario = txtCorreoU.getText().trim();

            // Guardar empresa y obtener ID
            int idEmpresa = empresaDao.RegistrarEmpresaYObtenerID(empresaTemporal, correoUsuario);

            if (idEmpresa > 0) {
                empresaTemporal.setId_empresa(idEmpresa); // asignar ID generado

                // Registrar usuario administrador
                Usuario u = new Usuario();
                u.setNombre(txtNombreU.getText().trim());
                u.setCorreo(correoUsuario);
                u.setPass(new String(txtPassU.getPassword()).trim());
                u.setRol("Administrador");
                u.setIdEmpresa(idEmpresa);
                u.setClave("admin");

                UsuarioDao usuarioDao = new UsuarioDao();
                if (usuarioDao.registrarUsuario(u)) {
                    // Guardar configuración local
                    ConfigApp.guardarDatos(idEmpresa, u.getCorreo());

                    // Generar PDF visual de confirmación
                    generarPdfRegistro.generarPdfRegistro(empresaTemporal.getNombre(), idEmpresa);

                    JOptionPane.showMessageDialog(frameUsuario,
                            "✅ Registro completado correctamente.\nTu empresa y usuario administrador fueron creados.",
                            "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);

                    frameEmpresa.dispose();
                    frameUsuario.dispose();
                } else {
                    JOptionPane.showMessageDialog(frameUsuario,
                            "Error al registrar el usuario administrador.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frameUsuario,
                        "No se pudo registrar la empresa. Intenta nuevamente.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frameUsuario.setVisible(true);
    }
}
