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

    // 🔹 Variable global para la empresa activa
    public static int idEmpresaActiva = 0;

    public static void pagar(
        JLabel lblTotal,
        VentaDao Vdao,
        Venta v,
        Detalle Dv,
        JTable TableVenta,
        ProductosDao proDao,
        JTextField txtIdCV,
        JTextField txtCodigoVenta,
        JLabel LabelVendedor,
        JFrame ventanaActual
    ) {
        if (!"".equals(lblTotal.getText())) {
            try {
                String idTexto = txtIdCV.getText();
                if (idTexto.isEmpty()) return;

                int cliente = Integer.parseInt(idTexto);
                double TotalPagar = Double.parseDouble(lblTotal.getText());
                String vendedor = LabelVendedor.getText();
                String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                // Setear datos de la venta
                v.setCliente(cliente);
                v.setVendedor(vendedor);
                v.setTotal(TotalPagar);
                v.setFecha(fechaActual);
                
                // 🔹 Asignamos la empresa activa a la venta
                v.setId_empresa(idEmpresaActiva);

                // Registrar venta
                Vdao.RegistrarVenta(v);

                int idVenta = Vdao.IdVenta();
                for (int i = 0; i < TableVenta.getRowCount(); i++) {
                    int id_pro = Integer.parseInt(TableVenta.getValueAt(i, 0).toString());
                    int cant = Integer.parseInt(TableVenta.getValueAt(i, 2).toString());
                    double precio = Double.parseDouble(TableVenta.getValueAt(i, 3).toString());

                    // Registrar detalle
                    Dv.setId_pro(id_pro);
                    Dv.setCantidad(cant);
                    Dv.setPrecio(precio);
                    Dv.setId(idVenta);
                    Vdao.RegistrarDetalle(Dv);

                    // Actualizar stock
                    Productos pro = proDao.BuscarId(id_pro, idEmpresaActiva); // 🔹 Buscar producto según empresa
                    if (pro != null) {
                        int nuevoStock = pro.getStock() - cant;
                        if (nuevoStock < 0) nuevoStock = 0;
                        Vdao.ActualizarStock(cant, id_pro, idEmpresaActiva); // 🔹 Usamos cantidad vendida + empresa
                        System.out.println("Stock actualizado: producto=" + id_pro + ", nuevoStock=" + nuevoStock);
                    } else {
                        System.out.println("❌ Producto ID " + id_pro + " no pertenece a la empresa activa: " + idEmpresaActiva);
                    }
                }

                // Generar PDF de venta
                Vdao.pdfV(idVenta, cliente, TotalPagar, vendedor);

                // Limpiar UI y cerrar ventana
                txtCodigoVenta.requestFocus();
                ventanaActual.dispose();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error durante el cobro: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Paga con $ ?");
        }
    }
}
