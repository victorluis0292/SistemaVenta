package Vista;

import Estilos.Estilos.PanelConEstilo;
import Modelo.LoaderDialog;

import javax.swing.*;
import java.awt.*;
import Estilos.Estilos;
import Modelo.Eventos;
import static Vista.Sistema.txtCodigoVenta;

//public final class ventanaCobroConTarjeta extends JFrame {

public final class ventanaCobroConTarjeta extends JDialog {
    private final JTextField txtMonto = new JTextField();
    private final JLabel lblTotalMasComision = new JLabel();
    private final ventanaCobrar ventanaPrincipal;

    private final double subtotal;       // saldo pendiente
    private final double comisionPorc = 0.045;

    public ventanaCobroConTarjeta(ventanaCobrar ventana) {
     
        
        super(ventana, "Pago con Tarjeta", true);

        this.ventanaPrincipal = ventana;

        setTitle("Pago con Tarjeta");
        setSize(350, 240);
        setLocationRelativeTo(ventana);

        PanelConEstilo panelRaiz = new PanelConEstilo();
        panelRaiz.setLayout(new GridLayout(5, 1, 10, 10));
       

        subtotal = ventanaPrincipal.getSaldoPendiente();
        double comision = calcularComision(subtotal);
        double totalConComision = subtotal + comision;

        lblTotalMasComision.setText(String.format("Saldo: $%.2f + Comisión: $%.2f = $%.2f", subtotal, comision, totalConComision));
        lblTotalMasComision.setFont(new Font("Arial", Font.BOLD, 14));
       // panelRaiz.add(lblTotalMasComision);  //ocultar los cálculos de la comisión 

        JLabel label = new JLabel("Monto a pagar con tarjeta:");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panelRaiz.add(label);

        txtMonto.setText(String.format("%.2f", totalConComision));
        txtMonto.setFont(new Font("BOLD", Font.PLAIN, 22));
        panelRaiz.add(txtMonto);
           new Eventos().aplicarSoloDecimal(txtMonto); // ← ¡Así de limpio!

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.addActionListener(e -> validarYEjecutar());
     Estilos.estiloBotonVerdeLima(btnAceptar);
        panelRaiz.add(btnAceptar);

        setContentPane(panelRaiz);

        txtMonto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarLeyenda(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarLeyenda(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarLeyenda(); }
        });

        txtMonto.addActionListener(e -> validarYEjecutar());
    }

    private void actualizarLeyenda() {
        try {
            double montoIngresado = Double.parseDouble(txtMonto.getText());
            if (montoIngresado < 0) return;
            double comision = calcularComision(montoIngresado);
            double total = montoIngresado + comision;
            lblTotalMasComision.setText(String.format("Monto: $%.2f + Comisión: $%.2f = $%.2f", montoIngresado, comision, total));
        } catch (NumberFormatException ignored) {}
    }

    private double calcularComision(double monto) {
        return Math.round(monto * comisionPorc * 100.0) / 100.0;
    }

    // ✅ Aquí está la validación única que decides entre los dos caminos
    private void validarYEjecutar() {
        try {
            double montoIngresado = Double.parseDouble(txtMonto.getText());
            double saldoPendiente = ventanaPrincipal.getSaldoPendiente();
            double comision = calcularComision(saldoPendiente);
            double totalConComision = saldoPendiente + comision;

            if (Math.abs(montoIngresado - totalConComision) < 0.01) {
                finalizarVentaConTarjeta();
            } else {
                procesarPago();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
            txtMonto.requestFocus();
        }
    }

    // 👇 tus métodos originales sin cambios
    private void procesarPago() {
        try {
            double monto = Double.parseDouble(txtMonto.getText());
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
                return;
            }

            double saldoPendiente = ventanaPrincipal.getSaldoPendiente();
            if (monto > saldoPendiente) {
                JOptionPane.showMessageDialog(this, String.format("El monto no puede ser mayor al saldo pendiente: $%.2f", saldoPendiente));
                return;
            }

            double comision = calcularComision(monto);

            JDialog loader = new LoaderDialog().mostrarLoader(this);
           
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

                @Override
                protected Void doInBackground() {
                    ventanaPrincipal.agregarPago("Tarjeta", monto, comision);
                    return null;
                }

                @Override
                protected void done() {
                    loader.dispose();
                    JOptionPane.showMessageDialog(ventanaCobroConTarjeta.this, "Pago con tarjeta registrado.");
                    dispose();
 ventanaPrincipal.enfocarCampoEfectivo();
                }
            };

            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
        }
    }

    private void finalizarVentaConTarjeta() {
        try {
            double montoIngresado = Double.parseDouble(txtMonto.getText());
            double saldoPendiente = ventanaPrincipal.getSaldoPendiente();
            double comision = calcularComision(saldoPendiente);
            double totalConComision = saldoPendiente + comision;

            if (Math.abs(montoIngresado - totalConComision) > 0.01) {
                JOptionPane.showMessageDialog(this, String.format(
                        "Debe ingresar el monto total exacto (saldo + comisión): $%.2f", totalConComision));
                txtMonto.requestFocus();
                return;
            }

            ventanaPrincipal.agregarPago("Tarjeta", saldoPendiente, comision);
            ventanaPrincipal.finalizarPagoTarjeta(0, 0);
            dispose();
            txtCodigoVenta.requestFocus();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
            txtMonto.requestFocus();
        }
    }
}
