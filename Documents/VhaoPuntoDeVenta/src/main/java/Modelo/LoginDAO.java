package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDAO {

    private final Conexion cn = new Conexion();

    // ✅ Método para iniciar sesión
    public login log(String correo, String pass) {
        login l = null;
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND pass = ?";

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) {
                System.out.println("❌ Error: conexión nula.");
                return null;
            }

            ps.setString(1, correo.trim());
            ps.setString(2, pass.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    l = new login();
                    l.setId(rs.getInt("id"));
                    l.setNombre(rs.getString("nombre"));
                    l.setCorreo(rs.getString("correo"));
                    l.setPass(rs.getString("pass"));
                    l.setRol(rs.getString("rol"));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error SQL (log): " + e.getMessage());
        }

        return l;
    }

    // ✅ Registrar nuevo usuario
    public boolean Registrar(login reg) {
        String sql = "INSERT INTO usuarios (nombre, correo, pass, rol) VALUES (?, ?, ?, ?)";

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) {
                System.out.println("❌ Conexión fallida al registrar.");
                return false;
            }

            ps.setString(1, reg.getNombre());
            ps.setString(2, reg.getCorreo());
            ps.setString(3, reg.getPass());
            ps.setString(4, reg.getRol());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al registrar: " + e.getMessage());
            return false;
        }
    }

    // ✅ Obtener lista de usuarios
    public List<login> ListarUsuarios() {
        List<login> Lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (con == null) {
                System.out.println("❌ Conexión fallida al listar usuarios.");
                return Lista;
            }

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
        }

        return Lista;
    }
}
