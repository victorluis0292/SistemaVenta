package Controlador;

import Modelo.Cliente;
import Modelo.ClienteDao;
import Modelo.Detalle;
import Modelo.Productos;
import Modelo.ProductosDao;
import Modelo.VentaDao;
import Vista.ConsultaCreditoCliente;
import Vista.CreditoClientePanel;
import Vista.FrmBusqueda2;
import Vista.Sistema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controlador del módulo "Crédito Cliente".
 *
 * Toda la lógica del módulo Crédito Cliente vive aquí.
 *
 * IMPORTANTE:
 * - La tabla utilizada es la del CreditoClientePanel.
 * - Los productos provenientes de FrmBusqueda2/VentanaCantidadBusqueda
 *   se agregan mediante agregarFila().
 * - cantidad se maneja como double para permitir cantidades decimales.
 */
public class CreditoClienteController {

    private final CreditoClientePanel panel;

    private final ClienteDao clienteDao = new ClienteDao();
    private final ProductosDao productoDao = new ProductosDao();
    private final VentaDao ventaDao = new VentaDao();

    private final DecimalFormat dfCantidad = new DecimalFormat("0.###");

    private int idClienteActual = 0;

    public CreditoClienteController(CreditoClientePanel panel) {
        this.panel = panel;
        inicializarListeners();
    }

    // ============================================================
    // LISTENERS
    // ============================================================

    private void inicializarListeners() {

        // --------------------------------------------------------
        // BUSCAR CLIENTE
        // --------------------------------------------------------

        panel.getTxtRuc().addActionListener(e -> buscarCliente());

        // --------------------------------------------------------
        // BUSCAR / AGREGAR PRODUCTO POR CÓDIGO
        // --------------------------------------------------------

        panel.getTxtCodigo().addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent evt) {

                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    evt.consume();
                    agregarProductoPorCodigo();
                }
            }
        });

        // --------------------------------------------------------
        // BOTÓN BUSCAR PRODUCTO
        // --------------------------------------------------------

        panel.getBtnBuscarProducto().addActionListener(e -> abrirBusquedaProductos());

        // --------------------------------------------------------
        // ELIMINAR PRODUCTO
        // --------------------------------------------------------

        panel.getBtnEliminarProducto().addActionListener(
                e -> eliminarFilaSeleccionada()
        );

        // --------------------------------------------------------
        // VER HISTORIAL
        // --------------------------------------------------------

        panel.getBtnVerHistorial().addActionListener(
                e -> verHistorial()
        );

        // --------------------------------------------------------
        // ACEPTAR / GENERAR CRÉDITO
        // --------------------------------------------------------

        panel.getBtnAceptar().addActionListener(
                e -> generarVentaCredito()
        );

        // --------------------------------------------------------
        // ACTUALIZAR TOTAL AUTOMÁTICAMENTE
        // --------------------------------------------------------

        panel.getModeloProductos().addTableModelListener(
                e -> recalcularTotal()
        );
    }

    // ============================================================
    // BUSCAR PRODUCTOS
    // ============================================================

    private void abrirBusquedaProductos() {

        int idEmpresa = Sistema.getIdEmpresaActiva();

        System.out.println("======================================");
        System.out.println("ABRIENDO BUSQUEDA DE PRODUCTOS");
        System.out.println("Empresa activa: " + idEmpresa);
        System.out.println("Origen: credito");
        System.out.println("======================================");

        /*
         * MUY IMPORTANTE:
         *
         * Mandamos explícitamente "credito".
         *
         * FrmBusqueda2 posteriormente manda este origen
         * a VentanaCantidadBusqueda.
         */
        FrmBusqueda2 frm = new FrmBusqueda2(
                idEmpresa,
                "credito"
        );

        frm.setLocationRelativeTo(panel);
        frm.setVisible(true);
    }

    // ============================================================
    // BUSCAR CLIENTE
    // ============================================================

    private void buscarCliente() {

        String texto = panel.getTxtRuc().getText().trim();

        if (texto.isEmpty()) {
            return;
        }

        int dni;

        try {

            dni = Integer.parseInt(texto);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    panel,
                    "DNI inválido"
            );

            return;
        }

        int idEmpresa = Sistema.getIdEmpresaActiva();

        Cliente cl = clienteDao.BuscarCliente(
                dni,
                idEmpresa
        );

        if (cl != null && cl.getNombre() != null) {

            panel.getTxtNombreCliente().setText(
                    cl.getNombre()
            );

            idClienteActual = cl.getId();

            panel.getTxtCodigo().requestFocus();

        } else {

            JOptionPane.showMessageDialog(
                    panel,
                    "El cliente no existe en esta empresa"
            );

            limpiarCliente();
        }
    }

    // ============================================================
    // AGREGAR PRODUCTO ESCRIBIENDO CÓDIGO
    // ============================================================

    private void agregarProductoPorCodigo() {

        String texto = panel.getTxtCodigo()
                .getText()
                .trim();

        if (texto.isEmpty()) {

            JOptionPane.showMessageDialog(
                    panel,
                    "Ingrese el código o ID del producto"
            );

            panel.getTxtCodigo().requestFocus();

            return;
        }

        int idEmpresa = Sistema.getIdEmpresaActiva();

        Productos pro = productoDao.BuscarPro(
                texto,
                idEmpresa
        );

        // --------------------------------------------------------
        // SI NO ENCONTRÓ POR CÓDIGO, INTENTAR POR ID
        // --------------------------------------------------------

        if (pro == null || pro.getNombre() == null) {

            try {

                int id = Integer.parseInt(texto);

                pro = productoDao.BuscarId(
                        id,
                        idEmpresa
                );

            } catch (NumberFormatException ignored) {
                // No era un ID numérico.
            }
        }

        // --------------------------------------------------------
        // PRODUCTO NO ENCONTRADO
        // --------------------------------------------------------

        if (pro == null || pro.getNombre() == null) {

            JOptionPane.showMessageDialog(
                    panel,
                    "El código o ID del producto no existe"
            );

            limpiarCodigo();

            return;
        }

        // --------------------------------------------------------
        // CANTIDAD POR DEFECTO
        // --------------------------------------------------------

        double cantidad = 1.0;

        double stock = pro.getStock();

        if (stock < cantidad) {

            JOptionPane.showMessageDialog(
                    panel,
                    "Stock no disponible"
            );

            limpiarCodigo();

            return;
        }

        // --------------------------------------------------------
        // CALCULAR TOTAL
        // --------------------------------------------------------

        double total =
                cantidad * pro.getPrecio();

        // --------------------------------------------------------
        // AGREGAR A LA TABLA NUEVA
        // --------------------------------------------------------

        agregarFila(
                pro.getId(),
                pro.getNombre(),
                cantidad,
                pro.getPrecio(),
                total
        );

        limpiarCodigo();
    }

    // ============================================================
    // AGREGAR FILA A LA TABLA DE CRÉDITO
    // ============================================================

    /**
     * Este método es PÚBLICO porque también lo utiliza
     * VentanaCantidadBusqueda cuando el producto viene
     * desde la búsqueda.
     */
    public void agregarFila(
            int idProducto,
            String nombre,
            double cantidad,
            double precio,
            double total
    ) {

        System.out.println("======================================");
        System.out.println("AGREGANDO PRODUCTO A CRÉDITO");
        System.out.println("ID: " + idProducto);
        System.out.println("Descripción: " + nombre);
        System.out.println("Cantidad: " + cantidad);
        System.out.println("Precio: " + precio);
        System.out.println("Total: " + total);
        System.out.println("======================================");

        /*
         * IMPORTANTE:
         *
         * Usamos directamente el modelo del
         * CreditoClientePanel.
         *
         * Ya NO usamos:
         *
         * Sistema.TableCreditClient
         */

        Object[] fila = new Object[]{
                idProducto,
                nombre,
                cantidad,
                precio,
                total
        };

        panel.getModeloProductos().addRow(fila);

        recalcularTotal();

        panel.getTablaProductos().revalidate();
        panel.getTablaProductos().repaint();
    }

    // ============================================================
    // AGREGAR FILAS DESDE NUEVA VENTA
    // ============================================================

    /**
     * Recibe productos provenientes de Nueva Venta
     * y los pasa a Crédito Cliente.
     */
    public void agregarFilasDesdeVenta(Object[][] filasVenta) {

        if (filasVenta == null) {
            return;
        }

        for (Object[] fila : filasVenta) {

            if (fila == null || fila.length < 5) {
                continue;
            }

            try {

                int idProducto =
                        Integer.parseInt(
                                fila[0].toString()
                        );

                String nombre =
                        fila[1].toString();

                double cantidad =
                        Double.parseDouble(
                                fila[2].toString()
                        );

                double precio =
                        Double.parseDouble(
                                fila[3].toString()
                        );

                double total =
                        Double.parseDouble(
                                fila[4].toString()
                        );

                agregarFila(
                        idProducto,
                        nombre,
                        cantidad,
                        precio,
                        total
                );

            } catch (Exception ex) {

                System.out.println(
                        "Error al transferir producto desde venta: "
                                + ex.getMessage()
                );
            }
        }
    }

    // ============================================================
    // ELIMINAR PRODUCTO
    // ============================================================

    private void eliminarFilaSeleccionada() {

        int fila =
                panel.getTablaProductos().getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(
                    panel,
                    "Seleccione una fila para eliminar"
            );

            return;
        }

        panel.getModeloProductos().removeRow(fila);

        recalcularTotal();

        panel.getTxtCodigo().requestFocus();
    }

    // ============================================================
    // CALCULAR TOTAL
    // ============================================================

    private void recalcularTotal() {

        DefaultTableModel modelo =
                panel.getModeloProductos();

        double total = 0.0;

        for (int i = 0; i < modelo.getRowCount(); i++) {

            Object valor =
                    modelo.getValueAt(i, 4);

            if (valor == null) {
                continue;
            }

            try {

                total += Double.parseDouble(
                        valor.toString()
                );

            } catch (NumberFormatException ex) {

                System.out.println(
                        "Valor de total inválido en fila "
                                + i + ": "
                                + valor
                );
            }
        }

        panel.getLblTotalValor().setText(
                String.format("%.2f", total)
        );
    }

    // ============================================================
    // VER HISTORIAL
    // ============================================================

    private void verHistorial() {

        String rucTexto =
                panel.getTxtRuc()
                        .getText()
                        .trim();

        String nombre =
                panel.getTxtNombreCliente()
                        .getText()
                        .trim();

        if (rucTexto.isEmpty() || nombre.isEmpty()) {

            JOptionPane.showMessageDialog(
                    panel,
                    "Ingresa Número de Cliente + ENTER"
            );

            panel.getTxtRuc().requestFocus();

            return;
        }

        int ruc;

        try {

            ruc = Integer.parseInt(rucTexto);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    panel,
                    "DNI inválido"
            );

            return;
        }

        ConsultaCreditoCliente.setIdEmpresaActiva(
                Sistema.getIdEmpresaActiva()
        );

        ConsultaCreditoCliente consulta =
                new ConsultaCreditoCliente(
                        ruc,
                        nombre,
                        0,
                        0
                );

        consulta.setVisible(true);
    }

    // ============================================================
    // GENERAR VENTA A CRÉDITO
    // ============================================================

    private void generarVentaCredito() {

        DefaultTableModel modelo =
                panel.getModeloProductos();

        // --------------------------------------------------------
        // VALIDAR PRODUCTOS
        // --------------------------------------------------------

        if (modelo.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    panel,
                    "No hay productos en la venta"
            );

            panel.getTxtCodigo().requestFocus();

            return;
        }

        // --------------------------------------------------------
        // VALIDAR CLIENTE
        // --------------------------------------------------------

        String nombreCliente =
                panel.getTxtNombreCliente()
                        .getText()
                        .trim();

        String rucTexto =
                panel.getTxtRuc()
                        .getText()
                        .trim();

        if (nombreCliente.isEmpty()) {

            JOptionPane.showMessageDialog(
                    panel,
                    "Debes buscar un cliente"
            );

            panel.getTxtRuc().requestFocus();

            return;
        }

        int dni;

        try {

            dni = Integer.parseInt(rucTexto);

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    panel,
                    "DNI de cliente inválido"
            );

            return;
        }

        // --------------------------------------------------------
        // GENERAR ID DE VENTA
        // --------------------------------------------------------

        int idVenta =
                ventaDao.IdVenta();

        String fechaActual =
                new SimpleDateFormat(
                        "dd/MM/yyyy"
                ).format(new Date());

        // --------------------------------------------------------
        // REGISTRAR PRODUCTOS
        // --------------------------------------------------------

        for (int i = 0;
             i < modelo.getRowCount();
             i++) {

            try {

                int idProducto =
                        Integer.parseInt(
                                modelo.getValueAt(
                                        i,
                                        0
                                ).toString()
                        );

                String nombreProducto =
                        modelo.getValueAt(
                                i,
                                1
                        ).toString();

                double cantidad =
                        Double.parseDouble(
                                modelo.getValueAt(
                                        i,
                                        2
                                ).toString()
                        );

                double precio =
                        Double.parseDouble(
                                modelo.getValueAt(
                                        i,
                                        3
                                ).toString()
                        );

                double total =
                        Double.parseDouble(
                                modelo.getValueAt(
                                        i,
                                        4
                                ).toString()
                        );

                // ------------------------------------------------
                // CREAR DETALLE
                // ------------------------------------------------

                Detalle detalle =
                        new Detalle();

                detalle.setId_pro(idProducto);
                detalle.setNombre(nombreProducto);
                detalle.setCantidad(cantidad);
                detalle.setPrecio(precio);
                detalle.setTotal(total);
                detalle.setId(idVenta);
                detalle.setCliente(nombreCliente);
                detalle.setDni(dni);
                detalle.setFecha(fechaActual);

                // ------------------------------------------------
                // REGISTRAR CRÉDITO
                // ------------------------------------------------

                ventaDao.RegistrarDetalleCreditocliente(
                        detalle
                );

                // ------------------------------------------------
                // ACTUALIZAR STOCK
                // ------------------------------------------------

                actualizarStock(
                        idProducto,
                        cantidad
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        panel,
                        "Error al registrar producto: "
                                + ex.getMessage()
                );

                ex.printStackTrace();

                return;
            }
        }

        // --------------------------------------------------------
        // LIMPIAR
        // --------------------------------------------------------

        limpiarTodo();

        JOptionPane.showMessageDialog(
                panel,
                "Registro exitoso"
        );
    }

    // ============================================================
    // ACTUALIZAR STOCK
    // ============================================================

    private void actualizarStock(
            int idProducto,
            double cantidad
    ) {

        int idEmpresa =
                Sistema.getIdEmpresaActiva();

        Productos pro =
                productoDao.BuscarId(
                        idProducto,
                        idEmpresa
                );

        if (pro != null) {

            boolean actualizado =
                    ventaDao.ActualizarStock(
                            cantidad,
                            idProducto,
                            idEmpresa
                    );

            if (!actualizado) {

                System.out.println(
                        "❌ No se pudo actualizar stock. "
                                + "Producto ID="
                                + idProducto
                );
            }

        } else {

            System.out.println(
                    "❌ Producto ID="
                            + idProducto
                            + " no pertenece a la empresa activa."
            );
        }
    }

    // ============================================================
    // LIMPIAR CÓDIGO
    // ============================================================

    private void limpiarCodigo() {

        panel.getTxtCodigo().setText("");

        panel.getTxtCodigo().requestFocus();
    }

    // ============================================================
    // LIMPIAR CLIENTE
    // ============================================================

    private void limpiarCliente() {

        panel.getTxtNombreCliente().setText("");

        idClienteActual = 0;
    }

    // ============================================================
    // LIMPIAR TODO
    // ============================================================

    private void limpiarTodo() {

        panel.getModeloProductos().setRowCount(0);

        panel.getTxtNombreCliente().setText("");

        panel.getTxtRuc().setText("");

        panel.getTxtCodigo().setText("");

        panel.getLblTotalValor().setText("-----");

        idClienteActual = 0;

        panel.getTxtRuc().requestFocus();
    }
}