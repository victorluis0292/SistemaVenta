
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
  public static void Graficar(String fecha) throws ParseException {
    String sql = "SELECT total FROM ventas WHERE fecha = ?";
    
    SimpleDateFormat entrada = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat salida = new SimpleDateFormat("yyyy-MM-dd");
    Date fechaConvertida = entrada.parse(fecha);
    String fechaFormateada = salida.format(fechaConvertida);

    double totalVentas = 0.0;
    DefaultPieDataset dataset = new DefaultPieDataset();

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, fechaFormateada);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double total = rs.getDouble("total");
                dataset.setValue("Venta: " + total, total);  // etiqueta para gráfico
                totalVentas += total;
            }
        }

        JFreeChart jf = ChartFactory.createPieChart("Reporte de Venta Total: " + totalVentas, dataset);
        ChartFrame f = new ChartFrame("Total de Ventas por día", jf);
        f.setSize(1000, 500);
        f.setLocationRelativeTo(null);
        f.setVisible(true);

    } catch (SQLException e) {
        System.out.println("Error SQL: " + e.toString());
    }
}

}
