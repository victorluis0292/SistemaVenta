// Modelo/MovimientoCajaDao.java
package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovimientoCajaDao {

    public boolean registrarMovimiento(MovimientoCaja movimiento) {
        String sql = "INSERT INTO movimientos_caja (tipo, concepto, nota, monto, gasto, fecha,id_turno) VALUES (?, ?, ?, ?, ?, ?,?)";
        Conexion conexion = new Conexion();

        try (Connection conn = conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, movimiento.getTipo());
            ps.setString(2, movimiento.getConcepto());
            ps.setString(3, movimiento.getNota());
            ps.setDouble(4, movimiento.getMonto());
         

            if (movimiento.getGasto() == null) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, movimiento.getGasto());
            }

            ps.setDate(6, new java.sql.Date(movimiento.getFecha().getTime()));
   ps.setDouble(7, movimiento.getidTurnoActual());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
