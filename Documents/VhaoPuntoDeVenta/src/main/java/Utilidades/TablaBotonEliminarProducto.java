package Utilidades;

import Modelo.VentaDao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;

public class TablaBotonEliminarProducto {

    // Renderiza el botón con imagen
    public static class ButtonRenderer extends JButton implements TableCellRenderer {
        private final ImageIcon iconoEliminar;

        public ButtonRenderer() {
            iconoEliminar = new ImageIcon(getClass().getResource("/Img/eliminar.png"));
            setIcon(iconoEliminar);
            setText(null);
            setOpaque(true);
            setBorderPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // Editor del botón con imagen y acción eliminar
    public static class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private JTable table;
        private final ImageIcon iconoEliminar;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;

            iconoEliminar = new ImageIcon(getClass().getResource("/Img/eliminar.png"));

            button = new JButton();
            button.setIcon(iconoEliminar);
            button.setText(null);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        String nombreProducto = table.getValueAt(selectedRow, 2).toString();

                        int respuesta = JOptionPane.showConfirmDialog(
                                null,
                                "¿Realmente desea eliminar el producto \"" + nombreProducto + "\"?",
                                "Confirmar eliminación",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                        if (respuesta == JOptionPane.YES_OPTION) {
                            try {
                                int id = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());

                                VentaDao dao = new VentaDao();
                                boolean eliminado = dao.eliminarProdCreditoPorId(id);

                                if (eliminado) {
                                    ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                                    JOptionPane.showMessageDialog(null, "Registro eliminado correctamente");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Error al eliminar");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Error interno al intentar eliminar");
                            }
                        } else {
                            cancelCellEditing();
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}
