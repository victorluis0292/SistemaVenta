package Servicios;

import Modelo.*;
import Modelo.ImprimirTicket;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

public class CobroService {

    private final VentaDao ventaDao = new VentaDao();
    private final ProductosDao productosDao = new ProductosDao();
    private int idEmpresaActiva;  // ✅ ahora es instancia y se setea

    public void setIdEmpresaActiva(int id) {
        this.idEmpresaActiva = id;
    }

    /**
     * Registra una venta, sus detalles y actualiza el stock.
     * Retorna el ID de la venta registrada.
     */
    public int procesarVenta(int idTurno, int idCliente, String vendedor, JTable tablaVenta, double total,String tipoPago, double pagaCon, double cambio, double comision, double subtotal) throws Exception {

        System.out.println("🚀 procesarVenta iniciado con idTurno=" + idTurno + ", idCliente=" + idCliente);

        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        Venta venta = new Venta();
        venta.setCliente(idCliente);
        venta.setVendedor(vendedor);
        venta.setTotal(total);
        venta.setFecha(fecha);
        venta.setTipopago(tipoPago);
        venta.setIdTurno(idTurno);
        venta.setPagaCon(pagaCon);
        venta.setCambio(cambio);
        venta.setComision(comision);
        venta.setSubtotal(subtotal);

        int idVenta = ventaDao.RegistrarVenta(venta);
        if (idVenta == 0) {
            throw new Exception("No se pudo registrar la venta. Verifica los datos o revisa el log.");
        }

        System.out.println("✅ Venta registrada con ID: " + idVenta);

        Detalle detalle = new Detalle();

        // Datos adicionales si es crédito
        int dniCliente = -1;
        String nombreCliente = "";
        if (tipoPago.equalsIgnoreCase("credito")) {
            dniCliente = ventaDao.obtenerDniPorIdCliente(idCliente);
        }

        for (int i = 0; i < tablaVenta.getRowCount(); i++) {
            int idProducto = Integer.parseInt(tablaVenta.getValueAt(i, 0).toString());
            int cantidad = Integer.parseInt(tablaVenta.getValueAt(i, 2).toString());
            double precio = Double.parseDouble(tablaVenta.getValueAt(i, 3).toString());
            double totalProducto = cantidad * precio;

            // Validar cantidad positiva
            if (cantidad < 0) {
                System.out.println("⚠ Cantidad negativa detectada para producto ID=" + idProducto + ", usando abs() para seguridad.");
                cantidad = Math.abs(cantidad);
            }

            detalle.setId_pro(idProducto);
            detalle.setCantidad(cantidad);
            detalle.setPrecio(precio);
            detalle.setId(idVenta);

            if (tipoPago.equalsIgnoreCase("credito")) {
                detalle.setTotal(totalProducto);
                detalle.setCliente(String.valueOf(idCliente));
                detalle.setNombre(nombreCliente);
                detalle.setDni(dniCliente);
                detalle.setFecha(fecha);

                ventaDao.RegistrarDetalleCreditocliente(detalle);
            } else {
                ventaDao.RegistrarDetalle(detalle);
            }

            // Buscar producto filtrando por la empresa activa
            Productos producto = productosDao.BuscarId(idProducto, idEmpresaActiva);
            if (producto != null) {
                int nuevoStock = producto.getStock() - cantidad;
                if (nuevoStock < 0) nuevoStock = 0;  // seguridad

                System.out.println("🔹 Producto ID=" + idProducto + ", stock actual=" + producto.getStock() +
                                   ", cantidad vendida=" + cantidad + ", stock nuevo=" + nuevoStock);

              ventaDao.ActualizarStock(cantidad, idProducto, idEmpresaActiva);

            } else {
                System.out.println("❌ Producto con ID " + idProducto + " no pertenece a la empresa activa.");
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

            String ticket = ImprimirTicket.generarTicketEfectivo(idVenta, pago, cambio, tipoPago);
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
