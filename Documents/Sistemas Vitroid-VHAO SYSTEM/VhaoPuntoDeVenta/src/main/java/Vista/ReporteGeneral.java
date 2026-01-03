package Vista;
import Controlador.CierreTurnoController;
import Modelo.Conexion;
import com.toedter.calendar.JDateChooser;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import Estilos.Estilos;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.TextStyle;
import java.util.Date;
import Modelo.CashInBoxDAO;
import Modelo.CierreTurnoModel;
import static Vista.OpcVentasDelDia.instance;
import java.math.BigDecimal;

/**
 *
 * @author vic
 */
public class ReporteGeneral extends javax.swing.JFrame {
 public static ReporteGeneral instance;
  private JLabel lblFechaActual, lblVentas, lblIngresos,lblSaldoInicial, lblEgresos, lblGanancia, lblTotal;
    private JLabel valorVentas, valorIngresos, valorSaldoInicial,valorEgresos, valorGanancia, valorTotal;

    private JButton btnHoy, btnSemana, btnMes, btnAnio, btnRango;
    private JDateChooser midate;
    private JComboBox<String> tipoReporteCombo;

  
    public ReporteGeneral() {
        setTitle("Reporte General");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null); // Centrado
        instance = this;
        initComponents();
    }
private void initComponents() {
    // Crear los paneles
    JPanel panelFechas = crearPanelFechas();
    JPanel panelDatos = crearPanelDatos();

    // Diseño vertical
    setLayout(new BorderLayout());
    add(panelFechas, BorderLayout.NORTH);
    add(panelDatos, BorderLayout.CENTER);
    cargarDatos();   
}

private JPanel crearPanelFechas() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

    // Panel superior con fecha y botón cerrar turno
    JPanel panelSuperior = new JPanel(new BorderLayout());
    panelSuperior.setBackground(Color.WHITE);

    lblFechaActual = new JLabel(getFechaActualFormateada(), SwingConstants.LEFT);
    lblFechaActual.setFont(new Font("SansSerif", Font.BOLD, 16));
    lblFechaActual.setForeground(new Color(40, 40, 40));

    JButton btnCerrarTurno = new JButton("Cerrar Turno");
    btnCerrarTurno.setFocusPainted(false);
    btnCerrarTurno.setBackground(new Color(220, 53, 69)); // rojo suave
    btnCerrarTurno.setForeground(Color.WHITE);
    btnCerrarTurno.setFont(new Font("SansSerif", Font.BOLD, 13));
    btnCerrarTurno.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnCerrarTurno.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    btnCerrarTurno.addActionListener(e -> {
        try {
            String textoTotal = valorTotal.getText().replace("$", "").replace(",", "").trim();
            BigDecimal efectivoEsperado = new BigDecimal(textoTotal);
            new CierreTurnoController(this, efectivoEsperado); // ← 'this' es el JFrame padre
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener el total en caja: " + ex.getMessage());
        }
    });

    panelSuperior.add(lblFechaActual, BorderLayout.CENTER);
    panelSuperior.add(btnCerrarTurno, BorderLayout.EAST);

    // Panel de botones de rango de fechas
    JPanel panelBotones = new JPanel(new GridLayout(1, 5, 10, 10));
    panelBotones.setBackground(Color.WHITE);

    btnHoy = crearBoton("Hoy");
    btnSemana = crearBoton("Semana");
    btnMes = crearBoton("Mes");
    btnAnio = crearBoton("Año");
    btnRango = crearBoton("Rango");

    panelBotones.add(btnHoy);
    panelBotones.add(btnSemana);
    panelBotones.add(btnMes);
    panelBotones.add(btnAnio);
    panelBotones.add(btnRango);

    // Panel contenedor vertical para separar con espacio
    JPanel contenedorCentro = new JPanel();
    contenedorCentro.setLayout(new BoxLayout(contenedorCentro, BoxLayout.Y_AXIS));
    contenedorCentro.setBackground(Color.WHITE);

    JPanel espacio = new JPanel();
    espacio.setPreferredSize(new Dimension(1, 10));
    espacio.setOpaque(false);

    contenedorCentro.add(espacio);         // espacio de separación
    contenedorCentro.add(panelBotones);    // botones

    // Agregar al panel principal
    panel.add(panelSuperior, BorderLayout.NORTH);
    panel.add(contenedorCentro, BorderLayout.CENTER);

    // Inicializar fecha y combo tipo
    midate = new JDateChooser();
    tipoReporteCombo = new JComboBox<>(new String[]{"Día", "Semana", "Mes", "Año"});
    midate.setDate(new Date());
    tipoReporteCombo.setSelectedItem("Día");

    // Acciones de los botones
    btnHoy.addActionListener(evt -> {
        midate.setDate(new Date());
        tipoReporteCombo.setSelectedItem("Día");
        lblFechaActual.setText(getFechaActualFormateada());
        cargarDatos();
    });

    btnSemana.addActionListener(evt -> {
        midate.setDate(new Date());
        tipoReporteCombo.setSelectedItem("Semana");
        lblFechaActual.setText(getFechaActualFormateada());
        cargarDatos();
    });

    btnMes.addActionListener(evt -> {
        midate.setDate(new Date());
        tipoReporteCombo.setSelectedItem("Mes");
        lblFechaActual.setText(getFechaActualFormateada());
        cargarDatos();
    });

    btnAnio.addActionListener(evt -> {
        midate.setDate(new Date());
        tipoReporteCombo.setSelectedItem("Año");
        lblFechaActual.setText(getFechaActualFormateada());
        cargarDatos();
    });

    btnRango.addActionListener(evt -> {
        JFrame padre = (JFrame) SwingUtilities.getWindowAncestor(panelBotones);
        FormRangoFechasDelCorte dialog = new FormRangoFechasDelCorte(padre, (inicio, fin) -> {
            cargarDatosRango(inicio, fin);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            lblFechaActual.setText("Del " + sdf.format(inicio) + " al " + sdf.format(fin));
        });
        dialog.setVisible(true);
    });

    return panel;
}

private JPanel crearPanelDatos() { //llena el panel de datos
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(10, 10, 10, 10);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    

    lblVentas = new JLabel("Has Vendido:");
    valorVentas = new JLabel("$0.00");

    lblIngresos = new JLabel("Ingreso :");
    valorIngresos = new JLabel("$0.00");
    
    lblSaldoInicial = new JLabel("Saldo Inicial en caja:");
    valorSaldoInicial = new JLabel("$0.00");
      
    lblEgresos = new JLabel("Has Gastado:");
    valorEgresos = new JLabel("$0.00");

    lblGanancia = new JLabel("Ganancia:");
    valorGanancia = new JLabel("$0.00");
    
    lblTotal = new JLabel("Saldo inicial  en caja:");
    valorTotal = new JLabel("$0.00");
    lblTotal = new JLabel("Total en caja:");
    valorTotal = new JLabel("$0.00");

    JButton btnDetalleVentas = new JButton("Detalle...");
    JButton btnDetalleIngresos = new JButton("Detalle...");
    JButton btnDetalleTotal = new JButton("Detalle...");
  Estilos.estiloEtiquetaGreen(lblVentas); Estilos.estiloEtiquetaGreen(valorVentas);
        Estilos.estiloEtiquetaRed(lblEgresos); Estilos.estiloEtiquetaRed(valorEgresos);
        Estilos.estiloEtiquetaBlue(lblGanancia); Estilos.estiloEtiquetaBlue(valorGanancia);
        
    agregarLinea(panel, gbc, 0, lblVentas, valorVentas, btnDetalleVentas);
    agregarLinea(panel, gbc, 1, lblIngresos, valorIngresos, btnDetalleIngresos);
    agregarLinea(panel, gbc, 2, lblSaldoInicial, valorSaldoInicial, null);
    agregarLinea(panel, gbc, 3, lblEgresos, valorEgresos, null);
    agregarLinea(panel, gbc, 4, lblGanancia, valorGanancia, null);
    agregarLinea(panel, gbc, 5, lblTotal, valorTotal, btnDetalleTotal);

    // Acciones de botones
    btnDetalleVentas.addActionListener(e -> new VentasDiaForm().setVisible(true));
    btnDetalleIngresos.addActionListener(e -> new verMovimientosCaja().setVisible(true));
    btnDetalleTotal.addActionListener(e -> new OpcVentasDelDia().setVisible(true));

    return panel;
}
private void cargarDatosRango(Date inicio, Date fin) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String fechaInicioStr = sdf.format(inicio);
    String fechaFinStr = sdf.format(fin);
 CashInBoxDAO cashDao = new CashInBoxDAO();
    try {
        double totalVentas = obtenerVentasRango(fechaInicioStr, fechaFinStr);
        double totalIngresos = obtenerIngresosRango(fechaInicioStr, fechaFinStr);
        double totalSaldoInicial = cashDao.obtenerSaldoInicialRango(fechaInicioStr, fechaFinStr);
        double totalEgresos = obtenerEgresosRango(fechaInicioStr, fechaFinStr);
        double ganancia = obtenerGananciaRango(fechaInicioStr, fechaFinStr);
        double totalFinal = totalVentas + totalIngresos - totalEgresos;

        valorVentas.setText(String.format("$%,.2f", totalVentas));
        valorIngresos.setText(String.format("$%,.2f", totalIngresos));
        valorSaldoInicial.setText(String.format("$%,.2f", totalSaldoInicial));
        valorEgresos.setText(String.format("$%,.2f", totalEgresos));
        valorGanancia.setText(String.format("$%,.2f", ganancia));
        valorTotal.setText(String.format("$%,.2f", totalFinal));

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al obtener datos del rango: " + e.getMessage());
    }
}

private void cargarDatos() {
    if (midate.getDate() == null) return;

    java.util.Date fechaSeleccionada = midate.getDate();
    String tipoReporte = (String) tipoReporteCombo.getSelectedItem();

    double totalVentas = 0;
    double totalIngresos = 0;
   double totalSaldoInicial = 0;
    double totalEgresos = 0;
    double ganancia = 0;
      CashInBoxDAO cashDao = new CashInBoxDAO(); // al principio del switch
        
    try {
        switch (tipoReporte) {
            case "Día":
                String fechaStr = new SimpleDateFormat("yyyy-MM-dd").format(fechaSeleccionada);
                totalVentas = obtenerVentasDia(fechaStr);
                totalIngresos = obtenerIngresosDia(fechaStr);
                totalSaldoInicial = cashDao.obtenerSaldoInicialDelDia();
                totalEgresos = obtenerEgresosDia(fechaStr);
                ganancia = obtenerGananciaDia(fechaStr);
                break;

            case "Semana":
                LocalDate localDateSemana = new java.sql.Date(fechaSeleccionada.getTime()).toLocalDate();
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int semana = localDateSemana.get(weekFields.weekOfWeekBasedYear());
                int anioSemana = localDateSemana.getYear();
                totalVentas = obtenerVentasSemana(anioSemana, semana);
                totalIngresos = obtenerIngresosSemana(anioSemana, semana);
                totalSaldoInicial = cashDao.obtenerSaldoInicialSemana(anioSemana, semana);
                totalEgresos = obtenerEgresosSemana(anioSemana, semana);
                ganancia = obtenerGananciaSemana(anioSemana, semana); // agregado
                break;

            case "Mes":
                LocalDate localDateMes = new java.sql.Date(fechaSeleccionada.getTime()).toLocalDate();
                int mes = localDateMes.getMonthValue();
                int anioMes = localDateMes.getYear();
                totalVentas = obtenerVentasMes(anioMes, mes);
                totalIngresos = obtenerIngresosMes(anioMes, mes);
                totalSaldoInicial = cashDao.obtenerSaldoInicialMes(anioMes, mes);
                totalEgresos = obtenerEgresosMes(anioMes, mes);
                ganancia = obtenerGananciaMes(anioMes, mes); // agregado
                break;

            case "Año":
                LocalDate localDateAnio = new java.sql.Date(fechaSeleccionada.getTime()).toLocalDate();
                int anio = localDateAnio.getYear();
                totalVentas = obtenerVentasAnio(anio);
                totalIngresos = obtenerIngresosAnio(anio);
                totalSaldoInicial = cashDao.obtenerSaldoInicialAnio(anio);
                totalEgresos = obtenerEgresosAnio(anio);
                ganancia = obtenerGananciaAnio(anio); // agregado
                break;
                
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al obtener datos: " + e.getMessage());
    }

    double totalFinal = totalVentas + totalIngresos +totalSaldoInicial- totalEgresos;

    valorVentas.setText("$" + String.format("%.2f", totalVentas));
    valorIngresos.setText("$" + String.format("%.2f", totalIngresos));
    valorSaldoInicial.setText("$" + String.format("%.2f", totalSaldoInicial));
    valorEgresos.setText("$" + String.format("%.2f", totalEgresos));
    valorGanancia.setText("$" + String.format("%.2f", ganancia));
    valorTotal.setText("$" + String.format("%.2f", totalFinal));
}

private void agregarLinea(JPanel panel, GridBagConstraints gbc, int fila,
                              JLabel etiqueta, JLabel valor, JButton boton) {
        gbc.gridy = fila;

        gbc.gridx = 0;
        gbc.weightx = 0.2;
        panel.add(etiqueta, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        panel.add(valor, gbc);

        if (boton != null) {
            gbc.gridx = 2;
            gbc.weightx = 0.5;
            gbc.anchor = GridBagConstraints.EAST;
            panel.add(boton, gbc);
        }
    }

private JButton crearBoton(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setBackground(new Color(230, 230, 230));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        return btn;
    }

    private String getFechaActualFormateada() {
        LocalDate hoy = LocalDate.now();
        String diaSemana = hoy.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es"));
        String mes = hoy.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es"));
        return diaSemana + ", " + hoy.getDayOfMonth() + " " + capitalize(mes) + ". " + hoy.getYear();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

 // Método utilitario para consultas SQL con pool y cierre seguro
private double consultaSQL(String sql, SQLConsumer<PreparedStatement> preparador) throws SQLException {
    double total = 0;
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        preparador.accept(ps);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        }
    }
    return total;
}

// Métodos para obtener ventas

private double obtenerVentasDia(String fecha) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(total), 0) FROM ventas WHERE DATE(fecha) = ?", ps -> ps.setString(1, fecha));
}

private double obtenerVentasSemana(int anio, int semana) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(total), 0) FROM ventas WHERE YEAR(fecha) = ? AND WEEK(fecha, 1) = ?", ps -> {
        ps.setInt(1, anio);
        ps.setInt(2, semana);
    });
}

private double obtenerVentasMes(int anio, int mes) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(total), 0) FROM ventas WHERE YEAR(fecha) = ? AND MONTH(fecha) = ?", ps -> {
        ps.setInt(1, anio);
        ps.setInt(2, mes);
    });
}

private double obtenerVentasAnio(int anio) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(total), 0) FROM ventas WHERE YEAR(fecha) = ?", ps -> ps.setInt(1, anio));
}
private double obtenerVentasRango(String fechaInicio, String fechaFin) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(total), 0) FROM ventas WHERE DATE(fecha) BETWEEN ? AND ?", ps -> {
        ps.setString(1, fechaInicio);
        ps.setString(2, fechaFin);
    });
}

// Métodos para obtener ingresos

private double obtenerIngresosDia(String fecha) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE DATE(fecha) = ? AND tipo = 'Ingreso'", ps -> ps.setString(1, fecha));
}

private double obtenerIngresosSemana(int anio, int semana) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE YEAR(fecha) = ? AND WEEK(fecha, 1) = ? AND tipo = 'Ingreso'", ps -> {
        ps.setInt(1, anio);
        ps.setInt(2, semana);
    });
}

private double obtenerIngresosMes(int anio, int mes) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE YEAR(fecha) = ? AND MONTH(fecha) = ? AND tipo = 'Ingreso'", ps -> {
        ps.setInt(1, anio);
        ps.setInt(2, mes);
    });
}

private double obtenerIngresosAnio(int anio) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE YEAR(fecha) = ? AND tipo = 'Ingreso'", ps -> ps.setInt(1, anio));
}
private double obtenerIngresosRango(String fechaInicio, String fechaFin) throws SQLException {
    return consultaSQL(
        "SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE DATE(fecha) BETWEEN ? AND ? AND tipo = 'Ingreso'",
        ps -> {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
        }
    );
}

// Métodos para obtener egresos

private double obtenerEgresosDia(String fecha) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE DATE(fecha) = ? AND tipo = 'Egreso'", ps -> ps.setString(1, fecha));
}

private double obtenerEgresosSemana(int anio, int semana) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE YEAR(fecha) = ? AND WEEK(fecha, 1) = ? AND tipo = 'Egreso'", ps -> {
        ps.setInt(1, anio);
        ps.setInt(2, semana);
    });
}

private double obtenerEgresosMes(int anio, int mes) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE YEAR(fecha) = ? AND MONTH(fecha) = ? AND tipo = 'Egreso'", ps -> {
        ps.setInt(1, anio);
        ps.setInt(2, mes);
    });
}

private double obtenerEgresosAnio(int anio) throws SQLException {
    return consultaSQL("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE YEAR(fecha) = ? AND tipo = 'Egreso'", ps -> ps.setInt(1, anio));
}
private double obtenerEgresosRango(String fechaInicio, String fechaFin) throws SQLException {
    return consultaSQL(
        "SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE DATE(fecha) BETWEEN ? AND ? AND tipo = 'Egreso'",
        ps -> {
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
        }
    );
}

// Métodos para obtener ganancia

private double obtenerGananciaDia(String fecha) throws SQLException {
    double ganancia = 0;
    String sql = "SELECT d.cantidad, d.precio, p.preciocompra " +
                 "FROM detalle d " +
                 "JOIN ventas v ON d.id_venta = v.id " +
                 "JOIN productos p ON d.id_pro = p.id " +
                 "WHERE DATE(v.fecha) = ? AND p.preciocompra > 0";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, fecha);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                double precioVenta = rs.getDouble("precio");
                double precioCompra = rs.getDouble("preciocompra");
                ganancia += (precioVenta - precioCompra) * cantidad;
            }
        }
    }
    return ganancia;
}


private double obtenerGananciaSemana(int anio, int semana) throws SQLException {
    double ganancia = 0;
    String sql = "SELECT d.cantidad, d.precio, p.preciocompra " +
                 "FROM detalle d " +
                 "JOIN ventas v ON d.id_venta = v.id " +
                 "JOIN productos p ON d.id_pro = p.id " +
                 "WHERE YEAR(v.fecha) = ? AND WEEK(v.fecha, 1) = ? AND p.preciocompra > 0";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, anio);
        ps.setInt(2, semana);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                double precioVenta = rs.getDouble("precio");
                double precioCompra = rs.getDouble("preciocompra");
                ganancia += (precioVenta - precioCompra) * cantidad;
            }
        }
    }
    return ganancia;
}


private double obtenerGananciaMes(int anio, int mes) throws SQLException {
    double ganancia = 0;
    String sql = "SELECT d.cantidad, d.precio, p.preciocompra " +
                 "FROM detalle d " +
                 "JOIN ventas v ON d.id_venta = v.id " +
                 "JOIN productos p ON d.id_pro = p.id " +
                 "WHERE YEAR(v.fecha) = ? AND MONTH(v.fecha) = ? AND p.preciocompra > 0";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, anio);
        ps.setInt(2, mes);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                double precioVenta = rs.getDouble("precio");
                double precioCompra = rs.getDouble("preciocompra");
                ganancia += (precioVenta - precioCompra) * cantidad;
            }
        }
    }
    return ganancia;
}

private double obtenerGananciaAnio(int anio) throws SQLException {
    double ganancia = 0;
    String sql = "SELECT d.cantidad, d.precio, p.preciocompra " +
                 "FROM detalle d " +
                 "JOIN ventas v ON d.id_venta = v.id " +
                 "JOIN productos p ON d.id_pro = p.id " +
                 "WHERE YEAR(v.fecha) = ? AND p.preciocompra > 0";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, anio);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                double precioVenta = rs.getDouble("precio");
                double precioCompra = rs.getDouble("preciocompra");
                ganancia += (precioVenta - precioCompra) * cantidad;
            }
        }
    }
    return ganancia;
}

private double obtenerGananciaRango(String fechaInicio, String fechaFin) throws SQLException {
    double ganancia = 0;
    String sql = "SELECT d.cantidad, d.precio, p.preciocompra " +
                 "FROM detalle d " +
                 "JOIN ventas v ON d.id_venta = v.id " +
                 "JOIN productos p ON d.id_pro = p.id " +
                 "WHERE DATE(v.fecha) BETWEEN ? AND ? AND p.preciocompra > 0";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, fechaInicio);
        ps.setString(2, fechaFin);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                double precioVenta = rs.getDouble("precio");
                double precioCompra = rs.getDouble("preciocompra");
                ganancia += (precioVenta - precioCompra) * cantidad;
            }
        }
    }
    return ganancia;
}
//mostrar el "Total en caja" del formulario ReporteGeneral dentro de ArchedBox
public BigDecimal getTotalEnCaja() { 
    try {
        String textoTotal = valorTotal.getText().replace("$", "").replace(",", "").trim();
        return new BigDecimal(textoTotal);
    } catch (Exception e) {
        return BigDecimal.ZERO;
    }
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
