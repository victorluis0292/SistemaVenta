/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vista;

import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
/**
 *
 * @author vic
 */
public class MovimientosCaja extends javax.swing.JFrame {
 
    private JPanel mainPanel;
    private JLabel lblTitle, lblUserIcon, lblUserName, lblCashBox;
    private JLabel lblSelectMovementType, lblGasto, lblMonto, lblConceptoEgresos, lblNota;
    private JButton btnIngreso, btnEgreso, btnHacerMovimiento;
    private JComboBox<String> cmbGasto;
    private JTextField txtMonto, txtConceptoEgresos;
    private JTextArea txtAreaNota;
    private JScrollPane scrollPaneNota;

    // Elementos para el autocompletado
    private JPopupMenu popupMenuSuggestions;
    private DefaultListModel<Object> listModelSuggestions;
    private JList<Object> listSuggestions;

    // Lista de conceptos para sugerencias
    private List<String> allExpenseConcepts = Arrays.asList(
            "Transporte", "Papelería", "Internet", "Alquiler", "Electricidad", "Publicidad");
    /**
     * Creates new form MovimientosCaja
     */
 
    public MovimientosCaja() {
        initComponents();
        setLocationRelativeTo(null); // Centrar ventana
    }
 private void initComponents() {
        // Configuración básica
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Movimientos de Caja");

        // Panel principal
        mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        lblTitle = new JLabel("Movimientos de caja");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setIcon(UIManager.getIcon("OptionPane.questionIcon")); // Ícono genérico

        // Información del usuario
        lblUserIcon = new JLabel("GP", SwingConstants.CENTER);
        lblUserIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUserIcon.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        lblUserIcon.setPreferredSize(new Dimension(40, 40));

        lblUserName = new JLabel("Geordani Puc", SwingConstants.LEFT);
        lblUserName.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        lblCashBox = new JLabel("Caja 1", SwingConstants.RIGHT);
        lblCashBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Tipo de movimiento
        lblSelectMovementType = new JLabel("Seleccionar tipo de movimiento.");
        lblSelectMovementType.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnIngreso = new JButton("Ingreso");
        btnIngreso.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnEgreso = new JButton("Egreso");
        btnEgreso.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnEgreso.setBackground(new Color(220, 230, 255));
        btnEgreso.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 255), 2));

        // Categoría de gasto
        lblGasto = new JLabel("Gasto");
        lblGasto.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cmbGasto = new JComboBox<>(new String[]{
            "Seleccione un gasto", "Proveedores", "Alquiler", "Servicios", "Sueldos", "Transporte", "Materiales"
        });
        cmbGasto.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Monto
        lblMonto = new JLabel("Monto");
        lblMonto.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtMonto = new JTextField("0");
        txtMonto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMonto.setHorizontalAlignment(JTextField.RIGHT);

        // Validación para solo números
        txtMonto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != '\b') {
                    e.consume(); // Solo números y punto
                }
            }
        });

        // Selección de todo al hacer clic
        txtMonto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                txtMonto.selectAll();
            }
        });

        // Concepto de egreso con autocompletado
        lblConceptoEgresos = new JLabel("Concepto de egresos");
        lblConceptoEgresos.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtConceptoEgresos = new JTextField();
        txtConceptoEgresos.setFont(new Font("Segoe UI", Font.PLAIN, 14));

lblConceptoEgresos.setHorizontalAlignment(SwingConstants.LEFT); // Alinear texto/cursor a la izquierda
        // Nota adicional
        lblNota = new JLabel("Nota");
        lblNota.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtAreaNota = new JTextArea("Escribe una nota");
        txtAreaNota.setLineWrap(true);
        txtAreaNota.setWrapStyleWord(true);
        txtAreaNota.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        scrollPaneNota = new JScrollPane(txtAreaNota);
        scrollPaneNota.setPreferredSize(new Dimension(200, 80));

        // Botón de acción
        btnHacerMovimiento = new JButton("HACER MOVIMIENTO");
        btnHacerMovimiento.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnHacerMovimiento.setBackground(new Color(0, 102, 204));
        btnHacerMovimiento.setForeground(Color.WHITE);

        // ------ AUTOCOMPLETADO PARA txtConceptoEgresos ------
        popupMenuSuggestions = new JPopupMenu();
        listModelSuggestions = new DefaultListModel<>();
        listSuggestions = new JList<>(listModelSuggestions);
        listSuggestions.setFocusable(false);
        popupMenuSuggestions.add(new JScrollPane(listSuggestions));

        // Document listener: actualiza sugerencias al escribir
        txtConceptoEgresos.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { showSuggestions(); }
            public void removeUpdate(DocumentEvent e) { showSuggestions(); }
            public void changedUpdate(DocumentEvent e) { showSuggestions(); }
        });

        // Navegación por teclado
       txtConceptoEgresos.addKeyListener(new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        if (popupMenuSuggestions.isVisible()) {
            int selectedIndex = listSuggestions.getSelectedIndex();
            int newIndex = selectedIndex;

            switch (e.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    newIndex = (selectedIndex < listModelSuggestions.getSize() - 1) ? selectedIndex + 1 : 0;
                    listSuggestions.setSelectedIndex(newIndex);
                    listSuggestions.ensureIndexIsVisible(newIndex);
                    e.consume();
                    break;
                case KeyEvent.VK_UP:
                    newIndex = (selectedIndex > 0) ? selectedIndex - 1 : listModelSuggestions.getSize() - 1;
                    listSuggestions.setSelectedIndex(newIndex);
                    listSuggestions.ensureIndexIsVisible(newIndex);
                    e.consume();
                    break;
                case KeyEvent.VK_ENTER:
                    if (selectedIndex != -1) {
                        txtConceptoEgresos.setText(listSuggestions.getSelectedValue().toString());
                        popupMenuSuggestions.setVisible(false);
                        e.consume();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    popupMenuSuggestions.setVisible(false);
                    e.consume();
                    break;
            }
        }
    }
});
        // Clic en sugerencia
        listSuggestions.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                txtConceptoEgresos.setText(listSuggestions.getSelectedValue().toString());
                popupMenuSuggestions.setVisible(false);
            }
        });

        // Ocultar sugerencias si se pierde el foco
        txtConceptoEgresos.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!listSuggestions.isFocusOwner()) {
                        popupMenuSuggestions.setVisible(false);
                    }
                });
            }
        });

        listSuggestions.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!txtConceptoEgresos.isFocusOwner()) {
                        popupMenuSuggestions.setVisible(false);
                    }
                });
            }
        });

        // Layout con GroupLayout
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTitle)
                .addGap(20)
                .addComponent(lblUserIcon, 40, 40, 40)
                .addComponent(lblUserName)
                .addComponent(lblCashBox))
            .addComponent(lblSelectMovementType)
            .addGroup(layout.createSequentialGroup()
                .addComponent(btnIngreso, 120, 120, 120)
                .addComponent(btnEgreso, 120, 120, 120))
            .addComponent(lblGasto)
            .addComponent(cmbGasto)
            .addComponent(lblMonto)
            .addComponent(txtMonto)
            .addComponent(lblConceptoEgresos)
            .addComponent(txtConceptoEgresos)
            .addComponent(lblNota)
            .addComponent(scrollPaneNota)
            .addComponent(btnHacerMovimiento, GroupLayout.Alignment.TRAILING, 200, 200, 200)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lblTitle)
                .addComponent(lblUserIcon)
                .addComponent(lblUserName)
                .addComponent(lblCashBox))
            .addGap(10)
            .addComponent(lblSelectMovementType)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(btnIngreso, 40, 40, 40)
                .addComponent(btnEgreso, 40, 40, 40))
            .addComponent(lblGasto)
            .addComponent(cmbGasto, 30, 30, 30)
            .addComponent(lblMonto)
            .addComponent(txtMonto, 30, 30, 30)
            .addComponent(lblConceptoEgresos)
            .addComponent(txtConceptoEgresos, 30, 30, 30)
            .addComponent(lblNota)
            .addComponent(scrollPaneNota, 80, 80, 80)
            .addGap(10)
            .addComponent(btnHacerMovimiento, 40, 40, 40)
        );

        getContentPane().add(mainPanel);
        mainPanel.setPreferredSize(new Dimension(650, 600));
        setResizable(false);
        pack();

        // Foco al iniciar
        SwingUtilities.invokeLater(() -> txtConceptoEgresos.requestFocusInWindow());
    }
    /**
     * Filters the `allExpenseConcepts` based on the text in `txtConceptoEgresos`
     * and displays them in the `popupMenuSuggestions`.
     */
    
    /**
     * Muestra las sugerencias en el popup basadas en lo que el usuario escribió
     */
    private void showSuggestions() {
        String input = txtConceptoEgresos.getText().toLowerCase();
        listModelSuggestions.clear();

        if (input.length() >= 2) {
            for (String concept : allExpenseConcepts) {
                if (concept.toLowerCase().contains(input)) {
                    listModelSuggestions.addElement(concept);
                }
            }

            if (listModelSuggestions.isEmpty()) {
                popupMenuSuggestions.setVisible(false);
            } else {
                listSuggestions.setSelectedIndex(0);
                popupMenuSuggestions.show(txtConceptoEgresos, 0, txtConceptoEgresos.getHeight());
                txtConceptoEgresos.requestFocusInWindow();
            }
        } else {
            popupMenuSuggestions.setVisible(false);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MovimientosCaja().setVisible(true));
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
   /*
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    /*
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        mainPanel.setMinimumSize(new java.awt.Dimension(200, 200));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 533, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 506, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
*/
    /**
     * @param args the command line arguments
     */
    
    
      /*
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
  */
}
