package Controlador;

import Modelo.ImprimirTicket;
import Modelo.Venta;
import Modelo.VentaDao;
import Vista.HistorialVentasPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Controlador de Historial de Ventas.
 * Regla de oro: para reimprimir SIEMPRE se usa el mismo método de
 * ImprimirTicket que usa ventanaCobrar al momento de la venta original.
 * Así evitamos que Historial y Venta muestren formatos distintos (ej. verdulería/kg).
 */
public class HistorialVentasController {

    private final HistorialVentasPanel panel;
    private final VentaDao ventaDao = new VentaDao();
    private final int idEmpresa;

    public HistorialVentasController(HistorialVentasPanel panel, int idEmpresa) {
        this.panel = panel;
        this.idEmpresa = idEmpresa;

        this.panel.getBtnPdf().addActionListener(this::onGenerarPdf);
        instalarBotonReimprimir();
    }

    /** Carga (o recarga) la tabla con las ventas de la empresa activa. */
    public void cargarVentas() {
        panel.limpiar();
        List<Venta> lista = ventaDao.ListarVentas(idEmpresa);
        for (Venta v : lista) {
            panel.agregarFila(v.getFolio(), v.getNombre_cli(), v.getVendedor(), v.getTotal(), v.getId());
        }
    }

    private void onGenerarPdf(ActionEvent e) {
        String folioTxt = panel.getTxtFolioSeleccionado().getText().trim();
        if (folioTxt.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Selecciona una fila primero");
            return;
        }
        try {
            int folio = Integer.parseInt(folioTxt);
            Venta venta = ventaDao.BuscarVentaPorFolio(folio, idEmpresa);
            if (venta == null) {
                JOptionPane.showMessageDialog(panel, "No se encontró la venta con folio " + folio);
                return;
            }
            ventaDao.pdfV(venta.getId(), venta.getVendedor());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "Folio inválido");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error al generar PDF: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Botón "Reimprimir" embebido en la columna 4 de la tabla.
     * Al hacer click, regenera el ticket con EXACTAMENTE el mismo método
     * que usó la venta original (ImprimirTicket.generarTicketEfectivo/Tarjeta),
     * garantizando el mismo formato (incluida la línea de verdulería en kg).
     */
    private void instalarBotonReimprimir() {
        JTable tabla = panel.getTableVentas();
        tabla.getColumnModel().getColumn(4).setCellRenderer((t, value, isSelected, hasFocus, row, column) -> {
            JButton btn = new JButton("Reimprimir");
            return btn;
        });

        tabla.getColumnModel().getColumn(4).setCellEditor(new ReimprimirButtonEditor());
    }

    /**
     * Clase interna con nombre (no anónima) porque Java no permite
     * combinar "extends" + "implements" en una clase anónima con "new".
     */
    private class ReimprimirButtonEditor extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {

        private final JButton boton = new JButton("Reimprimir");
        private int filaActual;

        ReimprimirButtonEditor() {
            boton.addActionListener(e -> {
                fireEditingStopped();
                reimprimir(filaActual);
            });
        }

        @Override
        public Object getCellEditorValue() {
            return "Reimprimir";
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            filaActual = row;
            return boton;
        }
    }

    private void reimprimir(int filaVista) {
        try {
            int filaModelo = panel.getTableVentas().convertRowIndexToModel(filaVista);
            DefaultTableModel modelo = panel.getModelo();
            int idVenta = (int) modelo.getValueAt(filaModelo, 5);

            // Traemos la venta completa desde BD para saber tipo de pago, pago y cambio reales
            Venta venta = ventaDao.BuscarVenta(idVenta);
            if (venta == null) {
                JOptionPane.showMessageDialog(panel, "No se pudo recuperar la venta.");
                return;
            }

            String tipoPago = venta.getTipopago() != null ? venta.getTipopago() : "Efectivo";
            String ticket;

            switch (tipoPago.toLowerCase()) {
                case "tarjeta":
                case "mixto":
                    double subtotal = venta.getSubtotal();
                    double comision = venta.getComision();
                    ticket = ImprimirTicket.generarTicketTarjeta(idVenta, comision, tipoPago, subtotal);
                    break;
                case "credito":
                    // El ticket de crédito requiere el detalle línea por línea
                    // (no solo el total de la venta). Si se necesita reimpresión
                    // de crédito desde Historial, hay que traer el detalle desde
                    // AbonoDao/VentaDao y usar ImprimirTicket.generarTicketCredito(...).
                    JOptionPane.showMessageDialog(panel,
                            "La reimpresión de ventas a crédito aún no está soportada desde Historial.");
                    return;
                default: // Efectivo
                    ticket = ImprimirTicket.generarTicketEfectivo(
                            idVenta, venta.getPagaCon(), venta.getCambio(), tipoPago);
                    break;
            }

            ImprimirTicket.imprimir(ticket);
            mostrarVistaPrevia(ticket);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error al reimprimir: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void mostrarVistaPrevia(String ticket) {
        JTextArea area = new JTextArea(ticket);
        area.setEditable(false);
        area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 20));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(500, 600));

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel), "Vista previa del ticket", true);
        dialog.getContentPane().add(scroll);
        dialog.pack();
        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }
}