package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    private Connection conn;

    // Constructor sin parámetros: obtiene conexión automáticamente
    public UsuarioDao() {
        try {
            this.conn = Conexion.getConnection(); // Usa tu clase Conexion con HikariCP
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Constructor con conexión personalizada (opcional)
    public UsuarioDao(Connection conn) {
        this.conn = conn;
    }

    // Registrar usuario en la base de datos
public boolean registrarUsuario(Usuario u) {
    String sql = "INSERT INTO usuarios (nombre, correo, pass, rol, id_empresa, clave) VALUES (?, ?, ?, ?, ?, ?)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, u.getNombre());
        ps.setString(2, u.getCorreo());
        ps.setString(3, u.getPass());
        ps.setString(4, u.getRol());   // <--- cambiado
        ps.setInt(5, u.getIdEmpresa());
        ps.setString(6, u.getClave());
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    // buscar por correo + empresa
 public boolean existeCorreo(String correo, int idEmpresa) {
    String sql = "SELECT id_usuario FROM usuarios WHERE correo = ? AND id_empresa = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, correo);
        ps.setInt(2, idEmpresa);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

// Validar login por correo, pass y empresa
public login log(String correo, String pass, int idEmpresa) {
    login l = null;
    String sql = "SELECT * FROM usuarios WHERE correo=? AND pass=? AND id_empresa=?";

        try (Connection con = Conexion.getConnection();
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

    // Listar todos los usuarios con nombres de rol y empresa
 public List<Usuario> listarUsuarios() {
    List<Usuario> lista = new ArrayList<>();
    String sql = "SELECT u.id_usuario AS id, u.nombre, u.correo, u.pass, u.rol, u.id_empresa, u.clave, " +
                 "e.nombre AS nombreEmpresa " +
                 "FROM usuarios u " +
                 "LEFT JOIN empresa e ON u.id_empresa = e.id_empresa";

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Usuario u = new Usuario();
            u.setId(rs.getInt("id"));
            u.setNombre(rs.getString("nombre"));
            u.setCorreo(rs.getString("correo"));
            u.setPass(rs.getString("pass"));
            u.setRol(rs.getString("rol"));  // Aquí asignamos el rol directamente
            u.setIdEmpresa(rs.getInt("id_empresa"));
            u.setClave(rs.getString("clave"));
            u.setNombreEmpresa(rs.getString("nombreEmpresa"));
            lista.add(u);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return lista;
}

}
