package Reportes;

import Modelo.Conexion;
import java.sql.*;
import javax.print.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JTable;

public class ImprimirTicket {

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

    /**
     * Genera el contenido del ticket de venta.
     *
     * @param idVenta  ID de la venta recién registrada.
     * @param pago     Monto que entregó el cliente.
     * @param cambio   Monto de cambio (pago - total).
     * @return         Cadena con todo el texto del ticket.
     */
    //ticket de efectivo
  public static String generarTicketReal(int idVenta, double pago, double cambio,String tipoPago) {
    StringBuilder sb = new StringBuilder();

    try (Connection con = Conexion.getConnection()) {

        // 1) Obtener nombre del negocio desde config
        String nombreNegocio = "Mi Negocio";
        String direccion = "Mi Dirección";
        String telefono = "Mi telefono";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT nombre,direccion,telefono FROM config LIMIT 1")) {
            if (rs.next()) {
                nombreNegocio = rs.getString("nombre");
                direccion = rs.getString("direccion");
                telefono = rs.getString("telefono");
            }
        }

        sb.append("     ").append(nombreNegocio).append("\n");
        sb.append(centrarConSaltos(direccion, 32));
        sb.append("Telefono: ").append(telefono).append("\n");
        sb.append("    ----------------------\n");
        sb.append("Venta ID: ").append(idVenta).append("\n");

        // Forzar zona horaria y formato fecha/hora
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-06:00"));
        Date ahora = new Date();
        SimpleDateFormat sdfTicket = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String horaActual = sdfTicket.format(ahora);
        sb.append("Fecha: ").append(horaActual).append("\n");

        // Datos de venta y cliente
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

        sb.append("----------------------\n");

        // Detalles de la venta
        String sqlDetalle =
            "SELECT p.nombre, d.cantidad, d.precio " +
            "FROM detalle d JOIN productos p ON d.id_pro = p.id " +
            "WHERE d.id_venta = ?";
        try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
            psDetalle.setInt(1, idVenta);
            try (ResultSet rsDetalle = psDetalle.executeQuery()) {
                while (rsDetalle.next()) {
                    String nombreProd = rsDetalle.getString("nombre");
                    int cantidad = rsDetalle.getInt("cantidad");
                    double precioUnit = rsDetalle.getDouble("precio");
                    double subTotal = cantidad * precioUnit;

                    sb.append(nombreProd).append("\n");
                    sb.append(String.format("%dx$%.2f   $%.2f\n", cantidad, precioUnit, subTotal));
                }
            }
        }

        sb.append("----------------------\n");
              // Línea de tipo de pago
        sb.append("Tipo: ").append(tipoPago).append("\n");
     
        sb.append(String.format("%-12s $%8.2f\n", "TOTAL:", totalVenta));
        sb.append(String.format("%-12s $%8.2f\n", "PAGA CON:", pago));
        sb.append(String.format("%-12s $%8.2f\n", "CAMBIO:", cambio));

        sb.append("----------------------\n");
        sb.append("¡Gracias por su compra!\n");
        sb.append("USAMOS VHAO PUNTO DE VENTAS\n");
        sb.append("\n\n\n");

    } catch (SQLException e) {
        e.printStackTrace();
        sb.append("Error al generar ticket.\n");
    }

    return sb.toString();
}
  
//ticket venta terminal
public static String generarTicketReal(int idVenta, double comision, String tipoPago, double subtotal) {
    StringBuilder sb = new StringBuilder();

    try (Connection con = Conexion.getConnection()) {

        // 1) Datos del negocio desde config
        String nombreNegocio = "Mi Negocio";
        String direccion = "Mi Dirección";
        String telefono = "Mi telefono";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT nombre,direccion,telefono FROM config LIMIT 1")) {
            if (rs.next()) {
                nombreNegocio = rs.getString("nombre");
                direccion = rs.getString("direccion");
                telefono = rs.getString("telefono");
            }
        }

        sb.append("     ").append(nombreNegocio).append("\n");
        sb.append(centrarConSaltos(direccion, 32));
        sb.append("Telefono: ").append(telefono).append("\n");
        sb.append("    ----------------------\n");
        sb.append("Venta ID: ").append(idVenta).append("\n");

        // Fecha y hora actual
        TimeZone.setDefault(TimeZone.getTimeZone("GMT-06:00"));
        Date ahora = new Date();
        SimpleDateFormat sdfTicket = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String horaActual = sdfTicket.format(ahora);
        sb.append("Fecha: ").append(horaActual).append("\n");

        // Cliente y total (sin comisión)
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

        sb.append("----------------------\n");

        // Detalles de productos
        String sqlDetalle =
            "SELECT p.nombre, d.cantidad, d.precio " +
            "FROM detalle d JOIN productos p ON d.id_pro = p.id " +
            "WHERE d.id_venta = ?";
        try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
            psDetalle.setInt(1, idVenta);
            try (ResultSet rsDetalle = psDetalle.executeQuery()) {
                while (rsDetalle.next()) {
                    String nombreProd = rsDetalle.getString("nombre");
                    int cantidad = rsDetalle.getInt("cantidad");
                    double precioUnit = rsDetalle.getDouble("precio");
                    double subTotal = cantidad * precioUnit;

                    sb.append(nombreProd).append("\n");
                    sb.append(String.format("%dx$%.2f   $%.2f\n", cantidad, precioUnit, subTotal));
                }
            }
        }

        sb.append("----------------------\n");

        // Tipo de pago
        sb.append("Tipo: ").append(tipoPago).append("\n");

        // Mostrar subtotal, comisión y total correctamente
        sb.append(String.format("%-12s $%8.2f\n", "SUBTOTAL:", subtotal));
        sb.append(String.format("%-12s $%8.2f\n", "COMISION:", comision));
        sb.append(String.format("%-12s $%8.2f\n", "TOTAL:", totalVenta));
        sb.append("----------------------\n");
        sb.append("¡Gracias por su compra!\n");
        sb.append("USAMOS VHAO PUNTO DE VENTAS\n");
        sb.append("\n\n\n");

    } catch (SQLException e) {
        e.printStackTrace();
        sb.append("Error al generar ticket.\n");
    }

    return sb.toString();
}
public static String generarTicketCredito(int idVenta, double total, String tipoPago, JTable TableConsultaCreditCliente, double pagaCon, double cambio) {
    StringBuilder ticket = new StringBuilder();

    ticket.append("     TIENDITA AIXA\n");
    ticket.append(" ENCINO BLANCO 427 , PASEOS DEL\n");
    ticket.append("   ROBLE , CIENEGA DE FLORES\n");
    ticket.append("Teléfono: 5626893369\n");
    ticket.append("------------------------------\n");
    ticket.append("Venta ID: ").append(idVenta).append("\n");
    ticket.append("Fecha: ").append(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date())).append("\n");
    ticket.append("Cliente: na\n");
    ticket.append("------------------------------\n");
    ticket.append("Productos:\n");

    for (int i = 0; i < TableConsultaCreditCliente.getRowCount(); i++) {
        String nombre = TableConsultaCreditCliente.getValueAt(i, 2).toString();
        String cantidad = TableConsultaCreditCliente.getValueAt(i, 3).toString();
        String precio = TableConsultaCreditCliente.getValueAt(i, 4).toString();
        String totalFila = TableConsultaCreditCliente.getValueAt(i, 5).toString();

        ticket.append(nombre).append("\n");
        ticket.append(cantidad).append("x$").append(precio).append("   $").append(totalFila).append("\n");
    }

    ticket.append("------------------------------\n");
    ticket.append("Tipo: ").append(tipoPago).append("\n");
    ticket.append(String.format("TOTAL:       $%7.2f\n", total));
    ticket.append(String.format("PAGA CON:    $%7.2f\n", pagaCon));
    ticket.append(String.format("CAMBIO:      $%7.2f\n", cambio));
    ticket.append("------------------------------\n");
    ticket.append("¡Gracias por su compra!\n");
    ticket.append("USAMOS VHAO PUNTO DE VENTAS\n");

    return ticket.toString();
}

private static String centrarConSaltos(String texto, int ancho) {
    StringBuilder resultado = new StringBuilder();
    String[] lineas = texto.split("\n");
    for (String linea : lineas) {
        if (linea.length() > ancho) {
            int start = 0;
            while (start < linea.length()) {
                int end = Math.min(start + ancho, linea.length());
                // No partir palabra
                if (end < linea.length() && linea.charAt(end) != ' ') {
                    int lastSpace = linea.lastIndexOf(' ', end);
                    if (lastSpace > start) {
                        end = lastSpace;
                    }
                }
                String sub = linea.substring(start, end).trim();
                int espaciosIzquierda = (ancho - sub.length()) / 2;
                resultado.append(repetirCaracter(' ', espaciosIzquierda)).append(sub).append("\n");
                start = end;
                while (start < linea.length() && linea.charAt(start) == ' ') start++;
            }
        } else {
            int espaciosIzquierda = (ancho - linea.length()) / 2;
            resultado.append(repetirCaracter(' ', espaciosIzquierda)).append(linea).append("\n");
        }
    }
       
    return resultado.toString();

}

private static String repetirCaracter(char c, int veces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < veces; i++) {
        sb.append(c);
    }
    return sb.toString();
}






}
