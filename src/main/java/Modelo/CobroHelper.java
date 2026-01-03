package Helper;

import Modelo.Detalle;
import Modelo.Productos;
import Modelo.ProductosDao;
import Modelo.Venta;
import Modelo.VentaDao;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public class CobroHelper {

    /**
     * Método para procesar el pago de una venta
     * @param lblTotal Etiqueta que contiene el total a pagar
     * @param Vdao Objeto VentaDao para operaciones con la base de datos
     * @param v Objeto Venta que se va a registrar
     * @param Dv Objeto Detalle para los productos de la venta
     * @param TableVenta JTable con los productos a vender
     * @param proDao Objeto ProductosDao para consultar productos
     * @param txtIdCV Campo de texto con ID de cliente
     * @param txtCodigoVenta Campo de texto para enfoque después de cobro
     * @param LabelVendedor Etiqueta con nombre del vendedor
     * @param ventanaActual JFrame de la ventana actual
     * @param idEmpresaActiva ID de la empresa activa
     */
    public static void pagar(JLabel lblTotal,
                             VentaDao Vdao,
                             Venta v,
                             Detalle Dv,
                             JTable TableVenta,
                             ProductosDao proDao,
                             JTextField txtIdCV,
                             JTextField txtCodigoVenta,
                             JLabel LabelVendedor,
                             JFrame ventanaActual,
                             int idEmpresaActiva) {

        try {
            // 1️⃣ Validación del total
            if (lblTotal.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Paga con $ ?");
                return;
            }

            // 2️⃣ Obtener cliente y total
            String idTexto = txtIdCV.getText().trim();
            if (idTexto.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No se encontró ID de cliente");
                return;
            }

            int cliente = Integer.parseInt(idTexto);
            double TotalPagar = Double.parseDouble(lblTotal.getText().trim());
            String vendedor = LabelVendedor.getText();
            String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            // 3️⃣ Registrar venta
            v.setCliente(cliente);
            v.setVendedor(vendedor);
            v.setTotal(TotalPagar);
            v.setFecha(fechaActual);
            v.setIdEmpresa(idEmpresaActiva);

            int idVenta = Vdao.RegistrarVenta(v); // Ahora retorna el ID generado
            System.out.println("Venta registrada correctamente con ID: " + idVenta);

            // 4️⃣ Registrar detalles y actualizar stock
            for (int i = 0; i < TableVenta.getRowCount(); i++) {
                int id_pro = Integer.parseInt(TableVenta.getValueAt(i, 0).toString());
                int cant = Integer.parseInt(TableVenta.getValueAt(i, 2).toString());
                double precio = Double.parseDouble(TableVenta.getValueAt(i, 3).toString());

                // Asignar valores al detalle
                Dv.setId_pro(id_pro);
                Dv.setCantidad(cant);
                Dv.setPrecio(precio);
                Dv.setId(idVenta);

                // Registrar detalle
                Vdao.RegistrarDetalle(Dv);

                // Actualizar stock
                Productos pro = proDao.BuscarId(id_pro, idEmpresaActiva);
                if (pro == null) {
                    JOptionPane.showMessageDialog(null, "Producto con ID " + id_pro + " no encontrado.");
                    continue;
                }

                System.out.println("Producto ID=" + id_pro + ", stock actual=" + pro.getStock() + ", cantidad vendida=" + cant);

                boolean stockActualizado = Vdao.ActualizarStock(cant, id_pro, idEmpresaActiva);
                if (!stockActualizado) {
                    System.out.println("Warning: No se pudo actualizar stock del producto ID=" + id_pro);
                } else {
                    System.out.println("Stock actualizado correctamente para producto ID=" + id_pro);
                }
            }

            // 5️⃣ Generar PDF de la venta (idVenta + vendedor)
            Vdao.pdfV(idVenta, vendedor);

            // 6️⃣ Limpiar foco y cerrar ventana
            txtCodigoVenta.requestFocus();
            ventanaActual.dispose();

            System.out.println("Venta procesada correctamente para empresa ID=" + idEmpresaActiva);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: verifica los valores numéricos");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error durante el cobro: " + e.getMessage());
        }
    }
}
