package vista;

import javax.swing.*;
import java.awt.*;

public class UsuarioView extends JFrame {
    public JTextField txtNombre, txtCorreo, txtRol, txtClave;
    public JPasswordField txtPass;
    public JButton btnGuardar;

    public UsuarioView() {
        setTitle("Registrar Usuario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2, 5, 5));

        add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        add(txtNombre);

        add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        add(txtCorreo);

        add(new JLabel("Contraseña:"));
        txtPass = new JPasswordField();
        add(txtPass);

        add(new JLabel("Rol:"));
        txtRol = new JTextField();
        add(txtRol);

        add(new JLabel("Clave (solo Administrador):"));
        txtClave = new JTextField();
        add(txtClave);

        btnGuardar = new JButton("Guardar");
        add(new JLabel());
        add(btnGuardar);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
