package Vista;

import Controlador.TurnoController;
import Modelo.TurnoModel;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CorteCajeroView extends JFrame {

    private static CorteCajeroView instance;
    private Login loginView;

    public static CorteCajeroView getInstance(String usuario, Login loginView) {
        if (instance == null || !instance.isDisplayable()) {
            instance = new CorteCajeroView(usuario, loginView);
        } else {
            instance.toFront();
            instance.requestFocus();
        }
        return instance;
    }

    public static CorteCajeroView getInstance() {
        return instance;
    }

    private JLabel lblSaldoInicial, lblVentas, lblIngresos, lblEgresos, lblGanancia, lblTotalEnCaja;
    private JButton btnCerrarTurno;
    private TurnoController controller;
    private String usuario;

    private BigDecimal saldoInicial, ventas, ingresos, egresos, ganancia, totalEnCaja;

    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    private CorteCajeroView(String usuario, Login loginView) {
        this.usuario = usuario;
        this.loginView = loginView;

        setTitle("Corte de Turno");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        controller = TurnoController.getInstance(usuario);

        if (controller.getTurnoActual() != null) {
            initComponents();
            cargarDatosTurno();
        } else {
            mostrarMensaje("No hay un turno abierto para este usuario.");
            cerrarVentana();
        }
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));

        lblSaldoInicial = new JLabel("Saldo Inicial: $0.00");
        lblVentas = new JLabel("Has Vendido: $0.00");
        lblIngresos = new JLabel("Ingresos: $0.00");
        lblEgresos = new JLabel("Has Gastado: $0.00");
        lblGanancia = new JLabel("Ganancia: $0.00");
        lblTotalEnCaja = new JLabel("Total en Caja: $0.00");

        btnCerrarTurno = new JButton("Cerrar Turno");
        btnCerrarTurno.setBackground(new Color(220, 53, 69));
        btnCerrarTurno.setForeground(Color.WHITE);
        btnCerrarTurno.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCerrarTurno.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCerrarTurno.addActionListener(e -> cerrarTurno());

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(lblSaldoInicial);
        panel.add(lblVentas);
        panel.add(lblIngresos);
        panel.add(lblEgresos);
       // panel.add(lblGanancia); //lo oucltamos de mientras Negocio dijo que no se ocupa XD 
        panel.add(lblTotalEnCaja);
        panel.add(btnCerrarTurno);

        add(panel);
    }

    public void cargarDatosTurno() {
        saldoInicial = controller.getSaldoInicial();
        ventas = controller.calcularVentasDelTurno();
        ingresos = controller.calcularIngresosDelTurno();
        egresos = controller.calcularEgresosDelTurno();
        ganancia = controller.calcularGananciaDelTurno();

        totalEnCaja = saldoInicial.add(ventas).add(ingresos).subtract(egresos);

        lblSaldoInicial.setText("Saldo Inicial: " + formatoMoneda.format(saldoInicial));
        lblVentas.setText("Has Vendido: " + formatoMoneda.format(ventas));
        lblIngresos.setText("Ingresos: " + formatoMoneda.format(ingresos));
        lblEgresos.setText("Has Gastado: " + formatoMoneda.format(egresos));
       // lblGanancia.setText("Ganancia: " + formatoMoneda.format(ganancia));
        lblTotalEnCaja.setText("Total en Caja: " + formatoMoneda.format(totalEnCaja));
    }

    /**
     * Método para refrescar los datos en la vista cuando hay cambios,
     * por ejemplo, después de registrar ingresos o egresos.
     */
    public void refrescarDatosTurno() {
        if (controller == null) {
            mostrarMensaje("Controlador no inicializado.");
            return;
        }

        if (controller.getTurnoActual() == null) {
            mostrarMensaje("No hay turno abierto para refrescar.");
            return;
        }

        saldoInicial = controller.getSaldoInicial();
        ventas = controller.calcularVentasDelTurno();
        ingresos = controller.calcularIngresosDelTurno();
        egresos = controller.calcularEgresosDelTurno();
        ganancia = controller.calcularGananciaDelTurno();

        totalEnCaja = saldoInicial.add(ventas).add(ingresos).subtract(egresos);

        lblSaldoInicial.setText("Saldo Inicial: " + formatoMoneda.format(saldoInicial));
        lblVentas.setText("Has Vendido: " + formatoMoneda.format(ventas));
        lblIngresos.setText("Ingresos: " + formatoMoneda.format(ingresos));
        lblEgresos.setText("Has Gastado: " + formatoMoneda.format(egresos));
        lblGanancia.setText("Ganancia: " + formatoMoneda.format(ganancia));
        lblTotalEnCaja.setText("Total en Caja: " + formatoMoneda.format(totalEnCaja));
    }

    private void cerrarTurno() {
        CierreTurnoView cierreView = new CierreTurnoView(this);
        cierreView.lblEsperado.setText(formatoMoneda.format(totalEnCaja));

        cierreView.btnCerrarTurno.addActionListener(e -> {
            try {
                BigDecimal contado = new BigDecimal(cierreView.txtCantidad.getText());
                BigDecimal diferencia = contado.subtract(totalEnCaja);

                cierreView.lblDiferencia.setText(formatoMoneda.format(diferencia));

                if (diferencia.abs().compareTo(new BigDecimal("10.00")) > 0) {
                    cierreView.lblMensaje.setText("⚠️ Hay una diferencia considerable.");
                } else {
                    cierreView.lblMensaje.setText("✅ Todo bien.");
                }

                int confirm = JOptionPane.showConfirmDialog(this, "¿Confirmar cierre de turno?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.cerrarTurno(ventas, ingresos, egresos, ganancia, this);

                    System.out.println("✅ Turno cerrado correctamente.");

                    cierreView.dispose();

                    Sistema.reiniciarInstancia();
                    System.out.println("🧹 Sistema reiniciado (ventana principal cerrada).");

                    cerrarVentana();

                    volverAlLogin();
                }
            } catch (NumberFormatException ex) {
                cierreView.lblMensaje.setText("❌ Valor ingresado no válido.");
            }
        });

        cierreView.setVisible(true);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void cerrarVentana() {
        this.dispose();
        instance = null;
    }

    public void volverAlLogin() {
        Sistema.reiniciarInstancia();
        if (loginView != null) {
            loginView.setVisible(true);
            System.out.println("🔁 Mostrando ventana de Login original.");
        } else {
            System.out.println("⚠️ LoginView es null, no se puede mostrar login.");
        }
    }

    public void setDatosTurno(TurnoModel turno) {
        if (turno != null) {
            lblSaldoInicial.setText("Saldo Inicial: " + formatoMoneda.format(turno.getSaldoInicial()));
            lblVentas.setText("Has Vendido: " + formatoMoneda.format(turno.getTotalVentas() != null ? turno.getTotalVentas() : BigDecimal.ZERO));
            lblIngresos.setText("Ingresos: " + formatoMoneda.format(turno.getIngresos() != null ? turno.getIngresos() : BigDecimal.ZERO));
            lblEgresos.setText("Has Gastado: " + formatoMoneda.format(turno.getEgresos() != null ? turno.getEgresos() : BigDecimal.ZERO));
            lblGanancia.setText("Ganancia: " + formatoMoneda.format(turno.getGanancia() != null ? turno.getGanancia() : BigDecimal.ZERO));
            lblTotalEnCaja.setText("Total en Caja: " + formatoMoneda.format(turno.getTotalEnCaja() != null ? turno.getTotalEnCaja() : BigDecimal.ZERO));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            CorteCajeroView.getInstance("vic", login).setVisible(true);
        });
    }
}
