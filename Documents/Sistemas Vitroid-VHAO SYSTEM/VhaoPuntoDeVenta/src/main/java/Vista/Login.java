
package Vista;

import Modelo.Conexion;
import Modelo.LoginDAO;
import Modelo.login;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import Modelo.licenciadeprograma;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;


public class Login extends javax.swing.JFrame {
    login lg = new login();
    LoginDAO login = new LoginDAO();
    public Login() {
        initComponents();
        this.setLocationRelativeTo(null);
        txtCorreo.setText("victorluishernandez92@gmail.com");
        txtPass.setText("");
    }
public void validar() {
    String correo = txtCorreo.getText();
    String pass = String.valueOf(txtPass.getPassword());

    if (!"".equals(correo) || !"".equals(pass)) {
        lg = login.log(correo, pass);

        if (lg.getCorreo() != null && lg.getPass() != null) {

            // 🛡️ Verificar la validez de la licencia antes de continuar
            if (!licenciadeprograma.licenciaValida()) {
                JOptionPane.showMessageDialog(null,
                  "❌ Producto caducado. Contacta al programador.\n" +
                  "📞 WhatsApp: 5524902980\n" +
                  "⚠️ Error al conectar con la base de datos.");

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.exit(0);
            }

            // ✅ Si hay internet, mostrar los días restantes
            if (licenciadeprograma.hayInternet()) {
                long dias = licenciadeprograma.diasRestantes();
                if (dias >= 0) {
                    JOptionPane.showMessageDialog(null,
                        "🔐 versión de prueba activa. Días restantes: " + dias,
                        "Licencia", JOptionPane.INFORMATION_MESSAGE);
                }
            }

         // ✅ Acceso permitido, abrir sistema o formulario de saldo inicial

  Modelo.CashInBoxDAO cashDAO = new Modelo.CashInBoxDAO();

                if (!cashDAO.existeSaldoHoy()) {
                    // Mostrar formulario saldo inicial
                    Vista.CashInBoxForm cashInBoxForm = new Vista.CashInBoxForm();
                    Controlador.CashInBoxController cashController = new Controlador.CashInBoxController(cashInBoxForm, cashDAO, lg);

                    cashInBoxForm.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent e) {
                            Sistema sis = Sistema.getInstancia();
                            sis.inicializarSistema(lg);
                            sis.setVisible(true);
                        }
                    });

                    cashController.iniciar();
                } else {
                    // Abrir sistema normal
                    Sistema sis = Sistema.getInstancia();
                    sis.inicializarSistema(lg);
                    sis.setVisible(true);
                }

                // Cerrar ventana login para que no quede visible
                dispose();

            } else {
                JOptionPane.showMessageDialog(null, "Correo o Contraseña incorrectos");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor ingresa usuario y contraseña");
        }
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCorreo = new javax.swing.JTextField();
        txtPass = new javax.swing.JPasswordField();
        btnIniciar = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        irapaypal = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 153));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Iniciar Sesión", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 1, 24), new java.awt.Color(0, 0, 255))); // NOI18N
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/iniciar.png"))); // NOI18N
        jLabel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 20, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 255));
        jLabel3.setText("Correo Electrónico");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 102, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 255));
        jLabel4.setText("Password");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 175, -1, -1));

        txtCorreo.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        txtCorreo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCorreoActionPerformed(evt);
            }
        });
        txtCorreo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCorreoKeyPressed(evt);
            }
        });
        jPanel2.add(txtCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 132, 240, 30));

        txtPass.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        txtPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPassActionPerformed(evt);
            }
        });
        txtPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPassKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPassKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPassKeyTyped(evt);
            }
        });
        jPanel2.add(txtPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 199, 240, 30));

        btnIniciar.setBackground(new java.awt.Color(0, 0, 204));
        btnIniciar.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        btnIniciar.setForeground(new java.awt.Color(255, 255, 255));
        btnIniciar.setText("Login");
        btnIniciar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });
        jPanel2.add(btnIniciar, new org.netbeans.lib.awtextra.AbsoluteConstraints(103, 239, 93, 34));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/vasocafe.png"))); // NOI18N
        jLabel5.setText("Invítame un Café :");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 340, 240, 40));

        irapaypal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        irapaypal.setForeground(new java.awt.Color(0, 0, 204));
        irapaypal.setText("http://paypal.me/victorluishernandez");
        irapaypal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                irapaypalMouseClicked(evt);
            }
        });
        jPanel2.add(irapaypal, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, 270, 40));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 20, 310, 440));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 380, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 40, 380));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/LOGO-VHAO-SYSTEM.png"))); // NOI18N
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 170, 120));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 420, 480));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Img/login.jpg"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, -10, 270, 470));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
      validar();
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void txtCorreoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCorreoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreoActionPerformed

    private void txtCorreoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCorreoKeyPressed
   // TODO add your handling code here:
    }//GEN-LAST:event_txtCorreoKeyPressed

    private void txtPassKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassKeyPressed
           // TODO add your handling code here:
    }//GEN-LAST:event_txtPassKeyPressed

    private void txtPassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassKeyTyped
           // TODO add your handling code here:
    }//GEN-LAST:event_txtPassKeyTyped

    private void txtPassKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassKeyReleased
          // TODO add your handling code here:
    }//GEN-LAST:event_txtPassKeyReleased

    private void txtPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPassActionPerformed
 validar();        // TODO add your handling code here:
    }//GEN-LAST:event_txtPassActionPerformed

    private void irapaypalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_irapaypalMouseClicked
       if (tieneConexion()) {
        try {
            Desktop.getDesktop().browse(new URI("http://paypal.me/victorluishernandez"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "No estás conectado a internet", "Sin conexión", JOptionPane.WARNING_MESSAGE);
    }
}

private boolean tieneConexion() {
    try {
        URL url = new URL("http://paypal.me/victorluishernandez");
        HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
        conexion.setRequestMethod("HEAD");
        conexion.setConnectTimeout(8000); // 3 segundos
        conexion.connect();
        int responseCode = conexion.getResponseCode();
        return (200 <= responseCode && responseCode <= 399);
    } catch (Exception e) {
        return false;
    }
    }//GEN-LAST:event_irapaypalMouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel5MouseClicked

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnIniciar;
    private javax.swing.JLabel irapaypal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField txtCorreo;
    private javax.swing.JPasswordField txtPass;
    // End of variables declaration//GEN-END:variables
}
