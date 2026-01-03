
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
import java.sql.Statement;
public class VentaDao {
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    int r;
    
    public int IdVenta(){
        int id = 0;
        String sql = "SELECT MAX(id) FROM ventas";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return id;
    }
     
   public int RegistrarVenta(Venta v) throws ParseException {
    String sql = "INSERT INTO ventas (cliente, vendedor, total, fecha) VALUES (?, ?, ?, ?)";
    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy");
Date fechaUtil = formatoEntrada.parse(v.getFecha()); // convierte a java.util.Date
java.sql.Date fechaSQL = new java.sql.Date(fechaUtil.getTime());

    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setInt(1, v.getCliente());  // Asegúrate de que cliente es un ID numérico
        ps.setString(2, v.getVendedor());
        ps.setDouble(3, v.getTotal());
       // ps.setString(4, v.getFecha());
        ps.setDate(4, fechaSQL);
        ps.execute();
    } catch (SQLException e) {
        System.out.println("Error al registrar venta: " + e.getMessage());
    } finally {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
    return r;
}
 

   public int RegistrarDetalle(Detalle Dv){
       String sql = "INSERT INTO detalle (id_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, Dv.getId_pro());
            ps.setInt(2, Dv.getCantidad());
            ps.setDouble(3, Dv.getPrecio());
            ps.setInt(4, Dv.getId());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return r;
    }
   
    
    
    
     public int RegistrarDetalleCreditocliente(Detalle Dv){
       String sql = "INSERT INTO detalle_creditocliente (id_pro, cantidad, precio,total, id_venta,cliente,nombre,dni,fecha) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
         con = cn.getConnection();
           ps = con.prepareStatement(sql);
           ps.setInt(1, Dv.getId_pro());
           ps.setInt(2, Dv.getCantidad());
            ps.setDouble(3, Dv.getPrecio());
            ps.setDouble(4, Dv.getTotal());
          ps.setInt(5, Dv.getId());
           ps.setString(6, Dv.getCliente());
           ps.setString(7, Dv.getNombre());
            ps.setInt(8, Dv.getDni());
            ps.setString(9, Dv.getFecha());
          ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
       }finally{
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
       return r;
    }
    // 
        
   public boolean EliminarClienteCredito(int dni){
       String sql = "DELETE FROM detalle_creditocliente WHERE dni = dni";
       try {
           ps = con.prepareStatement(sql);
           ps.setInt(1, dni);
           ps.execute();
           return true;
       } catch (SQLException e) {
           System.out.println(e.toString());
           return false;
       }finally{
           try {
               con.close();
           } catch (SQLException ex) {
               System.out.println(ex.toString());
           }
       }
   }
    
    public boolean ActualizarStock(int cant, int id){
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1,cant);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }
    
    
    public boolean ActualizarStockEntrada(int cant, int id){
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1,cant);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }
    
     public boolean ActualizarStockCreditCliente(int cant, int id){
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1,cant);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }
    
    public List Listarventas(){
       List<Venta> ListaVenta = new ArrayList();
     
      // String sql = "SELECT c.id AS id_clic, c.nombre, v.* FROM clientes c INNER JOIN ventas v ON c.id  = v.cliente ";
          String sql = "SELECT c.id AS id_cli, c.nombre, v.* FROM clientes c INNER JOIN ventas v ON c.id = v.cliente ORDER BY v.id DESC";

       
       try {
           con = cn.getConnection();
           ps = con.prepareStatement(sql);
           rs = ps.executeQuery();
           while (rs.next()) {               
               Venta vent = new Venta();
               vent.setId(rs.getInt("id"));
               vent.setNombre_cli(rs.getString("cliente"));
               vent.setVendedor(rs.getString("vendedor"));
               vent.setTotal(rs.getDouble("total"));
               
               ListaVenta.add(vent);
           }
       } catch (SQLException e) {
           System.out.println(e.toString());
       }
       return ListaVenta;
   }
    public Venta BuscarVenta(int id){
        Venta cl = new Venta();
        String sql = "SELECT * FROM ventas WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                 cl.setId(rs.getInt("id"));
                cl.setCliente(rs.getInt("cliente"));
                cl.setVendedor(rs.getString("vendedor"));
                 cl.setTotal(rs.getDouble("total"));
                 
                 cl.setFecha(rs.getString("fecha"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return cl;
    }

public void pdfV(int idventa, int Cliente, double total, String usuario) {
    try {
        // Obtener la venta
        Venta venta = BuscarVenta(idventa);
        String fechaDB = venta.getFecha();

        // Formatear la fecha al formato dd-MM-yyyy
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy");
        String fechaFormateada = fechaDB;
        try {
            Date fechaParseada = formatoEntrada.parse(fechaDB);
            fechaFormateada = formatoSalida.format(fechaParseada);
        } catch (ParseException e) {
            System.out.println("Error al formatear fecha: " + e.getMessage());
        }

        // Definir ubicación y nombre del archivo PDF
        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + File.separator + "venta.pdf");
        archivo = new FileOutputStream(salida);

        // Crear documento PDF tamaño A4
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, archivo);
        doc.open();

        // Cargar imagen del logo
        Image img = Image.getInstance(getClass().getResource("/Img/LOGO-VHAO-SYSTEM.png"));
        img.scaleToFit(60, 60); // Ajustar tamaño del logo

        // Fuente azul negrita
        Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);

        // Crear tabla de encabezado con 4 columnas
        PdfPTable Encabezado = new PdfPTable(4);
        Encabezado.setWidthPercentage(100); // Ancho al 100%
        Encabezado.getDefaultCell().setBorder(0); // Sin bordes
        float[] columnWidthsEncabezado = new float[]{20f, 30f, 70f, 40f};
        Encabezado.setWidths(columnWidthsEncabezado);
        Encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);

        // Agregar logo e información de la empresa
        Encabezado.addCell(img);
        Encabezado.addCell(""); // Espacio en blanco

        // Consultar configuración (empresa)
        String config = "SELECT * FROM config";
        String mensaje = "";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(config);
            rs = ps.executeQuery();
            if (rs.next()) {
                mensaje = rs.getString("mensaje");
                Encabezado.addCell("RUC:    " + rs.getString("ruc") +
                        "\nNombre: " + rs.getString("nombre") +
                        "\nTeléfono: " + rs.getString("telefono") +
                        "\nDirección: " + rs.getString("direccion"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        // Agregar vendedor, folio y fecha
        Paragraph datosVenta = new Paragraph("Vendedor: " + usuario +
                "\nFolio: " + idventa +
                "\nFecha: " + fechaFormateada);
        Encabezado.addCell(datosVenta);
        doc.add(Encabezado);

        doc.add(Chunk.NEWLINE); // Espacio
        doc.add(new Paragraph("DATOS DEL CLIENTE", negrita));
        doc.add(Chunk.NEWLINE);

        // Tabla de datos del cliente sin bordes
        PdfPTable clienteTabla = new PdfPTable(3);
        clienteTabla.setWidthPercentage(100);
        clienteTabla.getDefaultCell().setBorder(0);
        clienteTabla.setWidths(new float[]{50f, 25f, 25f});
        clienteTabla.setHorizontalAlignment(Element.ALIGN_LEFT);

        // Encabezados
        PdfPCell nombreEnc = new PdfPCell(new Phrase("Nombre", negrita));
        PdfPCell telEnc = new PdfPCell(new Phrase("Teléfono", negrita));
        PdfPCell dirEnc = new PdfPCell(new Phrase("Dirección", negrita));
        nombreEnc.setBorder(Rectangle.NO_BORDER);
        telEnc.setBorder(Rectangle.NO_BORDER);
        dirEnc.setBorder(Rectangle.NO_BORDER);
        clienteTabla.addCell(nombreEnc);
        clienteTabla.addCell(telEnc);
        clienteTabla.addCell(dirEnc);

        // Obtener datos del cliente
        String queryCliente = "SELECT * FROM clientes WHERE id = ?";
        try {
            ps = con.prepareStatement(queryCliente);
            ps.setInt(1, Cliente);
            rs = ps.executeQuery();
            if (rs.next()) {
                PdfPCell nombreCell = new PdfPCell(new Phrase(rs.getString("nombre")));
                PdfPCell telCell = new PdfPCell(new Phrase(rs.getString("telefono")));
                PdfPCell dirCell = new PdfPCell(new Phrase(rs.getString("direccion")));
                nombreCell.setBorder(Rectangle.NO_BORDER);
                telCell.setBorder(Rectangle.NO_BORDER);
                dirCell.setBorder(Rectangle.NO_BORDER);
                clienteTabla.addCell(nombreCell);
                clienteTabla.addCell(telCell);
                clienteTabla.addCell(dirCell);
            } else {
                // Cliente por defecto si no hay registro
                PdfPCell nombreCell = new PdfPCell(new Phrase("Público en General"));
                PdfPCell telCell = new PdfPCell(new Phrase("S/N"));
                PdfPCell dirCell = new PdfPCell(new Phrase("S/N"));
                nombreCell.setBorder(Rectangle.NO_BORDER);
                telCell.setBorder(Rectangle.NO_BORDER);
                dirCell.setBorder(Rectangle.NO_BORDER);
                clienteTabla.addCell(nombreCell);
                clienteTabla.addCell(telCell);
                clienteTabla.addCell(dirCell);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        doc.add(clienteTabla);
        doc.add(Chunk.NEWLINE);

        // Tabla de productos sin bordes
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.getDefaultCell().setBorder(0);
        tabla.setWidths(new float[]{10f, 50f, 15f, 15f});
        tabla.setHorizontalAlignment(Element.ALIGN_LEFT);

        // Encabezados de la tabla de productos
        String[] headers = {"Cant.", "Descripción", "P. Unit.", "P. Total"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, negrita));
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Fondo gris
            tabla.addCell(cell);
        }

        // Cargar productos de la venta
        String product = "SELECT d.id, d.id_pro, d.id_venta, d.precio, d.cantidad, p.nombre " +
                         "FROM detalle d INNER JOIN productos p ON d.id_pro = p.id WHERE d.id_venta = ?";
        try {
            ps = con.prepareStatement(product);
            ps.setInt(1, idventa);
            rs = ps.executeQuery();
            while (rs.next()) {
                double subTotal = rs.getInt("cantidad") * rs.getDouble("precio");

                PdfPCell cantCell = new PdfPCell(new Phrase(String.valueOf(rs.getInt("cantidad"))));
                PdfPCell descCell = new PdfPCell(new Phrase(rs.getString("nombre")));
                PdfPCell precCell = new PdfPCell(new Phrase(String.valueOf(rs.getDouble("precio"))));
                PdfPCell totalCell = new PdfPCell(new Phrase(String.valueOf(subTotal)));
                cantCell.setBorder(Rectangle.NO_BORDER);
                descCell.setBorder(Rectangle.NO_BORDER);
                precCell.setBorder(Rectangle.NO_BORDER);
                totalCell.setBorder(Rectangle.NO_BORDER);
                tabla.addCell(cantCell);
                tabla.addCell(descCell);
                tabla.addCell(precCell);
                tabla.addCell(totalCell);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        doc.add(tabla);
        doc.add(Chunk.NEWLINE);

        // Mostrar total
        Paragraph info = new Paragraph("Total $: " + total);
        info.setAlignment(Element.ALIGN_RIGHT);
        doc.add(info);

        doc.add(Chunk.NEWLINE);

        // Mensaje final desde la configuración
        Paragraph gr = new Paragraph(mensaje);
        gr.setAlignment(Element.ALIGN_CENTER);
        doc.add(gr);

        // Cerrar documento y abrir
        doc.close();
        archivo.close();
        Desktop.getDesktop().open(salida);

    } catch (DocumentException | IOException e) {
        System.out.println(e.toString());
    }
}


}
