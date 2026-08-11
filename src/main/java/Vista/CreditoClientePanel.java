package Vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Panel de UI para el módulo "Crédito Cliente".
 *
 * No contiene lógica de negocio:
 * toda la lógica vive en CreditoClienteController.
 *
 * Se instancia en Sistema y se agrega a la pestaña correspondiente.
 */
public class CreditoClientePanel extends JPanel {

    // ============================================================
    // CLIENTE
    // ============================================================

    private JTextField txtRuc;
    private JButton btnBuscarCliente;
    private JTextField txtNombreCliente;
    private JButton btnVerHistorial;

    // ============================================================
    // PRODUCTOS
    // ============================================================

    private JTextField txtCodigo;
    private JButton btnBuscarProducto;

    private JTable tablaProductos;
    private DefaultTableModel modeloProductos;
    private JScrollPane scrollProductos;
    private JButton btnEliminarProducto;

    // ============================================================
    // TOTAL / ACEPTAR
    // ============================================================

    private JLabel lblTotalValor;
    private JButton btnAceptar;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public CreditoClientePanel() {

        setLayout(null);
        setBackground(Color.WHITE);

        initComponentes();
    }

    // ============================================================
    // COMPONENTES
    // ============================================================

    private void initComponentes() {

        // ========================================================
        // TITULO
        // ========================================================

        JLabel lblTitulo = new JLabel("Crédito a Clientes");
        lblTitulo.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(255, 102, 102));
        lblTitulo.setBounds(700, 0, 250, 30);
        add(lblTitulo);


        // ========================================================
        // ID CLIENTE
        // ========================================================

        JLabel lblId = new JLabel("ID");
        lblId.setFont(new Font("Tahoma", Font.BOLD, 13));
        lblId.setBounds(160, 15, 30, 20);
        add(lblId);


        // ========================================================
        // CAMPO ID / DNI
        // ========================================================

        txtRuc = new JTextField();
        txtRuc.setFont(new Font("Tahoma", Font.PLAIN, 22));
        txtRuc.setBounds(190, 10, 100, 35);
        add(txtRuc);


        // ========================================================
        // BOTON ...
        // ========================================================

        btnBuscarCliente = new JButton("...");
        btnBuscarCliente.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnBuscarCliente.setToolTipText("Buscar cliente por ID o nombre");
        btnBuscarCliente.setBounds(295, 10, 45, 35);
        add(btnBuscarCliente);


        // ========================================================
        // CLIENTE
        // ========================================================

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblCliente.setBounds(350, 15, 70, 25);
        add(lblCliente);


        // ========================================================
        // NOMBRE CLIENTE
        // ========================================================

        txtNombreCliente = new JTextField();
        txtNombreCliente.setEditable(false);
        txtNombreCliente.setFont(new Font("Tahoma", Font.PLAIN, 20));
        txtNombreCliente.setForeground(new Color(51, 51, 255));
        txtNombreCliente.setBounds(420, 10, 200, 35);
        add(txtNombreCliente);


        // ========================================================
        // VER HISTORIAL
        // ========================================================

        btnVerHistorial = new JButton("Ver Historial");
        btnVerHistorial.setBounds(640, 15, 120, 30);
        add(btnVerHistorial);


        // ========================================================
        // CODIGO PRODUCTO
        // ========================================================

        JLabel lblCodigo = new JLabel("Código");
        lblCodigo.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblCodigo.setBounds(10, 55, 60, 20);
        add(lblCodigo);


        // ========================================================
        // CAMPO CODIGO
        // ========================================================

        txtCodigo = new JTextField();
        txtCodigo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtCodigo.setBounds(10, 75, 210, 35);
        add(txtCodigo);


        // ========================================================
        // BUSCAR PRODUCTO
        // ========================================================

        btnBuscarProducto = new JButton("buscar");
        btnBuscarProducto.setBounds(230, 75, 100, 35);
        add(btnBuscarProducto);


        // ========================================================
        // ELIMINAR PRODUCTO
        // ========================================================

        btnEliminarProducto = new JButton("Eliminar");
        btnEliminarProducto.setBounds(920, 75, 110, 35);
        add(btnEliminarProducto);


        // ========================================================
        // TABLA PRODUCTOS
        // ========================================================

        modeloProductos = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                        "ID",
                        "Descripcion",
                        "Cantidad",
                        "Precio U.",
                        "Precio Total"
                }
        ) {

            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };


        tablaProductos = new JTable(modeloProductos);

        tablaProductos.setRowHeight(25);

        tablaProductos.setFont(
                new Font("Tahoma", Font.PLAIN, 16)
        );

        tablaProductos.getTableHeader()
                .setReorderingAllowed(false);


        // ========================================================
        // SCROLL TABLA
        // ========================================================

        scrollProductos = new JScrollPane(tablaProductos);

        scrollProductos.setBounds(
                10,
                120,
                1020,
                340
        );

        add(scrollProductos);


        // ========================================================
        // TOTAL
        // ========================================================

        JLabel lblTotal = new JLabel("Total:");

        lblTotal.setFont(
                new Font("Tahoma", Font.PLAIN, 16)
        );

        lblTotal.setBounds(
                760,
                480,
                60,
                30
        );

        add(lblTotal);


        // ========================================================
        // VALOR TOTAL
        // ========================================================

        lblTotalValor = new JLabel("-----");

        lblTotalValor.setFont(
                new Font("Tahoma", Font.PLAIN, 24)
        );

        lblTotalValor.setBounds(
                830,
                475,
                150,
                35
        );

        add(lblTotalValor);


        // ========================================================
        // ACEPTAR
        // ========================================================

        btnAceptar = new JButton("ACEPTAR");

        btnAceptar.setBounds(
                910,
                475,
                120,
                40
        );

        add(btnAceptar);
    }


    // ============================================================
    // GETTERS USADOS POR CreditoClienteController
    // ============================================================

    public JTextField getTxtRuc() {
        return txtRuc;
    }


    public JButton getBtnBuscarCliente() {
        return btnBuscarCliente;
    }


    public JTextField getTxtNombreCliente() {
        return txtNombreCliente;
    }


    public JButton getBtnVerHistorial() {
        return btnVerHistorial;
    }


    public JTextField getTxtCodigo() {
        return txtCodigo;
    }


    public JButton getBtnBuscarProducto() {
        return btnBuscarProducto;
    }


    public JTable getTablaProductos() {
        return tablaProductos;
    }


    public DefaultTableModel getModeloProductos() {
        return modeloProductos;
    }


    public JButton getBtnEliminarProducto() {
        return btnEliminarProducto;
    }


    public JLabel getLblTotalValor() {
        return lblTotalValor;
    }


    public JButton getBtnAceptar() {
        return btnAceptar;
    }
}