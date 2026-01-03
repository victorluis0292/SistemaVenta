package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductosDao {
    Conexion cn = new Conexion();

    // Registrar productos incluyendo id_empresa
    public boolean RegistrarProductos(Productos pro) {
        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, precio, preciocompra, id_empresa) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setInt(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            ps.setDouble(6, pro.getPreciocompra());
            ps.setInt(7, pro.getId_empresa()); // NUEVO
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error en RegistrarProductos: " + e.toString());
            return false;
        }
    }

    // Listar productos filtrando por id_empresa
    public List<Productos> ListarProductos(int idEmpresa) {
        List<Productos> Listapro = new ArrayList<>();
        String sql = "SELECT pr.id AS id_proveedor, pr.nombre AS nombre_proveedor, p.* " +
                     "FROM proveedor pr INNER JOIN productos p ON pr.id = p.proveedor " +
                     "WHERE p.id_empresa = ? ORDER BY p.id DESC";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
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
                    pro.setId_empresa(idEmpresa); // NUEVO
                    Listapro.add(pro);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en ListarProductos: " + e.toString());
        }
        return Listapro;
    }

// Modificar producto incluyendo id_empresa
public boolean ModificarProductos(Productos pro) {

    String sql = "UPDATE productos "
               + "SET codigo=?, nombre=?, proveedor=?, stock=?, precio=?, preciocompra=?, id_empresa=? "
               + "WHERE id=?";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, pro.getCodigo());
        ps.setString(2, pro.getNombre());
        ps.setInt(3, pro.getProveedor());
        ps.setInt(4, pro.getStock());
        ps.setDouble(5, pro.getPrecio());
        ps.setDouble(6, pro.getPreciocompra());
        ps.setInt(7, pro.getId_empresa());   // FK correcta
        ps.setInt(8, pro.getId());           // 👈 ESTE FALTABA

        ps.executeUpdate();
        return true;

    } catch (SQLException e) {
        System.out.println("Error al modificar producto: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}




    // Buscar producto por código filtrando por id_empresa s
    public Productos BuscarPro(String cod, int idEmpresa) {
        Productos producto = new Productos();
        String sql = "SELECT * FROM productos WHERE codigo = ? AND id_empresa = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cod);
            ps.setInt(2, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto.setId(rs.getInt("id"));
                    producto.setCodigo(rs.getString("codigo"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setPrecio(rs.getDouble("precio"));
                    producto.setPreciocompra(rs.getDouble("preciocompra"));
                    producto.setStock(rs.getInt("stock"));
                    producto.setId_empresa(idEmpresa);
                    return producto;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en BuscarPro: " + e.toString());
        }
        return producto;
    }

    // Buscar producto por ID filtrando por id_empresa
    public Productos BuscarId(int id, int idEmpresa) {
        Productos pro = new Productos();
        String sql = "SELECT pr.id AS id_proveedor, pr.nombre AS nombre_proveedor, p.* " +
                     "FROM proveedor pr INNER JOIN productos p ON p.proveedor = pr.id " +
                     "WHERE p.id = ? AND p.id_empresa = ?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, idEmpresa);
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
                    pro.setId_empresa(idEmpresa);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en BuscarId: " + e.toString());
        }
        return pro;
    }

    // Eliminar productos (no depende de id_empresa, pero podrías añadir filtro si quieres)
    public boolean EliminarProductos(int id) {
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
}
