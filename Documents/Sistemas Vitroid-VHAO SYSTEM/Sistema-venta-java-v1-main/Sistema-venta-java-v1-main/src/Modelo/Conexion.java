package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexion {

    Connection con;

    public Connection getConnection() {
        try {
            
         // Configuración para conexión remota (en Hostinger)
           // String myBD = "jdbc:mysql://185.212.71.153:3306/u722149126_tienditaaixa?serverTimezone=UTC";
            String myBD = "jdbc:mysql://185.212.71.153/u722149126_tienditaaixa?useSSL=false&serverTimezone=UTC&connectTimeout=10000";

            con = DriverManager.getConnection(myBD, "u722149126_victor", "Lolo140516");
             // Mensaje en consola si la conexión es exitosa
            System.out.println("Conexión exitosa.");
      
      //configuracion para el local
      //String myBD = "jdbc:mysql://localhost:3306/puntedeventa-refresqueriaaixa?serverTimezone=UTC";
           //con = DriverManager.getConnection(myBD, "root", "");
    //System.out.println("Conexión exitosa.");
            return con;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al crear la conexión , "+e.getMessage());
            System.out.println(e.toString());
        }
        return null;
    }

}
