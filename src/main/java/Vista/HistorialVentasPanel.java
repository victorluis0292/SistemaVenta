package Vista;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.FlowLayout;

/**
 * Panel de Historial de Ventas.
 * SOLO responsabilidad visual: pintar tabla y botones.
 * Toda la lógica (listar, reimprimir, pdf) vive en HistorialVentasController.
 */
public class HistorialVentasPanel extends JPanel {

    private JTable tableVentas;
    private DefaultTableModel modelo;
    private JButton btnPdf;
    private JTextField txtFolioSeleccionado;

    public HistorialVentasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Historial de Ventas", JLabel.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(18f));
        add(titulo, BorderLayout.NORTH);

        // Columnas visibles: Folio, Cliente, Vendedor, Total, Ticket
        // Columna oculta (5): id interno de la venta
        modelo = new DefaultTableModel(new Object[]{"Folio", "Cliente", "Vendedor", "Total", "Ticket", "IdVenta"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4; // solo el botón "Reimprimir"
            }
        };

        tableVentas = new JTable(modelo);
        tableVentas.setRowHeight(28);
        tableVentas.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tableVentas);
        add(scroll, BorderLayout.CENTER);

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPdf = new JButton("Generar PDF");
        txtFolioSeleccionado = new JTextField(8);
        txtFolioSeleccionado.setEditable(false);
        panelSur.add(new JLabel("Folio seleccionado:"));
        panelSur.add(txtFolioSeleccionado);
        panelSur.add(btnPdf);
        add(panelSur, BorderLayout.SOUTH);

        tableVentas.getSelectionModel().addListSelectionListener(e -> {
            int fila = tableVentas.getSelectedRow();
            if (fila >= 0) {
                txtFolioSeleccionado.setText(String.valueOf(tableVentas.getValueAt(fila, 0)));
            }
        });

        // Ocultar columna IdVenta (índice 5) sin quitarla del modelo
        tableVentas.getColumnModel().getColumn(5).setMinWidth(0);
        tableVentas.getColumnModel().getColumn(5).setMaxWidth(0);
        tableVentas.getColumnModel().getColumn(5).setWidth(0);

        tableVentas.getColumnModel().getColumn(4).setMaxWidth(120);
        tableVentas.getColumnModel().getColumn(4).setMinWidth(100);
    }

    public JTable getTableVentas() {
        return tableVentas;
    }

    public DefaultTableModel getModelo() {
        return modelo;
    }

    public JButton getBtnPdf() {
        return btnPdf;
    }

    public JTextField getTxtFolioSeleccionado() {
        return txtFolioSeleccionado;
    }

    /** Limpia todas las filas de la tabla. */
    public void limpiar() {
        modelo.setRowCount(0);
    }

    /** Agrega una fila. idVentaOculto va en la columna 5 (oculta). */
    public void agregarFila(Object folio, String cliente, String vendedor, double total, int idVentaOculto) {
        modelo.addRow(new Object[]{folio, cliente, vendedor, total, "Reimprimir", idVentaOculto});
    }
}