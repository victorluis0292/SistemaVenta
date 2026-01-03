package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductosDao {
    Conexion cn = new Conexion();
    
    public boolean RegistrarProductos(Productos pro){
        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, precio, preciocompra) VALUES (?,?,?,?,?,?)";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setInt(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            ps.setDouble(6, pro.getPreciocompra());
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error en RegistrarProductos: " + e.toString());
            return false;
        }
    }
    
    public List<Productos> ListarProductos(){
       List<Productos> Listapro = new ArrayList<>();
       String sql = "SELECT pr.id AS id_proveedor, pr.nombre AS nombre_proveedor, p.* FROM proveedor pr INNER JOIN productos p ON pr.id = p.proveedor ORDER BY p.id DESC";
       try (Connection con = cn.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
           
           while (rs.next()) {               
               Productos pro = new Productos();
               pro.setId(rs.getInt("id"));
               pro.setCodigo(rs.getString("codigo"));
               pro.setNombre(rs.getString("nombre"));
               pro.setProveedor(rs.getInt("id_proveedor"));
               pro.setProveedorPro(rs.getString("nombre_proveedor"));
               pro.setStock(rs.getInt("stock"));
               pro.setPrecio(rs.getDouble("precio"));
               pro.setPreciocompra(rs.getDouble("preciocompra"));
               Listapro.add(pro);
           }
       } catch (SQLException e) {
           System.out.println("Error en ListarProductos: " + e.toString());
       }
       return Listapro;
   }
    
    public boolean EliminarProductos(int id){
       String sql = "DELETE FROM productos WHERE id = ?";
       try (Connection con = cn.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
           
           ps.setInt(1, id);
           ps.execute();
           return true;
       } catch (SQLException e) {
           System.out.println("Error en EliminarProductos: " + e.toString());
           return false;
       }
   }
    
  public boolean ModificarProductos(Productos pro) {
    String sql = "UPDATE productos SET codigo=?, nombre=?, proveedor=?, stock=?, precio=?, preciocompra=? WHERE id=?";
    
    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, pro.getCodigo());
        ps.setString(2, pro.getNombre());
        ps.setInt(3, pro.getProveedor());
        ps.setInt(4, pro.getStock());
        ps.setDouble(5, pro.getPrecio());
        ps.setDouble(6, pro.getPreciocompra());
        ps.setInt(7, pro.getId());
        ps.execute();
        return true;
    } catch (SQLException e) {
        System.out.println("Error al modificar producto: " + e.toString());
        return false;
    }
}
    
  public Productos BuscarPro(String cod) {
    Productos producto = new Productos();
    String sql;

    try (Connection con = cn.getConnection()) {
        // 1. Buscar por código (String, puede ser texto o código de barras)
        sql = "SELECT * FROM productos WHERE codigo = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cod);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto.setId(rs.getInt("id"));
                    producto.setCodigo(rs.getString("codigo"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setPreciocompra(rs.getDouble("preciocompra"));
                    producto.setStock(rs.getInt("stock"));
                    return producto;
                }
            }
        }

        // 2. Si no encontró por código y el texto es numérico, intentar por ID
        if (cod.matches("\\d+")) {
            try {
                long num = Long.parseLong(cod); // usa long para validar
                if (num <= Integer.MAX_VALUE) {
                    int id = (int) num;
                    sql = "SELECT * FROM productos WHERE id = ?";
                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setInt(1, id);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                producto.setId(rs.getInt("id"));
                                producto.setCodigo(rs.getString("codigo"));
                                producto.setNombre(rs.getString("nombre"));
                                producto.setPrecio(rs.getDouble("precio"));
                                producto.setPreciocompra(rs.getDouble("preciocompra"));
                                producto.setStock(rs.getInt("stock"));
                                return producto;
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // El número es demasiado grande para ser un ID, ignoramos
            }
        }

    } catch (SQLException e) {
        System.out.println("Error en BuscarPro: " + e.toString());
    }

    return producto;
}

    public Productos BuscarId(int id){
        Productos pro = new Productos();
        String sql = "SELECT pr.id AS id_proveedor, pr.nombre AS nombre_proveedor, p.* FROM proveedor pr INNER JOIN productos p ON p.proveedor = pr.id WHERE p.id = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pro.setId(rs.getInt("id"));
                    pro.setCodigo(rs.getString("codigo"));
                    pro.setNombre(rs.getString("nombre"));
                    pro.setProveedor(rs.getInt("proveedor"));
                    pro.setProveedorPro(rs.getString("nombre_proveedor"));
                    pro.setStock(rs.getInt("stock"));
                    pro.setPrecio(rs.getDouble("precio"));
                    pro.setPreciocompra(rs.getDouble("preciocompra"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en BuscarId: " + e.toString());
        }
        return pro;
    }
    
    public Proveedor BuscarProveedor(String nombre){
        Proveedor pr = new Proveedor();
        String sql = "SELECT * FROM proveedor WHERE nombre = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pr.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en BuscarProveedor: " + e.toString());
        }
        return pr;
    }
    
    public Config BuscarDatos(){
        Config conf = new Config();
        String sql = "SELECT * FROM config";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            if (rs.next()) {
                conf.setId(rs.getInt("id"));
                conf.setRuc(rs.getString("ruc"));
                conf.setNombre(rs.getString("nombre"));
                conf.setTelefono(rs.getString("telefono"));
                conf.setDireccion(rs.getString("direccion"));
                conf.setMensaje(rs.getString("mensaje"));
            }
        } catch (SQLException e) {
            System.out.println("Error en BuscarDatos: " + e.toString());
        }
        return conf;
    }
    
    public boolean ModificarDatos(Config conf){
       String sql = "UPDATE config SET ruc=?, nombre=?, telefono=?, direccion=?, mensaje=? WHERE id=?";
       try (Connection con = cn.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {
           
           System.out.println("RUC a guardar: '" + conf.getRuc() + "' (length: " + conf.getRuc().length() + ")");
           ps.setString(1, conf.getRuc());
           ps.setString(2, conf.getNombre());
           ps.setString(3, conf.getTelefono());
           ps.setString(4, conf.getDireccion());
           ps.setString(5, conf.getMensaje());
           ps.setInt(6, conf.getId());
           ps.execute();
           return true;
       } catch (SQLException e) {
           System.out.println("Error en ModificarDatos: " + e.toString());
           return false;
       }
   }
}
