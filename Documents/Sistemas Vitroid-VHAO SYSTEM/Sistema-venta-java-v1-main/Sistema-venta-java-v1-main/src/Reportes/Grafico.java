
package Reportes;

import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class Grafico {
    public static void Graficar(String fecha) throws ParseException{
        Connection con;
        Conexion cn = new Conexion();
        PreparedStatement ps;
        ResultSet rs;
 
        try {
            
            String sql = "SELECT total FROM ventas WHERE fecha = ?";
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
           // ps.setString(1, fecha);
           SimpleDateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
SimpleDateFormat salida = new SimpleDateFormat("yyyy-MM-dd");

Date fechaConvertida = entrada.parse(fecha);
String fechaFormateada = salida.format(fechaConvertida);

ps.setString(1, fechaFormateada);

            rs = ps.executeQuery();
            
            //
             double totalVentas = 0.0;
            //
             
             DefaultPieDataset dateset = new DefaultPieDataset();
            while(rs.next()){
                dateset.setValue(rs.getString("total"), rs.getDouble("total"));
                
                //jjjjj
                 double total = rs.getDouble("total");
                //dataset.setValue("Venta Total", total);
                totalVentas += total;
                
            }
            
             
            
            
            JFreeChart jf = ChartFactory.createPieChart("Reporte de Venta Total: "+ totalVentas, dateset); // Titulo central
            ChartFrame f = new ChartFrame("Total de Ventas por dia", jf); //titulo del framee
            f.setSize(1000, 500);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }
}
