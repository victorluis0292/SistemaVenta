package Modelo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CashInBoxDAO {

    // Registra saldo inicial y abre turno solo si NO hay un turno abierto para el usuario
   public boolean registrarSaldoInicial(CashInBox cash) {
    String sql = "INSERT INTO initialcashbalance (amount, usuario, fecha) VALUES (?, ?, NOW())";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setBigDecimal(1, cash.getAmount());
        ps.setString(2, cash.getUsuario());
        int rows = ps.executeUpdate();
        return rows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    // Verifica si existe saldo inicial para el día actual (en general, sin usuario)
    public boolean existeSaldoHoy() {
        String sql = "SELECT COUNT(*) FROM initialcashbalance WHERE DATE(fecha) = CURDATE()";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Obtiene el saldo inicial del día actual (último registro del día)
    public BigDecimal obtenerSaldoInicialDelDia() {
        String sql = "SELECT amount FROM initialcashbalance WHERE DATE(fecha) = CURDATE() ORDER BY id DESC LIMIT 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getBigDecimal("amount");
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener saldo inicial del día: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    // Obtiene saldo inicial de la semana (primer registro de la semana)
    public BigDecimal obtenerSaldoInicialSemana(int anio, int semana) {
        String sql = "SELECT amount FROM initialcashbalance " +
                     "WHERE YEAR(fecha) = ? AND WEEK(fecha, 1) = ? " +
                     "ORDER BY fecha ASC LIMIT 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, semana);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("amount");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener saldo inicial de la semana: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    // Obtiene saldo inicial del mes (primer registro del mes)
    public BigDecimal obtenerSaldoInicialMes(int anio, int mes) {
        String sql = "SELECT amount FROM initialcashbalance " +
                     "WHERE YEAR(fecha) = ? AND MONTH(fecha) = ? " +
                     "ORDER BY fecha ASC LIMIT 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);
            ps.setInt(2, mes);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("amount");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener saldo inicial del mes: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    // Obtiene saldo inicial del año (primer registro del año)
    public BigDecimal obtenerSaldoInicialAnio(int anio) {
        String sql = "SELECT amount FROM initialcashbalance " +
                     "WHERE YEAR(fecha) = ? " +
                     "ORDER BY fecha ASC LIMIT 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, anio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("amount");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener saldo inicial del año: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    // Obtiene saldo inicial en un rango de fechas (primer registro en rango)
    public BigDecimal obtenerSaldoInicialRango(String fechaInicio, String fechaFin) {
        String sql = "SELECT amount FROM initialcashbalance " +
                     "WHERE DATE(fecha) BETWEEN ? AND ? " +
                     "ORDER BY fecha ASC LIMIT 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("amount");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener saldo inicial del rango: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }
}
