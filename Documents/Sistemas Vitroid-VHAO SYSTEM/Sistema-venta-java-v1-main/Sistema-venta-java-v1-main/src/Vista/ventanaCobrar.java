/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import Modelo.Detalle;
import Modelo.Productos;
import Modelo.ProductosDao;
import Modelo.Proveedor;
import Modelo.ProveedorDao;
import Modelo.Venta;
import Modelo.VentaDao;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel; // se llamo con el  jframe de sistemas
import Vista.Sistema;
import static Vista.Sistema.TableVenta;
import static Vista.Sistema.txtIdCV;
import static Vista.Sistema.txtIdPro;
import static Vista.Sistema.LabelVendedor; 
import static Vista.Sistema.txtCodigoVenta;
import static Vista.Sistema.lblEnviaTotal;
import Modelo.Cliente;
import Modelo.ClienteDao;
import Modelo.Combo;
import Modelo.Conexion;
import Modelo.Config;
import Modelo.Detalle;
import Modelo.Entrada;
import Modelo.EntradaDao;
import Modelo.Eventos;
import Modelo.LoginDAO;
import Modelo.Productos;
import Modelo.ProductosDao;
import Modelo.Proveedor;
import Modelo.ProveedorDao;
import Modelo.Venta;
import Modelo.VentaDao;
import Modelo.login;
import Reportes.Grafico;
import static Vista.Sistema.TableVenta;
import static Vista.ConsultaCreditoCliente.TableConsultaCreditCliente;
import static Vista.Sistema.txtCodigoVenta;
import static Vista.Sistema.txtNombreClienteventa;
import static Vista.Sistema.txtIdCV;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
  import javax.swing.table.DefaultTableModel;     

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import static Vista.frmtabla2.modelo2;


/**
 *
 * @author vic
 */
//public class ventanaCobrar extends javax.swing.JFrame {
    public final class ventanaCobrar extends javax.swing.JFrame {
  DefaultTableModel modelo = new DefaultTableModel();
   Venta v = new Venta();
    VentaDao Vdao = new VentaDao(); 
     Detalle Dv = new Detalle();
      Proveedor pr = new Proveedor();
    ProveedorDao PrDao = new ProveedorDao();
    Productos pro = new Productos();
   
    ProductosDao proDao = new ProductosDao();
    Date fechaVenta = new Date();
    String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(fechaVenta);
  /**
     * Creates new form ventanaCobrar
     * @param args
     * @throws java.lang.Exception
     */
   
  DefaultTableModel tmp = new DefaultTableModel();
  
   int item;
    //double lblTotalpagar = 0.00;
   double TotalPagar = 0.00;
   //double TotalPagarCreditos= 0.00;
     // double Total = 0.00;
     
         double TotalPagar2 = 0.00;
    
    
    //double lblRecibePagar = 0.00;
   // double Totalpagar2 = 0.00;
    double TotalpagarEntrada = 0.00;
    
  
  
   
        
      
   
   
    public static DefaultTableModel modelo2;
   
    public ventanaCobrar() {
        initComponents();
          txtPaga1.requestFocus();
        this.setLocationRelativeTo(null);
         double envia = 0.00;
        
     
          //TotalPagarXCreditos();
         
          modelo2=new DefaultTableModel();
        modelo2.addColumn("ID");
        modelo2.addColumn("Descripcion");
        table2.setModel(modelo2);
         TotalPagarX();
       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        txtPaga1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblcambio = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnGenerarCobro = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Descripcion"
            }
        ));
        jScrollPane1.setViewportView(table2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        txtPaga1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtPaga1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPaga1ActionPerformed(evt);
            }
        });
        txtPaga1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPaga1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPaga1KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPaga1KeyTyped(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Paga Con:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Su cambio:");

        lblcambio.setFont(new java.awt.Font("Tahoma", 3, 24)); // NOI18N
        lblcambio.setText("0.00");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/money.png"))); // NOI18N
        jLabel10.setText("Total a Pagar:");

        lblTotal.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(0, 51, 255));
        lblTotal.setText("-----");

        btnGenerarCobro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/print.png"))); // NOI18N
        btnGenerarCobro.setText("Cobrarr");
        btnGenerarCobro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarCobroActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 51, 51));
        jButton1.setText("Cobro Con Tarjeta");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Pago Efectivo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(212, 212, 212)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(lblcambio, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(150, 150, 150)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel10))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtPaga1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(297, 297, 297)
                        .addComponent(btnGenerarCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(63, 63, 63)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(73, 73, 73)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addComponent(txtPaga1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(102, 102, 102)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblcambio, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addComponent(btnGenerarCobro, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPaga1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPaga1KeyReleased

    String  valor = txtPaga1.getText();
        lblcambio.setText(valor);
       Operacion(); 
       // OperacionCreditos();
        
// TODO add your handling code here:
    }//GEN-LAST:event_txtPaga1KeyReleased

    private void txtPaga1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPaga1ActionPerformed
//pagarenter();
 //pagarenterCreditos();// TODO add your handling code here:
    }//GEN-LAST:event_txtPaga1ActionPerformed

    private void btnGenerarCobroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarCobroActionPerformed
       // TODO add your handling code here:
      pagarenter();
     //  pagarenterCreditos();
     // pagarenterCreditos();
    }//GEN-LAST:event_btnGenerarCobroActionPerformed

    private void txtPaga1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPaga1KeyPressed
 if (evt.getKeyCode() == KeyEvent.VK_ENTER) {  






 }       // TODO add your handling code here:
    }//GEN-LAST:event_txtPaga1KeyPressed

    private void txtPaga1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPaga1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPaga1KeyTyped

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
  ventanaCobroConTarjeta cobrarConTarjeta = new ventanaCobroConTarjeta();
        cobrarConTarjeta.setVisible(true);     
        this.dispose();  // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed
    private void RegistrarVenta() {
        int cliente = Integer.parseInt(txtIdCV.getText());
        String vendedor = LabelVendedor.getText();
        double monto = TotalPagar;
        
        v.setCliente(cliente);
        v.setVendedor(vendedor);
        v.setTotal(monto);
        v.setFecha(fechaActual);
        Vdao.RegistrarVenta(v);
    }
    public void RegistrarDetalle() {
        int id = Vdao.IdVenta();
        for (int i = 0; i < TableVenta.getRowCount(); i++) {
            int id_pro = Integer.parseInt(TableVenta.getValueAt(i, 0).toString());
            int cant = Integer.parseInt(TableVenta.getValueAt(i, 2).toString());
            double precio = Double.parseDouble(TableVenta.getValueAt(i, 3).toString());
            Dv.setId_pro(id_pro);
            Dv.setCantidad(cant);
            Dv.setPrecio(precio);
            Dv.setId(id);
            Vdao.RegistrarDetalle(Dv);

        }
        int cliente = Integer.parseInt(txtIdCV.getText());
        Vdao.pdfV(id, cliente, TotalPagar, LabelVendedor.getText());
    }
    
    
    private void ActualizarStock() {
        for (int i = 0; i < TableVenta.getRowCount(); i++) {
            int id = Integer.parseInt(TableVenta.getValueAt(i, 0).toString());
            int cant = Integer.parseInt(TableVenta.getValueAt(i, 2).toString());
            pro = proDao.BuscarId(id);
            int StockActual = pro.getStock() - cant;
            Vdao.ActualizarStock(StockActual, id);

        }
    }
    
    //suma las filas de la tabla
   public void TotalPagarX() {
        TotalPagar = 0.00;
        int numFila = TableVenta.getRowCount();
        for (int i = 0; i < numFila; i++) {
            double cal = Double.parseDouble(String.valueOf(TableVenta.getModel().getValueAt(i, 4)));
            TotalPagar = TotalPagar + cal;
        }
        lblTotal.setText(String.format("%.2f", TotalPagar));
    }
   
   //REGISTRAR  VENTA DE VECINOS CREDITO
   // private void RegistrarVentaCreditos() {
     //   int cliente = Integer.parseInt(txtIdCV.getText());
    //    String vendedor = LabelVendedor.getText();
       // double monto = TotalPagarCreditos;
        
       // v.setCliente(cliente);
      //  v.setVendedor(vendedor);
       // v.setTotal(monto);
      //  v.setFecha(fechaActual);
      //  Vdao.RegistrarVenta(v);
    //}
    //public void RegistrarDetalleCreditos() {
       // int id = Vdao.IdVenta();
       // for (int i = 0; i < TableConsultaCreditCliente.getRowCount(); i++) {
          //  int id_pro = Integer.parseInt(TableConsultaCreditCliente.getValueAt(i, 0).toString());
           // int cant = Integer.parseInt(TableConsultaCreditCliente.getValueAt(i, 2).toString());
          //  double precio = Double.parseDouble(TableConsultaCreditCliente.getValueAt(i, 3).toString());
         //   Dv.setId_pro(id_pro);
         //   Dv.setCantidad(cant);
          //  Dv.setPrecio(precio);
         //   Dv.setId(id);
          //  Vdao.RegistrarDetalle(Dv);

       // }
       // int cliente = Integer.parseInt(txtIdCV.getText());
        //Vdao.pdfV(id, cliente, TotalPagarCreditos, LabelVendedor.getText());
    //}
    
    
   // private void ActualizarStockCreditos() {
       // for (int i = 0; i < TableConsultaCreditCliente.getRowCount(); i++) {
          //  int id = Integer.parseInt(TableConsultaCreditCliente.getValueAt(i, 0).toString());
           // int cant = Integer.parseInt(TableConsultaCreditCliente.getValueAt(i, 2).toString());
          //  pro = proDao.BuscarId(id);
         //   int StockActual = pro.getStock() - cant;
          //  Vdao.ActualizarStock(StockActual, id);

      //  }
  //  }
    
   //public void TotalPagarXCreditos() {
      //  TotalPagarCreditos = 0.00;
      //  int numFila = TableConsultaCreditCliente.getRowCount();
      //  for (int i = 0; i < numFila; i++) {
        //   double cal = Double.parseDouble(String.valueOf(TableConsultaCreditCliente.getModel().getValueAt(i,3)));
        //   TotalPagarCreditos = TotalPagarCreditos + cal;
      // }
      //  lblTotal.setText(String.format("%.2f", TotalPagarCreditos));
   // }
   
   
   
   //
   
   public void pagarenter(){
      if (!"".equals(txtPaga1.getText())) {

            // if (!"".equals(txtNombreClienteventa.getText())) {

                RegistrarVenta();
                RegistrarDetalle();
                ActualizarStock();

                LimpiarTableVenta();
                // LimpiarClienteventa();    se omitio  de momento
                //  LimparVenta();
                LimpiarCobro();
                txtCodigoVenta.requestFocus();
                this.dispose();
                // } else {
                //   JOptionPane.showMessageDialog(null, "Debes buscar un cliente");
                // }
            //} else {
            //JOptionPane.showMessageDialog(null, "Paga con $ ? ");
            // txtPaga1.requestFocus();
            //}
        } else {
            JOptionPane.showMessageDialog(null,"Paga con $ ? ");
            txtPaga1.requestFocus();
        }
   
   
   
   
   }
 //  public void pagarenterCreditos(){
     // if (!"".equals(txtPaga1.getText())) {

            // if (!"".equals(txtNombreClienteventa.getText())) {

              //  RegistrarVentaCreditos();
              //  RegistrarDetalleCreditos();
              //  ActualizarStockCreditos();

                //LimpiarTableVenta();
                // LimpiarClienteventa();    se omitio  de momento
                //  LimparVenta();
               // LimpiarCobro();
               // txtCodigoVenta.requestFocus();
              //  this.dispose();
              //
      //  } else {
        //    JOptionPane.showMessageDialog(null,"Paga con $ ? ");
        //    txtPaga1.requestFocus();
      //  }
   
   
   
   
 //  }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]  ) throws ClassNotFoundException, InstantiationException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        
        
       
        
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ventanaCobrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ventanaCobrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ventanaCobrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ventanaCobrar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ventanaCobrar().setVisible(true);
            }
        });
    }
               

     public void LimpiarCobro() {
        lblEnviaTotal.setText("");
          txtPaga1.setText("");
           lblcambio.setText("");
           txtIdCV.setText("");
           txtIdPro.setText("");
    }

  private void LimpiarTableVenta() {
        tmp = (DefaultTableModel) TableVenta.getModel();
        int fila = TableVenta.getRowCount();
        for (int i = 0; i < fila; i++) {
            tmp.removeRow(0);
        }
    }
  
  void Operacion(){
     
    double num1=Double.parseDouble(lblTotal.getText()); 
    double num2=Double.parseDouble(txtPaga1.getText());   
   
   

   double resta=0.0;
      resta = num2 - num1; 
    lblcambio.setText(String.valueOf(resta));
}
   // void OperacionCreditos(){
     
 //   double num1=Double.parseDouble(lblTotal.getText()); 
  //  double num2=Double.parseDouble(txtPaga1.getText());   
   
   

 //  double resta=0.0;
  //    resta = num2 - num1; 
  //  lblcambio.setText(String.valueOf(resta));
//}
 
  
  
  
 
  
  
 
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerarCobro;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    public static final javax.swing.JLabel lblTotal = new javax.swing.JLabel();
    private javax.swing.JLabel lblcambio;
    private javax.swing.JTable table2;
    private javax.swing.JTextField txtPaga1;
    // End of variables declaration//GEN-END:variables

 

   
}
