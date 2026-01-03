package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolDAO {
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;

    // Método para validar clave de administrador
    public boolean validarClaveAdministrador(String clave) {
        String sql = "SELECT * FROM usuarios WHERE rol = 'Administrador' AND clave = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, clave);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error validando clave administrador: " + e.getMessage());
        }
        return false;
    }
}
