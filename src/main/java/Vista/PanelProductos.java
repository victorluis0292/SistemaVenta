package Vista;

import Estilos.Estilos;
import Modelo.CatalogoGlobal;
import Modelo.CatalogoGlobalDao;
import Modelo.Combo;
import Modelo.Productos;
import Modelo.ProductosDao;
import Modelo.Proveedor;
import Modelo.ProveedorDao;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import Modelo.Categoria;
import Modelo.CategoriaDao;

public class PanelProductos extends JPanel {

    // ============================================================
    // CAMPOS PUBLICOS
    // ============================================================
    public JTextField txtBuscar;

    public JTextField txtCodigoPro,
            txtDesPro,
            txtCantPro,
            txtPrecioPro,
            txtPreciocompraPro,
            txtIdproducto,
            txtImagenPro;

    public JComboBox<Combo> cbxProveedorPro;
    public JComboBox<Categoria> cbxCategoria;

    public JButton btnGuardarpro,
            btnEditarpro,
            btnEliminarPro,
            btnNuevoPro,
            btnBuscarImagen;

    // "Se vende"
    public JRadioButton rbPorUnidad,
            rbAGranel,
            rbComoPaquete;

    private ButtonGroup grupoTipoVenta;

    public JTable TableProducto;
    public DefaultTableModel modelo;

    // ============================================================
    // DAO
    // ============================================================
    private final CategoriaDao categoriaDao = new CategoriaDao();
    private int idEmpresa;

    private ProductosDao proDao;

    private ProveedorDao proveedorDao;

    private final CatalogoGlobalDao catalogoDao =
            new CatalogoGlobalDao();
    // ============================================================
    // CATALOGO GLOBAL
    // ============================================================

    private Integer idCatalogoGlobalSeleccionado = null;

    // ============================================================
    // CATEGORIA
    // ============================================================

    private Integer idCategoriaActual = null;

    // ============================================================
    // CONTROL PROVEEDOR
    // ============================================================

    private boolean ignorarEventoProveedor = false;

    // ============================================================
    // FILTRO
    // ============================================================

    private TableRowSorter<DefaultTableModel> sorterProductos;

    // ============================================================
    // CALLBACK
    // ============================================================

    private final Runnable onProductosActualizados;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public PanelProductos(
            int idEmpresa,
            ProductosDao proDao,
            ProveedorDao proveedorDao) {

        this(
                idEmpresa,
                proDao,
                proveedorDao,
                null
        );
    }

    public PanelProductos(
            int idEmpresa,
            ProductosDao proDao,
            ProveedorDao proveedorDao,
            Runnable onProductosActualizados) {

        this.idEmpresa =
                idEmpresa;

        this.proDao =
                proDao;

        this.proveedorDao =
                proveedorDao;

        this.onProductosActualizados =
                onProductosActualizados;

        initComponents();

        llenarProveedor();
        llenarCategorias();
        ListarProductos();

        initEvents();
    }

    // ============================================================
    // NOTIFICAR CAMBIO
    // ============================================================

    private void notificarCambioDeProductos() {

        if (onProductosActualizados != null) {

            onProductosActualizados.run();
        }
    }

    // ============================================================
    // DISEÑO PRINCIPAL
    // ============================================================

    private void initComponents() {

        setLayout(
                new BorderLayout(
                        12,
                        12
                )
        );

        setBackground(
                new Color(
                        245,
                        247,
                        250
                )
        );

        setBorder(
                new EmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        // ========================================================
        // BUSCADOR SUPERIOR
        // ========================================================
        JPanel panelSuperior =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        panelSuperior.setBackground(
                Color.WHITE
        );

        panelSuperior.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        220,
                                        224,
                                        230
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                15,
                                10,
                                15
                        )
                )
        );


        JLabel lblTitulo =
                new JLabel(
                        "PRODUCTOS"
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        lblTitulo.setForeground(
                new Color(
                        0,
                        102,
                        204
                )
        );


        JPanel panelBusqueda =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        panelBusqueda.setOpaque(
                false
        );


        JLabel lblBuscar =
                new JLabel(
                        "Buscar:"
                );

        Estilos.estiloEtiqueta(
                lblBuscar
        );

        lblBuscar.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );


        txtBuscar =
                new JTextField();

        txtBuscar.setPreferredSize(
                new Dimension(
                        350,
                        34
                )
        );

        Estilos.estiloCampo(
                txtBuscar
        );


        panelBusqueda.add(
                lblBuscar
        );

        panelBusqueda.add(
                txtBuscar
        );


        panelSuperior.add(
                lblTitulo,
                BorderLayout.WEST
        );

        panelSuperior.add(
                panelBusqueda,
                BorderLayout.EAST
        );


        add(
                panelSuperior,
                BorderLayout.NORTH
        );


        // ========================================================
        // FORMULARIO
        // ========================================================

        Estilos.PanelConEstilo panelFormulario =
                new Estilos.PanelConEstilo();

        panelFormulario.setPreferredSize(
                new Dimension(
                        430,
                        0
                )
        );


        JPanel contenidoFormulario =
                new JPanel(
                        new GridBagLayout()
                );

        contenidoFormulario.setOpaque(
                false
        );


        JLabel tituloFormulario =
                new JLabel(
                        "Nuevo Producto"
                );

        tituloFormulario.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        tituloFormulario.setForeground(
                new Color(
                        0,
                        102,
                        204
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(
                        5,
                        5,
                        5,
                        5
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx =
                1.0;


        // ========================================================
        // CAMPOS
        // ========================================================

        txtCodigoPro =
                new JTextField();

        txtDesPro =
                new JTextField();

        txtCantPro =
                new JTextField();

        txtPrecioPro =
                new JTextField();

        txtPreciocompraPro =
                new JTextField();


        Estilos.estiloCampo(
                txtCodigoPro
        );

        Estilos.estiloCampo(
                txtDesPro
        );

        Estilos.estiloCampo(
                txtCantPro
        );

        Estilos.estiloCampo(
                txtPrecioPro
        );

        Estilos.estiloCampo(
                txtPreciocompraPro
        );


        // ========================================================
        // RADIO BUTTONS
        // ========================================================

        rbPorUnidad =
                new JRadioButton(
                        "Por Unidad/Pza",
                        true
                );

        rbAGranel =
                new JRadioButton(
                        "A Granel"
                );

        rbComoPaquete =
                new JRadioButton(
                        "Paquete / Kit"
                );


        Font fontRadio =
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                );

        rbPorUnidad.setFont(
                fontRadio
        );

        rbAGranel.setFont(
                fontRadio
        );

        rbComoPaquete.setFont(
                fontRadio
        );


        rbPorUnidad.setOpaque(
                false
        );

        rbAGranel.setOpaque(
                false
        );

        rbComoPaquete.setOpaque(
                false
        );


        grupoTipoVenta =
                new ButtonGroup();

        grupoTipoVenta.add(
                rbPorUnidad
        );

        grupoTipoVenta.add(
                rbAGranel
        );

        grupoTipoVenta.add(
                rbComoPaquete
        );


        JPanel panelTipoVenta =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                3,
                                0
                        )
                );

        panelTipoVenta.setOpaque(
                false
        );

        panelTipoVenta.add(
                rbPorUnidad
        );

        panelTipoVenta.add(
                rbAGranel
        );

        panelTipoVenta.add(
                rbComoPaquete
        );


        // ========================================================
        // CATEGORIA
        // ========================================================

        cbxCategoria =
                new JComboBox<>();

        estilizarCombo(
                cbxCategoria
        );


        // ========================================================
        // PROVEEDOR
        // ========================================================

        cbxProveedorPro =
                new JComboBox<>();

        estilizarCombo(
                cbxProveedorPro
        );


        // ========================================================
        // RENDERER PROVEEDOR / CATEGORIA
        // ========================================================

        DefaultListCellRenderer cleanRenderer =
                new DefaultListCellRenderer() {

                    @Override
                    public Component
                    getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus) {

                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus
                        );


                        if (value instanceof Combo) {

                            setText(
                                    ((Combo) value)
                                            .getNombre()
                            );

                        } else if (value instanceof Categoria) {

                            setText(
                                    ((Categoria) value)
                                            .getNombre()
                            );
                        }


                        if (isSelected) {

                            setBackground(
                                    new Color(
                                            220,
                                            235,
                                            250
                                    )
                            );

                            setForeground(
                                    Color.BLACK
                            );

                        } else {

                            setBackground(
                                    Color.WHITE
                            );

                            setForeground(
                                    Color.BLACK
                            );
                        }


                        return this;
                    }
                };


        cbxProveedorPro.setRenderer(
                cleanRenderer
        );

        cbxCategoria.setRenderer(
                cleanRenderer
        );


        // ========================================================
        // IMAGEN / CATALOGO
        // ========================================================

        txtImagenPro =
                new JTextField();

        txtImagenPro.setEditable(
                false
        );

        Estilos.estiloCampo(
                txtImagenPro
        );


        btnBuscarImagen =
                new JButton(
                        "📁"
                );

        btnBuscarImagen.setPreferredSize(
                new Dimension(
                        45,
                        34
                )
        );

        Estilos.estiloBotonAzul(
                btnBuscarImagen
        );


        JPanel panelImagen =
                new JPanel(
                        new BorderLayout(
                                5,
                                0
                        )
                );

        panelImagen.setOpaque(
                false
        );

        panelImagen.add(
                txtImagenPro,
                BorderLayout.CENTER
        );

        panelImagen.add(
                btnBuscarImagen,
                BorderLayout.EAST
        );


        // ========================================================
        // ID OCULTO
        // ========================================================

        txtIdproducto =
                new JTextField();

        txtIdproducto.setVisible(
                false
        );


        // ========================================================
        // AGREGAR CAMPOS
        // ========================================================

        int fila = 0;


        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 2;

        contenidoFormulario.add(
                tituloFormulario,
                gbc
        );


        agregarCampo(
                contenidoFormulario,
                "Código:",
                txtCodigoPro,
                gbc,
                fila++
        );


        agregarCampo(
                contenidoFormulario,
                "Descripción:",
                txtDesPro,
                gbc,
                fila++
        );


        agregarCampo(
                contenidoFormulario,
                "Se vende:",
                panelTipoVenta,
                gbc,
                fila++
        );


        agregarCampo(
                contenidoFormulario,
                "Categoría:",
                cbxCategoria,
                gbc,
                fila++
        );


        agregarCampo(
                contenidoFormulario,
                "Cantidad:",
                txtCantPro,
                gbc,
                fila++
        );


        agregarCampo(
                contenidoFormulario,
                "Precio Venta:",
                txtPrecioPro,
                gbc,
                fila++
        );


        agregarCampo(
                contenidoFormulario,
                "Precio Compra:",
                txtPreciocompraPro,
                gbc,
                fila++
        );


        agregarCampo(
                contenidoFormulario,
                "Proveedor:",
                cbxProveedorPro,
                gbc,
                fila++
        );


        agregarCampo(
                contenidoFormulario,
                "Imagen / Catálogo:",
                panelImagen,
                gbc,
                fila++
        );


        // ========================================================
        // BOTONES
        // ========================================================

        JPanel panelBotones =
                new JPanel(
                        new GridLayout(
                                2,
                                2,
                                6,
                                6
                        )
                );

        panelBotones.setOpaque(
                false
        );


        btnGuardarpro =
                new JButton(
                        "💾 Guardar"
                );

        btnEditarpro =
                new JButton(
                        "📝 Actualizar"
                );

        btnEliminarPro =
                new JButton(
                        "❌ Eliminar"
                );

        btnNuevoPro =
                new JButton(
                        "➕ Nuevo"
                );


        Estilos.estiloBotonAzul(
                btnGuardarpro
        );

        Estilos.estiloBotonAzul(
                btnEditarpro
        );

        Estilos.estiloBotonAzul(
                btnEliminarPro
        );

        Estilos.estiloBotonAzul(
                btnNuevoPro
        );


        btnGuardarpro.setToolTipText(
                "Guardar producto"
        );

        btnEditarpro.setToolTipText(
                "Editar producto seleccionado"
        );

        btnEliminarPro.setToolTipText(
                "Eliminar producto seleccionado"
        );

        btnNuevoPro.setToolTipText(
                "Nuevo producto"
        );


        panelBotones.add(
                btnGuardarpro
        );

        panelBotones.add(
                btnEditarpro
        );

        panelBotones.add(
                btnEliminarPro
        );

        panelBotones.add(
                btnNuevoPro
        );


        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 2;

        gbc.insets =
                new Insets(
                        12,
                        5,
                        5,
                        5
                );


        contenidoFormulario.add(
                panelBotones,
                gbc
        );


        // ========================================================
        // ESPACIO FINAL
        // ========================================================

        gbc.gridy = fila;
        gbc.weighty = 1.0;

        contenidoFormulario.add(
                Box.createVerticalGlue(),
                gbc
        );


        panelFormulario.add(
                contenidoFormulario,
                BorderLayout.CENTER
        );


        // ========================================================
        // TABLA
        // ========================================================

        Estilos.PanelConEstilo panelTabla =
                new Estilos.PanelConEstilo();


        JPanel encabezadoTabla =
                new JPanel(
                        new BorderLayout()
                );

        encabezadoTabla.setOpaque(
                false
        );


        JLabel tituloTabla =
                new JLabel(
                        "Productos registrados"
                );

        tituloTabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        tituloTabla.setForeground(
                new Color(
                        0,
                        102,
                        204
                )
        );


        encabezadoTabla.add(
                tituloTabla,
                BorderLayout.WEST
        );


        // ========================================================
        // MODELO
        // ========================================================

        String[] nombresColumnas = {

                "ID",

                "CODIGO",

                "DESCRIPCIÓN",

                "CATEGORÍA",

                "PROVEEDOR",

                "STOCK",

                "PRECIO",

                "Precio Compra",

                "ID_Prov",

                "IMAGEN_URL"
        };


        modelo =
                new DefaultTableModel(
                        null,
                        nombresColumnas
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };


        TableProducto =
                new JTable(
                        modelo
                );


        // ========================================================
        // ESTILO TABLA
        // ========================================================

        Estilos.estiloTabla(
                TableProducto
        );


        TableProducto.setAutoResizeMode(
        JTable.AUTO_RESIZE_OFF
        );


        TableProducto.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        TableProducto.setRowHeight(
                30
        );


        TableProducto.setShowVerticalLines(
                false
        );

        TableProducto.setShowHorizontalLines(
                false
        );


        TableProducto.setIntercellSpacing(
                new Dimension(
                        0,
                        0
                )
        );


        TableProducto.setFillsViewportHeight(
                true
        );


        TableProducto.setAutoCreateRowSorter(
                false
        );


        // ========================================================
        // ANCHOS
        // ========================================================

        configurarAnchosTabla();


        // ========================================================
        // SORTER
        // ========================================================

        sorterProductos =
                new TableRowSorter<>(
                        modelo
                );

        TableProducto.setRowSorter(
                sorterProductos
        );


        // ========================================================
        // CLICK TABLA
        // ========================================================

        TableProducto.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            MouseEvent evt) {

                        TableProductoMouseClicked(
                                evt
                        );
                    }
                }
        );


        // ========================================================
        // SCROLL
        // ========================================================

        JScrollPane scrollTabla =
                new JScrollPane(
                        TableProducto
                );


        scrollTabla.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                210,
                                214,
                                220
                        )
                )
        );


        scrollTabla.setBackground(
                Color.WHITE
        );


        scrollTabla.getViewport()
                .setBackground(
                        Color.WHITE
                );


        // ========================================================
        // PANEL TABLA
        // ========================================================

        panelTabla.add(
                encabezadoTabla,
                BorderLayout.NORTH
        );

        panelTabla.add(
                scrollTabla,
                BorderLayout.CENTER
        );


        // ========================================================
        // CONTENEDOR PRINCIPAL
        // ========================================================

        JPanel contenido =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        contenido.setOpaque(
                false
        );


        contenido.add(
                panelFormulario,
                BorderLayout.WEST
        );


        contenido.add(
                panelTabla,
                BorderLayout.CENTER
        );


        add(
                contenido,
                BorderLayout.CENTER
        );


        // ========================================================
        // ESTADO INICIAL
        // ========================================================

        btnGuardarpro.setEnabled(
                true
        );

        btnEditarpro.setEnabled(
                false
        );
    }


    // ============================================================
    // CONFIGURACION DE TABLA
    // ============================================================

    private void configurarAnchosTabla() {

        if (TableProducto == null) {
            return;
        }


        TableColumn columna;


        // ========================================================
        // ID
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(0);

        columna.setPreferredWidth(
                65
        );

        columna.setMinWidth(
                55
        );

        columna.setMaxWidth(
                90
        );


        // ========================================================
        // CODIGO
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(1);

        columna.setPreferredWidth(
                130
        );

        columna.setMinWidth(
                100
        );


        // ========================================================
        // DESCRIPCION
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(2);

        columna.setPreferredWidth(
                300
        );

        columna.setMinWidth(
                180
        );


        // ========================================================
        // CATEGORIA
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(3);

        columna.setPreferredWidth(
                150
        );

        columna.setMinWidth(
                110
        );


        // ========================================================
        // PROVEEDOR
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(4);

        columna.setPreferredWidth(
                180
        );

        columna.setMinWidth(
                120
        );


        // ========================================================
        // STOCK
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(5);

        columna.setPreferredWidth(
                90
        );

        columna.setMinWidth(
                70
        );

        columna.setMaxWidth(
                120
        );


        // ========================================================
        // PRECIO
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(6);

        columna.setPreferredWidth(
                110
        );

        columna.setMinWidth(
                90
        );


        // ========================================================
        // PRECIO COMPRA
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(7);

        columna.setPreferredWidth(
                130
        );

        columna.setMinWidth(
                100
        );


        // ========================================================
        // ID_PROV
        // OCULTO
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(8);

        columna.setMinWidth(
                0
        );

        columna.setMaxWidth(
                0
        );

        columna.setPreferredWidth(
                0
        );

        columna.setWidth(
                0
        );


        // ========================================================
        // IMAGEN_URL
        // OCULTO
        // ========================================================

        columna =
                TableProducto
                        .getColumnModel()
                        .getColumn(9);

        columna.setMinWidth(
                0
        );

        columna.setMaxWidth(
                0
        );

        columna.setPreferredWidth(
                0
        );

        columna.setWidth(
                0
        );
    }


    // ============================================================
    // COMBOBOX
    // ============================================================

    private void estilizarCombo(
            JComboBox<?> combo) {

        combo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        combo.setBackground(
                Color.WHITE
        );

        combo.setForeground(
                new Color(
                        33,
                        33,
                        33
                )
        );

        combo.setPreferredSize(
                new Dimension(
                        0,
                        34
                )
        );

        combo.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                200,
                                200,
                                200
                        ),
                        1,
                        true
                )
        );

        combo.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }


    // ============================================================
    // AGREGAR CAMPO
    // ============================================================

    private void agregarCampo(
            JPanel panel,
            String textoLabel,
            JComponent componente,
            GridBagConstraints gbc,
            int fila) {

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = fila;

        gbc.weightx = 0;


        JLabel label =
                new JLabel(
                        textoLabel
                );


        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );


        label.setForeground(
                new Color(
                        70,
                        70,
                        70
                )
        );


        panel.add(
                label,
                gbc
        );


        gbc.gridx = 1;

        gbc.weightx = 1.0;


        panel.add(
                componente,
                gbc
        );
    }


    // ============================================================
    // CLICK TABLA
    // ============================================================

    private void TableProductoMouseClicked(
            MouseEvent evt) {

        int filaSeleccionada =
                TableProducto.getSelectedRow();


        if (filaSeleccionada == -1) {
            return;
        }


        int filaReal =
                TableProducto.convertRowIndexToModel(
                        filaSeleccionada
                );


        // ========================================================
        // ID
        // ========================================================

        txtIdproducto.setText(
                valorTabla(
                        filaReal,
                        0
                )
        );


        // ========================================================
        // CODIGO
        // ========================================================

        txtCodigoPro.setText(
                valorTabla(
                        filaReal,
                        1
                )
        );


        // ========================================================
        // DESCRIPCION
        // ========================================================

        txtDesPro.setText(
                valorTabla(
                        filaReal,
                        2
                )
        );


        // ========================================================
        // CATEGORIA
        //
        // El combo ahora contiene objetos Categoria, así que
        // buscamos el item cuyo nombre coincida con el texto
        // guardado en la tabla (columna 3) y lo seleccionamos.
        // ========================================================

        String categoriaNombre =
                valorTabla(
                        filaReal,
                        3
                );


        for (
                int i = 0;
                i < cbxCategoria.getItemCount();
                i++
        ) {

            Categoria item =
                    cbxCategoria.getItemAt(i);


            if (
                    item != null
                    &&
                    item.getNombre() != null
                    &&
                    item.getNombre()
                            .equalsIgnoreCase(categoriaNombre)
            ) {

                cbxCategoria.setSelectedIndex(i);

                idCategoriaActual =
                        item.getIdCategoria();

                break;
            }
        }


        // ========================================================
        // STOCK
        // ========================================================

        txtCantPro.setText(
                valorTabla(
                        filaReal,
                        5
                )
        );


        // ========================================================
        // PRECIO VENTA
        // ========================================================

        txtPrecioPro.setText(
                valorTabla(
                        filaReal,
                        6
                )
        );


        // ========================================================
        // PRECIO COMPRA
        // ========================================================

        txtPreciocompraPro.setText(
                valorTabla(
                        filaReal,
                        7
                )
        );


        // ========================================================
        // PROVEEDOR
        // ========================================================

        Object idProvObj =
                modelo.getValueAt(
                        filaReal,
                        8
                );


        if (idProvObj != null) {

            try {

                int idProv =
                        Integer.parseInt(
                                idProvObj.toString()
                        );


                ignorarEventoProveedor =
                        true;


                for (
                        int i = 0;
                        i < cbxProveedorPro.getItemCount();
                        i++
                ) {

                    Combo item =
                            cbxProveedorPro
                                    .getItemAt(i);


                    if (
                            item != null
                            &&
                            item.getId() == idProv
                    ) {

                        cbxProveedorPro
                                .setSelectedIndex(i);

                        break;
                    }
                }


                ignorarEventoProveedor =
                        false;

            } catch (Exception ex) {

                ignorarEventoProveedor =
                        false;
            }
        }


        // ========================================================
        // IMAGEN / CATALOGO
        //
        // NUEVO:
        // Recuperamos la URL guardada del producto.
        // ========================================================

        Object imagenUrlObj =
                modelo.getValueAt(
                        filaReal,
                        9
                );


        if (
                imagenUrlObj != null
                &&
                !imagenUrlObj
                        .toString()
                        .trim()
                        .isEmpty()
        ) {

            txtImagenPro.setText(
                    imagenUrlObj
                            .toString()
                            .trim()
            );

        } else {

            txtImagenPro.setText(
                    ""
            );
        }


        // ========================================================
        // BOTONES
        // ========================================================

        btnEditarpro.setEnabled(
                true
        );

        btnGuardarpro.setEnabled(
                false
        );
    }


    // ============================================================
    // OBTENER VALOR DE TABLA
    // ============================================================

    private String valorTabla(
            int fila,
            int columna) {

        Object valor =
                modelo.getValueAt(
                        fila,
                        columna
                );


        if (valor == null) {
            return "";
        }


        return valor.toString();
    }


    // ============================================================
    // TIPO DE VENTA
    // ============================================================

    public String obtenerTipoVenta() {

        if (rbAGranel.isSelected()) {

            return "GRANEL";
        }


        if (rbComoPaquete.isSelected()) {

            return "KIT";
        }


        return "UNIDAD";
    }


    public void setTipoVenta(
            String tipoVenta) {

        if (tipoVenta == null) {

            rbPorUnidad.setSelected(
                    true
            );

            return;
        }


        switch (
                tipoVenta.toUpperCase()
        ) {

            case "GRANEL":

                rbAGranel.setSelected(
                        true
                );

                break;


            case "KIT":

                rbComoPaquete.setSelected(
                        true
                );

                break;


            default:

                rbPorUnidad.setSelected(
                        true
                );
        }
    }


    // ============================================================
    // PROVEEDORES
    // ============================================================

    public void llenarProveedor() {

        cbxProveedorPro.removeAllItems();


        cbxProveedorPro.addItem(
                new Combo(
                        0,
                        "-- Seleccione --"
                )
        );


        List<Proveedor> lista =
                proveedorDao
                        .ListarProveedorPorEmpresa(
                                idEmpresa
                        );


        if (lista != null) {

            for (
                    Proveedor proveedor
                    : lista
            ) {

                cbxProveedorPro.addItem(
                        new Combo(
                                proveedor.getId(),
                                proveedor.getNombre()
                        )
                );
            }
        }


        cbxProveedorPro.addItem(
                new Combo(
                        -1,
                        "+ Agregar proveedor"
                )
        );
    }


    // ============================================================
    // CATEGORIAS
    // ============================================================

    public void llenarCategorias() {

        cbxCategoria.removeAllItems();

        cbxCategoria.addItem(
                new Categoria(
                        0,
                        "-- Seleccione --",
                        idEmpresa
                )
        );

        List<Categoria> lista =
                categoriaDao.listarPorEmpresa(
                        idEmpresa
                );

        if (lista != null) {

            for (
                    Categoria cat
                    : lista
            ) {

                cbxCategoria.addItem(
                        cat
                );
            }
        }
    }


    // ============================================================
    // LISTAR PRODUCTOS
    // ============================================================

    public void ListarProductos() {

        List<Productos> ListaPro =
                proDao.ListarProductos(
                        idEmpresa
                );


        modelo =
                (DefaultTableModel)
                        TableProducto.getModel();


        TableProducto.setRowSorter(
                null
        );


        modelo.setRowCount(
                0
        );


        // ========================================================
        // AHORA TENEMOS 10 COLUMNAS
        // ========================================================

        Object[] ob =
                new Object[10];


        if (ListaPro != null) {

            for (
                    Productos producto
                    : ListaPro
            ) {

                // ------------------------------------------------
                // ID
                // ------------------------------------------------

                ob[0] =
                        producto.getId();


                // ------------------------------------------------
                // CODIGO
                // ------------------------------------------------

                ob[1] =
                        producto.getCodigo();


                // ------------------------------------------------
                // DESCRIPCION
                // ------------------------------------------------

                ob[2] =
                        producto.getNombre();


                // ------------------------------------------------
                // CATEGORIA
                // ------------------------------------------------

                ob[3] =
                        producto.getCategoria();


                // ------------------------------------------------
                // PROVEEDOR
                // ------------------------------------------------

                ob[4] =
                        producto.getProveedor();


                // ------------------------------------------------
                // STOCK
                // ------------------------------------------------

                ob[5] =
                        producto.getStock();


                // ------------------------------------------------
                // PRECIO
                // ------------------------------------------------

                ob[6] =
                        producto.getPrecio();


                // ------------------------------------------------
                // PRECIO COMPRA
                // ------------------------------------------------

                ob[7] =
                        producto.getPreciocompra();


                // ------------------------------------------------
                // ID PROVEEDOR
                //
                // Se conserva exactamente como estaba
                // en tu código.
                // ------------------------------------------------

                ob[8] =
                        producto.getProveedor();


                // ------------------------------------------------
                // IMAGEN URL
                //
                // NUEVO
                // ------------------------------------------------

                ob[9] =
                        producto.getImagenUrl();


                modelo.addRow(
                        ob
                );
            }
        }


        sorterProductos =
                new TableRowSorter<>(
                        modelo
                );


        TableProducto.setRowSorter(
                sorterProductos
        );


        // ========================================================
        // RESTAURAR ANCHOS
        // ========================================================

        configurarAnchosTabla();


        // ========================================================
        // FILTRO
        // ========================================================

        aplicarFiltroBusqueda();
    }


    // ============================================================
    // EVENTOS
    // ============================================================

    private void initEvents() {

        // ========================================================
        // VALIDAR CODIGO DUPLICADO
        // ========================================================

        Runnable validarCodigoExistente =
                () -> {

                    String codigoIngresado =
                            txtCodigoPro
                                    .getText()
                                    .trim();


                    String idActual =
                            txtIdproducto
                                    .getText()
                                    .trim();


                    if (codigoIngresado.isEmpty()) {
                        return;
                    }


                    for (
                            int i = 0;
                            i < modelo.getRowCount();
                            i++
                    ) {

                        String idFila =
                                modelo.getValueAt(
                                        i,
                                        0
                                ).toString();


                        String codigoEnTabla =
                                modelo.getValueAt(
                                        i,
                                        1
                                ).toString();


                        if (
                                codigoEnTabla
                                        .equalsIgnoreCase(
                                                codigoIngresado
                                        )
                                &&
                                (
                                        idActual.isEmpty()
                                        ||
                                        !idFila.equals(
                                                idActual
                                        )
                                )
                        ) {

                            JOptionPane.showMessageDialog(
                                    this,
                                    "¡Este producto ya fue registrado!\n"
                                    + "El código '"
                                    + codigoIngresado
                                    + "' ya existe en el sistema.",
                                    "Código Duplicado",
                                    JOptionPane.WARNING_MESSAGE
                            );


                            txtCodigoPro.requestFocus();

                            txtCodigoPro.selectAll();

                            break;
                        }
                    }
                };


        txtCodigoPro.addActionListener(
                e ->
                        validarCodigoExistente.run()
        );


        txtCodigoPro.addFocusListener(
                new java.awt.event.FocusAdapter() {

                    @Override
                    public void focusLost(
                            java.awt.event.FocusEvent evt) {

                        if (
                                !txtCodigoPro
                                        .getText()
                                        .trim()
                                        .isEmpty()
                        ) {

                            validarCodigoExistente.run();
                        }
                    }
                }
        );


        // ========================================================
        // CATALOGO GLOBAL
        // ========================================================

        btnBuscarImagen.addActionListener(
                e -> {

                    String sugerido =
                            txtDesPro.getText()
                                    == null
                                    ? ""
                                    : txtDesPro
                                            .getText()
                                            .trim();


                    CatalogoGlobal seleccionado =
                            mostrarSelectorCatalogoGlobal(
                                    sugerido
                            );


                    if (seleccionado != null) {

                        txtImagenPro.setText(
                                seleccionado
                                        .getImagenUrl()
                                        == null
                                        ? ""
                                        : seleccionado
                                                .getImagenUrl()
                                                .trim()
                        );


                        idCatalogoGlobalSeleccionado =
                                seleccionado.getId();
                    }
                }
        );


        // ========================================================
        // GUARDAR
        // ========================================================

        btnGuardarpro.addActionListener(
                e -> {

                    Categoria categoriaSeleccionada =
                            (Categoria)
                                    cbxCategoria.getSelectedItem();


                    if (
                            categoriaSeleccionada == null
                            ||
                            categoriaSeleccionada.getIdCategoria() == 0
                    ) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Por favor seleccione una categoría válida.",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE
                        );

                        cbxCategoria.requestFocus();

                        return;
                    }


                    Combo itemProveedor =
                            (Combo)
                                    cbxProveedorPro
                                            .getSelectedItem();


                    if (
                            itemProveedor == null
                            ||
                            itemProveedor.getId() == 0
                            ||
                            itemProveedor.getId() == -1
                    ) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Por favor seleccione un proveedor válido.",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE
                        );

                        cbxProveedorPro.requestFocus();

                        return;
                    }


                    if (
                            !txtCodigoPro
                                    .getText()
                                    .trim()
                                    .isEmpty()
                            &&
                            !txtDesPro
                                    .getText()
                                    .trim()
                                    .isEmpty()
                            &&
                            !txtPrecioPro
                                    .getText()
                                    .trim()
                                    .isEmpty()
                    ) {

                        String codigoIngresado =
                                txtCodigoPro
                                        .getText()
                                        .trim();


                        for (
                                int i = 0;
                                i < modelo.getRowCount();
                                i++
                        ) {

                            if (
                                    modelo.getValueAt(
                                            i,
                                            1
                                    )
                                            .toString()
                                            .equalsIgnoreCase(
                                                    codigoIngresado
                                            )
                            ) {

                                JOptionPane.showMessageDialog(
                                        this,
                                        "El código '"
                                        + codigoIngresado
                                        + "' ya fue registrado en otro producto.",
                                        "Código Duplicado",
                                        JOptionPane.WARNING_MESSAGE
                                );


                                txtCodigoPro.requestFocus();

                                txtCodigoPro.selectAll();

                                return;
                            }
                        }


                        try {

                            Productos pro =
                                    new Productos();


                            pro.setCodigo(
                                    codigoIngresado
                            );


                            pro.setNombre(
                                    txtDesPro
                                            .getText()
                                            .trim()
                            );


                            pro.setCategoria(
                                    categoriaSeleccionada
                                            .getNombre()
                            );


                            pro.setStock(
                                    Integer.parseInt(
                                            txtCantPro
                                                    .getText()
                                                    .trim()
                                    )
                            );


                            pro.setPrecio(
                                    Double.parseDouble(
                                            txtPrecioPro
                                                    .getText()
                                                    .trim()
                                    )
                            );


                            pro.setPreciocompra(
                                    Double.parseDouble(
                                            txtPreciocompraPro
                                                    .getText()
                                                    .trim()
                                    )
                            );


                            pro.setProveedor(
                                    itemProveedor.getId()
                            );


                            pro.setProveedorPro(
                                    itemProveedor
                                            .getNombre()
                            );


                            pro.setImagenUrl(
                                    txtImagenPro
                                            .getText()
                                            .trim()
                            );


                            pro.setId_empresa(
                                    idEmpresa
                            );


                            pro.setIdCatalogoGlobal(
                                    idCatalogoGlobalSeleccionado
                            );


                            pro.setIdCategoria(
                                    categoriaSeleccionada
                                            .getIdCategoria()
                            );


                            boolean exito =
                                    proDao.RegistrarProductos(
                                            pro
                                    );


                            if (exito) {

                                JOptionPane.showMessageDialog(
                                        this,
                                        "¡Producto registrado con éxito!"
                                );


                                ListarProductos();

                                limpiarFormulario();

                                notificarCambioDeProductos();

                            } else {

                                JOptionPane.showMessageDialog(
                                        this,
                                        "Error al registrar el producto.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }

                        } catch (
                                NumberFormatException ex
                        ) {

                            JOptionPane.showMessageDialog(
                                    this,
                                    "Cantidad y precios deben contener valores numéricos válidos.",
                                    "Datos inválidos",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Hay campos obligatorios vacíos.",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
        );


        // ========================================================
        // NUEVO
        // ========================================================

        btnNuevoPro.addActionListener(
                e -> {

                    limpiarFormulario();

                    btnGuardarpro.setEnabled(
                            true
                    );

                    btnEditarpro.setEnabled(
                            false
                    );
                }
        );


        // ========================================================
        // ELIMINAR
        // ========================================================

        btnEliminarPro.addActionListener(
                e -> {

                    if (
                            !txtIdproducto
                                    .getText()
                                    .trim()
                                    .isEmpty()
                    ) {

                        int pregunta =
                                JOptionPane.showConfirmDialog(
                                        this,
                                        "¿Está seguro de eliminar este producto?",
                                        "Advertencia",
                                        JOptionPane.YES_NO_OPTION
                                );


                        if (
                                pregunta
                                        == JOptionPane.YES_OPTION
                        ) {

                            try {

                                int id =
                                        Integer.parseInt(
                                                txtIdproducto
                                                        .getText()
                                                        .trim()
                                        );


                                proDao.EliminarProductos(
                                        id
                                );


                                ListarProductos();

                                limpiarFormulario();

                                notificarCambioDeProductos();

                            } catch (
                                    NumberFormatException ex
                            ) {

                                JOptionPane.showMessageDialog(
                                        this,
                                        "El ID del producto no es válido.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Seleccione un producto de la tabla para eliminar."
                        );
                    }
                }
        );


        // ========================================================
        // EDITAR
        // ========================================================

        btnEditarpro.addActionListener(
                e -> {

                    if (
                            txtIdproducto
                                    .getText()
                                    .trim()
                                    .isEmpty()
                    ) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Seleccione un producto de la tabla para editar."
                        );

                        return;
                    }


                    Categoria categoriaSeleccionada =
                            (Categoria)
                                    cbxCategoria.getSelectedItem();


                    if (
                            categoriaSeleccionada == null
                            ||
                            categoriaSeleccionada.getIdCategoria() == 0
                    ) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Por favor seleccione una categoría válida.",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE
                        );

                        cbxCategoria.requestFocus();

                        return;
                    }


                    Combo itemProveedor =
                            (Combo)
                                    cbxProveedorPro
                                            .getSelectedItem();


                    if (
                            itemProveedor == null
                            ||
                            itemProveedor.getId() == 0
                            ||
                            itemProveedor.getId() == -1
                    ) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Por favor seleccione un proveedor válido.",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE
                        );

                        cbxProveedorPro.requestFocus();

                        return;
                    }


                    if (
                            !txtCodigoPro
                                    .getText()
                                    .trim()
                                    .isEmpty()
                            &&
                            !txtDesPro
                                    .getText()
                                    .trim()
                                    .isEmpty()
                            &&
                            !txtPrecioPro
                                    .getText()
                                    .trim()
                                    .isEmpty()
                    ) {

                        try {

                            Productos pro =
                                    new Productos();


                            pro.setId(
                                    Integer.parseInt(
                                            txtIdproducto
                                                    .getText()
                                                    .trim()
                                    )
                            );


                            pro.setCodigo(
                                    txtCodigoPro
                                            .getText()
                                            .trim()
                            );


                            pro.setNombre(
                                    txtDesPro
                                            .getText()
                                            .trim()
                            );


                            pro.setCategoria(
                                    categoriaSeleccionada
                                            .getNombre()
                            );


                            pro.setStock(
                                    Integer.parseInt(
                                            txtCantPro
                                                    .getText()
                                                    .trim()
                                    )
                            );


                            pro.setPrecio(
                                    Double.parseDouble(
                                            txtPrecioPro
                                                    .getText()
                                                    .trim()
                                    )
                            );


                            pro.setPreciocompra(
                                    Double.parseDouble(
                                            txtPreciocompraPro
                                                    .getText()
                                                    .trim()
                                    )
                            );


                            pro.setProveedor(
                                    itemProveedor.getId()
                            );


                            pro.setProveedorPro(
                                    itemProveedor
                                            .getNombre()
                            );


                            // ====================================================
                            // IMPORTANTE:
                            // Al editar conserva la URL que está en el campo.
                            // ====================================================

                            pro.setImagenUrl(
                                    txtImagenPro
                                            .getText()
                                            .trim()
                            );


                            pro.setId_empresa(
                                    idEmpresa
                            );


                            pro.setIdCatalogoGlobal(
                                    idCatalogoGlobalSeleccionado
                            );


                            pro.setIdCategoria(
                                    categoriaSeleccionada
                                            .getIdCategoria()
                            );


                            boolean exito =
                                    proDao.ModificarProductos(
                                            pro
                                    );


                            if (exito) {

                                JOptionPane.showMessageDialog(
                                        this,
                                        "¡Producto actualizado con éxito!"
                                );


                                ListarProductos();

                                limpiarFormulario();

                                notificarCambioDeProductos();

                            } else {

                                JOptionPane.showMessageDialog(
                                        this,
                                        "Error al actualizar el producto.",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }

                        } catch (
                                NumberFormatException ex
                        ) {

                            JOptionPane.showMessageDialog(
                                    this,
                                    "Cantidad y precios deben contener valores numéricos válidos.",
                                    "Datos inválidos",
                                    JOptionPane.WARNING_MESSAGE
                            );
                        }

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Hay campos obligatorios vacíos.",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE
                        );
                    }
                }
        );


        // ========================================================
        // PROVEEDOR
        // ========================================================

        cbxProveedorPro.addActionListener(
                e -> {

                    if (ignorarEventoProveedor) {
                        return;
                    }


                    Combo seleccionado =
                            (Combo)
                                    cbxProveedorPro
                                            .getSelectedItem();


                    if (
                            seleccionado != null
                            &&
                            seleccionado.getId() == -1
                    ) {

                        abrirDialogoAgregarProveedor();
                    }
                }
        );


        // ========================================================
        // BUSQUEDA
        // ========================================================

        txtBuscar
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {

                                aplicarFiltroBusqueda();
                            }


                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {

                                aplicarFiltroBusqueda();
                            }


                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {

                                aplicarFiltroBusqueda();
                            }
                        }
                );
    }


    // ============================================================
    // LIMPIAR FORMULARIO
    // ============================================================

    private void limpiarFormulario() {

        txtIdproducto.setText(
                ""
        );

        txtCodigoPro.setText(
                ""
        );

        txtDesPro.setText(
                ""
        );

        txtCantPro.setText(
                ""
        );

        txtPrecioPro.setText(
                ""
        );

        txtPreciocompraPro.setText(
                ""
        );

        txtImagenPro.setText(
                ""
        );


        idCatalogoGlobalSeleccionado =
                null;


        idCategoriaActual =
                null;


        rbPorUnidad.setSelected(
                true
        );


        if (
                cbxCategoria.getItemCount()
                        > 0
        ) {

            cbxCategoria.setSelectedIndex(
                    0
            );
        }


        if (
                cbxProveedorPro.getItemCount()
                        > 0
        ) {

            ignorarEventoProveedor =
                    true;

            cbxProveedorPro.setSelectedIndex(
                    0
            );

            ignorarEventoProveedor =
                    false;
        }


        TableProducto.clearSelection();


        btnGuardarpro.setEnabled(
                true
        );


        btnEditarpro.setEnabled(
                false
        );
    }


    // ============================================================
    // FILTRO
    // ============================================================

    private void aplicarFiltroBusqueda() {

        if (sorterProductos == null) {
            return;
        }


        String texto =
                txtBuscar.getText()
                        == null
                        ? ""
                        : txtBuscar
                                .getText()
                                .trim();


        if (texto.isEmpty()) {

            sorterProductos.setRowFilter(
                    null
            );

            return;
        }


        try {

            RowFilter<
                    DefaultTableModel,
                    Integer
                    > filtro =
                    RowFilter.regexFilter(
                            "(?i)"
                            +
                            Pattern.quote(
                                    texto
                            )
                    );


            sorterProductos.setRowFilter(
                    filtro
            );

        } catch (
                java.util.regex.PatternSyntaxException ex
        ) {

            sorterProductos.setRowFilter(
                    null
            );
        }
    }


    // ============================================================
    // DIALOGO AGREGAR PROVEEDOR
    // ============================================================

    private void abrirDialogoAgregarProveedor() {

        JDialog dialog =
                new JDialog(
                        (Frame)
                                SwingUtilities
                                        .getWindowAncestor(
                                                this
                                        ),
                        "Agregar Proveedor",
                        true
                );


        dialog.setSize(
                450,
                340
        );


        dialog.setLocationRelativeTo(
                this
        );


        dialog.setLayout(
                new GridBagLayout()
        );


        dialog.getContentPane()
                .setBackground(
                        new Color(
                                245,
                                247,
                                250
                        )
                );


        GridBagConstraints gbc =
                new GridBagConstraints();


        gbc.insets =
                new Insets(
                        8,
                        10,
                        8,
                        10
                );

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        JTextField txtRuc =
                new JTextField(
                        15
                );

        JTextField txtNombre =
                new JTextField(
                        15
                );

        JTextField txtTelefono =
                new JTextField(
                        15
                );

        JTextField txtDireccion =
                new JTextField(
                        15
                );


        Estilos.estiloCampo(
                txtRuc
        );

        Estilos.estiloCampo(
                txtNombre
        );

        Estilos.estiloCampo(
                txtTelefono
        );

        Estilos.estiloCampo(
                txtDireccion
        );


        int fila = 0;


        // ========================================================
        // RFC
        // ========================================================

        gbc.gridx = 0;
        gbc.gridy = fila;

        dialog.add(
                crearLabelDialogo(
                        "RFC:"
                ),
                gbc
        );


        gbc.gridx = 1;

        dialog.add(
                txtRuc,
                gbc
        );


        fila++;


        // ========================================================
        // NOMBRE
        // ========================================================

        gbc.gridx = 0;
        gbc.gridy = fila;

        dialog.add(
                crearLabelDialogo(
                        "Nombre:"
                ),
                gbc
        );


        gbc.gridx = 1;

        dialog.add(
                txtNombre,
                gbc
        );


        fila++;


        // ========================================================
        // TELEFONO
        // ========================================================

        gbc.gridx = 0;
        gbc.gridy = fila;

        dialog.add(
                crearLabelDialogo(
                        "Teléfono:"
                ),
                gbc
        );


        gbc.gridx = 1;

        dialog.add(
                txtTelefono,
                gbc
        );


        fila++;


        // ========================================================
        // DIRECCION
        // ========================================================

        gbc.gridx = 0;
        gbc.gridy = fila;

        dialog.add(
                crearLabelDialogo(
                        "Dirección:"
                ),
                gbc
        );


        gbc.gridx = 1;

        dialog.add(
                txtDireccion,
                gbc
        );


        fila++;


        // ========================================================
        // BOTONES
        // ========================================================

        JButton btnGuardar =
                new JButton(
                        "💾 Guardar"
                );


        JButton btnCancelar =
                new JButton(
                        "Cancelar"
                );


        Estilos.estiloBotonAzul(
                btnGuardar
        );

        Estilos.estiloBotonAzul(
                btnCancelar
        );


        JPanel panelBotones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                10,
                                0
                        )
                );

        panelBotones.setOpaque(
                false
        );


        panelBotones.add(
                btnGuardar
        );

        panelBotones.add(
                btnCancelar
        );


        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;


        dialog.add(
                panelBotones,
                gbc
        );


        // ========================================================
        // RESTAURAR SELECCION
        // ========================================================

        Runnable restaurarSeleccion =
                () -> {

                    ignorarEventoProveedor =
                            true;


                    if (
                            cbxProveedorPro
                                    .getItemCount()
                                    > 0
                    ) {

                        cbxProveedorPro
                                .setSelectedIndex(
                                        0
                                );
                    }


                    ignorarEventoProveedor =
                            false;
                };


        // ========================================================
        // CANCELAR
        // ========================================================

        btnCancelar.addActionListener(
                e -> {

                    dialog.dispose();

                    restaurarSeleccion.run();
                }
        );


        dialog.addWindowListener(
                new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(
                            java.awt.event.WindowEvent e) {

                        restaurarSeleccion.run();
                    }
                }
        );


        // ========================================================
        // GUARDAR PROVEEDOR
        // ========================================================

        btnGuardar.addActionListener(
                e -> {

                    if (
                            txtNombre
                                    .getText()
                                    .trim()
                                    .isEmpty()
                    ) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "El nombre es obligatorio.",
                                "Advertencia",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }


                    Proveedor nuevo =
                            new Proveedor();


                    nuevo.setRuc(
                            txtRuc
                                    .getText()
                                    .trim()
                    );


                    nuevo.setNombre(
                            txtNombre
                                    .getText()
                                    .trim()
                    );


                    nuevo.setTelefono(
                            txtTelefono
                                    .getText()
                                    .trim()
                    );


                    nuevo.setDireccion(
                            txtDireccion
                                    .getText()
                                    .trim()
                    );


                    nuevo.setIdEmpresa(
                            idEmpresa
                    );


                    if (
                            proveedorDao
                                    .RegistrarProveedor(
                                            nuevo
                                    )
                    ) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Proveedor agregado con éxito."
                        );


                        dialog.dispose();


                        llenarProveedor();


                        seleccionarProveedorPorNombre(
                                nuevo.getNombre()
                        );

                    } else {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Error al guardar el proveedor.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
        );


        dialog.setVisible(
                true
        );
    }


    // ============================================================
    // LABEL DIALOGO
    // ============================================================

    private JLabel crearLabelDialogo(
            String texto) {

        JLabel label =
                new JLabel(
                        texto
                );


        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );


        label.setForeground(
                new Color(
                        70,
                        70,
                        70
                )
        );


        return label;
    }


    // ============================================================
    // SELECCIONAR PROVEEDOR
    // ============================================================

    private void seleccionarProveedorPorNombre(
            String nombre) {

        ignorarEventoProveedor =
                true;


        for (
                int i = 0;
                i < cbxProveedorPro.getItemCount();
                i++
        ) {

            Combo item =
                    cbxProveedorPro.getItemAt(
                            i
                    );


            if (
                    item != null
                    &&
                    item.getNombre() != null
                    &&
                    item.getNombre()
                            .equals(nombre)
            ) {

                cbxProveedorPro
                        .setSelectedIndex(
                                i
                        );

                break;
            }
        }


        ignorarEventoProveedor =
                false;
    }


    // ============================================================
    // CATALOGO GLOBAL
    // ============================================================

    private CatalogoGlobal
    mostrarSelectorCatalogoGlobal(
            String filtroInicial) {

        JDialog dialog =
                new JDialog(
                        (Frame)
                                SwingUtilities
                                        .getWindowAncestor(
                                                this
                                        ),
                        "Catálogo Global",
                        true
                );


        dialog.setSize(
                700,
                520
        );


        dialog.setLocationRelativeTo(
                this
        );


        dialog.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );


        dialog.getContentPane()
                .setBackground(
                        new Color(
                                245,
                                247,
                                250
                        )
                );


        // ========================================================
        // BUSQUEDA CATALOGO
        // ========================================================

        JPanel panelBusqueda =
                new JPanel(
                        new BorderLayout(
                                6,
                                0
                        )
                );


        panelBusqueda.setBorder(
                new EmptyBorder(
                        10,
                        10,
                        0,
                        10
                )
        );


        panelBusqueda.setOpaque(
                false
        );


        JLabel lblBuscarCat =
                new JLabel(
                        "Buscar:"
                );


        lblBuscarCat.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );


        JTextField txtBuscarCat =
                new JTextField(
                        filtroInicial == null
                                ? ""
                                : filtroInicial
                );


        Estilos.estiloCampo(
                txtBuscarCat
        );


        JButton btnBuscarCat =
                new JButton(
                        "🔎 Buscar"
                );


        JButton btnVerTodo =
                new JButton(
                        "Ver todo"
                );


        Estilos.estiloBotonAzul(
                btnBuscarCat
        );

        Estilos.estiloBotonAzul(
                btnVerTodo
        );


        JPanel panelBotonesBusqueda =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                4,
                                0
                        )
                );

        panelBotonesBusqueda.setOpaque(
                false
        );


        panelBotonesBusqueda.add(
                btnBuscarCat
        );

        panelBotonesBusqueda.add(
                btnVerTodo
        );


        panelBusqueda.add(
                lblBuscarCat,
                BorderLayout.WEST
        );


        panelBusqueda.add(
                txtBuscarCat,
                BorderLayout.CENTER
        );


        panelBusqueda.add(
                panelBotonesBusqueda,
                BorderLayout.EAST
        );


        // ========================================================
        // CONTENEDOR
        // ========================================================

        JPanel panelContenedor =
                new JPanel(
                        new GridLayout(
                                0,
                                3,
                                10,
                                10
                        )
                );

        panelContenedor.setBackground(
                Color.WHITE
        );


        JScrollPane scrollPane =
                new JScrollPane(
                        panelContenedor
                );


        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                210,
                                214,
                                220
                        )
                )
        );


        final CatalogoGlobal[] elegido =
                {null};


        JPanel panelPrincipal =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        panelPrincipal.setOpaque(
                false
        );


        panelPrincipal.add(
                panelBusqueda,
                BorderLayout.NORTH
        );


        panelPrincipal.add(
                scrollPane,
                BorderLayout.CENTER
        );


        dialog.add(
                panelPrincipal,
                BorderLayout.CENTER
        );


        // ========================================================
        // CANCELAR
        // ========================================================

        JButton btnCancelar =
                new JButton(
                        "Cancelar"
                );


        Estilos.estiloBotonAzul(
                btnCancelar
        );


        JPanel panelSur =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        panelSur.setOpaque(
                false
        );


        panelSur.add(
                btnCancelar
        );


        dialog.add(
                panelSur,
                BorderLayout.SOUTH
        );


        btnCancelar.addActionListener(
                e ->
                        dialog.dispose()
        );


        // ========================================================
        // CARGAR
        // ========================================================

        Runnable[] cargarRef =
                new Runnable[1];


        cargarRef[0] =
                () -> {

                    String filtro =
                            txtBuscarCat
                                    .getText()
                                    == null
                                    ? ""
                                    : txtBuscarCat
                                            .getText()
                                            .trim();


                    cargarResultadosCatalogo(
                            filtro,
                            panelContenedor,
                            dialog,
                            elegido
                    );
                };


        btnBuscarCat.addActionListener(
                e ->
                        cargarRef[0].run()
        );


        btnVerTodo.addActionListener(
                e -> {

                    txtBuscarCat.setText(
                            ""
                    );

                    cargarRef[0].run();
                }
        );


        txtBuscarCat.addActionListener(
                e ->
                        cargarRef[0].run()
        );


        // ========================================================
        // BUSQUEDA EN TIEMPO REAL
        // ========================================================

        txtBuscarCat
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e) {

                                cargarRef[0].run();
                            }


                            @Override
                            public void removeUpdate(
                                    DocumentEvent e) {

                                cargarRef[0].run();
                            }


                            @Override
                            public void changedUpdate(
                                    DocumentEvent e) {

                                cargarRef[0].run();
                            }
                        }
                );


        cargarResultadosCatalogo(
                txtBuscarCat
                        .getText()
                        .trim(),
                panelContenedor,
                dialog,
                elegido
        );


        dialog.setVisible(
                true
        );


        return elegido[0];
    }


    // ============================================================
    // CARGAR CATALOGO
    // ============================================================

    private void cargarResultadosCatalogo(
            String filtro,
            JPanel panelContenedor,
            JDialog dialog,
            CatalogoGlobal[] elegido) {

        panelContenedor.removeAll();


        panelContenedor.setLayout(
                new BorderLayout()
        );


        panelContenedor.add(
                new JLabel(
                        "Cargando catálogo...",
                        JLabel.CENTER
                ),
                BorderLayout.CENTER
        );


        panelContenedor.revalidate();

        panelContenedor.repaint();


        new Thread(
                () -> {

                    List<CatalogoGlobal> lista =
                            (
                                    filtro == null
                                    ||
                                    filtro.isEmpty()
                            )
                            ?
                            catalogoDao
                                    .buscarPorNombre(
                                            ""
                                    )
                            :
                            catalogoDao
                                    .buscarPorNombre(
                                            filtro
                                    );


                    SwingUtilities.invokeLater(
                            () -> {

                                panelContenedor.removeAll();


                                if (
                                        lista == null
                                        ||
                                        lista.isEmpty()
                                ) {

                                    panelContenedor.setLayout(
                                            new BorderLayout()
                                    );


                                    panelContenedor.add(
                                            new JLabel(
                                                    "No se encontraron productos en el catálogo global.",
                                                    JLabel.CENTER
                                            ),
                                            BorderLayout.CENTER
                                    );

                                } else {

                                    panelContenedor.setLayout(
                                            new GridLayout(
                                                    0,
                                                    3,
                                                    10,
                                                    10
                                            )
                                    );


                                    for (
                                            CatalogoGlobal cat
                                            : lista
                                    ) {

                                        JButton btnItem =
                                                new JButton();


                                        btnItem.setToolTipText(
                                                "Seleccionar "
                                                +
                                                cat.getNombre()
                                        );


                                        btnItem.setFocusPainted(
                                                false
                                        );


                                        btnItem.setBackground(
                                                Color.WHITE
                                        );


                                        btnItem.setBorder(
                                                BorderFactory.createCompoundBorder(
                                                        BorderFactory.createLineBorder(
                                                                new Color(
                                                                        220,
                                                                        220,
                                                                        220
                                                                )
                                                        ),
                                                        BorderFactory.createEmptyBorder(
                                                                8,
                                                                8,
                                                                8,
                                                                8
                                                        )
                                                )
                                        );


                                        String imageUrl =
                                                cat.getImagenUrl();


                                        // ====================================================
                                        // CARGAR IMAGEN
                                        // ====================================================

                                        if (
                                                imageUrl != null
                                                &&
                                                !imageUrl
                                                        .trim()
                                                        .isEmpty()
                                        ) {

                                            try {

                                                URL url =
                                                        new URL(
                                                                imageUrl
                                                                        .trim()
                                                        );


                                                BufferedImage img =
                                                        ImageIO.read(
                                                                url
                                                        );


                                                if (img != null) {

                                                    Image dimg =
                                                            img.getScaledInstance(
                                                                    90,
                                                                    90,
                                                                    Image.SCALE_SMOOTH
                                                            );


                                                    btnItem.setIcon(
                                                            new ImageIcon(
                                                                    dimg
                                                            )
                                                    );
                                                }

                                            } catch (
                                                    Exception ex
                                            ) {

                                                // Continúa con texto
                                            }
                                        }


                                        btnItem.setText(
                                                "<html><center>"
                                                +
                                                cat.getNombre()
                                                +
                                                "</center></html>"
                                        );


                                        btnItem.setVerticalTextPosition(
                                                SwingConstants.BOTTOM
                                        );


                                        btnItem.setHorizontalTextPosition(
                                                SwingConstants.CENTER
                                        );


                                        // ====================================================
                                        // SELECCIONAR
                                        // ====================================================

                                        btnItem.addActionListener(
                                                evt -> {

                                                    elegido[0] =
                                                            cat;


                                                    if (
                                                            txtDesPro
                                                                    .getText()
                                                                    .trim()
                                                                    .isEmpty()
                                                    ) {

                                                        txtDesPro.setText(
                                                                cat.getNombre()
                                                        );
                                                    }


                                                    txtImagenPro.setText(
                                                            cat.getImagenUrl()
                                                                    == null
                                                                    ? ""
                                                                    : cat.getImagenUrl()
                                                                            .trim()
                                                    );


                                                    idCatalogoGlobalSeleccionado =
                                                            cat.getId();


                                                    dialog.dispose();
                                                }
                                        );


                                        panelContenedor.add(
                                                btnItem
                                        );
                                    }
                                }


                                panelContenedor.revalidate();

                                panelContenedor.repaint();
                            }
                    );

                }
        ).start();
    }
}