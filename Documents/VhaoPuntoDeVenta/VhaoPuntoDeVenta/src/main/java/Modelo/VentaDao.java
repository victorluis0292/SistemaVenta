
package Modelo;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.filechooser.FileSystemView;
import Modelo.Conexion; // IMPORTANTE: usa tu clase
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;
public class VentaDao {
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    int r;
    
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
        System.out.println("Error en IdVenta: " + e.toString());
    }
    return id;
}

     
 public int RegistrarVenta(Venta v) throws Exception {  // Cambié a Exception para lanzar
    int idGenerado = 0;
    String sql = "INSERT INTO ventas (cliente, vendedor, total, fecha, tipopago, id_turno) VALUES (?, ?, ?, ?, ?, ?)";

    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
    Date fechaUtil = formatoEntrada.parse(v.getFecha());
    java.sql.Date fechaSQL = new java.sql.Date(fechaUtil.getTime());

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, v.getCliente());
        ps.setString(2, v.getVendedor());
        ps.setDouble(3, v.getTotal());
        ps.setDate(4, fechaSQL);
        ps.setString(5, v.getTipopago());
        ps.setInt(6, v.getIdTurno());
        System.out.println("📝 Insertando venta con id_turno: " + v.getIdTurno());

        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                idGenerado = rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        System.out.println("❌ Error al registrar venta: " + e.getMessage());
        throw e; // Propaga la excepción para que se maneje arriba
    }

    // Si no se generó el ID, lanzamos excepción para que se detecte el fallo
    if (idGenerado == 0) {
        throw new Exception("❌ No se pudo registrar la venta. ID generado es 0.");
    }

    return idGenerado;
}


   public int RegistrarDetalle(Detalle Dv) {
    int filasAfectadas = 0;
    String sql = "INSERT INTO detalle (id_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, Dv.getId_pro());
        ps.setInt(2, Dv.getCantidad());
        ps.setDouble(3, Dv.getPrecio());
        ps.setInt(4, Dv.getId());
        filasAfectadas = ps.executeUpdate();
    } catch (SQLException e) {
        System.out.println("Error en RegistrarDetalle: " + e.toString());
    }
    return filasAfectadas;
}

    
    
  public int RegistrarDetalleCreditocliente(Detalle Dv) {
    int filasAfectadas = 0;
    String sql = "INSERT INTO detalle_creditocliente (id_pro, cantidad, precio, total, id_venta, cliente, nombre, dni, fecha) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, Dv.getId_pro());
        ps.setInt(2, Dv.getCantidad());
        ps.setDouble(3, Dv.getPrecio());
        ps.setDouble(4, Dv.getTotal());
        ps.setInt(5, Dv.getId());

        // Verifica si cliente es int o String, ajusta según sea necesario
        // Por ejemplo, si cliente es int:
        // ps.setInt(6, Integer.parseInt(Dv.getCliente()));
        ps.setString(6, Dv.getCliente()); // si cliente es String, bien así

        ps.setString(7, Dv.getNombre());
        ps.setInt(8, Dv.getDni());

        // Convertir fecha String a java.sql.Date (opcional pero recomendado)
        java.sql.Date fechaSQL = null;
        try {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date fechaUtil = formato.parse(Dv.getFecha());
            fechaSQL = new java.sql.Date(fechaUtil.getTime());
        } catch (ParseException e) {
            System.out.println("Formato de fecha inválido: " + e.getMessage());
            // Puedes manejar esto según tu lógica, por ejemplo asignar fecha actual o devolver error
        }
        ps.setDate(9, fechaSQL);

        filasAfectadas = ps.executeUpdate();

    } catch (SQLException e) {
        System.out.println("Error en RegistrarDetalleCreditocliente: " + e.toString());
    }

    return filasAfectadas;
}

        
public boolean EliminarClienteCredito(int dni){
    String sql = "DELETE FROM detalle_creditocliente WHERE dni = ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, dni);
        ps.execute();
        return true;
    } catch (SQLException e) {
        System.out.println(e.toString());
        return false;
    }
}
   
  public boolean ActualizarStock(int cant, int id) {
    String sql = "UPDATE productos SET stock = ? WHERE id = ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setInt(1, cant);
        ps.setInt(2, id);
        
        int filasActualizadas = ps.executeUpdate();
        return filasActualizadas > 0;  // true si se actualizó al menos una fila
        
    } catch (SQLException e) {
        System.out.println("Error al actualizar stock: " + e.toString());
        return false;
    }
}

    
    
  public boolean ActualizarStockEntrada(int cant, int id) {
    String sql = "UPDATE productos SET stock = ? WHERE id = ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, cant);
        ps.setInt(2, id);

        int filasActualizadas = ps.executeUpdate();
        return filasActualizadas > 0;

    } catch (SQLException e) {
        System.out.println("Error al actualizar stock: " + e.toString());
        return false;
    }
}


  public boolean ActualizarStockCreditCliente(int cant, int id) {
    String sql = "UPDATE productos SET stock = ? WHERE id = ?";
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, cant);
        ps.setInt(2, id);

        int filasActualizadas = ps.executeUpdate();
        return filasActualizadas > 0;

    } catch (SQLException e) {
        System.out.println("Error al actualizar stock con crédito: " + e.toString());
        return false;
    }
}

    
       public List<Venta> Listarventas() {
        List<Venta> ListaVenta = new ArrayList<>();
        String sql = "SELECT c.id AS id_cli, c.nombre, v.* FROM clientes c INNER JOIN ventas v ON c.id = v.cliente ORDER BY v.id DESC";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Venta vent = new Venta();
                vent.setId(rs.getInt("id"));
                vent.setNombre_cli(rs.getString("nombre"));
                vent.setVendedor(rs.getString("vendedor"));
                vent.setTotal(rs.getDouble("total"));
                ListaVenta.add(vent);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ListaVenta;
    }
    public Venta BuscarVenta(int id) {
        Venta cl = new Venta();
        String sql = "SELECT * FROM ventas WHERE id = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cl.setId(rs.getInt("id"));
                    cl.setCliente(rs.getInt("cliente"));
                    cl.setVendedor(rs.getString("vendedor"));
                    cl.setTotal(rs.getDouble("total"));
                    cl.setFecha(rs.getString("fecha"));
                    cl.setTipopago(rs.getString("tipopago"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return cl;
         }
    
public void pdfV(int idventa, int Cliente, double total, String usuario) {
    try {
        // Buscar venta y formatear fecha
        Venta venta = BuscarVenta(idventa);
        String fechaDB = venta.getFecha();
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormateada = fechaDB;
        try {
            Date fechaParseada = formatoEntrada.parse(fechaDB);
            fechaFormateada = formatoSalida.format(fechaParseada);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Crear archivo con nombre dinámico
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        String nombreArchivo = "venta_" + idventa + ".pdf";
        File salida = new File(url + File.separator + nombreArchivo);
        FileOutputStream archivo = new FileOutputStream(salida);

        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, archivo);
        doc.open();

        // Logo
        Image img = Image.getInstance(getClass().getResource("/Img/LOGO-VHAO-SYSTEM.png"));
        img.scaleToFit(60, 60);

        // Fuentes
        Font negritaAzul = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLUE);
        Font normal = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
        Font totalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);

        // Encabezado
        PdfPTable encabezado = new PdfPTable(4);
        encabezado.setWidthPercentage(100);
        encabezado.setWidths(new float[]{20f, 30f, 70f, 40f});
        encabezado.getDefaultCell().setBorder(0);
        encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);

        encabezado.addCell(img);
        encabezado.addCell(""); // Espacio

        String mensaje = "";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement("SELECT * FROM config");
            rs = ps.executeQuery();
            if (rs.next()) {
                mensaje = rs.getString("mensaje");
                encabezado.addCell("RUC: " + rs.getString("ruc") +
                        "\nNombre: " + rs.getString("nombre") +
                        "\nTeléfono: " + rs.getString("telefono") +
                        "\nDirección: " + rs.getString("direccion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        encabezado.addCell(new Paragraph("Vendedor: " + usuario +
                "\nFolio: " + idventa +
                "\nFecha: " + fechaFormateada, normal));
        doc.add(encabezado);
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);

        // Cliente
        doc.add(new Paragraph("DATOS DEL CLIENTE", negritaAzul));
        doc.add(Chunk.NEWLINE);

        PdfPTable clienteTabla = new PdfPTable(3);
        clienteTabla.setWidthPercentage(100);
        clienteTabla.setWidths(new float[]{50f, 25f, 25f});
        clienteTabla.getDefaultCell().setBorder(0);

        // Encabezados
        clienteTabla.addCell(new PdfPCell(new Phrase("Nombre", negritaAzul)){{ setBorder(0); }});
        clienteTabla.addCell(new PdfPCell(new Phrase("Teléfono", negritaAzul)){{ setBorder(0); }});
        clienteTabla.addCell(new PdfPCell(new Phrase("Dirección", negritaAzul)){{ setBorder(0); }});

        try {
            ps = con.prepareStatement("SELECT * FROM clientes WHERE id = ?");
            ps.setInt(1, Cliente);
            rs = ps.executeQuery();
            if (rs.next()) {
                clienteTabla.addCell(new PdfPCell(new Phrase(rs.getString("nombre"))){{ setBorder(0); }});
                clienteTabla.addCell(new PdfPCell(new Phrase(rs.getString("telefono"))){{ setBorder(0); }});
                clienteTabla.addCell(new PdfPCell(new Phrase(rs.getString("direccion"))){{ setBorder(0); }});
            } else {
                clienteTabla.addCell(new PdfPCell(new Phrase("Público en General")){{ setBorder(0); }});
                clienteTabla.addCell(new PdfPCell(new Phrase("S/N")){{ setBorder(0); }});
                clienteTabla.addCell(new PdfPCell(new Phrase("S/N")){{ setBorder(0); }});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        doc.add(clienteTabla);
        doc.add(Chunk.NEWLINE);
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);

        // Tabla de productos
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{10f, 50f, 15f, 15f});
        tabla.getDefaultCell().setBorder(0);

        String[] headers = {"Cant.", "Descripción", "P. Unit.", "P. Total"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, negritaAzul));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(cell);
        }

        NumberFormat moneda = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

        try {
            ps = con.prepareStatement("SELECT d.cantidad, d.precio, p.nombre " +
                    "FROM detalle d INNER JOIN productos p ON d.id_pro = p.id WHERE d.id_venta = ?");
            ps.setInt(1, idventa);
            rs = ps.executeQuery();
            while (rs.next()) {
                double precio = rs.getDouble("precio");
                int cantidad = rs.getInt("cantidad");
                double subTotal = precio * cantidad;

                PdfPCell cantCell = new PdfPCell(new Phrase(String.valueOf(cantidad)));
                PdfPCell descCell = new PdfPCell(new Phrase(rs.getString("nombre")));
                PdfPCell precCell = new PdfPCell(new Phrase(moneda.format(precio)));
                PdfPCell totalCell = new PdfPCell(new Phrase(moneda.format(subTotal)));

                cantCell.setBorder(0); cantCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                descCell.setBorder(0);
                precCell.setBorder(0); precCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalCell.setBorder(0); totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                tabla.addCell(cantCell);
                tabla.addCell(descCell);
                tabla.addCell(precCell);
                tabla.addCell(totalCell);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        doc.add(tabla);
        doc.add(Chunk.NEWLINE);
        doc.add(new LineSeparator());

        // Total
        Paragraph totalParrafo = new Paragraph("Total: " + moneda.format(total), totalFont);
        totalParrafo.setAlignment(Element.ALIGN_RIGHT);
        doc.add(totalParrafo);

        doc.add(Chunk.NEWLINE);
        doc.add(new LineSeparator());
        doc.add(Chunk.NEWLINE);

        // Mensaje de agradecimiento
        Paragraph despedida = new Paragraph(mensaje, normal);
        despedida.setAlignment(Element.ALIGN_CENTER);
        doc.add(despedida);

        doc.close();
        archivo.close();

        Desktop.getDesktop().open(salida);

    } catch (DocumentException | IOException e) {
        e.printStackTrace();
    }
}


}