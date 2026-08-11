package Controlador;

import Modelo.ImprimirTicket;
import Modelo.Venta;
import Modelo.VentaDao;
import Vista.HistorialVentasPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de Historial de Ventas.
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
            // Asumiendo columnas: [0: Folio, 1: Cliente, 2: Vendedor, 3: Total, 4: ID/Acción]
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
     */
    private void instalarBotonReimprimir() {
        JTable tabla = panel.getTableVentas();
        
        // Renderer para mostrar el botón
        tabla.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            private final JButton btn = new JButton("Reimprimir");

            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                return btn;
            }
        });

        tabla.getColumnModel().getColumn(4).setCellEditor(new ReimprimirButtonEditor());
    }

    private class ReimprimirButtonEditor extends javax.swing.AbstractCellEditor
            implements javax.swing.table.TableCellEditor {

        private final JButton boton = new JButton("Reimprimir");
        private int filaActual;
        private Object valorCelda;

        ReimprimirButtonEditor() {
            boton.addActionListener(e -> {
                fireEditingStopped();
                reimprimir(filaActual, valorCelda);
            });
        }

        @Override
        public Object getCellEditorValue() {
            return valorCelda; // Devuelve el ID original para no sobrescribir el modelo
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                       boolean isSelected, int row, int column) {
            this.filaActual = row;
            this.valorCelda = value; // Guarda el valor entero almacenado en la celda (id_venta)
            return boton;
        }
    }

  private void reimprimir(int filaVista, Object valorIdCelda) {
        try {
            int idVenta = -1;

            // 1. Intentamos obtener el ID enviado
            if (valorIdCelda instanceof Integer) {
                idVenta = (Integer) valorIdCelda;
            } else if (valorIdCelda != null && !valorIdCelda.toString().equals("Reimprimir")) {
                idVenta = Integer.parseInt(valorIdCelda.toString().trim());
            }

            // 2. Leemos la fila del modelo
            int filaModelo = panel.getTableVentas().convertRowIndexToModel(filaVista);
            DefaultTableModel modelo = panel.getModelo();

            // Intentamos buscar la venta primero por ID
            Venta venta = null;
            if (idVenta > 0) {
                venta = ventaDao.BuscarVenta(idVenta);
            }

            // 3. Si no se encontró por ID, leemos la Columna 0 (que normalmente almacena el Folio)
            if (venta == null) {
                Object valCol0 = modelo.getValueAt(filaModelo, 0);
                if (valCol0 != null) {
                    int folio = Integer.parseInt(valCol0.toString().trim());
                    // Buscamos por Folio e ID de Empresa
                    venta = ventaDao.BuscarVentaPorFolio(folio, idEmpresa);
                }
            }

            // 4. Si aún es null, mostramos un aviso detallado
            if (venta == null) {
                JOptionPane.showMessageDialog(panel, 
                    "No se pudo recuperar la venta.\n" +
                    "Verifica si BuscarVenta o BuscarVentaPorFolio corresponden con la BD.",
                    "Venta no encontrada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener el ID correcto de la venta recuperada
            idVenta = venta.getId();

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
                    List<Object[]> detalleCredito = ventaDao.listarDetalleCreditoPorVenta(idVenta);

                    if (detalleCredito.isEmpty()) {
                        JOptionPane.showMessageDialog(panel, "No se encontró detalle de crédito para esta venta.");
                        return;
                    }

                    List<String[]> listaProductosCredito = new ArrayList<>();
                    for (Object[] fila : detalleCredito) {
                        listaProductosCredito.add(new String[]{
                            String.valueOf(fila[0]), // producto
                            String.valueOf(fila[1]), // cantidad
                            String.valueOf(fila[2])  // precio
                        });
                    }

                    ticket = ImprimirTicket.generarTicketCredito(
                            idVenta, 
                            venta.getTotal(), 
                            tipoPago, 
                            listaProductosCredito,
                            venta.getPagaCon(), 
                            venta.getCambio(), 
                            venta.getNombre_cli()
                    );
                    break;

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
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, 500));

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel), "Vista previa del ticket", true);
        dialog.getContentPane().add(scroll);
        dialog.pack();
        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }
}