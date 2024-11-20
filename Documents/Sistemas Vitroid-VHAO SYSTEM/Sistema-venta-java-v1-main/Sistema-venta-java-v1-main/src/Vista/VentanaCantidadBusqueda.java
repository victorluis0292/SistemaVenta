/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import Modelo.Productos;
import Modelo.ProductosDao;
import static Vista.Sistema.txtIdPro;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static Vista.Sistema.TableVenta;
/**
 *
 * @author vic
 */
public class VentanaCantidadBusqueda extends javax.swing.JFrame {
    ProductosDao proDao = new ProductosDao();
    Productos pro = new Productos();
      DefaultTableModel modelo = new DefaultTableModel();
    //DefaultTableModel tmp = new DefaultTableModel()
       DefaultTableModel tmp = new DefaultTableModel();
     int item;
       String codigo;
    /**
     * Creates new form VentanaCantidadBusqueda
     */
    public VentanaCantidadBusqueda() {
        initComponents();
          this.setLocationRelativeTo(null);
           txtCodigoEntrada.setEditable(false); 
           txtDescripcionEntrada.setEditable(false); 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel14 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        txtCodigoEntrada = new javax.swing.JTextField();
        txtDescripcionEntrada = new javax.swing.JTextField();
        txtCantidadEntrada = new javax.swing.JTextField();
        txtPrecioEntrada = new javax.swing.JTextField();
        txtStockDisponible1 = new javax.swing.JTextField();
        txtIdPro1 = new javax.swing.JTextField();
        lblcambio1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel14.setBackground(new java.awt.Color(204, 204, 204));
        jPanel14.setMinimumSize(new java.awt.Dimension(1000, 1000));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel38.setText("Código");
        jPanel14.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, -1, -1));

        jLabel41.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel41.setText("Descripción");
        jPanel14.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel42.setText("Cantidad");
        jPanel14.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, -1));

        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel43.setText("Precio");
        jPanel14.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 250, -1, -1));

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(0, 0, 255));
        jLabel44.setText("Stock Disponible");
        jPanel14.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 70, -1, -1));

        txtCodigoEntrada.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        txtCodigoEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoEntradaActionPerformed(evt);
            }
        });
        txtCodigoEntrada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCodigoEntradaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoEntradaKeyTyped(evt);
            }
        });
        jPanel14.add(txtCodigoEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 240, 30));

        txtDescripcionEntrada.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        txtDescripcionEntrada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDescripcionEntradaKeyTyped(evt);
            }
        });
        jPanel14.add(txtDescripcionEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 360, 30));

        txtCantidadEntrada.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        txtCantidadEntrada.setForeground(new java.awt.Color(102, 51, 255));
        txtCantidadEntrada.setText("1");
        txtCantidadEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantidadEntradaActionPerformed(evt);
            }
        });
        txtCantidadEntrada.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCantidadEntradaKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantidadEntradaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantidadEntradaKeyTyped(evt);
            }
        });
        jPanel14.add(txtCantidadEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 60, 40));

        txtPrecioEntrada.setEditable(false);
        jPanel14.add(txtPrecioEntrada, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 240, 80, 30));

        txtStockDisponible1.setEditable(false);
        txtStockDisponible1.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        jPanel14.add(txtStockDisponible1, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 60, 79, 30));
        jPanel14.add(txtIdPro1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 20, -1, -1));
        jPanel14.add(lblcambio1, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 350, 60, 20));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 673, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(277, 277, 277))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodigoEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoEntradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoEntradaActionPerformed
public void sedDato(String codigo){



this.codigo=codigo;
txtCodigoEntrada.setText(codigo);



} 
    private void txtCodigoEntradaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoEntradaKeyPressed

    }//GEN-LAST:event_txtCodigoEntradaKeyPressed

    private void txtCodigoEntradaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoEntradaKeyTyped

       // event.numberKeyPress(evt);

        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoEntradaKeyTyped

    private void txtDescripcionEntradaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionEntradaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDescripcionEntradaKeyTyped

    private void txtCantidadEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantidadEntradaActionPerformed
        //LimpiarEntrada();        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadEntradaActionPerformed

    private void txtCantidadEntradaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadEntradaKeyPressed
        // TODO add your handling code here:

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            //si el campo tiene un nuemero
           if (!"".equals(txtCantidadEntrada.getText())) {
                //if ("1".equals(txtCantidadEntrada.getText())) {
                    int id = Integer.parseInt(txtIdPro.getText());
                    String descripcion = txtDescripcionEntrada.getText();
                    int cant = Integer.parseInt(txtCantidadEntrada.getText());
                    double precio = Double.parseDouble(txtPrecioEntrada.getText());
                    double total = cant * precio;
                    int stock = Integer.parseInt(txtStockDisponible1.getText());
                    // aqui le digo que no importa si el producto ingresado es mayor o menor al stock
                     //stock = 0;
                    if (stock >= cant) {
                   

                        // !=  >=   38 != 50 esto estaba antes no dejaba meter una entrada mayor al stock
                        item = item + 1;
                        tmp = (DefaultTableModel) TableVenta.getModel();
                        for (int i = 0; i < TableVenta.getRowCount(); i++) {
                            if (TableVenta.getValueAt(i, 0).equals(txtDescripcionEntrada.getText())) {
                          //  if (TableVenta.getValueAt(i, 1).equals(txtDescripcionEntrada.getText())) {
                                JOptionPane.showMessageDialog(null, "El producto ya esta registrado");
                                //LimpiarEntrada();
                                txtCodigoEntrada.requestFocus();
                                return;

                            }
                        }
                        ArrayList lista = new ArrayList();
                        lista.add(item);
                        lista.add(id);
                        lista.add(descripcion);
                        lista.add(cant);
                        lista.add(precio);
                        lista.add(total);
                        Object[] O = new Object[5];
                        O[0] = lista.get(1);
                        O[1] = lista.get(2);
                        O[2] = lista.get(3);
                        O[3] = lista.get(4);
                        O[4] = lista.get(5);
                        tmp.addRow(O);
                        TableVenta.setModel(tmp);
                      //  LimpiarEntrada();
                       // TotalPagarEntrada();
                      //  LimparVenta();
  dispose();
                        txtCodigoEntrada.requestFocus();

                    } else {
                        JOptionPane.showMessageDialog(null, "Stock no disponible");

                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Ingrese cantidad");
                }
            }
      
    }//GEN-LAST:event_txtCantidadEntradaKeyPressed

    private void txtCantidadEntradaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadEntradaKeyReleased


    }//GEN-LAST:event_txtCantidadEntradaKeyReleased

    private void txtCantidadEntradaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantidadEntradaKeyTyped
 //String  valor = txtCantidadEntrada.getText();
   //int valor = Integer.parseInt(txtCantidadEntrada.getText());
   // consulta.sedDato(Integer.parseInt(txtcodigo.getText()));
    //  txtPrecioEntrada.setText(valor);
     // txtPrecioEntrada.setText(valor+"");
       //Operacion();         // TODO add your handling code here:
    }//GEN-LAST:event_txtCantidadEntradaKeyTyped

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
enter();        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowActivated

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
            java.util.logging.Logger.getLogger(VentanaCantidadBusqueda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaCantidadBusqueda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaCantidadBusqueda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaCantidadBusqueda.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaCantidadBusqueda().setVisible(true);
            }
        });
    }
public void enter(){
       // if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
     
            if (!"".equals(txtCodigoEntrada.getText())) {
                String cod = txtCodigoEntrada.getText();
                pro = proDao.BuscarPro(cod);
                if (pro.getNombre() != null) {
                    txtIdPro.setText("" + pro.getId());
                    txtDescripcionEntrada.setText("" + pro.getNombre());
                    txtPrecioEntrada.setText("" + pro.getPrecio());
                    txtStockDisponible1.setText("" + pro.getStock());
                    txtCantidadEntrada.requestFocus();
                } else {
                    //LimpiarEntrada();

                    txtCodigoEntrada.requestFocus();
                    JOptionPane.showMessageDialog(null, "EL CODIGO DE PRODUCTO NO EXISTE");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Ingrese el codigo del productos");
                txtCodigoEntrada.requestFocus();
                //LimpiarEntrada();
            }
      //  }
    
     }
    void Operacion(){
      // int valor = Integer.parseInt(txtCantidadEntrada.getText());
    double num1=Double.parseDouble(txtPrecioEntrada.getText()); 
     //double num2=Double.parseDouble(txtCantidadEntrada.getText());   
   int num2 = Integer.parseInt(txtCantidadEntrada.getText());
   

   double multiplicacion=0.0;
      multiplicacion = num2 * num1; 
    txtPrecioEntrada.setText(String.valueOf(multiplicacion));
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JLabel lblcambio1;
    private javax.swing.JTextField txtCantidadEntrada;
    private javax.swing.JTextField txtCodigoEntrada;
    private javax.swing.JTextField txtDescripcionEntrada;
    private javax.swing.JTextField txtIdPro1;
    private javax.swing.JTextField txtPrecioEntrada;
    private javax.swing.JTextField txtStockDisponible1;
    // End of variables declaration//GEN-END:variables
}