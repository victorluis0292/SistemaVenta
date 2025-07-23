package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AbonoDao {

    /**
     * Registra un abono en la base de datos.
     */
public boolean registrarAbono(int idVenta, double monto, String nota, String tipoPago, int dniCliente) {
    String sql = "INSERT INTO abonos_credito (id_venta, fecha, monto, tipo_pago, nota, dni, aplicado) VALUES (?, ?, ?, ?, ?, ?, 0)";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        Timestamp ahora = new Timestamp(System.currentTimeMillis());

        ps.setInt(1, idVenta);
        ps.setTimestamp(2, ahora);
        ps.setDouble(3, monto);
        ps.setString(4, tipoPago);
        ps.setString(5, nota);
        ps.setInt(6, dniCliente);

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error al registrar abono: " + e.getMessage());
        return false;
    }
}


    /**
     * Cambia el estado de los abonos de una venta a '1' (pagado).
     */

public boolean actualizarAbonosAplicados(int dni, int idVenta) {
    String sql = "UPDATE abonos_credito SET aplicado = 1, id_venta = ? WHERE dni = ? AND aplicado = 0";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, idVenta);
        ps.setInt(2, dni);
        int filas = ps.executeUpdate();
        return filas > 0;
    } catch (SQLException e) {
        System.err.println("❌ Error al actualizar abonos aplicados: " + e.getMessage());
        return false;
    }
}

    
    
    // Método para obtener los abonos según id_venta  par el PDF mostrarlo
       public List<Abono> obtenerAbonosPorVenta(int idVenta) {
        List<Abono> abonos = new ArrayList<>();
        String sql = "SELECT monto, fecha FROM abonos_credito WHERE id_venta = ? AND aplicado = 1";
         try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double monto = rs.getDouble("monto");
                Date fecha = rs.getDate("fecha");
                abonos.add(new Abono(monto, fecha));
                System.out.println("Abono encontrado: Monto=" + monto + ", Fecha=" + fecha);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener abonos: " + e.getMessage());
        }
        System.out.println("Total abonos encontrados para venta " + idVenta + ": " + abonos.size());
        return abonos;
    }

}
