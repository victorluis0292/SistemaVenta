package Vista;

import Controlador.TurnoController;
import Modelo.LoaderDialog;
import Servicios.CobroService;
import Modelo.ImprimirTicket;
import Estilos.Estilos;
import Modelo.AbrirCajaEfectivo;
import Modelo.Detalle;
import Modelo.Eventos;
import Modelo.TurnoModel;
import Modelo.VentaDao;
import static Vista.Sistema.txtCodigoVenta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import Modelo.AbonoDao;

public final class ventanaCobrar extends JDialog {
    private int idEmpresaActiva;

    private boolean cobroRealizado = false;
    private JTable TableConsultaCreditCliente;

    private boolean esPrimeraVez = true;
    private double totalPagar = 0.00;
    private double totalPagado = 0.00;
    private double totalComision = 0.00;
    private double ultimoPagoEfectivo = 0.00;

    private JLabel lblTotal = new JLabel("0.00");
    private JTextField txtPaga = new JTextField();
    private JLabel lblCambio = new JLabel("0.00");
    private DefaultTableModel modeloPagos = new DefaultTableModel(new String[]{"Método", "Monto (Comisión)"}, 0);
    private JTable tablaPagos = new JTable(modeloPagos);

    // Flag para indicar si esta venta es a crédito
    private boolean esVentaCredito = false;

    // Constructor para ventas normales (efectivo/tarjeta/mixto)
    public ventanaCobrar(JFrame parent) {
        super(parent, "Pago", true); // modal
        initComponentes();
    }

    public void setIdEmpresaActiva(int idEmpresa) {
        this.idEmpresaActiva = idEmpresa;
    }

    // Constructor para indicar si es venta crédito
    public ventanaCobrar(JFrame parent, boolean esCredito, JTable TableConsultaCreditCliente) {
        super(parent, "Pago", true);
        this.esVentaCredito = esCredito;
        this.TableConsultaCreditCliente = TableConsultaCreditCliente;
        initComponentes();
    }

    private void initComponentes() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setTitle("Pago");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        Estilos.PanelConEstilo panel = new Estilos.PanelConEstilo();
        panel.setLayout(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Total a Pagar:"));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(lblTotal);

        panel.add(new JLabel("Paga Con (Efectivo):"));
        txtPaga.setFont(new Font("Arial", Font.PLAIN, 24));
        panel.add(txtPaga);
        new Eventos().aplicarSoloDecimal(txtPaga);

        panel.add(new JLabel("Su cambio:"));
        lblCambio.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(lblCambio);

        JButton btnCobrar = new JButton("Cobrar Efectivo");
        btnCobrar.addActionListener(e -> cobrarEfectivo());
        panel.add(btnCobrar);

        JButton btnTarjeta = new JButton("Cobro con Tarjeta");
        btnTarjeta.addActionListener(e -> {
            ventanaCobroConTarjeta tarjeta = new ventanaCobroConTarjeta(this);
            tarjeta.setVisible(true);
        });
        panel.add(btnTarjeta);

        add(panel, BorderLayout.CENTER);
        tablaPagos.setFillsViewportHeight(true);
        add(new JScrollPane(tablaPagos), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(getParent());

        txtPaga.addActionListener(e -> cobrarEfectivo());
        txtPaga.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                actualizarCambioTemporal();
            }
        });

        // -------------------- LOG al abrir la ventana --------------------
        System.out.println("========================================");
        System.out.println("🟦 [LOG] Se ha abierto ventanaCobrar.");
        System.out.println("🟦 [LOG] Flag esVentaCredito = " + esVentaCredito);
        try {
            String txtId = Sistema.txtIdCV.getText();
            int idTmp = (txtId == null || txtId.trim().isEmpty()) ? -1 : Integer.parseInt(txtId.trim());
            String nombreTmp = (idTmp > 0) ? new VentaDao().obtenerNombreClientePorId(idTmp) : "N/A";
            System.out.println("🟦 [LOG] Campo Sistema.txtIdCV = '" + txtId + "' -> interpreto id = " + idTmp + " - nombre actual en campo = " + nombreTmp);
        } catch (Exception ex) {
            System.out.println("⚠️ [LOG] No se pudo leer Sistema.txtIdCV: " + ex.getMessage());
        }
        System.out.println("========================================");
    }

    public void setTotal(double total) {
        this.totalPagar = total;
        this.totalPagado = 0.0;
        this.totalComision = 0.0;
        lblTotal.setText(String.format("%.2f", totalPagar));
        lblCambio.setText("0.00");
        modeloPagos.setRowCount(0);
        txtPaga.setText("");
        txtPaga.setEditable(true);

        // Log cuando se establece total
        System.out.println("🔢 [LOG] setTotal() -> totalPagar establecido: " + totalPagar);
    }

    public double getSaldoPendiente() {
        return totalPagar - totalPagado;
    }

    public double getTotalPagado() {
        return totalPagado;
    }

    private void cobrarEfectivo() {
        try {
            String pagaTexto = txtPaga.getText().replace(",", "");

            if (pagaTexto == null || pagaTexto.isEmpty()) {
                lblCambio.setText("0.00");
                JOptionPane.showMessageDialog(this, "Ingresa un monto");
                txtPaga.requestFocus();
                return;
            }

            BigDecimal pagoOriginal = new BigDecimal(pagaTexto); // lo que realmente paga el cliente

            if (pagoOriginal.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
                return;
            }

            ultimoPagoEfectivo = pagoOriginal.doubleValue(); // ✅ AQUÍ ya se guarda correctamente

            BigDecimal saldoPendiente = BigDecimal.valueOf(getSaldoPendiente());

            BigDecimal pagoAplicado = pagoOriginal;

            // Si el pago es mayor que el saldo pendiente, se ajusta solo para sumar el saldo restante
            if (pagoAplicado.compareTo(saldoPendiente) > 0) {
                pagoAplicado = saldoPendiente;
            }

            BigDecimal cambio = pagoOriginal.subtract(saldoPendiente);
            if (cambio.compareTo(BigDecimal.ZERO) < 0) {
                cambio = BigDecimal.ZERO;
            }

            lblCambio.setText(cambio.setScale(2, RoundingMode.HALF_UP).toString());

            // Aquí agregamos solo el pago aplicado al total pagado
            agregarPago("Efectivo", pagoAplicado.doubleValue(), 0.00);

            // Log del pago efectuado
            System.out.println("💵 [LOG] cobrarEfectivo -> pagoOriginal: " + pagoOriginal + " | pagoAplicado: " + pagoAplicado + " | cambio calculado: " + cambio);

            if (getSaldoPendiente() <= 0) {
                txtPaga.setEditable(false);
                procesarVentaFinal();
            }

            txtPaga.setText("");
        } catch (NumberFormatException | ArithmeticException e) {
            lblCambio.setText("Error");
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
        }
    }

    public void agregarPago(String metodo, double monto, double comision) {
        if (comision > 0) {
            totalComision += comision;
            totalPagar += comision;  // sumar comisión si se desea
        }

        double saldoPendiente = getSaldoPendiente();
        if (monto > saldoPendiente) {
            monto = saldoPendiente;
        }

        totalPagado += monto;

        modeloPagos.addRow(new Object[]{
                metodo,
                String.format("$%.2f (Comisión $%.2f)", monto, comision)
        });

        lblTotal.setText(String.format("%.2f", getSaldoPendiente()));

        // Log al agregar pago
        System.out.println("➕ [LOG] agregarPago -> metodo: " + metodo + ", monto aplicado: " + monto + ", totalPagado now: " + totalPagado + ", saldo pendiente now: " + getSaldoPendiente());
    }

    public void finalizarPagoTarjeta() {
        procesarVentaFinal();
        txtCodigoVenta.requestFocus();
    }

    private void procesarVentaFinal() {
        System.out.println("👉 procesarVentaFinal llamado");

        JDialog loader = new LoaderDialog().mostrarLoader(this);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            int idVenta;
            boolean esCredito = false;
            String tipoPagoFinal = "";
            String ticket = "";
            int idCliente;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    System.out.println("🚀 doInBackground iniciado");

                    // Leemos el id que esté puesto en la UI, pero luego aplicaremos la regla:
                    // si es VENTA NORMAL forzamos idCliente = 1 (Mostrador)
                    try {
                        String idCampo = Sistema.txtIdCV.getText();
                        idCliente = (idCampo == null || idCampo.trim().isEmpty()) ? -1 : Integer.parseInt(idCampo.trim());
                    } catch (Exception ex) {
                        idCliente = -1;
                    }

                    // ------ REGLA SOLICITADA: EN VENTA NORMAL, USAR SIEMPRE CLIENTE MOSTRADOR ID=1 ------
                    if (!esVentaCredito) {
                        System.out.println("🟢 [RULE] Venta NORMAL detectada -> Forzando idCliente = 1 (Cliente Mostrador). (Se ignora campo previo Sistema.txtIdCV)");
                        idCliente = 1;
                    } else {
                        System.out.println("🟣 [RULE] Venta CRÉDITO detectada -> Se usará idCliente desde campo/System (o seleccionado). idCliente=" + idCliente);
                    }
                    // -------------------------------------------------------------------------

                    String vendedor = Sistema.LabelVendedor.getText();
                    CobroService servicio = new CobroService();
                    // 🔹 Establecer la empresa activa ANTES de procesar la venta
                    servicio.setIdEmpresaActiva(idEmpresaActiva); // el valor que cargas al inicio de sesión
                    System.out.println("DEBUG ventanaCobrar: idEmpresaActiva seteada a " + idEmpresaActiva + " antes de procesVenta");

                    TurnoModel turno = TurnoController.getTurnoGlobal();
                    if (turno == null) {
                        SwingUtilities.invokeLater(() -> {
                            loader.dispose();
                            JOptionPane.showMessageDialog(ventanaCobrar.this,
                                    "No hay un turno abierto. No se puede procesar la venta.",
                                    "Turno no encontrado",
                                    JOptionPane.WARNING_MESSAGE);
                        });
                        return null;
                    }

                    int idTurno = turno.getId();
                    System.out.println("✅ ID del turno recibido: " + idTurno);

                    tipoPagoFinal = determinarTipoPagoFinal();
                    System.out.println("Tipo de pago final: '" + tipoPagoFinal + "'");

                    // --- CAMBIO: obtengo pagaCon y cambio para pasar a procesarVenta ---
                    double pagaCon = ultimoPagoEfectivo; // monto pagado en efectivo
                    double cambio = 0.0;
                    try {
                        cambio = Double.parseDouble(lblCambio.getText());
                    } catch (NumberFormatException ex) {
                        cambio = 0.0;
                    }
                    // --- FIN DEL CAMBIO ---
                    // Calcula subtotal restando la comisión del total
                    double subtotal = totalPagar - totalComision;

                    System.out.println("========================================");
                    System.out.println("🟢 INICIO PROCESAR VENTA");
                    System.out.println("ID Empresa Activa: " + idEmpresaActiva);
                    System.out.println("ID Turno: " + idTurno);
                    System.out.println("ID Cliente usado: " + idCliente + (esVentaCredito ? " (CRÉDITO)" : " (NORMAL - FORZADO MOSTRADOR)"));
                    System.out.println("Vendedor: " + vendedor);
                    System.out.println("Total a pagar (con comisiones si aplica): " + totalPagar);
                    System.out.println("Total comision: " + totalComision);
                    System.out.println("Subtotal (sin comision): " + subtotal);
                    System.out.println("Tipo pago final estimado: " + tipoPagoFinal);
                    System.out.println("========================================");

                    // Llamada al servicio que registra la venta (mantengo tu firma ampliada)
                    idVenta = servicio.procesarVenta(idTurno, idCliente, vendedor, Sistema.TableVenta, totalPagar, tipoPagoFinal, pagaCon, cambio, totalComision, subtotal);

                    System.out.println("✅ Venta creada con ID: " + idVenta);

                    if (idVenta == 0) {
                        throw new Exception("No se pudo registrar la venta. ID generado es 0.");
                    }

                    esCredito = esVentaCredito || tipoPagoFinal.equalsIgnoreCase("credito");
                    System.out.println("Flag esCredito (post-registro): " + esCredito);

                    if (esCredito) {
                        VentaDao ventaDao = new VentaDao();
                        DefaultTableModel modeloCredito = (DefaultTableModel) TableConsultaCreditCliente.getModel();

                        for (int i = 0; i < modeloCredito.getRowCount(); i++) {
                            String concepto = modeloCredito.getValueAt(i, 2).toString().toLowerCase();

                            if (concepto.contains("abono")) {
                                System.out.println("⛔ Fila " + i + " ignorada por ser abono: " + concepto);
                                continue;
                            }

                            Detalle detalle = new Detalle();
                            detalle.setId_pro(Integer.parseInt(modeloCredito.getValueAt(i, 1).toString()));
                            detalle.setCantidad(Integer.parseInt(modeloCredito.getValueAt(i, 3).toString()));
                            detalle.setPrecio(Double.parseDouble(modeloCredito.getValueAt(i, 4).toString()));
                            detalle.setId(idVenta);

                            int filas = ventaDao.RegistrarDetalle(detalle);
                            System.out.println("✅ Producto registrado (fila " + i + "): " + concepto + ", filas afectadas: " + filas);
                        }
                    }

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        loader.dispose();
                        JOptionPane.showMessageDialog(ventanaCobrar.this,
                                "Error al procesar la venta: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                    e.printStackTrace();
                    throw e;
                }
                return null;
            }

            @Override
            protected void done() {
                System.out.println("🟢 done() ejecutado");
                loader.dispose();

                try {
                    // Nombre del cliente según el idCliente que usamos
                    String nombreCliente = new VentaDao().obtenerNombreClientePorId(idCliente);
                    System.out.println("👤 Nombre cliente recuperado por id (" + idCliente + "): " + nombreCliente);

                    if (esCredito) {
                        System.out.println("Venta a crédito detectada, intentando eliminar créditos...");

                        int dni = new VentaDao().obtenerDniPorIdCliente(idCliente);
                        System.out.println("DNI obtenido: " + dni);

                        if (dni != -1) {
                            boolean eliminado = new VentaDao().eliminarCreditosDelCliente(dni);
                            System.out.println("¿Se eliminaron los créditos? " + eliminado);

                            AbonoDao abonoDao = new AbonoDao();
                            boolean abonosActualizados = abonoDao.actualizarAbonosAplicados(dni, idVenta);
                            System.out.println("¿Se actualizaron los abonos a aplicados? " + abonosActualizados);
                        } else {
                            System.out.println("❌ No se pudo obtener el DNI del cliente.");
                        }

                        if (tipoPagoFinal.equalsIgnoreCase("tarjeta")) {
                            double subtotal = totalPagar - totalComision;
                            ticket = ImprimirTicket.generarTicketCreditoConTarjeta(
                                    idVenta,
                                    nombreCliente,
                                    subtotal,
                                    totalComision,
                                    totalPagar,
                                    TableConsultaCreditCliente
                            );

                        } else if (tipoPagoFinal.equalsIgnoreCase("mixto")) {
                            double subtotal = totalPagar - totalComision;
                            ticket = ImprimirTicket.generarTicketCreditoMixto(
                                    idVenta,
                                    nombreCliente,
                                    subtotal,
                                    totalComision,
                                    totalPagar,
                                    TableConsultaCreditCliente // ✅ AÑADE ESTO
                            );

                        } else {
                            double cambioCredito = Math.max(0, ultimoPagoEfectivo - totalPagar);
                            ticket = ImprimirTicket.generarTicketCredito(
                                    idVenta,
                                    totalPagar,
                                    tipoPagoFinal,
                                    TableConsultaCreditCliente,
                                    ultimoPagoEfectivo,
                                    cambioCredito,
                                    nombreCliente
                            );
                        }

                    } else {
                        // VENTA NORMAL -> tickets y flujo normal
                        switch (tipoPagoFinal.toLowerCase()) {
                            case "efectivo":
                                double cambio = Math.max(0, ultimoPagoEfectivo - totalPagar);
                                ticket = ImprimirTicket.generarTicketEfectivo(idVenta, ultimoPagoEfectivo, cambio, tipoPagoFinal);
                                break;
                            case "tarjeta":
                            case "mixto":
                                double subtotal = totalPagar - totalComision;
                                ticket = ImprimirTicket.generarTicketTarjeta(idVenta, totalComision, tipoPagoFinal, subtotal);
                                break;
                            default:
                                double cambioDef = Math.max(0, totalPagado - totalPagar);
                                ticket = ImprimirTicket.generarTicketEfectivo(idVenta, totalPagado, cambioDef, tipoPagoFinal);
                                break;
                        }
                    }

                    if (ticket != null && !ticket.isEmpty()) {
                        if (!esCredito) {
                            AbrirCajaEfectivo.main(null);
                            System.out.println("1.- Caja de efectivo abierta");
                        }

                        ImprimirTicket.imprimir(ticket);
                        System.out.println("2.- Ticket enviado a impresora");

                        mostrarTicketDialogSoloInformativo(ticket);
                        System.out.println("3.- Vista previa mostrada");
                    }

                    DefaultTableModel tmp = (DefaultTableModel) Sistema.TableVenta.getModel();
                    tmp.setRowCount(0);
                    Sistema.lblEnviaTotal.setText("");
                    dispose();
                    txtCodigoVenta.requestFocus();

                    // -------------------- LOG FINAL --------------------
                    System.out.println("========================================");
                    System.out.println("✅ VENTA FINALIZADA (idVenta=" + idVenta + ")");
                    System.out.println("Cliente usado en esta venta -> ID: " + idCliente + " | Nombre: " + nombreCliente + " | EsCredito: " + esCredito);
                    System.out.println("Ticket generado? " + (ticket != null && !ticket.isEmpty()));
                    System.out.println("========================================");

                } catch (Exception e) {
                    System.err.println("❌ Error en done(): " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    public void enfocarCampoEfectivo() {
        txtPaga.requestFocus();
    }

    private String determinarTipoPagoFinal() {
        boolean hayEfectivo = false;
        boolean hayTarjeta = false;

        for (int i = 0; i < modeloPagos.getRowCount(); i++) {
            String metodo = modeloPagos.getValueAt(i, 0).toString();
            if (metodo.equalsIgnoreCase("Efectivo")) {
                hayEfectivo = true;
            }
            if (metodo.equalsIgnoreCase("Tarjeta")) {
                hayTarjeta = true;
            }
        }

        if (hayEfectivo && hayTarjeta) {
            return "Mixto";
        } else if (hayTarjeta) {
            return "Tarjeta";
        } else if (esVentaCredito) {
            return "Credito";
        } else {
            return "Efectivo";
        }
    }

    private void actualizarCambioTemporal() {
        try {
            double pago = Double.parseDouble(txtPaga.getText());
            double saldoPendiente = getSaldoPendiente();

            double cambio = pago - saldoPendiente;
            lblCambio.setText(String.format("%.2f", cambio > 0 ? cambio : 0.00));
        } catch (NumberFormatException e) {
            lblCambio.setText("0.00");
        }
    }

    private void mostrarTicketDialogSoloInformativo(String ticket) {
        JDialog dialog = new JDialog(this, "Vista previa del ticket", true);
        JTextArea area = new JTextArea(ticket);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 30));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(800, 800));

        dialog.add(scroll);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dialog.dispose();
                }
            }
        });

        dialog.setVisible(true);
    }

    public double getPagaConCredito() {
        try {
            return Double.parseDouble(txtPaga.getText().trim());
        } catch (NumberFormatException e) {
            return 0.00;
        }
    }

    public double getCambioCredito() {
        try {
            return Double.parseDouble(lblCambio.getText().trim());
        } catch (NumberFormatException e) {
            return 0.00;
        }
    }

    public boolean isCobroRealizado() {
        return cobroRealizado;
    }

}
