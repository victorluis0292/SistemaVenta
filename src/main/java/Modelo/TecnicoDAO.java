package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TecnicoDAO {

    public boolean validarNumControl(String numcontrol) {
        String sql = "SELECT COUNT(*) FROM tecnicos WHERE numcontrol = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numcontrol); // primero asignamos el valor
            try (ResultSet rs = ps.executeQuery()) { // luego ejecutamos la consulta
                if (rs.next()) {
                    return rs.getInt(1) > 0; // devuelve true si existe
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // si no hay coincidencias o hay error
    }

    public String obtenerNombrePorNumControl(String numcontrol) {
        String sql = "SELECT nombre_tecnico FROM tecnicos WHERE numcontrol = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, numcontrol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre_tecnico");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
    