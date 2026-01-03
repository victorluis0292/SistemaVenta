package Servicios;

import Modelo.*;
import Reportes.ImprimirTicket;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class CobroService {

    private final VentaDao ventaDao = new VentaDao();
    private final ProductosDao productosDao = new ProductosDao();

    /**
     * Registra una venta, sus detalles y actualiza el stock.
     * Retorna el ID de la venta registrada.
     */
public int procesarVenta(int idTurno, int idCliente, String vendedor, JTable tablaVenta, double total, String tipoPago) throws Exception {
    System.out.println("🚀 procesarVenta iniciado con idTurno=" + idTurno + ", idCliente=" + idCliente);

    String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

    Venta venta = new Venta();
    venta.setCliente(idCliente);
    venta.setVendedor(vendedor);
    venta.setTotal(total);
    venta.setFecha(fecha);
    venta.setTipopago(tipoPago);
    venta.setIdTurno(idTurno);

    int idVenta = ventaDao.RegistrarVenta(venta);
    if (idVenta == 0) {
        throw new Exception("No se pudo registrar la venta. Verifica los datos o revisa el log.");
    }

    Detalle detalle = new Detalle();

    // Si es crédito, necesitamos obtener datos adicionales del cliente
    int dniCliente = -1;
    String nombreCliente = "";
    if (tipoPago.equalsIgnoreCase("credito")) {
        // Obtener DNI y nombre cliente de la base
        // Puedes crear un método en ventaDao para esto, o obtenerlo desde otra tabla
        dniCliente = ventaDao.obtenerDniPorIdCliente(idCliente);
       // nombreCliente = obtenerNombreCliente(idCliente); // Crea este método para obtener nombre si no tienes uno
    }

    for (int i = 0; i < tablaVenta.getRowCount(); i++) {
        int idProducto = Integer.parseInt(tablaVenta.getValueAt(i, 0).toString());
        int cantidad = Integer.parseInt(tablaVenta.getValueAt(i, 2).toString());
        double precio = Double.parseDouble(tablaVenta.getValueAt(i, 3).toString());
        double totalProducto = cantidad * precio;

        detalle.setId_pro(idProducto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        detalle.setId(idVenta);

        if (tipoPago.equalsIgnoreCase("credito")) {
            // Llenar campos extras para crédito
            detalle.setTotal(totalProducto);
            detalle.setCliente(String.valueOf(idCliente)); // o como manejes cliente en detalle_creditocliente
            detalle.setNombre(nombreCliente);
            detalle.setDni(dniCliente);
            detalle.setFecha(fecha);

            ventaDao.RegistrarDetalleCreditocliente(detalle);
        } else {
            ventaDao.RegistrarDetalle(detalle);
        }

        Productos producto = productosDao.BuscarId(idProducto);
        if (producto != null) {
            int nuevoStock = producto.getStock() - cantidad;
            ventaDao.ActualizarStock(nuevoStock, idProducto);
        }
    }
    return idVenta;
}

    /**
     * Genera y manda a imprimir el ticket de una venta.
     */
    public void generarYImprimirTicket(int idVenta, double pago, double cambio, String tipoPago) {
        try {
            if (tipoPago.equalsIgnoreCase("Efectivo") || tipoPago.equalsIgnoreCase("Mixto")) {
                AbrirCajaEfectivo.main(null);
            }

            String ticket = ImprimirTicket.generarTicketReal(idVenta, pago, cambio, tipoPago);
            ImprimirTicket.imprimir(ticket);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al imprimir ticket: " + e.getMessage());
        }
    }

    /**
     * Muestra el ticket generado en un JDialog.
     */
    public void mostrarTicketDialog(JFrame parent, String ticketTexto) {
        JTextArea area = new JTextArea(ticketTexto);
        area.setEditable(false);
        area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 30));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(800, 800));

        final JDialog dialog = new JDialog(parent, "Vista previa de ticket", true);
        dialog.getContentPane().add(scroll);

        JButton btnOK = new JButton("OK");
        btnOK.setVisible(false);
        dialog.getContentPane().add(btnOK, java.awt.BorderLayout.SOUTH);
        dialog.getRootPane().setDefaultButton(btnOK);

        dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ENTER"), "close");
        dialog.getRootPane().getActionMap().put("close", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
