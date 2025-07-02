
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

                v.setCliente(cliente);
                v.setVendedor(vendedor);
                v.setTotal(TotalPagar);
                v.setFecha(fechaActual);
                Vdao.RegistrarVenta(v);

                int idVenta = Vdao.IdVenta();
                for (int i = 0; i < TableVenta.getRowCount(); i++) {
                    int id_pro = Integer.parseInt(TableVenta.getValueAt(i, 0).toString());
                    int cant = Integer.parseInt(TableVenta.getValueAt(i, 2).toString());
                    double precio = Double.parseDouble(TableVenta.getValueAt(i, 3).toString());

                    Dv.setId_pro(id_pro);
                    Dv.setCantidad(cant);
                    Dv.setPrecio(precio);
                    Dv.setId(idVenta);
                    Vdao.RegistrarDetalle(Dv);

                    Productos pro = proDao.BuscarId(id_pro);
                    int nuevoStock = pro.getStock() - cant;
                    Vdao.ActualizarStock(nuevoStock, id_pro);
                }

                Vdao.pdfV(idVenta, cliente, TotalPagar, vendedor);
                //LimpiarTableVenta();
                //LimpiarCobro();
                txtCodigoVenta.requestFocus();
                ventanaActual.dispose();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error durante el cobro: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Paga con $ ?");
        }
    }
}
