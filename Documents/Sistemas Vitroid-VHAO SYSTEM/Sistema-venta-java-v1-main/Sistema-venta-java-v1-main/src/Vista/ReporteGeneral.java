package Vista;
import Modelo.Conexion;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
/**
 *
 * @author vic
 */
public class ReporteGeneral extends javax.swing.JFrame {

 private JDateChooser midate;
    private JComboBox<String> tipoReporteCombo;
    private JLabel lblVentas, lblEgresos, lblIngresos, lblTotal;
    private JLabel valorVentas, valorEgresos, valorIngresos, valorTotal;
    
    public ReporteGeneral() {
        setTitle("Reporte General");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        cargarDatos();

        midate.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                cargarDatos();
            }
        });

        tipoReporteCombo.addActionListener(e -> cargarDatos());
    }
private void initComponents() {
        midate = new JDateChooser();
        midate.setDateFormatString("yyyy-MM-dd");
        midate.setDate(new java.util.Date());

        String[] tipos = {"Día", "Semana", "Mes", "Año"};
        tipoReporteCombo = new JComboBox<>(tipos);

        lblVentas = new JLabel("Ventas:");
        lblIngresos = new JLabel("Ingresos:");
        lblEgresos = new JLabel("Egresos:");
        lblTotal = new JLabel("Total en caja:");

        valorVentas = new JLabel("0.00");
        valorIngresos = new JLabel("0.00");
        valorEgresos = new JLabel("0.00");
        valorTotal = new JLabel("0.00");

        setLayout(new GridLayout(6, 2, 10, 10));
        add(new JLabel("Fecha:"));
        add(midate);
        add(new JLabel("Tipo de reporte:"));
        add(tipoReporteCombo);
        add(lblVentas);
        add(valorVentas);
        add(lblIngresos);
        add(valorIngresos);
        add(lblEgresos);
        add(valorEgresos);
        add(lblTotal);
        add(valorTotal);
    }
    private void cargarDatos() {
        if (midate.getDate() == null) return;

        java.util.Date fechaSeleccionada = midate.getDate();
        String tipoReporte = (String) tipoReporteCombo.getSelectedItem();

        double totalVentas = 0;
        double totalIngresos = 0;
        double totalEgresos = 0;

        try {
            switch (tipoReporte) {
                case "Día":
                    String fechaStr = new SimpleDateFormat("yyyy-MM-dd").format(fechaSeleccionada);
                    totalVentas = obtenerVentasDia(fechaStr);
                    totalIngresos = obtenerIngresosDia(fechaStr);
                    totalEgresos = obtenerEgresosDia(fechaStr);
                    break;

                case "Semana":
                    LocalDate localDateSemana = new java.sql.Date(fechaSeleccionada.getTime()).toLocalDate();
                    WeekFields weekFields = WeekFields.of(Locale.getDefault());
                    int semana = localDateSemana.get(weekFields.weekOfWeekBasedYear());
                    int anioSemana = localDateSemana.getYear();
                    totalVentas = obtenerVentasSemana(anioSemana, semana);
                    totalIngresos = obtenerIngresosSemana(anioSemana, semana);
                    totalEgresos = obtenerEgresosSemana(anioSemana, semana);
                    break;

                case "Mes":
                    LocalDate localDateMes = new java.sql.Date(fechaSeleccionada.getTime()).toLocalDate();
                    int mes = localDateMes.getMonthValue();
                    int anioMes = localDateMes.getYear();
                    totalVentas = obtenerVentasMes(anioMes, mes);
                    totalIngresos = obtenerIngresosMes(anioMes, mes);
                    totalEgresos = obtenerEgresosMes(anioMes, mes);
                    break;

                case "Año":
                    LocalDate localDateAnio = new java.sql.Date(fechaSeleccionada.getTime()).toLocalDate();
                    int anio = localDateAnio.getYear();
                    totalVentas = obtenerVentasAnio(anio);
                    totalIngresos = obtenerIngresosAnio(anio);
                    totalEgresos = obtenerEgresosAnio(anio);
                    break;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al obtener datos: " + e.getMessage());
        }

        double totalFinal = totalVentas + totalIngresos - totalEgresos;

        valorVentas.setText(String.format("%.2f", totalVentas));
        valorIngresos.setText(String.format("%.2f", totalIngresos));
        valorEgresos.setText(String.format("%.2f", totalEgresos));
        valorTotal.setText(String.format("%.2f", totalFinal));
    }
  // Métodos para obtener ventas
    private double obtenerVentasDia(String fecha) throws SQLException {
        return consultaSQL("SELECT SUM(total) FROM ventas WHERE DATE(fecha) = ?", ps -> ps.setString(1, fecha));
    }

    private double obtenerVentasSemana(int anio, int semana) throws SQLException {
        return consultaSQL("SELECT SUM(total) FROM ventas WHERE YEAR(fecha) = ? AND WEEK(fecha, 1) = ?", ps -> {
            ps.setInt(1, anio);
            ps.setInt(2, semana);
        });
    }

    private double obtenerVentasMes(int anio, int mes) throws SQLException {
        return consultaSQL("SELECT SUM(total) FROM ventas WHERE YEAR(fecha) = ? AND MONTH(fecha) = ?", ps -> {
            ps.setInt(1, anio);
            ps.setInt(2, mes);
        });
    }

    private double obtenerVentasAnio(int anio) throws SQLException {
        return consultaSQL("SELECT SUM(total) FROM ventas WHERE YEAR(fecha) = ?", ps -> ps.setInt(1, anio));
    }

    // Métodos para obtener ingresos
    private double obtenerIngresosDia(String fecha) throws SQLException {
        return consultaSQL("SELECT SUM(monto) FROM movimientos_caja WHERE DATE(fecha) = ? AND tipo = 'Ingreso'", ps -> ps.setString(1, fecha));
    }

    private double obtenerIngresosSemana(int anio, int semana) throws SQLException {
        return consultaSQL("SELECT SUM(monto) FROM movimientos_caja WHERE YEAR(fecha) = ? AND WEEK(fecha, 1) = ? AND tipo = 'Ingreso'", ps -> {
            ps.setInt(1, anio);
            ps.setInt(2, semana);
        });
    }

    private double obtenerIngresosMes(int anio, int mes) throws SQLException {
        return consultaSQL("SELECT SUM(monto) FROM movimientos_caja WHERE YEAR(fecha) = ? AND MONTH(fecha) = ? AND tipo = 'Ingreso'", ps -> {
            ps.setInt(1, anio);
            ps.setInt(2, mes);
        });
    }

    private double obtenerIngresosAnio(int anio) throws SQLException {
        return consultaSQL("SELECT SUM(monto) FROM movimientos_caja WHERE YEAR(fecha) = ? AND tipo = 'Ingreso'", ps -> ps.setInt(1, anio));
    }

    // Métodos para obtener egresos
    private double obtenerEgresosDia(String fecha) throws SQLException {
        return consultaSQL("SELECT SUM(monto) FROM movimientos_caja WHERE DATE(fecha) = ? AND tipo = 'Egreso'", ps -> ps.setString(1, fecha));
    }

    private double obtenerEgresosSemana(int anio, int semana) throws SQLException {
        return consultaSQL("SELECT SUM(monto) FROM movimientos_caja WHERE YEAR(fecha) = ? AND WEEK(fecha, 1) = ? AND tipo = 'Egreso'", ps -> {
            ps.setInt(1, anio);
            ps.setInt(2, semana);
        });
    }

    private double obtenerEgresosMes(int anio, int mes) throws SQLException {
        return consultaSQL("SELECT SUM(monto) FROM movimientos_caja WHERE YEAR(fecha) = ? AND MONTH(fecha) = ? AND tipo = 'Egreso'", ps -> {
            ps.setInt(1, anio);
            ps.setInt(2, mes);
        });
    }

    private double obtenerEgresosAnio(int anio) throws SQLException {
        return consultaSQL("SELECT SUM(monto) FROM movimientos_caja WHERE YEAR(fecha) = ? AND tipo = 'Egreso'", ps -> ps.setInt(1, anio));
    }

    // Método utilitario para consultas SQL
    private double consultaSQL(String sql, SQLConsumer<PreparedStatement> preparador) throws SQLException {
        double total = 0;
        try (Connection con = new Conexion().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            preparador.accept(ps);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) total = rs.getDouble(1);
        }
        return total;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
     /*
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
*/
    /**
     * @param args the command line arguments
     */
    
    @FunctionalInterface
    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReporteGeneral().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
