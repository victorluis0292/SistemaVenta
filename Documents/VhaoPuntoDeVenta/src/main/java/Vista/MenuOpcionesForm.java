/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;


import Controlador.MovimientosCajaController;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author vic
 */
public class MenuOpcionesForm extends javax.swing.JDialog {

     private JButton btnMovimientosCaja;
    private JButton btnCambiarCaja;
    private JButton btnArqueoCaja;
    private JButton btnCorteCaja;
    private JButton btnVentasDia;
    private JButton btnProveedores;
    private JButton btnReportes;
    private JButton btnConfiguracion;
    private JButton btnSalir;
    private JPanel panelBotones;
    /**
     * Creates new form MenuOpcionesForm
     */
    public MenuOpcionesForm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Menú de Opciones");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(parent);

        initComponents();
    }
     private void initComponents() {
        panelBotones = new JPanel(new GridLayout(3, 3, 15, 15));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Inicializar botones con texto e íconos
        btnMovimientosCaja = new JButton("<html><center>Movimientos<br>de caja</center></html>");
        btnMovimientosCaja.setIcon(loadIcon("/Img/movimientos.png"));
        btnMovimientosCaja.setVerticalTextPosition(JButton.BOTTOM);
        btnMovimientosCaja.setHorizontalTextPosition(JButton.CENTER);

        btnCambiarCaja = new JButton("Cambiar caja");
        btnCambiarCaja.setIcon(loadIcon("/Icons/cambiar_caja.png"));

        btnArqueoCaja = new JButton("Arqueo de caja");
        btnArqueoCaja.setIcon(loadIcon("/Icons/arqueo.png"));

        btnCorteCaja = new JButton("Corte de caja");
        btnCorteCaja.setIcon(loadIcon("/Icons/corte.png"));

        btnVentasDia = new JButton("Ventas del día");
        btnVentasDia.setIcon(loadIcon("/Icons/ventas.png"));

        btnProveedores = new JButton("Proveedores");
        btnProveedores.setIcon(loadIcon("/Icons/proveedores.png"));

        btnReportes = new JButton("Reportes");
        btnReportes.setIcon(loadIcon("/Icons/reportes.png"));

        btnConfiguracion = new JButton("Configuración");
        btnConfiguracion.setIcon(loadIcon("/Icons/configuracion.png"));

        btnSalir = new JButton("Salir");
        btnSalir.setIcon(loadIcon("/Icons/salir.png"));

        // Agregar botones al panel
        panelBotones.add(btnMovimientosCaja);
        panelBotones.add(btnCambiarCaja);
        panelBotones.add(btnArqueoCaja);
        panelBotones.add(btnCorteCaja);
        panelBotones.add(btnVentasDia);
        panelBotones.add(btnProveedores);
        panelBotones.add(btnReportes);
        panelBotones.add(btnConfiguracion);
        panelBotones.add(btnSalir);

        // Añadir el panel al diálogo
        getContentPane().add(panelBotones, BorderLayout.CENTER);

        // Eventos para los botones

        btnMovimientosCaja.addActionListener(e -> {
            // Ocultar menú actual
            this.setVisible(false);

            // Crear y mostrar MovimientosCaja
            MovimientosCaja mc = new MovimientosCaja();
            new MovimientosCajaController(mc);
            mc.setLocationRelativeTo(null);
            mc.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            // Cuando MovimientosCaja se cierre, mostrar menú otra vez
           // mc.addWindowListener(new java.awt.event.WindowAdapter() {
             //   @Override
               // public void windowClosed(java.awt.event.WindowEvent e) {
                 //   MenuOpcionesForm.this.setVisible(true);
                //}
            //});

            mc.setVisible(true);
        });

        btnCambiarCaja.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "Cambiar caja aún no disponible.");
        });

        btnArqueoCaja.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "Arqueo de caja aún no disponible.");
        });

        btnCorteCaja.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "Corte de caja aún no disponible.");
        });

        btnVentasDia.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "Ventas del día aún no disponible.");
        });

        btnProveedores.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "Proveedores aún no disponible.");
        });

        btnReportes.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "Reportes aún no disponible.");
        });

        btnConfiguracion.addActionListener(e -> {
            javax.swing.JOptionPane.showMessageDialog(this, "Configuración aún no disponible.");
        });

        btnSalir.addActionListener(e -> {
            dispose(); // Cierra el diálogo
        });
    }
       
   private ImageIcon loadIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("No se pudo cargar el icono: " + path);
            return null;
        }
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

        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setLayout(new java.awt.GridLayout(3, 3, 5, 5));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(313, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(248, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
*/
    /**
     * @param args the command line arguments
     */
 
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // Ignorar, usar look and feel por defecto
        }

        java.awt.EventQueue.invokeLater(() -> {
            MenuOpcionesForm dialog = new MenuOpcionesForm(new JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
