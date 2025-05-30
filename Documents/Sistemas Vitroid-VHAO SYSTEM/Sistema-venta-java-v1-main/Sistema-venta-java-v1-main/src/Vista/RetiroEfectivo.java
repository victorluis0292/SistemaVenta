package Vista;

import Modelo.Conexion;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author vic
 */
public class RetiroEfectivo extends javax.swing.JFrame {

    /**
     * Creates new form Corte
     */
    public RetiroEfectivo() {
       initComponentss();
        cargarDatos();

        // Listener para cuando se cambia la fecha en el JDateChooser
        Midate.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                cargarDatos();
            }
        });
    }
  
   private void cargarDatos() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Proveedor");
        modelo.addColumn("Total");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaSeleccionada = (Midate.getDate() != null) ? sdf.format(Midate.getDate()) : sdf.format(new java.util.Date());

        double totalGeneral = 0;

        String sql = "SELECT p.nombre AS Proveedor, SUM(d.cantidad * d.precio) AS Total " +
                     "FROM detalle d " +
                     "INNER JOIN ventas v ON d.id_venta = v.id " +
                     "INNER JOIN productos pr ON d.id_pro = pr.id " +
                     "INNER JOIN proveedor p ON pr.proveedor = p.id " +
                     "WHERE v.fecha = ? " +
                     "GROUP BY p.nombre";

        try {
            Conexion conexion = new Conexion();
            Connection con = conexion.getConnection();

            if (con != null) {
                System.out.println("Fecha usada para la consulta: " + fechaSeleccionada);

                try (PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, fechaSeleccionada);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String proveedor = rs.getString("Proveedor");
                        double total = rs.getDouble("Total");
                        totalGeneral += total;
                        modelo.addRow(new Object[]{proveedor, String.format("%.2f", total)});
                    }
                }
                TablaVentasProvedor.setModel(modelo);
                TotalPagoProv.setText(String.format("%.2f", totalGeneral));

                con.close();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }
    
     @SuppressWarnings("unchecked")
    private void initComponentss() {
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaVentasProvedor = new javax.swing.JTable();
        Midate = new JDateChooser();
        MontoTotalV = new javax.swing.JLabel();
        TotalPagoProv = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Retiro de Efectivo");

        TablaVentasProvedor.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Proveedor", "Total"}
        ));
        jScrollPane1.setViewportView(TablaVentasProvedor);

        Midate.setDateFormatString("yyyy-MM-dd");
        Midate.setDate(new java.util.Date());

        MontoTotalV.setText("Monto Total:");

        TotalPagoProv.setText("0.00");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(Midate, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(MontoTotalV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TotalPagoProv, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Midate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MontoTotalV)
                    .addComponent(TotalPagoProv))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    /*
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        MontoTotalV = new javax.swing.JLabel();
        TotalPagoProv = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaVentasProvedor = new javax.swing.JTable();
        Midate = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        MontoTotalV.setText("Monto Total:");

        TotalPagoProv.setText("TotalPagoProv");

        TablaVentasProvedor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Pago a provedores", "$total"
            }
        ));
        jScrollPane1.setViewportView(TablaVentasProvedor);

        Midate.setDateFormatString("d/MM/yyyy HH:mm");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(89, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Midate, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(MontoTotalV)
                        .addGap(32, 32, 32)
                        .addComponent(TotalPagoProv, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(52, 52, 52))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(MontoTotalV)
                            .addComponent(TotalPagoProv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Midate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
*/
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Error al cargar Look and Feel: " + ex.getMessage());
        }

        java.awt.EventQueue.invokeLater(() -> new RetiroEfectivo().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public com.toedter.calendar.JDateChooser Midate;
    private javax.swing.JLabel MontoTotalV;
    private javax.swing.JTable TablaVentasProvedor;
    private javax.swing.JLabel TotalPagoProv;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
