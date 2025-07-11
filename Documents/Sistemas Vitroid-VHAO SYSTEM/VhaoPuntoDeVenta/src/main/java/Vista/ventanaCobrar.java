    package Vista;

    import Modelo.LoaderDialog;
    import Servicios.CobroService;
    import Reportes.ImprimirTicket;
   import   Estilos.Estilos;
import Modelo.AbrirCajaEfectivo;
import Modelo.Eventos;
   import static Vista.Sistema.txtCodigoVenta;
    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

   //  public final class ventanaCobrar extends JFrame {
        public final class ventanaCobrar extends JDialog {
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
        

       public ventanaCobrar(JFrame parent) {
            super(parent, "Pago", true); // ← modal
                setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // Ignora cierres automáticos

            setTitle("Pago");
     setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            setLocationRelativeTo(null);
               
                


            setLayout(new BorderLayout(10, 10));
  // remplazar por
     //   JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        Estilos.PanelConEstilo panel = new Estilos.PanelConEstilo();
panel.setLayout(new GridLayout(5, 2, 10, 10));
          
          panel.add(new JLabel("Total a Pagar:"));
            lblTotal.setFont(new Font("Arial", Font.BOLD, 24));
            panel.add(lblTotal);

            panel.add(new JLabel("Paga Con (Efectivo):"));
            txtPaga.setFont(new Font("Arial", Font.PLAIN, 24));
            panel.add(txtPaga);
            new Eventos().aplicarSoloDecimal(txtPaga); // ← ¡Así de limpio!
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
             setLocationRelativeTo(parent);

            txtPaga.addActionListener(e -> cobrarEfectivo());
            txtPaga.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    actualizarCambioTemporal();
                }
            });
        
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

        BigDecimal pago = new BigDecimal(pagaTexto);
        if (pago.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
            return;
        }

 BigDecimal saldoPendiente = BigDecimal.valueOf(getSaldoPendiente());
boolean esMixto = modeloPagos.getRowCount() > 0;

if (pago.compareTo(saldoPendiente) <= 0 && pago.add(BigDecimal.valueOf(totalPagado)).compareTo(BigDecimal.valueOf(totalPagar)) < 0) {
    JOptionPane.showMessageDialog(this, "La suma total de los pagos no cubre el total a pagar.");
    lblCambio.setText("0.00");
    return;
}


        BigDecimal cambio = pago.subtract(saldoPendiente);
        if (cambio.compareTo(BigDecimal.ZERO) < 0) {
            cambio = BigDecimal.ZERO;
        }

        lblCambio.setText(cambio.setScale(2, RoundingMode.HALF_UP).toString());

        // Guardar pago para imprimir correctamente el ticket después
        ultimoPagoEfectivo = pago.doubleValue();

        // Registrar el pago
        double montoUsado = Math.min(pago.doubleValue(), saldoPendiente.doubleValue());
        agregarPago("Efectivo", montoUsado, 0.00);

        // Finalizar si ya se completó el total
        if (getSaldoPendiente() <= 0) {
            txtPaga.setEditable(false);
            procesarVentaFinal();
        }

        txtPaga.setText(""); // Limpiar campo al final
        // Eventos.numberDecimalKeyPress();
    } catch (NumberFormatException | ArithmeticException e) {
        lblCambio.setText("Error");
        JOptionPane.showMessageDialog(this, "Ingrese un monto válido.");
    }
}

      public void agregarPago(String metodo, double monto, double comision) {
    if (comision > 0) {
        totalComision += comision;
        totalPagar += comision;  // con esto quiras la comision si lo deseas
    }

    // No permitir que el pago supere el saldo pendiente real
    double saldoPendiente = getSaldoPendiente();
    if (monto > saldoPendiente) {
        monto = saldoPendiente;
    }

    // Sumar solo el monto (la comisión ya fue tratada arriba)
    totalPagado += monto;

    modeloPagos.addRow(new Object[]{
        metodo,
        String.format("$%.2f (Comisión $%.2f)", monto, comision)
    });

    // Mostrar el saldo pendiente REAL (sin ajustes raros)
    lblTotal.setText(String.format("%.2f", getSaldoPendiente()));
}


        public void finalizarPagoTarjeta(double comision, double subtotal) {
            procesarVentaFinal();
                 
                 txtCodigoVenta.requestFocus();
        }

  private void procesarVentaFinal() {
    JDialog loader = new LoaderDialog().mostrarLoader(this);

   SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

        int idVenta;
        String ticket;

        @Override
        protected Void doInBackground() throws Exception {
            int cliente = Integer.parseInt(Sistema.txtIdCV.getText());
            String vendedor = Sistema.LabelVendedor.getText();
            CobroService servicio = new CobroService();

            String tipoPagoFinal = determinarTipoPagoFinal();
            idVenta = servicio.procesarVenta(cliente, vendedor, Sistema.TableVenta, totalPagar, tipoPagoFinal);

            if (tipoPagoFinal.equalsIgnoreCase("Efectivo")) {
                double pago = ultimoPagoEfectivo;
                double cambio = Math.max(0, pago - totalPagar);
                ticket = ImprimirTicket.generarTicketReal(idVenta, pago, cambio, tipoPagoFinal);
            } else if (tipoPagoFinal.equalsIgnoreCase("Tarjeta")) {
                double subtotal = totalPagar - totalComision;
                ticket = ImprimirTicket.generarTicketReal(idVenta, totalComision, tipoPagoFinal, subtotal);
            } else if (tipoPagoFinal.equalsIgnoreCase("Mixto")) {
                double subtotal = totalPagar - totalComision;
                ticket = ImprimirTicket.generarTicketReal(idVenta, totalComision, tipoPagoFinal, subtotal);
            } else {
                double pago = totalPagado;
                double cambio = Math.max(0, totalPagado - totalPagar);
                ticket = ImprimirTicket.generarTicketReal(idVenta, pago, cambio, tipoPagoFinal);
            }

            return null;
        }

        @Override
   
protected void done() {
    loader.dispose();

    // 1.- Abrir la caja registradora
    AbrirCajaEfectivo.main(null);
    System.out.println("1.- abrir la caja");

    // 2.- Imprimir el ticket directamente
    ImprimirTicket.imprimir(ticket);
    System.out.println("2.- imprimir ticket");

    // 3.- Mostrar vista previa (informativa, sin opción de cancelar)
    mostrarTicketDialogSoloInformativo(ticket);
    System.out.println("3.- vista previa del ticket");

    // Limpiar y cerrar ventana
    DefaultTableModel tmp = (DefaultTableModel) Sistema.TableVenta.getModel();
    tmp.setRowCount(0);
    Sistema.lblEnviaTotal.setText("");
    dispose();
    txtCodigoVenta.requestFocus();
}

    };
    worker.execute();
}
  public void enfocarCampoEfectivo(){
  txtPaga.requestFocus();
  }

/**
 * Método para determinar el tipo de pago final según los pagos ingresados.
 * Si solo hay efectivo -> "Efectivo"
 * Si solo hay tarjeta -> "Tarjeta"
 * Si hay ambos -> "Mixto"
 */
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
    } else {
        return "Efectivo";
    }
}
//  2.25


 private void actualizarCambioTemporal() {
    try {
        double pago = Double.parseDouble(txtPaga.getText());
        double saldoPendiente = getSaldoPendiente(); // Este ya incluye comisiones

        double cambio = pago - saldoPendiente;
        lblCambio.setText(String.format("%.2f", cambio > 0 ? cambio : 0.00));
    } catch (NumberFormatException e) {
        lblCambio.setText("0.00");
    }
}

private double calcularComisionPagos() {
    double totalComision = 0.0;
    for (int i = 0; i < modeloPagos.getRowCount(); i++) {
        String pagoTexto = modeloPagos.getValueAt(i, 1).toString();
        int index = pagoTexto.indexOf("Comisión $");
        if (index != -1) {
            try {
                String comisionStr = pagoTexto.substring(index + 10).replace(")", "").trim();
                totalComision += Double.parseDouble(comisionStr);
            } catch (NumberFormatException ignored) {}
        }
    }
    return totalComision;
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

    // Cerrar al presionar ENTER
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

    }
