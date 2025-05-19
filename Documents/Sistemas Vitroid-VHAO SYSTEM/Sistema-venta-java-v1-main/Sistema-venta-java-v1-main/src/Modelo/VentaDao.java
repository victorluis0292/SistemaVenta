
package Modelo;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
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
/*    public void pdfV(int idventa, int Cliente, double total, String usuario) {
        try {
            Date date = new Date();
            FileOutputStream archivo;
            String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
            File salida = new File(url + "venta.pdf");
            archivo = new FileOutputStream(salida);
            Document doc = new Document();
            PdfWriter.getInstance(doc, archivo);
            doc.open();
            Image img = Image.getInstance(getClass().getResource("/Img/LOGO-VHAO-SYSTEM.png"));
            //Fecha
            Paragraph fecha = new Paragraph();
            Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);
            fecha.add(Chunk.NEWLINE);
            fecha.add("Vendedor: " + usuario + "\nFolio: " + idventa + "\nFecha: "
                    + new SimpleDateFormat("dd/MM/yyyy").format(date) + "\n\n");
            PdfPTable Encabezado = new PdfPTable(4);
            Encabezado.setWidthPercentage(100);
            Encabezado.getDefaultCell().setBorder(0);
            float[] columnWidthsEncabezado = new float[]{20f, 30f, 70f, 40f};
            Encabezado.setWidths(columnWidthsEncabezado);
            Encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
            Encabezado.addCell(img);
            Encabezado.addCell("");
            //info empresa
            String config = "SELECT * FROM config";
            String mensaje = "";
            try {
                con = cn.getConnection();
                ps = con.prepareStatement(config);
                rs = ps.executeQuery();
                if (rs.next()) {
                    mensaje = rs.getString("mensaje");
                    Encabezado.addCell("Ruc:    " + rs.getString("ruc") + "\nNombre: " + rs.getString("nombre") + "\nTeléfono: " + rs.getString("telefono") + "\nDirección: " + rs.getString("direccion") + "\n\n");
                }
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            //
            Encabezado.addCell(fecha);
            doc.add(Encabezado);
            //cliente
            Paragraph cli = new Paragraph();
            cli.add(Chunk.NEWLINE);
            cli.add("DATOS DEL CLIENTE" + "\n\n");
            doc.add(cli);

            PdfPTable proveedor = new PdfPTable(3);
            proveedor.setWidthPercentage(100);
            proveedor.getDefaultCell().setBorder(0);
            float[] columnWidthsCliente = new float[]{50f, 25f, 25f};
            proveedor.setWidths(columnWidthsCliente);
            proveedor.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell cliNom = new PdfPCell(new Phrase("Nombre", negrita));
            PdfPCell cliTel = new PdfPCell(new Phrase("Télefono", negrita));
            PdfPCell cliDir = new PdfPCell(new Phrase("Dirección", negrita));
            cliNom.setBorder(Rectangle.NO_BORDER);
            cliTel.setBorder(Rectangle.NO_BORDER);
            cliDir.setBorder(Rectangle.NO_BORDER);
            proveedor.addCell(cliNom);
            proveedor.addCell(cliTel);
            proveedor.addCell(cliDir);
            String prove = "SELECT * FROM clientes WHERE id = ?";
            try {
                ps = con.prepareStatement(prove);
                ps.setInt(1, Cliente);
                rs = ps.executeQuery();
                if (rs.next()) {
                    proveedor.addCell(rs.getString("nombre"));
                    proveedor.addCell(rs.getString("telefono"));
                    proveedor.addCell(rs.getString("direccion") + "\n\n");
                } else {
                    proveedor.addCell("Publico en General");
                    proveedor.addCell("S/N");
                    proveedor.addCell("S/N" + "\n\n");
                }

            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            doc.add(proveedor);

            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);
            tabla.getDefaultCell().setBorder(0);
            float[] columnWidths = new float[]{10f, 50f, 15f, 15f};
            tabla.setWidths(columnWidths);
            tabla.setHorizontalAlignment(Element.ALIGN_LEFT);
            PdfPCell c1 = new PdfPCell(new Phrase("Cant.", negrita));
            PdfPCell c2 = new PdfPCell(new Phrase("Descripción.", negrita));
            PdfPCell c3 = new PdfPCell(new Phrase("P. unt.", negrita));
            PdfPCell c4 = new PdfPCell(new Phrase("P. Total", negrita));
            c1.setBorder(Rectangle.NO_BORDER);
            c2.setBorder(Rectangle.NO_BORDER);
            c3.setBorder(Rectangle.NO_BORDER);
            c4.setBorder(Rectangle.NO_BORDER);
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            c4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tabla.addCell(c1);
            tabla.addCell(c2);
            tabla.addCell(c3);
            tabla.addCell(c4);
            String product = "SELECT d.id, d.id_pro,d.id_venta, d.precio, d.cantidad, p.nombre FROM detalle d INNER JOIN productos p ON d.id_pro = p.id WHERE d.id_venta = ?";
         //String product = "SELECT d.id, d.id_pro,d.id_venta, d.precio, d.cantidad, p.id, p.nombre_product FROM detalle d INNER JOIN productos p ON d.id_pro = p.id WHERE d.id_venta = ?";
            try {
                ps = con.prepareStatement(product);
                ps.setInt(1, idventa);
                rs = ps.executeQuery();
                while (rs.next()) {
                    double subTotal = rs.getInt("cantidad") * rs.getDouble("precio");
                    tabla.addCell(rs.getString("cantidad"));
                   tabla.addCell(rs.getString("nombre"));
                    tabla.addCell(rs.getString("precio"));
                    tabla.addCell(String.valueOf(subTotal));
                }

            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            doc.add(tabla);
            Paragraph info = new Paragraph();
            info.add(Chunk.NEWLINE);
            info.add("Total $: " + total);
             
            info.setAlignment(Element.ALIGN_RIGHT);
            doc.add(info);
            Paragraph firma = new Paragraph();
            firma.add(Chunk.NEWLINE);
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);
            Paragraph gr = new Paragraph();
            gr.add(Chunk.NEWLINE);
            gr.add(mensaje);
            gr.setAlignment(Element.ALIGN_CENTER);
            doc.add(gr);
            doc.close();
            archivo.close();
            Desktop.getDesktop().open(salida);
        } catch (DocumentException | IOException e) {
            System.out.println(e.toString());
        }
    }
*/
public void pdfV(int idventa, int Cliente, double total, String usuario) {
    try {
        // Obtener la venta desde la base de datos
        Venta venta = BuscarVenta(idventa);
        String fechaDB = venta.getFecha(); // fecha desde BD
// Formatear la fecha desde la base de datos
SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd"); // formato en que viene desde la BD
SimpleDateFormat formatoSalida = new SimpleDateFormat("dd-MM-yyyy"); // formato deseado para mostrar
String fechaFormateada = fechaDB;
try {
    Date fechaParseada = formatoEntrada.parse(fechaDB);
    fechaFormateada = formatoSalida.format(fechaParseada);
} catch (ParseException e) {
    System.out.println("Error al formatear fecha: " + e.getMessage());
}
        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + "venta.pdf");
        archivo = new FileOutputStream(salida);
        Document doc = new Document();
        PdfWriter.getInstance(doc, archivo);
        doc.open();

        Image img = Image.getInstance(getClass().getResource("/Img/LOGO-VHAO-SYSTEM.png"));

        Font negrita = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLUE);

        PdfPTable Encabezado = new PdfPTable(4);
        Encabezado.setWidthPercentage(100);
        Encabezado.getDefaultCell().setBorder(0);
        float[] columnWidthsEncabezado = new float[]{20f, 30f, 70f, 40f};
        Encabezado.setWidths(columnWidthsEncabezado);
        Encabezado.setHorizontalAlignment(Element.ALIGN_LEFT);
        Encabezado.addCell(img);
        Encabezado.addCell("");

        // info empresa
        String config = "SELECT * FROM config";
        String mensaje = "";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(config);
            rs = ps.executeQuery();
            if (rs.next()) {
                mensaje = rs.getString("mensaje");
                Encabezado.addCell("Ruc:    " + rs.getString("ruc") +
                        "\nNombre: " + rs.getString("nombre") +
                        "\nTeléfono: " + rs.getString("telefono") +
                        "\nDirección: " + rs.getString("direccion") + "\n\n");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        // Sección con vendedor, folio y fecha desde la BD
        Paragraph datosVenta = new Paragraph();
   datosVenta.add("Vendedor: " + usuario +
        "\nFolio: " + idventa +
        "\nFecha: " + fechaFormateada + "\n\n");
        Encabezado.addCell(datosVenta);
        doc.add(Encabezado);

        // Datos del cliente
        Paragraph cli = new Paragraph();
        cli.add(Chunk.NEWLINE);
        cli.add("DATOS DEL CLIENTE" + "\n\n");
        doc.add(cli);

        PdfPTable proveedor = new PdfPTable(3);
        proveedor.setWidthPercentage(100);
        proveedor.getDefaultCell().setBorder(0);
        float[] columnWidthsCliente = new float[]{50f, 25f, 25f};
        proveedor.setWidths(columnWidthsCliente);
        proveedor.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell cliNom = new PdfPCell(new Phrase("Nombre", negrita));
        PdfPCell cliTel = new PdfPCell(new Phrase("Télefono", negrita));
        PdfPCell cliDir = new PdfPCell(new Phrase("Dirección", negrita));
        cliNom.setBorder(Rectangle.NO_BORDER);
        cliTel.setBorder(Rectangle.NO_BORDER);
        cliDir.setBorder(Rectangle.NO_BORDER);
        proveedor.addCell(cliNom);
        proveedor.addCell(cliTel);
        proveedor.addCell(cliDir);

        String prove = "SELECT * FROM clientes WHERE id = ?";
        try {
            ps = con.prepareStatement(prove);
            ps.setInt(1, Cliente);
            rs = ps.executeQuery();
            if (rs.next()) {
                proveedor.addCell(rs.getString("nombre"));
                proveedor.addCell(rs.getString("telefono"));
                proveedor.addCell(rs.getString("direccion") + "\n\n");
            } else {
                proveedor.addCell("Publico en General");
                proveedor.addCell("S/N");
                proveedor.addCell("S/N" + "\n\n");
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        doc.add(proveedor);

        // Tabla de productos
        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.getDefaultCell().setBorder(0);
        float[] columnWidths = new float[]{10f, 50f, 15f, 15f};
        tabla.setWidths(columnWidths);
        tabla.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell c1 = new PdfPCell(new Phrase("Cant.", negrita));
        PdfPCell c2 = new PdfPCell(new Phrase("Descripción.", negrita));
        PdfPCell c3 = new PdfPCell(new Phrase("P. unt.", negrita));
        PdfPCell c4 = new PdfPCell(new Phrase("P. Total", negrita));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        c3.setBorder(Rectangle.NO_BORDER);
        c4.setBorder(Rectangle.NO_BORDER);
        c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        c2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        c3.setBackgroundColor(BaseColor.LIGHT_GRAY);
        c4.setBackgroundColor(BaseColor.LIGHT_GRAY);
        tabla.addCell(c1);
        tabla.addCell(c2);
        tabla.addCell(c3);
        tabla.addCell(c4);

        String product = "SELECT d.id, d.id_pro,d.id_venta, d.precio, d.cantidad, p.nombre FROM detalle d INNER JOIN productos p ON d.id_pro = p.id WHERE d.id_venta = ?";
        try {
            ps = con.prepareStatement(product);
            ps.setInt(1, idventa);
            rs = ps.executeQuery();
            while (rs.next()) {
                double subTotal = rs.getInt("cantidad") * rs.getDouble("precio");
                tabla.addCell(rs.getString("cantidad"));
                tabla.addCell(rs.getString("nombre"));
                tabla.addCell(rs.getString("precio"));
                tabla.addCell(String.valueOf(subTotal));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        doc.add(tabla);

        Paragraph info = new Paragraph();
        info.add(Chunk.NEWLINE);
        info.add("Total $: " + total);
        info.setAlignment(Element.ALIGN_RIGHT);
        doc.add(info);

        Paragraph firma = new Paragraph();
        firma.add(Chunk.NEWLINE);
        firma.setAlignment(Element.ALIGN_CENTER);
        doc.add(firma);

        Paragraph gr = new Paragraph();
        gr.add(Chunk.NEWLINE);
        gr.add(mensaje);
        gr.setAlignment(Element.ALIGN_CENTER);
        doc.add(gr);

        doc.close();
        archivo.close();
        Desktop.getDesktop().open(salida);

    } catch (DocumentException | IOException e) {
        System.out.println(e.toString());
    }
}
   // public void pdfV(int id, int cliente, JLabel TotalPagar, String text) {
     //   throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    //}

    
}
