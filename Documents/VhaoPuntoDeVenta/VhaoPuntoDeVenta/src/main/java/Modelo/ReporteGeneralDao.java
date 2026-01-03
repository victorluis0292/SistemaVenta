package Modelo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Clase para consultas generales de reportes.
 */
public class ReporteGeneralDao {

    /**
     * Obtiene el saldo inicial del día desde initialcashbalance.
     * Este método puede llamarse también desde CashInBoxDAO si quieres centralizar.
     */
    private static BigDecimal obtenerSaldoInicialDelDia() {
        BigDecimal saldo = BigDecimal.ZERO;
        String sql = "SELECT amount FROM initialcashbalance WHERE DATE(fecha) = CURDATE() ORDER BY id DESC LIMIT 1";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                saldo = rs.getBigDecimal("amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return saldo;
    }

    /**
     * Calcula el total en caja considerando ventas, movimientos y saldo inicial.
     * @return total en caja hoy
     */
    public static BigDecimal obtenerTotalEnCajaHoy() {
    BigDecimal totalVentas = BigDecimal.ZERO;
    BigDecimal totalIngresos = BigDecimal.ZERO;
    BigDecimal totalEgresos = BigDecimal.ZERO;
    BigDecimal saldoInicial = obtenerSaldoInicialDelDia();

    try (Connection con = Conexion.getConnection()) {

        // Total ventas hoy
        String sqlVentas = "SELECT IFNULL(SUM(total), 0) FROM ventas WHERE DATE(fecha) = CURDATE()";
        try (PreparedStatement ps = con.prepareStatement(sqlVentas);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalVentas = rs.getBigDecimal(1);
        }

        // Total ingresos hoy (movimientos_caja tipo 'Ingreso')
        String sqlIngresos = "SELECT IFNULL(SUM(monto), 0) FROM movimientos_caja WHERE tipo = 'Ingreso' AND DATE(fecha) = CURDATE()";
        try (PreparedStatement ps = con.prepareStatement(sqlIngresos);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalIngresos = rs.getBigDecimal(1);
        }

        // Total egresos hoy (movimientos_caja tipo 'Egreso')
        String sqlEgresos = "SELECT IFNULL(SUM(monto), 0) FROM movimientos_caja WHERE tipo = 'Egreso' AND DATE(fecha) = CURDATE()";
        try (PreparedStatement ps = con.prepareStatement(sqlEgresos);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalEgresos = rs.getBigDecimal(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return saldoInicial.add(totalVentas).add(totalIngresos).subtract(totalEgresos);
}

}
