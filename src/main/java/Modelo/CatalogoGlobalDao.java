package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CatalogoGlobalDao {

    public List<CatalogoGlobal> buscarPorNombre(String filtro) {
        List<CatalogoGlobal> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, categoria, imagen_url FROM catalogo_global "
                   + "WHERE activo = 1 AND nombre LIKE ? ORDER BY nombre";
        // Aquí se cambió a Conexion.getConnection()
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + filtro.trim().toUpperCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CatalogoGlobal c = new CatalogoGlobal();
                    c.setId(rs.getInt("id"));
                    c.setNombre(rs.getString("nombre"));
                    c.setCategoria(rs.getString("categoria"));
                    c.setImagenUrl(rs.getString("imagen_url"));
                    lista.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}