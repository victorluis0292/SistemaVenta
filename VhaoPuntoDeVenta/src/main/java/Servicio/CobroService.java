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

       // Formatear fecha actual
    String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

    // Crear venta
    Venta venta = new Venta();
    venta.setCliente(idCliente);
    venta.setVendedor(vendedor);
    venta.setTotal(total);
    venta.setFecha(fecha);
    venta.setTipopago(tipoPago);
    venta.setIdTurno(idTurno); // ✅ ASIGNACIÓN DEL TURNO

    // 🔍 LOG: mostrar datos de la venta antes de insertar
    System.out.println("📦 Intentando registrar venta:");
    System.out.println("   Cliente: " + idCliente);
    System.out.println("   Vendedor: " + vendedor);
    System.out.println("   Total: " + total);
    System.out.println("   Fecha: " + fecha);
    System.out.println("   Tipo de pago: " + tipoPago);
    System.out.println("   ID Turno: " + idTurno);

    // Registrar venta y obtener ID generado
    int idVenta = ventaDao.RegistrarVenta(venta);
    System.out.println("🔁 ID de venta generado: " + idVenta);

    // Validar si no se registró la venta
    if (idVenta == 0) {
        System.err.println("❌ Error: la venta no fue registrada correctamente.");
        throw new Exception("No se pudo registrar la venta. Verifica los datos o revisa el log.");
    }

    // Registrar detalles
    Detalle detalle = new Detalle();
    for (int i = 0; i < tablaVenta.getRowCount(); i++) {
        int idProducto = Integer.parseInt(tablaVenta.getValueAt(i, 0).toString());
        int cantidad = Integer.parseInt(tablaVenta.getValueAt(i, 2).toString());
        double precio = Double.parseDouble(tablaVenta.getValueAt(i, 3).toString());

        detalle.setId_pro(idProducto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        detalle.setId(idVenta);

        // 🔍 LOG: detalle antes de insertar
        System.out.println("🧾 Registrando detalle:");
        System.out.println("   Producto ID: " + idProducto + " | Cantidad: " + cantidad + " | Precio: " + precio);

        ventaDao.RegistrarDetalle(detalle);

        // Actualizar stock
        Productos producto = productosDao.BuscarId(idProducto);
        if (producto != null) {
            int nuevoStock = producto.getStock() - cantidad;
            System.out.println("📉 Actualizando stock de producto ID " + idProducto + ": nuevo stock = " + nuevoStock);
            ventaDao.ActualizarStock(nuevoStock, idProducto);
        } else {
            System.err.println("⚠️ Producto no encontrado: ID = " + idProducto);
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
