package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CashInBoxDAO {

    public boolean registrarSaldoInicial(CashInBox cash) {
        String sql = "INSERT INTO initialcashbalance (amount) VALUES (?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, cash.getAmount());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean existeSaldoHoy() {
        String sql = "SELECT COUNT(*) FROM initialcashbalance WHERE DATE(fecha) = CURDATE()";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // Si hay al menos un registro hoy
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // En Modelo.CashInBoxDAO
public double obtenerSaldoInicialDelDia() {
    double saldo = 0.0;
    String sql = "SELECT amount FROM initialcashbalance WHERE DATE(fecha) = CURDATE() ORDER BY id DESC LIMIT 1";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            saldo = rs.getDouble("amount");
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener saldo inicial: " + e.getMessage());
    }

    return saldo;
}
public double obtenerSaldoInicialSemana(int anio, int semana) {
    double saldo = 0.0;
    String sql = "SELECT amount FROM initialcashbalance " +
                 "WHERE YEAR(fecha) = ? AND WEEK(fecha, 1) = ? " +
                 "ORDER BY fecha ASC LIMIT 1"; // asumimos que el primer saldo es el inicial

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, anio);
        ps.setInt(2, semana);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                saldo = rs.getDouble("amount");
            }
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener saldo inicial de la semana: " + e.getMessage());
    }

    return saldo;
}
public double obtenerSaldoInicialMes(int anio, int mes) {
    double saldo = 0.0;
    String sql = "SELECT amount FROM initialcashbalance " +
                 "WHERE YEAR(fecha) = ? AND MONTH(fecha) = ? " +
                 "ORDER BY fecha ASC LIMIT 1"; // asumimos que el primero del mes es el inicial

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, anio);
        ps.setInt(2, mes);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                saldo = rs.getDouble("amount");
            }
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener saldo inicial del mes: " + e.getMessage());
    }

    return saldo;
}
public double obtenerSaldoInicialAnio(int anio) {
    double saldo = 0.0;
    String sql = "SELECT amount FROM initialcashbalance " +
                 "WHERE YEAR(fecha) = ? " +
                 "ORDER BY fecha ASC LIMIT 1"; // el primer registro del año

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, anio);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                saldo = rs.getDouble("amount");
            }
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener saldo inicial del año: " + e.getMessage());
    }

    return saldo;
}
public double obtenerSaldoInicialRango(String fechaInicio, String fechaFin) {
    double saldo = 0.0;
    String sql = "SELECT amount FROM initialcashbalance " +
                 "WHERE DATE(fecha) BETWEEN ? AND ? " +
                 "ORDER BY fecha ASC LIMIT 1";

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, fechaInicio);
        ps.setString(2, fechaFin);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                saldo = rs.getDouble("amount");
            }
        }

    } catch (SQLException e) {
        System.out.println("Error al obtener saldo inicial del rango: " + e.getMessage());
    }

    return saldo;
}


}
