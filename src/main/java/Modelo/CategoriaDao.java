package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    /** Lista todas las categorías de una empresa, ordenadas alfabéticamente. */
    public List<Categoria> listarPorEmpresa(int idEmpresa) {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id_categoria, nombre, id_empresa FROM categorias WHERE id_empresa = ? ORDER BY nombre";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEmpresa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Categoria(
                            rs.getInt("id_categoria"),
                            rs.getString("nombre"),
                            rs.getInt("id_empresa")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /** Registra una categoría nueva y devuelve el id generado (0 si falla). */
    public int registrar(String nombre, int idEmpresa) {
        String sql = "INSERT INTO categorias (nombre, id_empresa) VALUES (?, ?)";

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre.trim());
            ps.setInt(2, idEmpresa);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLIntegrityConstraintViolationException dup) {
            System.out.println("⚠ La categoría ya existe para esta empresa: " + nombre);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean eliminar(int idCategoria) {
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idCategoria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}