package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class loginDAO {

    private final Conexion cn = new Conexion();

    // Validar login por correo, pass y empresa
public login log(String correo, String pass, int idEmpresa) {
    login l = null;
    String sql = "SELECT * FROM usuarios WHERE correo=? AND pass=? AND id_empresa=?";

    try (Connection con = cn.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, correo.trim());
        ps.setString(2, pass.trim());
        ps.setInt(3, idEmpresa);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                l = new login();
                l.setId(rs.getInt("id_usuario"));
                l.setNombre(rs.getString("nombre"));
                l.setCorreo(rs.getString("correo"));
                l.setPass(rs.getString("pass"));
                l.setRol(rs.getString("rol"));
                l.setIdEmpresa(rs.getInt("id_empresa"));
                l.setClave(rs.getString("clave"));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return l;
}

    // Registrar usuario
    public boolean Registrar(login reg) {
        String sql = "INSERT INTO usuarios (nombre, correo, pass, rol, id_empresa, clave) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, reg.getNombre());
            ps.setString(2, reg.getCorreo());
            ps.setString(3, reg.getPass());
            ps.setString(4, reg.getRol());
            ps.setInt(5, reg.getIdEmpresa());
            ps.setString(6, reg.getClave());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    // Listar usuarios filtrando por empresa
    public List<login> ListarUsuarios(int idEmpresa) {
        List<login> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE id_empresa = ?";

        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    login lg = new login();
                    lg.setId(rs.getInt("id_usuario"));
                    lg.setNombre(rs.getString("nombre"));
                    lg.setCorreo(rs.getString("correo"));
                    lg.setRol(rs.getString("rol"));
                    lg.setIdEmpresa(rs.getInt("id_empresa"));
                    lg.setClave(rs.getString("clave"));
                    lista.add(lg);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al listar usuarios: " + e.getMessage());
        }

        return lista;
    }

    // Validar clave de administrador
    public boolean validarClaveAdmin(String clave, int idEmpresa) {
        String sql = "SELECT * FROM usuarios WHERE rol='Administrador' AND clave=? AND id_empresa=?";
        try (Connection con = cn.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, clave);
            ps.setInt(2, idEmpresa);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
