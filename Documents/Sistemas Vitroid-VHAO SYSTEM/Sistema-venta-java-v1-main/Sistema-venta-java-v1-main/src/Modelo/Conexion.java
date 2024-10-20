package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {

    Connection con;

    public Connection getConnection() {
        try {
            String myBD = "jdbc:mysql://localhost:3306/puntedeventa-refresqueriaaixa?serverTimezone=UTC";
            con = DriverManager.getConnection(myBD, "root", "");
            return con;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al crear la conexión , "+e.getMessage());
            System.out.println(e.toString());
        }
        return null;
    }

}
