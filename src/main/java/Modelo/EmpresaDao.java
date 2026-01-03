package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
public class EmpresaDao {

    /**
     * Busca los datos de una empresa por su ID.
     * @param idEmpresa ID de la empresa a buscar.
     * @return Objeto Empresa con los datos, o null si no se encuentra.
     */
    public Empresa BuscarDatos(int idEmpresa) {
        Empresa empresa = null;
        String sql = "SELECT * FROM empresa WHERE id_empresa = ?";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    empresa = new Empresa();
                    empresa.setId_empresa(rs.getInt("id_empresa"));
                    empresa.setRuc(rs.getString("ruc"));
                    empresa.setNombre(rs.getString("nombre"));
                    empresa.setTelefono(rs.getString("telefono"));
                    empresa.setDireccion(rs.getString("direccion"));
                    empresa.setMensaje(rs.getString("mensaje"));
                    empresa.setEstado(rs.getInt("estado"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar datos de la empresa: " + e.getMessage());
            e.printStackTrace();
        }

        return empresa;
    }
    
    /**
     * Modifica los datos de una empresa.
     * @param empresa Objeto Empresa con los datos actualizados.
     * @return true si la actualización fue exitosa, false si falló.
     */
  public boolean ModificarDatos(Empresa empresa) {
    String sql = "UPDATE empresa SET ruc=?, nombre=?, telefono=?, direccion=?, mensaje=?, estado=? WHERE id_empresa=?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, empresa.getRuc());
        ps.setString(2, empresa.getNombre());
        ps.setString(3, empresa.getTelefono());
        ps.setString(4, empresa.getDireccion());
        ps.setString(5, empresa.getMensaje());
        ps.setInt(6, empresa.getEstado());
        ps.setInt(7, empresa.getId_empresa());

        int filas = ps.executeUpdate();
        return filas > 0;
    } catch (SQLException e) {
        System.err.println("Error en ModificarDatos: " + e.getMessage());
        return false;
    }
}
  public boolean cambiarEstadoEmpresa(int idEmpresa, boolean activa) {
    String sql = "UPDATE empresa SET estado = ? WHERE id_empresa = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, activa ? 1 : 0);
        ps.setInt(2, idEmpresa);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al cambiar estado de la empresa: " + e.getMessage());
        return false;
    }
}


    /**
     * Método para verificar si la tabla empresa tiene datos.
     * @param idEmpresa ID de la empresa.
     * @return true si existe, false si no.
     */
    public boolean existeEmpresa(int idEmpresa) {
        return BuscarDatos(idEmpresa) != null;
    }

    /**
     * Registra una nueva empresa en la base de datos.
     * @param empresa Objeto Empresa con los datos a registrar.
     * @return true si el registro fue exitoso y se asigna el id_empresa generado.
     */
    
public int RegistrarEmpresaYObtenerID(Empresa empresa) {
    String sql = "INSERT INTO empresa (ruc, nombre, telefono, direccion, mensaje, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?)";
    int idGenerado = -1;

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, empresa.getRuc());
        ps.setString(2, empresa.getNombre());
        ps.setString(3, empresa.getTelefono());
        ps.setString(4, empresa.getDireccion());
        ps.setString(5, empresa.getMensaje());
        ps.setInt(6, empresa.getEstado());

        // 🔹 Fecha de registro: hora local de la computadora del usuario
java.util.Date ahora = new java.util.Date();
ps.setTimestamp(7, new java.sql.Timestamp(ahora.getTime()));


        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            idGenerado = rs.getInt(1);
        }

    } catch (SQLException e) {
        System.err.println("Error al registrar empresa: " + e.getMessage());
    }

    return idGenerado;
}

public int RegistrarEmpresaYObtenerID(Empresa empresa, String correoUsuario) {
    String sql = "INSERT INTO empresa (ruc, nombre, telefono, direccion, mensaje, estado, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?)";
    int idGenerado = -1;

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setString(1, empresa.getRuc());
        ps.setString(2, empresa.getNombre());
        ps.setString(3, empresa.getTelefono());
        ps.setString(4, empresa.getDireccion());
        ps.setString(5, empresa.getMensaje());
        ps.setInt(6, empresa.getEstado());

        // 🔹 Fecha de registro: hora local de la computadora del usuario
java.util.Date ahora = new java.util.Date();
ps.setTimestamp(7, new java.sql.Timestamp(ahora.getTime()));


        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            idGenerado = rs.getInt(1);
        }

        // 🔹 Si se registró correctamente, enviar correo al usuario
        if (idGenerado > 0 && correoUsuario != null && !correoUsuario.isEmpty()) {
            String asunto = "Registro de Empresa Exitoso";
            String mensaje = "Hola " + empresa.getNombre() +
                    ",\n\nTu empresa ha sido registrada correctamente.\n" +
                    "Tu ID de empresa es: " + idGenerado + "\n" +
                    "Utilízalo para acceder a tu empresa con tus credenciales.\n\n" +
                    "¡Gracias por registrarte!\nSistema de Ventas";

            // Aquí podrías agregar código para enviar el correo
        }

    } catch (SQLException e) {
        System.err.println("Error al registrar empresa: " + e.getMessage());
    }

    return idGenerado;
}

public boolean RegistrarEmpresa(Empresa empresa) {
    return RegistrarEmpresaYObtenerID(empresa) > 0;
}
}
