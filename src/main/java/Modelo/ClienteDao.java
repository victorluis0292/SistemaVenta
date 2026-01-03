/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author USUARIO
 */
public class ClienteDao {
    
    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    
 public boolean RegistrarCliente(Cliente cl){
    String sql = "INSERT INTO clientes (dni, nombre, telefono, direccion, id_empresa) VALUES (?,?,?,?,?)";
    try {
        con = cn.getConnection();
        ps = con.prepareStatement(sql);
        ps.setString(1, cl.getDni());
        ps.setString(2, cl.getNombre());
        ps.setString(3, cl.getTelefono());
        ps.setString(4, cl.getDireccion());
        ps.setInt(5, cl.getIdEmpresa()); // Nuevo
        ps.execute();
        return true;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, e.toString());
        return false;
    }finally{
        try { con.close(); } catch (SQLException e) { System.out.println(e.toString()); }
    }
}
  
public List<Cliente> ListarCliente(int idEmpresa){
   List<Cliente> ListaCl = new ArrayList();
   String sql = "SELECT * FROM clientes WHERE id_empresa = ?";
   try {
       con = cn.getConnection();
       ps = con.prepareStatement(sql);
       ps.setInt(1, idEmpresa);
       rs = ps.executeQuery();
       while (rs.next()) {
           Cliente cl = new Cliente();
           cl.setId(rs.getInt("id"));
           cl.setDni(rs.getString("dni"));
           cl.setNombre(rs.getString("nombre"));
           cl.setTelefono(rs.getString("telefono"));
           cl.setDireccion(rs.getString("direccion"));
           cl.setIdEmpresa(rs.getInt("id_empresa")); // Nuevo
           ListaCl.add(cl);
       }
   } catch (SQLException e) {
       System.out.println(e.toString());
   }
   return ListaCl;
}
   
   public boolean EliminarCliente(int id){
       String sql = "DELETE FROM clientes WHERE id = ?";
       try {
           ps = con.prepareStatement(sql);
           ps.setInt(1, id);
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
   
   public boolean ModificarCliente(Cliente cl){
       String sql = "UPDATE clientes SET dni=?, nombre=?, telefono=?, direccion=? WHERE id=?";
       try {
           ps = con.prepareStatement(sql);   
           ps.setString(1, cl.getDni());
           ps.setString(2, cl.getNombre());
           ps.setString(3, cl.getTelefono());
           ps.setString(4, cl.getDireccion());
           ps.setInt(5, cl.getId());
           ps.execute();
           return true;
       } catch (SQLException e) {
           System.out.println(e.toString());
           return false;
       }finally{
           try {
               con.close();
           } catch (SQLException e) {
               System.out.println(e.toString());
           }
       }
   }
   
public Cliente BuscarCliente(int dni, int idEmpresa) {
    Cliente c = new Cliente();
    String sql = "SELECT * FROM clientes WHERE dni = ? AND id_empresa = ?";
    try {
        con = cn.getConnection(); // ✅ Se debe abrir la conexión
        ps = con.prepareStatement(sql);
        ps.setInt(1, dni);
        ps.setInt(2, idEmpresa);
        rs = ps.executeQuery();
        if (rs.next()) {
            c.setId(rs.getInt("id")); // coincide con los demás métodos
            c.setDni(rs.getString("dni"));
            c.setNombre(rs.getString("nombre"));
            c.setTelefono(rs.getString("telefono"));
            c.setDireccion(rs.getString("direccion"));
            c.setIdEmpresa(rs.getInt("id_empresa"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try { con.close(); } catch (SQLException e) { System.out.println(e.toString()); }
    }
    return c;
}

}
