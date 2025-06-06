package Reportes;

import Modelo.Conexion;
import java.sql.*;
import javax.print.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

            byte[] bytes = contenidoTicket.getBytes("ISO-8859-1");
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
    public static String generarTicketReal(int idVenta, double pago, double cambio) {
        StringBuilder sb = new StringBuilder();
        Conexion cn = new Conexion();
        Connection con = cn.getConnection();

        try {
            // 1) Obtener nombre del negocio desde config
            String nombreNegocio = "Mi Negocio";
            String direccion = "Mi Dirección";
            String telefono = "Mi telefono";
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT nombre,direccion,telefono  FROM config LIMIT 1")) {
                if (rs.next()) {
                    nombreNegocio = rs.getString("nombre");
                    direccion  = rs.getString("direccion");
                    telefono  = rs.getString("telefono");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //int anchoTicket = 32; // o el ancho de tu ticket en caracteres

            sb.append("     ").append(nombreNegocio).append("\n");
            sb.append(centrarConSaltos(direccion, 32));
            sb.append("Telefono: ").append(telefono).append("\n");
            sb.append("    ----------------------\n");
            sb.append("Venta ID: ").append(idVenta).append("\n");

            // 2) Forzar a Java a usar GMT-06:00 (sin DST)
            TimeZone.setDefault(TimeZone.getTimeZone("GMT-06:00"));

            // 3) Depuración (opcional): imprimir hora y zona JVM
            Date ahora = new Date();
            SimpleDateFormat sdfDebug = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String horaJVM = sdfDebug.format(ahora);
            TimeZone tz = TimeZone.getDefault();
            System.out.println("Hora JVM (incluye segundos): " + horaJVM);
            System.out.println("TimeZone JVM por defecto: " + tz.getID());
            System.out.println("Offset actual (ms): " + tz.getOffset(ahora.getTime()));

            // 4) Formatear fecha y hora para ticket (solo HH:mm)
            SimpleDateFormat sdfTicket = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String horaActual = sdfTicket.format(ahora);
            sb.append("Fecha: ").append(horaActual).append("\n");

            // 5) Datos de venta y cliente
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

            // 6) Detalles de la venta (dos líneas por producto, sin repetir nombre en la segunda)
            String sqlDetalle =
                    "SELECT p.nombre, d.cantidad, d.precio " +
                    "FROM detalle d JOIN productos p ON d.id_pro = p.id " +
                    "WHERE d.id_venta = ?";
            try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle)) {
                psDetalle.setInt(1, idVenta);
                try (ResultSet rsDetalle = psDetalle.executeQuery()) {
                    while (rsDetalle.next()) {
                        String nombreProd   = rsDetalle.getString("nombre");
                        int cantidad        = rsDetalle.getInt("cantidad");
                        double precioUnit   = rsDetalle.getDouble("precio");
                        double subTotal     = cantidad * precioUnit;

                        // Primera línea: solo el nombre del producto
                        sb.append(nombreProd).append("\n");

                        // Segunda línea: "<cantidad>x$<precioUnit>   $<subTotal>"
                        // Ejemplo: "2x$10.00   $20.00"
                        sb.append(String.format(
                            "%dx$%.2f   $%.2f\n",
                            cantidad,
                            precioUnit,
                            subTotal
                        ));
                    }
                }
            }

            sb.append("----------------------\n");

        // 7) Imprimir líneas de TOTAL, PAGA CON y CAMBIO con anchos fijos
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
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return sb.toString();
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
