package Modelo;

import Controlador.TurnoController;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.*;
import javax.swing.filechooser.FileSystemView;
import Vista.Sistema;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JOptionPane;
public class VentaDao {
    private Conexion cn = new Conexion();

    // Obtiene el último ID de venta
    public int IdVenta() {
        int id = 0;
        String sql = "SELECT MAX(id) FROM ventas";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error en IdVenta: " + e);
        }
        return id;
    }

    // Registra una venta
    public int RegistrarVenta(Venta venta) throws Exception {
        if (venta.getIdEmpresa() <= 0) {
            venta.setIdEmpresa(Sistema.getIdEmpresaActiva());
        }
        if (venta.getIdTurno() <= 0 && TurnoController.getTurnoGlobal() != null) {
            venta.setIdTurno(TurnoController.getTurnoGlobal().getId());
        }

        if (venta.getIdEmpresa() <= 0)
            throw new Exception("❌ No se puede registrar la venta: id_empresa inválido.");
        if (venta.getIdTurno() <= 0)
            throw new Exception("❌ No se puede registrar la venta: id_turno inválido.");

        int folio = obtenerFolio(venta.getIdEmpresa());
        venta.setFolio(folio);

        int idGenerado = 0;
        String sql = "INSERT INTO ventas (id_empresa, cliente, vendedor, total, fecha, tipopago, fecha_hora, id_turno, pagacon, cambio, comision, subtotal, folio) "
                   + "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?)";

        SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
        java.sql.Date fechaSQL = new java.sql.Date(formatoEntrada.parse(venta.getFecha()).getTime());

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, venta.getIdEmpresa());
            ps.setInt(2, venta.getCliente());
            ps.setString(3, venta.getVendedor());
            ps.setDouble(4, venta.getTotal());
            ps.setDate(5, fechaSQL);
            ps.setString(6, venta.getTipopago());
            ps.setInt(7, venta.getIdTurno());
            ps.setDouble(8, venta.getPagaCon());
            ps.setDouble(9, venta.getCambio());
            ps.setDouble(10, venta.getComision());
            ps.setDouble(11, venta.getSubtotal());
            ps.setInt(12, folio);

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idGenerado = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new Exception("❌ Error al registrar venta: " + e.getMessage(), e);
        }

        if (idGenerado == 0)
            throw new Exception("❌ No se pudo registrar la venta. ID generado es 0.");

        return idGenerado;
    }

    // Registra detalle de venta
    public int RegistrarDetalle(Detalle detalle) {
        int filas = 0;
        String sql = "INSERT INTO detalle (id_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getId_pro());
            ps.setInt(2, detalle.getCantidad());
            ps.setDouble(3, detalle.getPrecio());
            ps.setInt(4, detalle.getId());
            filas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en RegistrarDetalle: " + e);
        }
        return filas;
    }

    // Registra detalle de crédito de cliente
    public int RegistrarDetalleCreditoCliente(Detalle detalle) {
        int filas = 0;
        String sql = "INSERT INTO detalle_creditocliente (id_pro, cantidad, precio, total, id_venta, cliente, nombre, dni, fecha, id_empresa) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, detalle.getId_pro());
            ps.setInt(2, detalle.getCantidad());
            ps.setDouble(3, detalle.getPrecio());
            ps.setDouble(4, detalle.getTotal());
            ps.setInt(5, detalle.getId());
            ps.setString(6, detalle.getCliente());
            ps.setString(7, detalle.getNombre());
            ps.setInt(8, detalle.getDni());
            ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            ps.setInt(10, Sistema.getIdEmpresaActiva());

            filas = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en RegistrarDetalleCreditoCliente: " + e);
        }
        return filas;
    }

    // Eliminar créditos de cliente
    public boolean eliminarCreditosDelCliente(int dni) {
        String sql = "DELETE FROM detalle_creditocliente WHERE dni = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, dni);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar créditos: " + e.getMessage());
            return false;
        }
    }

    public boolean ActualizarStock(int cantidadVendida, int idProducto, int idEmpresaActiva) {
        String sql = "UPDATE productos SET stock = stock - ? WHERE id = ? AND id_empresa = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidadVendida);
            ps.setInt(2, idProducto);
            ps.setInt(3, idEmpresaActiva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e);
            return false;
        }
    }

    // Listar ventas por empresa
    public List<Venta> ListarVentas(int idEmpresa) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT c.id AS id_cli, c.nombre, v.* "
                   + "FROM clientes c INNER JOIN ventas v ON c.id = v.cliente "
                   + "WHERE v.id_empresa = ? ORDER BY v.id DESC";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
               while (rs.next()) {
    Venta v = new Venta();
    v.setId(rs.getInt("id"));
    v.setIdEmpresa(rs.getInt("id_empresa"));
    v.setNombre_cli(rs.getString("nombre"));
    v.setVendedor(rs.getString("vendedor"));
    v.setTotal(rs.getDouble("total"));
    v.setFolio(rs.getInt("folio")); // <-- importante
    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return lista;
    }

    // Buscar venta por ID por empresa
 public Venta BuscarVenta(int idVenta) {
    Venta venta = null;
    String sql = "SELECT v.id, v.folio, v.id_empresa, v.cliente, v.vendedor, " +
                 "v.total, v.subtotal, v.pagacon, v.cambio, v.comision, v.fecha, v.tipopago, v.fecha_hora, v.id_turno, " +
                 "c.nombre AS nombre_cliente " +   // <-- agregamos nombre del cliente
                 "FROM ventas v " +
                 "LEFT JOIN clientes c ON v.cliente = c.id " + // <-- LEFT JOIN para traer nombre
                 "WHERE v.id = ?";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idVenta);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                venta = new Venta();
                venta.setId(rs.getInt("id"));
                venta.setFolio(rs.getInt("folio"));
                venta.setIdEmpresa(rs.getInt("id_empresa"));
                venta.setCliente(rs.getInt("cliente"));
                venta.setVendedor(rs.getString("vendedor"));
                venta.setTotal(rs.getDouble("total"));
                venta.setSubtotal(rs.getDouble("subtotal"));
                venta.setPagaCon(rs.getDouble("pagacon"));
                venta.setCambio(rs.getDouble("cambio"));
                venta.setComision(rs.getDouble("comision"));
                venta.setFecha(rs.getString("fecha"));
                venta.setTipopago(rs.getString("tipopago"));
                venta.setFechaHora(rs.getString("fecha_hora"));
                venta.setIdTurno(rs.getInt("id_turno"));

                // Asignamos nombre del cliente
                venta.setNombre_cli(rs.getString("nombre_cliente"));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al buscar venta: " + e.getMessage());
    }

    return venta;
}

    public int obtenerFolio(int idEmpresa) {
        int folio = 1;
        String sql = "SELECT MAX(folio) AS max_folio FROM ventas WHERE id_empresa = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt("max_folio") > 0) {
                    folio = rs.getInt("max_folio") + 1;
                }
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return folio;
    }
public boolean ActualizarStockEntrada(int stockNuevo, int idProducto, int idEmpresa) {
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(
             "UPDATE productos SET stock = ? WHERE id = ? AND id_empresa = ?")) {
        ps.setInt(1, stockNuevo);
        ps.setInt(2, idProducto);
        ps.setInt(3, idEmpresa);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
public int obtenerDniPorIdCliente(int idCliente) {
    int dni = -1;
    String sql = "SELECT dni FROM clientes WHERE id = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCliente);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                dni = rs.getInt("dni");
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return dni;
}
public int RegistrarDetalleCreditocliente(Detalle Dv) {
    int filasAfectadas = 0;
    String sql = "INSERT INTO detalle_creditocliente (id_pro, cantidad, precio, total, id_venta, cliente, nombre, dni, fecha, id_empresa) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, Dv.getId_pro());
        ps.setInt(2, Dv.getCantidad());
        ps.setDouble(3, Dv.getPrecio());
        ps.setDouble(4, Dv.getTotal());
        ps.setInt(5, Dv.getId());
        ps.setString(6, Dv.getCliente()); 
        ps.setString(7, Dv.getNombre());
        ps.setInt(8, Dv.getDni());

        // Fecha actual
        ps.setTimestamp(9, new java.sql.Timestamp(System.currentTimeMillis()));

        // Id de la empresa activa desde Sistema
        ps.setInt(10, Sistema.getIdEmpresaActiva());

        filasAfectadas = ps.executeUpdate();

    } catch (SQLException e) {
        System.out.println("Error en RegistrarDetalleCreditocliente: " + e.toString());
    }

    return filasAfectadas;
}

public String obtenerNombreClientePorId(int idCliente) {
    String nombre = "";
    String sql = "SELECT nombre FROM clientes WHERE id = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCliente);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                nombre = rs.getString("nombre");
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return nombre;
}
public boolean eliminarAbonoCreditoPorId(int idAbono) {
    String sql = "DELETE FROM abonos WHERE id = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idAbono);
        int filasAfectadas = ps.executeUpdate();
        return filasAfectadas > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
public boolean eliminarProdCreditoPorId(int idDetalle, int idEmpresa) {
    String sql = "DELETE FROM detalle_creditocliente WHERE id = ? AND id_empresa = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idDetalle);
        ps.setInt(2, idEmpresa);

        int filasAfectadas = ps.executeUpdate();
        System.out.println("[DAO LOG] Filas afectadas al eliminar producto crédito ID " 
                            + idDetalle + " | Empresa: " + idEmpresa + " → " + filasAfectadas);

        return filasAfectadas > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

public Venta BuscarVentaPorFolio(int folio, int idEmpresa) {
    Venta v = null;
    String sql = "SELECT * FROM ventas WHERE folio = ? AND id_empresa = ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, folio);
        ps.setInt(2, idEmpresa);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                v = new Venta();
                v.setId(rs.getInt("id"));
                v.setFolio(rs.getInt("folio"));
                v.setCliente(rs.getInt("cliente"));
                v.setVendedor(rs.getString("vendedor"));
                v.setTotal(rs.getDouble("total"));
                v.setFechaHora(rs.getString("fecha_hora"));
                v.setTipopago(rs.getString("tipopago"));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return v;
}
public void asegurarClienteMostrador() {
    String sqlCheck = "SELECT COUNT(*) FROM clientes WHERE id = 1";
    String sqlInsert = "INSERT INTO clientes (id, dni, nombre, telefono, direccion, id_empresa) "
                     + "VALUES (1, '00000000', 'Cliente Mostrador', '', '', ?)";
    try (Connection con = Conexion.getConnection();
         PreparedStatement checkPs = con.prepareStatement(sqlCheck);
         ResultSet rs = checkPs.executeQuery()) {

        if (rs.next() && rs.getInt(1) == 0) {
            try (PreparedStatement insertPs = con.prepareStatement(sqlInsert)) {
                insertPs.setInt(1, Sistema.getIdEmpresaActiva()); // Asigna empresa actual
                insertPs.executeUpdate();
                System.out.println("✅ Cliente 'Mostrador' creado automáticamente (ID 1)");
            }
        } else {
            System.out.println("✔ Cliente 'Mostrador' ya existe (ID 1)");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // Genera PDF de venta
public void pdfV(int idVenta, String usuario) {
    try (Connection con = Conexion.getConnection()) {
        // Buscar venta
        Venta venta = BuscarVenta(idVenta);
        if (venta == null) {
            System.err.println("No se encontró la venta con ID=" + idVenta);
            return;
        }

        System.out.println("DEBUG: idVenta=" + idVenta);
        System.out.println("DEBUG: id_empresa de la venta=" + venta.getIdEmpresa());

        // Buscar empresa de la venta
        String nombreEmpresa = "DESCONOCIDA";
        String rucEmpresa = "";
        String telefonoEmpresa = "";
        String direccionEmpresa = "";

        String sqlEmpresa = "SELECT * FROM empresa WHERE id_empresa = ?";
        try (PreparedStatement ps = con.prepareStatement(sqlEmpresa)) {
            ps.setInt(1, venta.getIdEmpresa());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    nombreEmpresa = rs.getString("nombre");
                    rucEmpresa = rs.getString("ruc");
                    telefonoEmpresa = rs.getString("telefono");
                    direccionEmpresa = rs.getString("direccion");

                    // Debug
                    System.out.println("DEBUG: Empresa encontrada:");
                    System.out.println("DEBUG: nombre=" + nombreEmpresa);
                    System.out.println("DEBUG: ruc=" + rucEmpresa);
                    System.out.println("DEBUG: telefono=" + telefonoEmpresa);
                    System.out.println("DEBUG: direccion=" + direccionEmpresa);
                } else {
                    System.out.println("DEBUG: No se encontró la empresa con id_empresa=" + venta.getIdEmpresa());
                }
            }
        }

        // Formatear fecha
        String fechaDB = venta.getFechaHora();
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String fechaFormateada = fechaDB;
        try {
            fechaFormateada = formatoSalida.format(formatoEntrada.parse(fechaDB));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear archivo PDF
        String url = System.getProperty("user.home") + "/Desktop";
        String nombreArchivo = "venta_" + venta.getFolio() + ".pdf"; // <-- folio en nombre
        File salida = new File(url + File.separator + nombreArchivo);

        try (FileOutputStream archivo = new FileOutputStream(salida)) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, archivo);
            doc.open();

            // Encabezado
            Font negrita = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            doc.add(new Paragraph("EMPRESA: " + nombreEmpresa, negrita));
            if (!rucEmpresa.isEmpty()) doc.add(new Paragraph("RFC: " + rucEmpresa));
            if (!telefonoEmpresa.isEmpty()) doc.add(new Paragraph("Tel: " + telefonoEmpresa));
            if (!direccionEmpresa.isEmpty()) doc.add(new Paragraph("Dirección: " + direccionEmpresa));
            doc.add(new Paragraph("FOLIO: " + venta.getFolio(), negrita));
            doc.add(new Paragraph("Vendedor: " + usuario));
            doc.add(new Paragraph("Fecha: " + fechaFormateada));
            doc.add(new Paragraph("Tipo pago: " + venta.getTipopago()));
            doc.add(Chunk.NEWLINE);

            // Detalles de productos
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{10f, 50f, 20f, 20f});
            tabla.addCell("Cant.");
            tabla.addCell("Descripción");
            tabla.addCell("P. Unit.");
            tabla.addCell("P. Total");

            NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

            String sqlDetalle = "SELECT d.cantidad, d.precio, p.nombre " +
                                "FROM detalle d INNER JOIN productos p ON d.id_pro = p.id " +
                                "WHERE d.id_venta = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int cantidad = rs.getInt("cantidad");
                        double precio = rs.getDouble("precio");
                        double subTotal = cantidad * precio;
                        tabla.addCell(String.valueOf(cantidad));
                        tabla.addCell(rs.getString("nombre"));
                        tabla.addCell(moneda.format(precio));
                        tabla.addCell(moneda.format(subTotal));
                    }
                }
            }

            doc.add(tabla);
            doc.add(Chunk.NEWLINE);

            // Total
            doc.add(new Paragraph("TOTAL: " + moneda.format(venta.getTotal()), negrita));

            doc.close();
        }

        Desktop.getDesktop().open(salida);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al generar PDF: " + e.getMessage());
    }
}


}
