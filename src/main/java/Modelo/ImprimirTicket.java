package Modelo;

import Modelo.Conexion;
import java.sql.*;
import javax.print.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ImprimirTicket {

    /**
     * Obtiene el folio real de la venta a partir del ID interno.
     */
    private static int obtenerFolio(int idVenta) {
        int folio = 0;
        try (Connection con = Conexion.getConnection()) {
            String sql = "SELECT folio FROM ventas WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        folio = rs.getInt("folio");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return folio;
    }

    /**
     * Envía el contenido del ticket a la impresora POS-58.
     */
    public static void imprimir(String contenidoTicket) {
        try {
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
            PrintService thermalPrinter = null;

            for (PrintService service : services) {
                if (service.getName().contains("POS-58")) {
                    thermalPrinter = service;
                    break;
                }
            }

            if (thermalPrinter == null) {
                System.out.println("Impresora POS-58 no encontrada.");
                return;
            }

            byte[] bytes = contenidoTicket.getBytes(StandardCharsets.ISO_8859_1);

            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(bytes, flavor, null);

            DocPrintJob job = thermalPrinter.createPrintJob();
            job.print(doc, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =============================================
    // TICKET EFECTIVO
    // =============================================
   public static String generarTicketEfectivo(int idVenta, double pago, double cambio, String tipoPago) {
    StringBuilder sb = new StringBuilder();
    int folio = obtenerFolio(idVenta);

    try (Connection con = Conexion.getConnection()) {

        // Datos empresa
        String nombreNegocio = "Mi Negocio";
        String direccion = "Mi Dirección";
        String telefono = "Mi Teléfono";

        String sqlEmpresa =
            "SELECT e.nombre, e.direccion, e.telefono " +
            "FROM empresa e " +
            "JOIN ventas v ON v.id_empresa = e.id_empresa " +
            "WHERE v.id = ?";

        try (PreparedStatement psEmp = con.prepareStatement(sqlEmpresa)) {
            psEmp.setInt(1, idVenta);
            try (ResultSet rs = psEmp.executeQuery()) {
                if (rs.next()) {
                    nombreNegocio = rs.getString("nombre");
                    direccion = rs.getString("direccion");
                    telefono = rs.getString("telefono");
                }
            }
        }

        sb.append("     ").append(nombreNegocio).append("\n");
        sb.append(centrarConSaltos(direccion, 32));
        sb.append("Tel: ").append(telefono).append("\n");
        sb.append("------------------------------\n");
        sb.append("Folio: ").append(folio).append("\n");

        // Fecha y hora
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-06:00"));
        Date ahora = new Date();
        SimpleDateFormat sdfTicket = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sb.append("Fecha: ").append(sdfTicket.format(ahora)).append("\n");

        // Datos venta y cliente
        String sqlVenta =
            "SELECT v.total, c.nombre AS cliente " +
            "FROM ventas v LEFT JOIN clientes c ON v.cliente = c.id " +
            "WHERE v.id = ?";
        double totalVenta = 0;
        try (PreparedStatement psVenta = con.prepareStatement(sqlVenta)) {
            psVenta.setInt(1, idVenta);
            try (ResultSet rsVenta = psVenta.executeQuery()) {
                if (rsVenta.next()) {
                    String cliente = rsVenta.getString("cliente");
                    sb.append("Cliente: ")
                      .append(cliente != null ? cliente : "Público en general")
                      .append("\n");
                    totalVenta = rsVenta.getDouble("total");
                }
            }
        }

        sb.append("------------------------------\n");

        // Detalles venta (incluyendo proveedor para distinguir verdulería)
        String sqlDetalle =
            "SELECT p.nombre, d.cantidad, d.precio, pr.nombre AS proveedor " +
            "FROM detalle d " +
            "JOIN productos p ON d.id_pro = p.id " +
            "JOIN proveedor pr ON p.proveedor = pr.id " +
            "WHERE d.id_venta = ?";
        try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
            psDetalle.setInt(1, idVenta);
            try (ResultSet rsDetalle = psDetalle.executeQuery()) {
                while (rsDetalle.next()) {
                    String nombreProd = rsDetalle.getString("nombre");
                    double cantidad = rsDetalle.getDouble("cantidad");   // 👈 antes getInt
                    double precioUnit = rsDetalle.getDouble("precio");
                    String proveedor = rsDetalle.getString("proveedor");
                    double subTotal = cantidad * precioUnit;

                    sb.append(nombreProd).append("\n");

                    if ("verduleria".equalsIgnoreCase(proveedor)) {
                        // cantidad ya está en kilos (ej. 0.200 kg), NO se divide entre 1000
                        sb.append(String.format("%.3fkg x$%.2f/kg   $%.2f\n",
                                cantidad, precioUnit, subTotal));
                    } else {
                        // productos normales
                        sb.append(formatearLineaProducto(cantidad, precioUnit, subTotal));
                    }
                }
            }
        }

        sb.append("------------------------------\n");
        sb.append("Tipo: ").append(tipoPago).append("\n");
        sb.append(String.format("%-12s $%8.2f\n", "TOTAL:", totalVenta));
        sb.append(String.format("%-12s $%8.2f\n", "PAGA CON:", pago));
        sb.append(String.format("%-12s $%8.2f\n", "CAMBIO:", cambio));
        sb.append("------------------------------\n");
        sb.append("¡Gracias por su compra!\n");
        sb.append("USAMOS VHAO PUNTO DE VENTAS\n\n\n");

    } catch (SQLException e) {
        e.printStackTrace();
        sb.append("Error al generar ticket.\n");
    }

    return sb.toString();
}

    // =============================================
    // TICKET TARJETA
    // =============================================
    public static String generarTicketTarjeta(int idVenta, double comision, String tipoPago, double subtotal) {
        StringBuilder sb = new StringBuilder();
        int folio = obtenerFolio(idVenta);

        
            
               try (Connection con = Conexion.getConnection()) {

            // Datos empresa
            String nombreNegocio = "Mi Negocio";
            String direccion = "Mi Dirección";
            String telefono = "Mi Teléfono";

            String sqlEmpresa = 
                "SELECT e.nombre, e.direccion, e.telefono " +
                "FROM empresa e " +
                "JOIN ventas v ON v.id_empresa = e.id_empresa " +
                "WHERE v.id = ?";
            
            
             try (PreparedStatement psEmp = con.prepareStatement(sqlEmpresa)) {
                psEmp.setInt(1, idVenta);
                try (ResultSet rs = psEmp.executeQuery()) {
                    if (rs.next()) {
                        nombreNegocio = rs.getString("nombre");
                        direccion = rs.getString("direccion");
                        telefono = rs.getString("telefono");
                    }
                }
            }

            sb.append("     ").append(nombreNegocio).append("\n");
            sb.append(centrarConSaltos(direccion, 32));
            sb.append("Telefono: ").append(telefono).append("\n");
            sb.append("----------------------\n");
            sb.append("Folio: ").append(folio).append("\n");

            // Fecha y hora
            TimeZone.setDefault(TimeZone.getTimeZone("GMT-06:00"));
            Date ahora = new Date();
            SimpleDateFormat sdfTicket = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sb.append("Fecha: ").append(sdfTicket.format(ahora)).append("\n");

            // Cliente y total
            String sqlVenta =
                "SELECT v.total, c.nombre AS cliente " +
                "FROM ventas v LEFT JOIN clientes c ON v.cliente = c.id " +
                "WHERE v.id = ?";
            double totalVenta = 0;
            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta)) {
                psVenta.setInt(1, idVenta);
                try (ResultSet rsVenta = psVenta.executeQuery()) {
                    if (rsVenta.next()) {
                        String cliente = rsVenta.getString("cliente");
                        sb.append("Cliente: ").append(cliente != null ? cliente : "Público en general").append("\n");
                        totalVenta = rsVenta.getDouble("total");
                    }
                }
            }

            sb.append("----------------------\n");

            // Detalles productos
            String sqlDetalle =
                "SELECT p.nombre, d.cantidad, d.precio " +
                "FROM detalle d JOIN productos p ON d.id_pro = p.id " +
                "WHERE d.id_venta = ?";
            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
                psDetalle.setInt(1, idVenta);
                try (ResultSet rsDetalle = psDetalle.executeQuery()) {
                    while (rsDetalle.next()) {
                        String nombreProd = rsDetalle.getString("nombre");
                        double cantidad = rsDetalle.getDouble("cantidad");   // 👈 antes getInt
                        double precioUnit = rsDetalle.getDouble("precio");
                        double subTotal = cantidad * precioUnit;

                        sb.append(nombreProd).append("\n");
                        sb.append(formatearLineaProducto(cantidad, precioUnit, subTotal));
                    }
                }
            }

            sb.append("----------------------\n");
            sb.append("Tipo: ").append(tipoPago).append("\n");
            sb.append(String.format("%-12s $%8.2f\n", "SUBTOTAL:", subtotal));
            sb.append(String.format("%-12s $%8.2f\n", "COMISION:", comision));
            sb.append(String.format("%-12s $%8.2f\n", "TOTAL:", totalVenta));
            sb.append("----------------------\n");
            sb.append("¡Gracias por su compra!\n");
            sb.append("USAMOS VHAO PUNTO DE VENTAS\n\n\n");

        } catch (SQLException e) {
            e.printStackTrace();
            sb.append("Error al generar ticket.\n");
        }

        return sb.toString();
    }
// Dentro de la clase ImprimirTicket.java

// =============================================
// TICKET CRÉDITO CON TARJETA
// =============================================
public static String generarTicketCreditoConTarjeta(int idVenta, String cliente, double subtotal, double comision, double total, JTable tablaCredito) {
    StringBuilder sb = new StringBuilder();
    int folio = obtenerFolio(idVenta);

    String nombreNegocio = "Mi Negocio";
    String direccion = "Mi Dirección";
    String telefono = "Mi Teléfono";

    try (Connection con = Conexion.getConnection()) {
        // Obtener id_empresa de la venta
        int idEmpresa = 0;
        try (PreparedStatement psEmp = con.prepareStatement("SELECT id_empresa FROM ventas WHERE id = ?")) {
            psEmp.setInt(1, idVenta);
            try (ResultSet rsEmp = psEmp.executeQuery()) {
                if (rsEmp.next()) {
                    idEmpresa = rsEmp.getInt("id_empresa");
                }
            }
        }

        // Obtener datos de la empresa
        if (idEmpresa > 0) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT nombre, direccion, telefono FROM empresa WHERE id_empresa = ?")) {
                ps.setInt(1, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nombreNegocio = rs.getString("nombre");
                        direccion = rs.getString("direccion");
                        telefono = rs.getString("telefono");
                    }
                }
            }
        }

        // Encabezado
        sb.append("     ").append(nombreNegocio).append("\n");
        sb.append(centrarConSaltos(direccion, 32));
        sb.append("Tel: ").append(telefono).append("\n");
        sb.append("------------------------------\n");
        sb.append("Folio: ").append(folio).append("\n");

        // Fecha y hora
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-06:00"));
        Date ahora = new Date();
        SimpleDateFormat sdfTicket = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sb.append("Fecha: ").append(sdfTicket.format(ahora)).append("\n");

        // Cliente
        sb.append("Cliente: ").append(cliente != null ? cliente : "Público en general").append("\n");
        sb.append("------------------------------\n");

        // Detalles de productos / abonos
        for (int i = 0; i < tablaCredito.getRowCount(); i++) {
            String nombreProd = tablaCredito.getValueAt(i, 2).toString(); // columna 2 = nombre
            int cantidad = Integer.parseInt(tablaCredito.getValueAt(i, 3).toString()); // columna 3 = cantidad
            double precio = Double.parseDouble(tablaCredito.getValueAt(i, 4).toString()); // columna 4 = precio
            double sub = cantidad * precio;
            sb.append(nombreProd).append("\n");
            sb.append(String.format("%dx$%.2f   $%.2f\n", cantidad, precio, sub));
        }

        sb.append("------------------------------\n");
        sb.append(String.format("%-12s $%.2f\n", "SUBTOTAL:", subtotal));
        sb.append(String.format("%-12s $%.2f\n", "COMISION:", comision));
        sb.append(String.format("%-12s $%.2f\n", "TOTAL:", total));
        sb.append("Tipo: Tarjeta Crédito\n");
        sb.append("------------------------------\n");
        sb.append("¡Gracias por su compra!\n");
        sb.append("USAMOS VHAO PUNTO DE VENTAS\n\n");

    } catch (SQLException e) {
        e.printStackTrace();
        sb.append("Error al generar ticket.\n");
    }

    return sb.toString();
}

// =============================================
// TICKET CRÉDITO MIXTO (EFECTIVO + TARJETA)
// =============================================
public static String generarTicketCreditoMixto(int idVenta, String cliente, double subtotal, double comision, double total, JTable tablaCredito) {
    StringBuilder sb = new StringBuilder();
    int folio = obtenerFolio(idVenta);

    String nombreNegocio = "Mi Negocio";
    String direccion = "Mi Dirección";
    String telefono = "Mi Teléfono";

    try (Connection con = Conexion.getConnection()) {
        // Obtener id_empresa de la venta
        int idEmpresa = 0;
        try (PreparedStatement psEmp = con.prepareStatement("SELECT id_empresa FROM ventas WHERE id = ?")) {
            psEmp.setInt(1, idVenta);
            try (ResultSet rsEmp = psEmp.executeQuery()) {
                if (rsEmp.next()) {
                    idEmpresa = rsEmp.getInt("id_empresa");
                }
            }
        }

        // Obtener datos de la empresa
        if (idEmpresa > 0) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT nombre, direccion, telefono FROM empresa WHERE id_empresa = ?")) {
                ps.setInt(1, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nombreNegocio = rs.getString("nombre");
                        direccion = rs.getString("direccion");
                        telefono = rs.getString("telefono");
                    }
                }
            }
        }

        // Encabezado
        sb.append("     ").append(nombreNegocio).append("\n");
        sb.append(centrarConSaltos(direccion, 32));
        sb.append("Tel: ").append(telefono).append("\n");
        sb.append("------------------------------\n");
        sb.append("Folio: ").append(folio).append("\n");

        // Fecha y hora
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-06:00"));
        Date ahora = new Date();
        SimpleDateFormat sdfTicket = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sb.append("Fecha: ").append(sdfTicket.format(ahora)).append("\n");

        // Cliente
        sb.append("Cliente: ").append(cliente != null ? cliente : "Público en general").append("\n");
        sb.append("------------------------------\n");

        // Detalles de productos / abonos
        for (int i = 0; i < tablaCredito.getRowCount(); i++) {
            String nombreProd = tablaCredito.getValueAt(i, 2).toString(); // columna 2 = nombre
            int cantidad = Integer.parseInt(tablaCredito.getValueAt(i, 3).toString()); // columna 3 = cantidad
            double precio = Double.parseDouble(tablaCredito.getValueAt(i, 4).toString()); // columna 4 = precio
            double sub = cantidad * precio;
            sb.append(nombreProd).append("\n");
            sb.append(String.format("%dx$%.2f   $%.2f\n", cantidad, precio, sub));
        }

        sb.append("------------------------------\n");
        sb.append(String.format("%-12s $%.2f\n", "SUBTOTAL:", subtotal));
        sb.append(String.format("%-12s $%.2f\n", "COMISION:", comision));
        sb.append(String.format("%-12s $%.2f\n", "TOTAL:", total));
        sb.append("Tipo: Mixto Crédito\n");
        sb.append("------------------------------\n");
        sb.append("¡Gracias por su compra!\n");
        sb.append("USAMOS VHAO PUNTO DE VENTAS\n\n");

    } catch (SQLException e) {
        e.printStackTrace();
        sb.append("Error al generar ticket.\n");
    }

    return sb.toString();
}

// =============================================
// TICKET CRÉDITO NORMAL
// =============================================
public static String generarTicketCredito(int idVenta, double total, String tipoPago, JTable tablaCredito, double pagaCon, double cambio, String cliente) {
    StringBuilder sb = new StringBuilder();
    int folio = obtenerFolio(idVenta);

    String nombreNegocio = "Mi Negocio";
    String direccion = "Mi Dirección";
    String telefono = "Mi Teléfono";

    try (Connection con = Conexion.getConnection()) {
        // Obtener id_empresa de la venta
        int idEmpresa = 0;
        try (PreparedStatement psEmp = con.prepareStatement("SELECT id_empresa FROM ventas WHERE id = ?")) {
            psEmp.setInt(1, idVenta);
            try (ResultSet rsEmp = psEmp.executeQuery()) {
                if (rsEmp.next()) {
                    idEmpresa = rsEmp.getInt("id_empresa");
                }
            }
        }

        // Obtener datos de la empresa
        if (idEmpresa > 0) {
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT nombre, direccion, telefono FROM empresa WHERE id_empresa = ?")) {
                ps.setInt(1, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nombreNegocio = rs.getString("nombre");
                        direccion = rs.getString("direccion");
                        telefono = rs.getString("telefono");
                    }
                }
            }
        }

        // Encabezado
        sb.append("     ").append(nombreNegocio).append("\n");
        sb.append(centrarConSaltos(direccion, 32));
        sb.append("Tel: ").append(telefono).append("\n");
        sb.append("------------------------------\n");
        sb.append("Folio: ").append(folio).append("\n");

        // Fecha y hora
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-06:00"));
        Date ahora = new Date();
        SimpleDateFormat sdfTicket = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sb.append("Fecha: ").append(sdfTicket.format(ahora)).append("\n");

        // Cliente
        sb.append("Cliente: ").append(cliente != null ? cliente : "Público en general").append("\n");
        sb.append("------------------------------\n");

        // Detalles de productos
        for (int i = 0; i < tablaCredito.getRowCount(); i++) {
            String nombreProd = tablaCredito.getValueAt(i, 2).toString(); // columna 2 = nombre
            int cantidad = Integer.parseInt(tablaCredito.getValueAt(i, 3).toString()); // columna 3 = cantidad
            double precio = Double.parseDouble(tablaCredito.getValueAt(i, 4).toString()); // columna 4 = precio
            double sub = cantidad * precio;
            sb.append(nombreProd).append("\n");
            sb.append(String.format("%dx$%.2f   $%.2f\n", cantidad, precio, sub));
        }

        sb.append("------------------------------\n");
        sb.append(String.format("%-12s $%.2f\n", "TOTAL:", total));
        sb.append(String.format("%-12s $%.2f\n", "PAGA CON:", pagaCon));
        sb.append(String.format("%-12s $%.2f\n", "CAMBIO:", cambio));
        sb.append("Tipo: " + tipoPago + "\n");
        sb.append("------------------------------\n");
        sb.append("¡Gracias por su compra!\n");
        sb.append("USAMOS VHAO PUNTO DE VENTAS\n\n");

    } catch (SQLException e) {
        e.printStackTrace();
        sb.append("Error al generar ticket.\n");
    }

    return sb.toString();
}

    // =============================================
    // UTILITY
    // =============================================
    private static String centrarConSaltos(String texto, int ancho) {
        StringBuilder sb = new StringBuilder();
        if (texto.length() < ancho) {
            int espacios = (ancho - texto.length()) / 2;
            for (int i = 0; i < espacios; i++) sb.append(" ");
            sb.append(texto).append("\n");
        } else {
            sb.append(texto).append("\n");
        }
        return sb.toString();
    }

    /**
     * Formatea la línea de un producto en el ticket.
     * Si la cantidad es un número entero (ej. 1.0, 2.0), se muestra sin decimales.
     * Si tiene decimales (ej. 0.2, 1.5 -> verdulería u otros productos por peso), se muestran con 3 decimales.
     */
    private static String formatearLineaProducto(double cantidad, double precioUnit, double subTotal) {
        String cantidadStr;
        if (cantidad == Math.floor(cantidad)) {
            cantidadStr = String.valueOf((int) cantidad);
        } else {
            cantidadStr = String.format("%.3f", cantidad);
        }
        return String.format("%sx$%.2f   $%.2f\n", cantidadStr, precioUnit, subTotal);
    }
}