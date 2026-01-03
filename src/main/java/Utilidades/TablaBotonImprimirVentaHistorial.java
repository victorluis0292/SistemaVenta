package Utilidades;

import Modelo.Conexion;
import Modelo.ImprimirTicket;
import Modelo.Venta;
import Modelo.VentaDao;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class TablaBotonImprimirVentaHistorial extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener {

    private final JButton renderButton;
    private final JButton editButton;
    private JTable table;
    private int row;

    public TablaBotonImprimirVentaHistorial() {
        ImageIcon iconoOriginal = new ImageIcon(getClass().getResource("/Img/print.png"));
        Image imgEscalada = iconoOriginal.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon iconoEscalado = new ImageIcon(imgEscalada);

        renderButton = new JButton(iconoEscalado);
        renderButton.setFocusPainted(false);
        renderButton.setBackground(new Color(144, 202, 249));
        renderButton.setForeground(Color.BLACK);

        editButton = new JButton(iconoEscalado);
        editButton.setFocusPainted(false);
        editButton.setBackground(new Color(144, 202, 249));
        editButton.setForeground(Color.BLACK);
        editButton.addActionListener(this);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        return editButton;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
        try {
            // Columna 5: id único de la venta (oculto)
            int idVenta = (int) table.getValueAt(row, 5);
            System.out.println("DEBUG: fila=" + row + ", idVenta=" + idVenta);
            imprimirVentaHistorial(idVenta);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener ID de la venta: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

private void imprimirVentaHistorial(int idVenta) {
    try (Connection con = Conexion.getConnection()) {
        Venta venta = new VentaDao().BuscarVenta(idVenta);
        if (venta == null) {
            JOptionPane.showMessageDialog(null, "No se encontró la venta con ID " + idVenta);
            return;
        }

        // Empresa
        String sqlEmpresa = "SELECT nombre, direccion, telefono FROM empresa WHERE id_empresa = ?";
        String nombreNegocio = "", direccion = "", telefono = "";
        try (PreparedStatement psEmp = con.prepareStatement(sqlEmpresa)) {
            psEmp.setInt(1, venta.getIdEmpresa());
            try (ResultSet rsEmp = psEmp.executeQuery()) {
                if (rsEmp.next()) {
                    nombreNegocio = rsEmp.getString("nombre");
                    direccion = rsEmp.getString("direccion");
                    telefono = rsEmp.getString("telefono");
                }
            }
        }

        // Productos y abonos
        List<String[]> productosYAbonos = obtenerProductosYAbonosPorIdVenta(idVenta);

        // Construir ticket
        StringBuilder ticket = new StringBuilder();
        ticket.append("     ").append(nombreNegocio).append("\n");
        ticket.append(centrarTexto(direccion, 32)).append("\n");
        ticket.append("Tel: ").append(telefono).append("\n");
        ticket.append("------------------------------\n");
        ticket.append("Folio: ").append(venta.getFolio()).append("\n");
        ticket.append("Fecha: ").append(venta.getFechaHora()).append("\n");

        String clienteNombre = (venta.getNombre_cli() != null && !venta.getNombre_cli().isEmpty())
                ? venta.getNombre_cli()
                : "Público en general";
        ticket.append("Cliente: ").append(clienteNombre).append("\n");
        ticket.append("------------------------------\n");

        // Calcular subtotal y total de abonos
        double subtotalProductos = 0.0;
        double totalAbonos = 0.0;

        for (String[] item : productosYAbonos) {
            int cantidad = Integer.parseInt(item[1]);
            double precio = Double.parseDouble(item[2]);

            if (item[3].equalsIgnoreCase("producto")) {
                subtotalProductos += cantidad * precio;
                ticket.append(item[0]).append("\n");
                ticket.append(String.format("%dx$%.2f   $%.2f\n", cantidad, precio, cantidad * precio));
            } else if (item[3].equalsIgnoreCase("abono")) {
                totalAbonos += precio;
                ticket.append("ABONO\n");
                ticket.append(String.format("%dx$%.2f   $%.2f\n", cantidad, precio, cantidad * precio));
            }
        }
        ticket.append("------------------------------\n");

        double subtotalFinal = subtotalProductos - totalAbonos;

        // Valores de pago
        String tipoPago = venta.getTipopago();
        double total = venta.getTotal();
        double pagado = venta.getPagaCon();
        double cambio = venta.getCambio();
        double comision = venta.getComision();

        // Mostrar según tipo de pago
        switch (tipoPago.toLowerCase()) {
            case "efectivo":
            case "credito":
                ticket.append(String.format("%-12s $%8.2f\n", "TOTAL:", total));
                ticket.append(String.format("%-12s $%8.2f\n", "PAGA CON:", pagado));
                ticket.append(String.format("%-12s $%8.2f\n", "CAMBIO:", cambio));
                ticket.append("Tipo: ").append(capitalizar(tipoPago)).append("\n");
                break;

            case "tarjeta":
                ticket.append(String.format("%-12s $%8.2f\n", "SUBTOTAL:", subtotalFinal));
                ticket.append(String.format("%-12s $%8.2f\n", "COMISIÓN:", comision));
                ticket.append(String.format("%-12s $%8.2f\n", "TOTAL:", total));
                ticket.append("Tipo: Tarjeta Crédito\n");
                break;

            case "mixto":
                ticket.append(String.format("%-12s $%8.2f\n", "SUBTOTAL:", subtotalFinal));
                ticket.append(String.format("%-12s $%8.2f\n", "COMISIÓN:", comision));
                ticket.append(String.format("%-12s $%8.2f\n", "TOTAL:", total));
                ticket.append("Tipo: Mixto\n");
                break;

            default:
                ticket.append(String.format("%-12s $%8.2f\n", "TOTAL:", total));
                ticket.append("Tipo: ").append(capitalizar(tipoPago)).append("\n");
        }

        ticket.append("------------------------------\n");
        ticket.append("¡Gracias por su compra!\n");
        ticket.append("USAMOS VHAO PUNTO DE VENTAS\n");

        // Imprimir
        ImprimirTicket.imprimir(ticket.toString());
        mostrarTicketDialog(ticket.toString());

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al imprimir ticket: " + ex.getMessage());
    }
}

// 🔹 Método para capitalizar la primera letra
private String capitalizar(String texto) {
    if (texto == null || texto.isEmpty()) return "";
    return texto.substring(0,1).toUpperCase() + texto.substring(1).toLowerCase();
}

private List<String[]> obtenerProductosYAbonosPorIdVenta(int idVenta) {
    List<String[]> lista = new ArrayList<>();
    try (Connection con = Conexion.getConnection()) {
        String sql = "SELECT p.nombre, d.cantidad, d.precio, 'producto' AS tipo " +
                     "FROM detalle d " +
                     "JOIN productos p ON d.id_pro = p.id " +
                     "WHERE d.id_venta = ? " +
                     "UNION ALL " +
                     "SELECT 'ABONO', 1, a.monto, 'abono' " +  // <-- Cambiado de DATE_FORMAT(a.fecha, ...) a 'ABONO'
                     "FROM abonos_credito a " +
                     "WHERE a.id_venta = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            ps.setInt(2, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombre = rs.getString(1);
                    int cantidad = rs.getInt(2);
                    double precio = rs.getDouble(3);
                    String tipo = rs.getString(4);
                    lista.add(new String[]{nombre, String.valueOf(cantidad), String.valueOf(precio), tipo});
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

    private void mostrarTicketDialog(String ticket) {
        JDialog dialog = new JDialog((Frame) null, "Vista previa del ticket", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextArea area = new JTextArea(ticket);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(500, 600));

        dialog.add(scroll);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dialog.dispose();
                }
            }
        });

        dialog.setVisible(true);
    }

    private String centrarTexto(String texto, int ancho) {
        if (texto == null) return "";
        texto = texto.trim();
        if (texto.length() >= ancho) return texto;

        int espacios = (ancho - texto.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espacios; i++) {
            sb.append(" ");
        }
        sb.append(texto);
        return sb.toString();
    }

}
