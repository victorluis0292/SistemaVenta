package Vista;

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
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class PanelProductos extends JPanel {

    public JTextField txtBuscar;
    public JTextField txtCodigoPro, txtDesPro, txtCantPro, txtPrecioPro, txtPreciocompraPro, txtIdproducto, txtImagenPro;
    public JComboBox<Combo> cbxProveedorPro;
    public JComboBox<String> cbxCategoria;
    public JButton btnGuardarpro, btnEditarpro, btnEliminarPro, btnNuevoPro, btnBuscarImagen;

    // "Se vende": Por Unidad/Pza, A Granel (usa decimales) o Como paquete (kit)
    public JRadioButton rbPorUnidad, rbAGranel, rbComoPaquete;
    private ButtonGroup grupoTipoVenta;

    public JTable TableProducto;
    public DefaultTableModel modelo;

    // Referencias de control locales
    private int idEmpresa;
    private ProductosDao proDao;
    private ProveedorDao proveedorDao;
    private final CatalogoGlobalDao catalogoDao = new CatalogoGlobalDao();

    // Guarda el registro de catalogo_global elegido en el selector visual,
    // para poder vincular el producto (id_catalogo_global) al guardarlo.
    private Integer idCatalogoGlobalSeleccionado = null;

    // Evita que el listener de cbxProveedorPro se dispare cuando nosotros
    // mismos cambiamos la selección por código (ej. tras refrescar la lista).
    private boolean ignorarEventoProveedor = false;

    // Filtra/ordena la tabla de productos sin tocar la base de datos cada vez
    // que el usuario escribe en el buscador.
    private TableRowSorter<DefaultTableModel> sorterProductos;

    // Callback opcional: se ejecuta cada vez que se guarda o elimina un producto,
    // para que quien construyó este panel pueda refrescar otras pantallas
    // (por ejemplo, el panel de Verdulería en Nueva Venta) que dependen de estos datos.
    private final Runnable onProductosActualizados;

    /** Constructor original, sin callback (mantiene compatibilidad con código existente). */
    public PanelProductos(int idEmpresa, ProductosDao proDao, ProveedorDao proveedorDao) {
        this(idEmpresa, proDao, proveedorDao, null);
    }

    /** Constructor con callback: úsalo para que otras pantallas se enteren de los cambios. */
    public PanelProductos(int idEmpresa, ProductosDao proDao, ProveedorDao proveedorDao, Runnable onProductosActualizados) {
        this.idEmpresa = idEmpresa;
        this.proDao = proDao;
        this.proveedorDao = proveedorDao;
        this.onProductosActualizados = onProductosActualizados;

        initComponents();
        llenarProveedor();
        ListarProductos();
        initEvents(); // 🚀 Inicializamos todos los botones de forma interna
    }

    /** Avisa a quien construyó este panel (si dio un callback) que los productos cambiaron. */
    private void notificarCambioDeProductos() {
        if (onProductosActualizados != null) {
            onProductosActualizados.run();
        }
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(Color.WHITE);

        // 1. ZONA SUPERIOR: Buscador
        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBuscador.setBackground(Color.WHITE);
        JLabel lblBuscar = new JLabel("Buscar ");
        lblBuscar.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtBuscar = new JTextField(30);
        panelBuscador.add(lblBuscar);
        panelBuscador.add(txtBuscar);
        this.add(panelBuscador, BorderLayout.NORTH);

        // 2. ZONA IZQUIERDA: Formulario Rosa
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(new Color(255, 204, 255));
        panelFormulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Nuevo Producto",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 12), Color.BLUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigoPro = new JTextField(15);
        txtDesPro = new JTextField(15);

        // --- "Se vende": Por Unidad/Pza | A Granel (usa decimales) | Como paquete (kit) ---
        rbPorUnidad = new JRadioButton("Por Unidad/Pza", true); // seleccionado por defecto
        rbAGranel = new JRadioButton("A Granel (Usa Decimales)");
        rbComoPaquete = new JRadioButton("Como paquete (kit)");

        Font fontRadio = new Font("Tahoma", Font.PLAIN, 11);
        rbPorUnidad.setFont(fontRadio);
        rbAGranel.setFont(fontRadio);
        rbComoPaquete.setFont(fontRadio);

        rbPorUnidad.setOpaque(false);
        rbAGranel.setOpaque(false);
        rbComoPaquete.setOpaque(false);

        grupoTipoVenta = new ButtonGroup();
        grupoTipoVenta.add(rbPorUnidad);
        grupoTipoVenta.add(rbAGranel);
        grupoTipoVenta.add(rbComoPaquete);

        JPanel panelTipoVenta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelTipoVenta.setOpaque(false);
        panelTipoVenta.add(rbPorUnidad);
        panelTipoVenta.add(rbAGranel);
        panelTipoVenta.add(rbComoPaquete);

        cbxCategoria = new JComboBox<>(new String[]{"Verduleria", "General", "Abarrotes", "Bebidas"});
        txtCantPro = new JTextField(15);
        txtPrecioPro = new JTextField(15);
        txtPreciocompraPro = new JTextField(15);

        cbxProveedorPro = new JComboBox<>();
        cbxProveedorPro.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Combo) {
                    setText(((Combo) value).getNombre());
                }
                return this;
            }
        });

        txtImagenPro = new JTextField(10);
        txtImagenPro.setEditable(false);
        btnBuscarImagen = new JButton("📁");

        JPanel panelImagen = new JPanel(new BorderLayout(5, 0));
        panelImagen.setOpaque(false);
        panelImagen.add(txtImagenPro, BorderLayout.CENTER);
        panelImagen.add(btnBuscarImagen, BorderLayout.EAST);

        txtIdproducto = new JTextField();

        int fila = 0;
        agregarCampo(panelFormulario, "Código:", txtCodigoPro, gbc, fila++);
        agregarCampo(panelFormulario, "Descripción:", txtDesPro, gbc, fila++);
        agregarCampo(panelFormulario, "Se vende:", panelTipoVenta, gbc, fila++);
        agregarCampo(panelFormulario, "Categoría:", cbxCategoria, gbc, fila++);
        agregarCampo(panelFormulario, "Cantidad:", txtCantPro, gbc, fila++);
        agregarCampo(panelFormulario, "Precio Venta:", txtPrecioPro, gbc, fila++);
        agregarCampo(panelFormulario, "Precio Compra:", txtPreciocompraPro, gbc, fila++);
        agregarCampo(panelFormulario, "Proveedor:", cbxProveedorPro, gbc, fila++);
        agregarCampo(panelFormulario, "Imagen / Cat:", panelImagen, gbc, fila++);

        // 3. ZONA INFERIOR: Botones
        JPanel panelBotones = new JPanel(new GridLayout(1, 4, 5, 0));
        panelBotones.setOpaque(false);

        btnGuardarpro = new JButton("💾");
        btnEditarpro = new JButton("📝");
        btnEliminarPro = new JButton("❌");
        btnNuevoPro = new JButton("➕");

        panelBotones.add(btnGuardarpro);
        panelBotones.add(btnEditarpro);
        panelBotones.add(btnEliminarPro);
        panelBotones.add(btnNuevoPro);

        gbc.gridx = 0;
        gbc.gridy = fila++;
        gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);

        this.add(panelFormulario, BorderLayout.WEST);

        // 4. ZONA CENTRAL: Tabla
        String[] nombresColumnas = {"ID", "CODIGO", "DESCRIPCIÓN", "CATEGORÍA", "PROVEEDOR", "STOCK", "PRECIO", "Precio Compra", "ID_Prov"};
        modelo = new DefaultTableModel(null, nombresColumnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        TableProducto = new JTable(modelo);
        TableProducto.setRowHeight(25);
        TableProducto.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));

        // Ordenador/filtro de filas: permite implementar el buscador en vivo
        // sin volver a consultar la base de datos en cada tecla.
        sorterProductos = new TableRowSorter<>(modelo);
        TableProducto.setRowSorter(sorterProductos);

        TableProducto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                TableProductoMouseClicked(evt);
            }
        });

        JScrollPane scrollTabla = new JScrollPane(TableProducto);
        this.add(scrollTabla, BorderLayout.CENTER);
    }

    private void agregarCampo(JPanel panel, String textoLabel, JComponent componente, GridBagConstraints gbc, int fila) {
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = fila;
        JLabel label = new JLabel(textoLabel);
        label.setFont(new Font("Tahoma", Font.BOLD, 12));
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(componente, gbc);
    }

    // ==========================================
    // LÓGICA INTERNA Y EVENTOS AUTÓNOMOS
    // ==========================================

    /**
     * Devuelve el tipo de venta seleccionado en el radio group:
     * "UNIDAD", "GRANEL" o "KIT".
     * Útil para cuando se conecte esta selección con el guardado del producto
     * (por ejemplo, para decidir si Cantidad admite decimales o no).
     */
    public String obtenerTipoVenta() {
        if (rbAGranel.isSelected()) return "GRANEL";
        if (rbComoPaquete.isSelected()) return "KIT";
        return "UNIDAD";
    }

    /** Selecciona el radio button correspondiente al tipo de venta dado. */
    public void setTipoVenta(String tipoVenta) {
        if (tipoVenta == null) {
            rbPorUnidad.setSelected(true);
            return;
        }
        switch (tipoVenta.toUpperCase()) {
            case "GRANEL":
                rbAGranel.setSelected(true);
                break;
            case "KIT":
                rbComoPaquete.setSelected(true);
                break;
            default:
                rbPorUnidad.setSelected(true);
        }
    }

    public void llenarProveedor() {
        cbxProveedorPro.removeAllItems();
        List<Proveedor> lista = proveedorDao.ListarProveedorPorEmpresa(idEmpresa);
        for (int i = 0; i < lista.size(); i++) {
            int id = lista.get(i).getId();
            String nombre = lista.get(i).getNombre();
            cbxProveedorPro.addItem(new Combo(id, nombre));
        }
        // Opción especial al final para agregar un proveedor nuevo sin salir del formulario
        cbxProveedorPro.addItem(new Combo(-1, "+ Agregar proveedor"));
    }

    public void ListarProductos() {
        List<Productos> ListaPro = proDao.ListarProductos(idEmpresa);
        modelo = (DefaultTableModel) TableProducto.getModel();
        modelo.setRowCount(0);

        Object[] ob = new Object[9];
        for (int i = 0; i < ListaPro.size(); i++) {
            ob[0] = ListaPro.get(i).getId();
            ob[1] = ListaPro.get(i).getCodigo();
            ob[2] = ListaPro.get(i).getNombre();
            ob[3] = ListaPro.get(i).getCategoria();
            ob[4] = ListaPro.get(i).getProveedor();
            ob[5] = ListaPro.get(i).getStock();
            ob[6] = ListaPro.get(i).getPrecio();
            ob[7] = ListaPro.get(i).getPreciocompra();
            ob[8] = ListaPro.get(i).getId_empresa();
            modelo.addRow(ob);
        }
        TableProducto.setModel(modelo);

        // Al reasignar el modelo, el sorter/filtro anterior se pierde; lo reconectamos
        // y si había texto en el buscador, lo volvemos a aplicar.
        sorterProductos = new TableRowSorter<>(modelo);
        TableProducto.setRowSorter(sorterProductos);
        aplicarFiltroBusqueda();
    }

    private void initEvents() {
        // 1. Botón Buscar Imagen: abre directo la galería del catálogo global
        //    (con lo que ya se escribió en Descripción como filtro inicial;
        //    si está vacío, muestra TODO el catálogo). Ya no pide texto en un
        //    popup aparte: la búsqueda vive dentro de la propia galería.
        btnBuscarImagen.addActionListener(e -> {
            String sugerido = txtDesPro.getText() == null ? "" : txtDesPro.getText().trim();

            CatalogoGlobal seleccionado = mostrarSelectorCatalogoGlobal(sugerido);
            if (seleccionado != null) {
                txtImagenPro.setText(seleccionado.getImagenUrl() == null ? "" : seleccionado.getImagenUrl().trim());
                idCatalogoGlobalSeleccionado = seleccionado.getId();
            }
        });

        // 2. Botón Guardar
        btnGuardarpro.addActionListener(e -> {
            if (!txtCodigoPro.getText().isEmpty() && !txtDesPro.getText().isEmpty() && !txtPrecioPro.getText().isEmpty()) {
                Productos pro = new Productos();
                pro.setCodigo(txtCodigoPro.getText());
                pro.setNombre(txtDesPro.getText());
                pro.setCategoria(cbxCategoria.getSelectedItem().toString());
                pro.setStock(Integer.parseInt(txtCantPro.getText()));
                pro.setPrecio(Double.parseDouble(txtPrecioPro.getText()));
                pro.setPreciocompra(Double.parseDouble(txtPreciocompraPro.getText()));

                Combo itemProveedor = (Combo) cbxProveedorPro.getSelectedItem();
                if (itemProveedor != null) {
                    pro.setProveedor(itemProveedor.getId());
                    pro.setProveedorPro(itemProveedor.getNombre());
                }

                pro.setImagenUrl(txtImagenPro.getText());
                pro.setId_empresa(idEmpresa);

                // Vínculo real con catalogo_global (null si no se seleccionó nada del catálogo).
                // Requiere que Modelo.Productos tenga: setIdCatalogoGlobal(Integer)
                pro.setIdCatalogoGlobal(idCatalogoGlobalSeleccionado);

                // NOTA: "obtenerTipoVenta()" ya está listo (UNIDAD / GRANEL / KIT).
                // Falta conectarlo con Modelo.Productos y la tabla `productos` en BD
                // (ej. pro.setTipoVenta(obtenerTipoVenta())) cuando agreguemos esa columna.

                if (proDao.RegistrarProductos(pro)) {
                    JOptionPane.showMessageDialog(this, "¡Producto registrado con éxito!");
                    ListarProductos();
                    limpiarFormulario();
                    notificarCambioDeProductos(); // 🔔 avisa (ej. refresca Verdulería en Nueva Venta)
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Hay campos obligatorios vacíos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 3. Botón Nuevo / Limpiar
        btnNuevoPro.addActionListener(e -> limpiarFormulario());

        // 4. Botón Eliminar
        btnEliminarPro.addActionListener(e -> {
            if (!txtIdproducto.getText().isEmpty()) {
                int pregunta = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este producto?", "Advertencia", JOptionPane.YES_NO_OPTION);
                if (pregunta == JOptionPane.YES_OPTION) {
                    int id = Integer.parseInt(txtIdproducto.getText());
                    proDao.EliminarProductos(id);
                    ListarProductos();
                    limpiarFormulario();
                    notificarCambioDeProductos(); // 🔔 avisa (ej. refresca Verdulería en Nueva Venta)
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para eliminar.");
            }
        });

        // 5. ComboBox Proveedor: detecta la opción "+ Agregar proveedor"
        cbxProveedorPro.addActionListener(e -> {
            if (ignorarEventoProveedor) return;

            Combo seleccionado = (Combo) cbxProveedorPro.getSelectedItem();
            if (seleccionado != null && seleccionado.getId() == -1) {
                abrirDialogoAgregarProveedor();
            }
        });

        // 6. Buscador: filtra la tabla en tiempo real mientras escribes,
        //    sin volver a consultar la base de datos.
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { aplicarFiltroBusqueda(); }
            @Override public void removeUpdate(DocumentEvent e) { aplicarFiltroBusqueda(); }
            @Override public void changedUpdate(DocumentEvent e) { aplicarFiltroBusqueda(); }
        });
    }

    // Aplica el texto actual de txtBuscar como filtro sobre la tabla de productos.
    // Busca coincidencias en cualquier columna (código, descripción, categoría, etc.),
    // sin distinguir mayúsculas/minúsculas.
    private void aplicarFiltroBusqueda() {
        if (sorterProductos == null) return;

        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            sorterProductos.setRowFilter(null);
            return;
        }

        try {
            RowFilter<DefaultTableModel, Integer> filtro =
                    RowFilter.regexFilter("(?i)" + Pattern.quote(texto));
            sorterProductos.setRowFilter(filtro);
        } catch (java.util.regex.PatternSyntaxException ex) {
            // Si el texto produce un patrón inválido (raro con quote()), no filtramos.
            sorterProductos.setRowFilter(null);
        }
    }

    // 🧾 Diálogo para agregar un proveedor nuevo sin salir del formulario de productos.
    private void abrirDialogoAgregarProveedor() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Agregar Proveedor", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtRuc = new JTextField(15);
        JTextField txtNombre = new JTextField(15);
        JTextField txtTelefono = new JTextField(15);
        JTextField txtDireccion = new JTextField(15);

        int fila = 0;
        gbc.gridx = 0; gbc.gridy = fila; dialog.add(new JLabel("RFC:"), gbc);
        gbc.gridx = 1; dialog.add(txtRuc, gbc);
        fila++;

        gbc.gridx = 0; gbc.gridy = fila; dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; dialog.add(txtNombre, gbc);
        fila++;

        gbc.gridx = 0; gbc.gridy = fila; dialog.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1; dialog.add(txtTelefono, gbc);
        fila++;

        gbc.gridx = 0; gbc.gridy = fila; dialog.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; dialog.add(txtDireccion, gbc);
        fila++;

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(76, 175, 80));
        btnGuardar.setForeground(Color.WHITE);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(244, 67, 54));
        btnCancelar.setForeground(Color.WHITE);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 2;
        dialog.add(panelBotones, gbc);

        // Si cancela o cierra sin guardar, regresamos el combo a la primera opción real
        // para no dejar visualmente atorada la opción "+ Agregar proveedor".
        Runnable restaurarSeleccion = () -> {
            ignorarEventoProveedor = true;
            if (cbxProveedorPro.getItemCount() > 0) {
                cbxProveedorPro.setSelectedIndex(0);
            }
            ignorarEventoProveedor = false;
        };

        btnCancelar.addActionListener(e -> {
            dialog.dispose();
            restaurarSeleccion.run();
        });

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                restaurarSeleccion.run();
            }
        });

        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre es obligatorio.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Proveedor nuevo = new Proveedor();
            nuevo.setRuc(txtRuc.getText().trim());
            nuevo.setNombre(txtNombre.getText().trim());
            nuevo.setTelefono(txtTelefono.getText().trim());
            nuevo.setDireccion(txtDireccion.getText().trim());
            nuevo.setIdEmpresa(idEmpresa);

            if (proveedorDao.RegistrarProveedor(nuevo)) {
                JOptionPane.showMessageDialog(dialog, "Proveedor agregado con éxito.");
                dialog.dispose();

                // Refrescamos la lista y dejamos seleccionado el proveedor recién creado
                llenarProveedor();
                seleccionarProveedorPorNombre(nuevo.getNombre());
            } else {
                JOptionPane.showMessageDialog(dialog, "Error al guardar el proveedor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    // Busca en el combo (ya refrescado) el proveedor por nombre y lo deja seleccionado.
    // Nota: RegistrarProveedor no devuelve el id generado, por eso se ubica por nombre.
    private void seleccionarProveedorPorNombre(String nombre) {
        ignorarEventoProveedor = true;
        for (int i = 0; i < cbxProveedorPro.getItemCount(); i++) {
            Combo item = cbxProveedorPro.getItemAt(i);
            if (item.getNombre() != null && item.getNombre().equals(nombre)) {
                cbxProveedorPro.setSelectedIndex(i);
                break;
            }
        }
        ignorarEventoProveedor = false;
    }

    // 🖼️ Selector visual del catálogo global: se abre mostrando directamente
    //    todas las imágenes disponibles (o filtradas por "filtroInicial" si
    //    no viene vacío), con una barra de búsqueda propia por si el usuario
    //    quiere acotar más sin salir del diálogo. Devuelve el CatalogoGlobal
    //    elegido, o null si se cancela.
    private CatalogoGlobal mostrarSelectorCatalogoGlobal(String filtroInicial) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Catálogo Global", true);
        dialog.setSize(600, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // --- Barra de búsqueda superior ---
        JPanel panelBusqueda = new JPanel(new BorderLayout(6, 0));
        panelBusqueda.setBorder(new EmptyBorder(10, 10, 0, 10));
        JLabel lblBuscarCat = new JLabel("Buscar: ");
        lblBuscarCat.setFont(new Font("Tahoma", Font.BOLD, 12));
        JTextField txtBuscarCat = new JTextField(filtroInicial == null ? "" : filtroInicial);
        JButton btnBuscarCat = new JButton("🔎 Buscar");
        JButton btnVerTodo = new JButton("Ver todo");
        JPanel panelBotonesBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        panelBotonesBusqueda.add(btnBuscarCat);
        panelBotonesBusqueda.add(btnVerTodo);

        panelBusqueda.add(lblBuscarCat, BorderLayout.WEST);
        panelBusqueda.add(txtBuscarCat, BorderLayout.CENTER);
        panelBusqueda.add(panelBotonesBusqueda, BorderLayout.EAST);

        // --- Contenedor de resultados (galería) ---
        JPanel panelContenedor = new JPanel(new GridLayout(0, 3, 10, 10));
        panelContenedor.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(panelContenedor);

        final CatalogoGlobal[] elegido = {null};

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.add(panelBusqueda, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        dialog.add(panelPrincipal, BorderLayout.CENTER);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Tahoma", Font.BOLD, 12));
        btnCancelar.addActionListener(e -> dialog.dispose());

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSur.add(btnCancelar);
        dialog.add(panelSur, BorderLayout.SOUTH);

        // --- Acción de búsqueda (reutilizable) ---
        Runnable[] cargarRef = new Runnable[1];
        cargarRef[0] = () -> {
            String filtro = txtBuscarCat.getText() == null ? "" : txtBuscarCat.getText().trim();
            cargarResultadosCatalogo(filtro, panelContenedor, dialog, elegido);
        };

        btnBuscarCat.addActionListener(e -> cargarRef[0].run());
        btnVerTodo.addActionListener(e -> {
            txtBuscarCat.setText("");
            cargarRef[0].run();
        });
        txtBuscarCat.addActionListener(e -> cargarRef[0].run()); // Enter

        // Carga inicial: SIEMPRE se muestra el catálogo completo al abrir.
        // El texto sugerido (Descripción) solo queda precargado en la caja de
        // búsqueda por comodidad, pero no filtra automáticamente la primera vez.
        cargarResultadosCatalogo("", panelContenedor, dialog, elegido);

        dialog.setVisible(true);
        return elegido[0];
    }

    // Consulta catalogo_global con el filtro dado y repinta la galería del diálogo.
    // Filtro vacío = trae todo el catálogo (buscarPorNombre usa LIKE '%filtro%').
    private void cargarResultadosCatalogo(String filtro, JPanel panelContenedor, JDialog dialog, CatalogoGlobal[] elegido) {
        panelContenedor.removeAll();
        panelContenedor.setLayout(new BorderLayout());
        panelContenedor.add(new JLabel("Cargando catálogo...", JLabel.CENTER), BorderLayout.CENTER);
        panelContenedor.revalidate();
        panelContenedor.repaint();

        new Thread(() -> {
            List<CatalogoGlobal> resultados = catalogoDao.buscarPorNombre(filtro == null ? "" : filtro);

            SwingUtilities.invokeLater(() -> {
                panelContenedor.removeAll();

                if (resultados == null || resultados.isEmpty()) {
                    panelContenedor.setLayout(new BorderLayout());
                    panelContenedor.add(new JLabel("No hay imágenes en el catálogo global para este filtro.", JLabel.CENTER), BorderLayout.CENTER);
                } else {
                    panelContenedor.setLayout(new GridLayout(0, 3, 10, 10));
                    for (CatalogoGlobal item : resultados) {
                        panelContenedor.add(crearItemCatalogo(item, dialog, elegido));
                    }
                }

                panelContenedor.revalidate();
                panelContenedor.repaint();
            });
        }).start();
    }

    // Construye la tarjeta (miniatura + nombre) de un registro de catalogo_global
    // para la galería del selector, con carga asíncrona de la imagen.
    private JPanel crearItemCatalogo(CatalogoGlobal item, JDialog dialog, CatalogoGlobal[] elegido) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        itemPanel.setBackground(Color.WHITE);

        JButton btnImgItem = new JButton("Cargando...");
        btnImgItem.setEnabled(false);
        itemPanel.add(btnImgItem, BorderLayout.CENTER);

        JLabel lblTexto = new JLabel(item.getNombre(), JLabel.CENTER);
        lblTexto.setFont(new Font("Tahoma", Font.PLAIN, 11));
        itemPanel.add(lblTexto, BorderLayout.SOUTH);

        String urlImg = item.getImagenUrl();

        // Hilo independiente con HttpURLConnection y cabecera User-Agent para evitar bloqueos
        new Thread(() -> {
            System.out.println("[LOG-CATALOGO] Descargando URL: " + urlImg);
            Image imagenEscalada = null;

            if (urlImg != null && !urlImg.trim().isEmpty()) {
                try {
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new URL(urlImg).openConnection();
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
                    connection.setConnectTimeout(6000);
                    connection.setReadTimeout(6000);
                    connection.connect();

                    try (java.io.InputStream inputStream = connection.getInputStream()) {
                        BufferedImage bufferedImage = ImageIO.read(inputStream);
                        if (bufferedImage != null) {
                            imagenEscalada = bufferedImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                            System.out.println("[LOG-CATALOGO] ✅ ÉXITO: (" + bufferedImage.getWidth() + "x" + bufferedImage.getHeight() + ")");
                        } else {
                            System.out.println("[LOG-CATALOGO] ⚠️ No se pudo decodificar la imagen.");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[LOG-CATALOGO] ❌ Error de conexión: " + e.getMessage());
                }
            } else {
                System.out.println("[LOG-CATALOGO] El registro '" + item.getNombre() + "' no tiene imagen_url.");
            }

            final Image imgFinal = imagenEscalada;
            SwingUtilities.invokeLater(() -> {
                if (imgFinal != null) {
                    btnImgItem.setIcon(new ImageIcon(imgFinal));
                    btnImgItem.setText("");
                } else {
                    btnImgItem.setText("Sin vista previa");
                }
                btnImgItem.setEnabled(true);
                btnImgItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
            });
        }).start();

        btnImgItem.addActionListener(e -> {
            elegido[0] = item;
            dialog.dispose();
        });

        return itemPanel;
    }

    private void limpiarFormulario() {
        txtIdproducto.setText("");
        txtCodigoPro.setText("");
        txtDesPro.setText("");
        txtCantPro.setText("");
        txtPrecioPro.setText("");
        txtPreciocompraPro.setText("");
        txtImagenPro.setText("");
        idCatalogoGlobalSeleccionado = null;
        rbPorUnidad.setSelected(true); // valor por defecto al limpiar
        btnGuardarpro.setEnabled(true);
        btnEditarpro.setEnabled(false);
        btnEliminarPro.setEnabled(false);
    }

    private void TableProductoMouseClicked(MouseEvent evt) {
        int filaVista = TableProducto.rowAtPoint(evt.getPoint());
        if (filaVista < 0) return;

        // 👇 Clave: con RowSorter/filtro activo, el índice visible no siempre
        // coincide con el índice real del modelo. Hay que convertirlo.
        int fila = TableProducto.convertRowIndexToModel(filaVista);

        btnEditarpro.setEnabled(true);
        btnEliminarPro.setEnabled(true);
        btnGuardarpro.setEnabled(false);

        // Al seleccionar un producto existente de la tabla, se limpia la selección
        // de catálogo pendiente (no se conoce cuál usó ese producto sin una consulta extra).
        idCatalogoGlobalSeleccionado = null;

        txtIdproducto.setText(TableProducto.getValueAt(fila, 0).toString());
        txtCodigoPro.setText(TableProducto.getValueAt(fila, 1).toString());
        txtDesPro.setText(TableProducto.getValueAt(fila, 2).toString());
        cbxCategoria.setSelectedItem(TableProducto.getValueAt(fila, 3).toString());
        txtCantPro.setText(TableProducto.getValueAt(fila, 5).toString());
        txtPrecioPro.setText(TableProducto.getValueAt(fila, 6).toString());
        txtPreciocompraPro.setText(TableProducto.getValueAt(fila, 7).toString());

        int idProveedor = -1;
        try {
            idProveedor = Integer.parseInt(TableProducto.getValueAt(fila, 8).toString());
        } catch (Exception e) {}

        ignorarEventoProveedor = true;
        for (int i = 0; i < cbxProveedorPro.getItemCount(); i++) {
            Combo item = cbxProveedorPro.getItemAt(i);
            if (item.getId() == idProveedor) {
                cbxProveedorPro.setSelectedIndex(i);
                break;
            }
        }
        ignorarEventoProveedor = false;
    }
}