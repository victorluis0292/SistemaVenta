package Controlador;

import Modelo.RolDAO;
import javax.swing.JOptionPane;

public class RolController {

    private RolDAO rolDAO;

    // Constructor
    public RolController() {
        this.rolDAO = new RolDAO();
    }

    /**
     * Solicita la clave de administrador mediante un JOptionPane.
     * @return true si la clave es correcta, false si es incorrecta o se canceló.
     */
    public boolean solicitarClaveAdministrador() {
        String claveIngresada = JOptionPane.showInputDialog(
                null,
                "Ingrese código de administrador:",
                "Validación de Administrador",
                JOptionPane.PLAIN_MESSAGE
        );

        if (claveIngresada == null) {
            // El usuario canceló el input
            return false;
        }

        claveIngresada = claveIngresada.trim();
        if (claveIngresada.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debe ingresar una clave",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        // Validar clave con la base de datos a través de RolDAO
        if (rolDAO.validarClaveAdministrador(claveIngresada)) {
            return true; // clave correcta
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Clave incorrecta",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false; // clave incorrecta
        }
    }
}
