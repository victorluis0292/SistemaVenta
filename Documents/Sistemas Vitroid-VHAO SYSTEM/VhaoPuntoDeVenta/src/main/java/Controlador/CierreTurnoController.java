package Controlador;

import Vista.CierreTurnoView;
import Vista.Login;
import Vista.Sistema;

import javax.swing.*;
import java.math.BigDecimal;

public class CierreTurnoController {
    private final CierreTurnoView vista;
    private final BigDecimal efectivoEsperado;

    public CierreTurnoController(JFrame parent, BigDecimal efectivoEsperado) {
        this.vista = new CierreTurnoView(parent);
        this.efectivoEsperado = efectivoEsperado;

        vista.lblEsperado.setText(String.format("$%,.2f", efectivoEsperado));

        vista.btnCerrarTurno.addActionListener(e -> cerrarTurno());
        vista.btnCancelar.addActionListener(e -> vista.dispose());
        vista.txtCantidad.addCaretListener(e -> calcularDiferencia());

        vista.setVisible(true);
    }

    private void calcularDiferencia() {
        try {
            String texto = vista.txtCantidad.getText().replace(",", "").trim();
            if (texto.isEmpty()) {
                vista.lblDiferencia.setText("$0.00");
                vista.lblMensaje.setText("");
                return;
            }

            BigDecimal efectivoReal = new BigDecimal(texto);
            BigDecimal diferencia = efectivoReal.subtract(efectivoEsperado);

            vista.lblDiferencia.setText(String.format("$%,.2f", diferencia));

            if (diferencia.compareTo(BigDecimal.ZERO) == 0) {
                vista.lblMensaje.setText("✅ ¡Muy bien! Todo en orden");
                vista.lblMensaje.setForeground(new java.awt.Color(0, 128, 0));
            } else {
                vista.lblMensaje.setText("⚠️ Diferencia detectada");
                vista.lblMensaje.setForeground(new java.awt.Color(200, 0, 0));
            }

        } catch (NumberFormatException ex) {
            vista.lblDiferencia.setText("⚠️ Error");
            vista.lblMensaje.setText("Ingrese una cantidad válida");
            vista.lblMensaje.setForeground(new java.awt.Color(200, 0, 0));
        }
    }

   private void cerrarTurno() {
    String texto = vista.txtCantidad.getText().replace(",", "").trim();

    if (texto.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "Debe ingresar el efectivo real en caja.");
        return;
    }

    try {
        BigDecimal efectivoReal = new BigDecimal(texto);
        BigDecimal diferencia = efectivoReal.subtract(efectivoEsperado);

        // Quitar confirmación, cerrar directo
        JOptionPane.showMessageDialog(vista, "✅ Turno cerrado correctamente. Cerrando sesión...");

        // Cerrar ventana de cierre turno
        vista.dispose();

        // Cerrar todas las ventanas abiertas, incluyendo Sistema
        for (java.awt.Window w : java.awt.Window.getWindows()) {
            if (w.isShowing()) {
                w.dispose();
            }
        }

        // Reiniciar instancia singleton Sistema para liberar memoria
        Sistema.reiniciarInstancia();

        // Mostrar Login
        javax.swing.SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(vista, "Cantidad inválida.");
    }
}

}
