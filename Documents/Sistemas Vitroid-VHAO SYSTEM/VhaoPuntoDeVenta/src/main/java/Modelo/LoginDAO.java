
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginDAO {
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    Conexion cn = new Conexion();
    
   public login log(String correo, String pass) {
    login l = null;
    String sql = "SELECT * FROM usuarios WHERE correo = ? AND pass = ?";
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        con = cn.getConnection();
        if (con == null) {
            System.out.println("❌ Error: conexión nula.");
            return null;
        }

        ps = con.prepareStatement(sql);
        ps.setString(1, correo);
        ps.setString(2, pass);
        rs = ps.executeQuery();

        if (rs.next()) {
            l = new login();
            l.setId(rs.getInt("id"));
            l.setNombre(rs.getString("nombre"));
            l.setCorreo(rs.getString("correo"));
            l.setPass(rs.getString("pass"));
            l.setRol(rs.getString("rol"));
        }

    } catch (SQLException e) {
        System.out.println("❌ Error SQL: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            System.out.println("❌ Error al cerrar conexión: " + ex.getMessage());
        }
    }

    return l;
}
public boolean Registrar(login reg) {
    String sql = "INSERT INTO usuarios (nombre, correo, pass, rol) VALUES (?,?,?,?)";
    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = cn.getConnection();
        if (con == null) {
            System.out.println("❌ Conexión fallida");
            return false;
        }

        ps = con.prepareStatement(sql);
        ps.setString(1, reg.getNombre());
        ps.setString(2, reg.getCorreo());
        ps.setString(3, reg.getPass());
        ps.setString(4, reg.getRol());
        ps.execute();
        return true;
    } catch (SQLException e) {
        System.out.println("❌ Error al registrar: " + e.getMessage());
        return false;
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            System.out.println("❌ Error al cerrar recursos: " + ex.getMessage());
        }
    }
}

    public List<login> ListarUsuarios() {
    List<login> Lista = new ArrayList<>();
    String sql = "SELECT * FROM usuarios";
    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        con = cn.getConnection();
        if (con == null) {
            System.out.println("❌ Conexión fallida");
            return Lista;
        }

        ps = con.prepareStatement(sql);
        rs = ps.executeQuery();

        while (rs.next()) {
            login lg = new login();
            lg.setId(rs.getInt("id"));
            lg.setNombre(rs.getString("nombre"));
            lg.setCorreo(rs.getString("correo"));
            lg.setRol(rs.getString("rol"));
            Lista.add(lg);
        }

    } catch (SQLException e) {
        System.out.println("❌ Error al listar usuarios: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            System.out.println("❌ Error al cerrar recursos: " + ex.getMessage());
        }
    }

    return Lista;
}

}
