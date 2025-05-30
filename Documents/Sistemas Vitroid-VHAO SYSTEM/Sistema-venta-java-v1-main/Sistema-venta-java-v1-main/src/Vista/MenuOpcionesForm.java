/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

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

        // Inicializar botones con texto
       btnMovimientosCaja = new JButton("<html><center>Movimientos<br>de caja</center></html>");
       btnMovimientosCaja.setHorizontalTextPosition(JButton.CENTER);
        btnCambiarCaja = new JButton("Cambiar caja");
        btnArqueoCaja = new JButton("Arqueo de caja");
        btnCorteCaja = new JButton("Corte de caja");
        btnVentasDia = new JButton("Ventas del día");
        btnProveedores = new JButton("Proveedores");
        btnReportes = new JButton("Reportes");
        btnConfiguracion = new JButton("Configuración");
        btnSalir = new JButton("Salir");

        // Agregar íconos (ajusta las rutas de tus íconos aquí)
        btnMovimientosCaja.setIcon(loadIcon("/Img/movimientos.png"));
        btnMovimientosCaja.setVerticalTextPosition(JButton.BOTTOM);
        btnCambiarCaja.setIcon(loadIcon("/Icons/cambiar_caja.png"));
        btnArqueoCaja.setIcon(loadIcon("/Icons/arqueo.png"));
        btnCorteCaja.setIcon(loadIcon("/Icons/corte.png"));
        btnVentasDia.setIcon(loadIcon("/Icons/ventas.png"));
        btnProveedores.setIcon(loadIcon("/Icons/proveedores.png"));
        btnReportes.setIcon(loadIcon("/Icons/reportes.png"));
        btnConfiguracion.setIcon(loadIcon("/Icons/configuracion.png"));
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

        // Eventos
      btnMovimientosCaja.addActionListener(e -> {
    this.dispose();  // Cierra el diálogo MenuOpcionesForm
    MovimientosCaja mc = new MovimientosCaja();
    mc.setLocationRelativeTo(null); // Centrar el nuevo JFrame (por si no lo hiciste en su constructor)
    mc.setVisible(true);
});

        btnCambiarCaja.addActionListener(e -> {
            
            JOptionPane.showMessageDialog(this, "Cambiar caja aún no disponible.");
            // new CambiarCaja().setVisible(true); // Descomenta cuando tengas este formulario
        });

        btnArqueoCaja.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Arqueo de caja aún no disponible.");
            // new ArqueoCaja().setVisible(true);
        });

        btnCorteCaja.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Corte de caja aún no disponible.");
            // new CorteCaja().setVisible(true);
        });

        btnVentasDia.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Ventas del día aún no disponible.");
            // new VentasDia().setVisible(true);
        });

        btnProveedores.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Proveedores aún no disponible.");
            // new Proveedores().setVisible(true);
        });

        btnReportes.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Reportes aún no disponible.");
            // new Reportes().setVisible(true);
        });

        btnConfiguracion.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Configuración aún no disponible.");
            // new Configuracion().setVisible(true);
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
       /* Set the Nimbus look and feel */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // Si falla, ignora y usa el look and feel por defecto
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
