package Controlador;

import Modelo.Usuario;
import Modelo.UsuarioDao;
import vista.UsuarioView;

import javax.swing.*;
import Modelo.login;

public class UsuarioController {
    private UsuarioView view;
    private UsuarioDao dao;
    private int idEmpresa; // ID de la empresa dinámica

    public UsuarioController(UsuarioView view, UsuarioDao dao, int idEmpresa) {
        this.view = view;
        this.dao = dao;
        this.idEmpresa = idEmpresa; // Se asigna desde el constructor

        // Listener para el botón guardar
        this.view.btnGuardar.addActionListener(e -> registrarUsuario());
    }

    private void registrarUsuario() {
    String nombre = view.txtNombre.getText().trim();
    String correo = view.txtCorreo.getText().trim();
    String pass = new String(view.txtPass.getPassword()).trim();
    String rol = view.txtRol.getText().trim();
    String clave = view.txtClave.getText().trim();

    // Validación de campos obligatorios
    if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || rol.isEmpty()) {
        JOptionPane.showMessageDialog(view, "Todos los campos obligatorios excepto clave");
        return;
    }

    // Verificar si el correo ya existe
    if (dao.existeCorreo(correo, idEmpresa)) {
        JOptionPane.showMessageDialog(view, "El correo ya está registrado en esta empresa");
        return;
    }

    // Solo asignar clave si es Administrador
    if (!rol.equalsIgnoreCase("Administrador")) {
        clave = null;
    }

    // Crear usuario
    Usuario u = new Usuario();
    u.setNombre(nombre);
    u.setCorreo(correo);
    u.setPass(pass);
    u.setRol(rol);        // Guardamos el nombre del rol directamente
    u.setIdEmpresa(idEmpresa);
    u.setClave(clave);

    // Registrar usuario
    if (dao.registrarUsuario(u)) {
        JOptionPane.showMessageDialog(view, "Usuario registrado correctamente");
        limpiarCampos();
    } else {
        JOptionPane.showMessageDialog(view, "Error al registrar usuario");
    }
}

    private void limpiarCampos() {
        view.txtNombre.setText("");
        view.txtCorreo.setText("");
        view.txtPass.setText("");
        view.txtRol.setText("");
        view.txtClave.setText("");
    }
}
