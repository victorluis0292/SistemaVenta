package Vista;

import javax.swing.*;
import java.awt.*;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DialogoMapeoColumnasImportarExcel extends JDialog {

    private static final String[] CAMPOS_OBLIGATORIOS = {"Código", "Descripción"};
    private static final String OPCION_NO_USAR = "-- No usar --";

    private final Map<String, JComboBox<String>> combosPorCampo = new LinkedHashMap<>();
    private Map<String, Integer> resultado = null;

    public DialogoMapeoColumnasImportarExcel(Window parent, List<String> encabezadosExcel) {

        super(parent, "Configurar importación", ModalityType.APPLICATION_MODAL);

        setSize(480, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        JPanel panelCampos = new JPanel(new GridBagLayout());
        panelCampos.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        String[] opciones = construirOpciones(encabezadosExcel);

        int fila = 0;

        for (String campo : Utilidades.ImportadorExcel.CAMPOS) {

            gbc.gridx = 0;
            gbc.gridy = fila;
            gbc.weightx = 0;
            panelCampos.add(new JLabel(campo + ":"), gbc);

            JComboBox<String> combo = new JComboBox<>(opciones);
            combo.setSelectedItem(buscarCoincidencia(campo, encabezadosExcel));

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            panelCampos.add(combo, gbc);

            combosPorCampo.put(campo, combo);
            fila++;
        }

        add(new JScrollPane(panelCampos), BorderLayout.CENTER);

        JButton btnImportar = new JButton("Importar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.add(btnImportar);
        panelBotones.add(btnCancelar);

        add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> {
            resultado = null;
            dispose();
        });

        btnImportar.addActionListener(e -> {

            if (!validarObligatorios()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Debes asignar una columna a los campos obligatorios: "
                                + String.join(", ", CAMPOS_OBLIGATORIOS),
                        "Faltan campos",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            resultado = construirMapeo(encabezadosExcel);
            dispose();
        });
    }

    private boolean validarObligatorios() {

        for (String obligatorio : CAMPOS_OBLIGATORIOS) {

            JComboBox<String> combo = combosPorCampo.get(obligatorio);

            if (combo.getSelectedItem() == null
                    || combo.getSelectedItem().equals(OPCION_NO_USAR)) {
                return false;
            }
        }

        return true;
    }

    private Map<String, Integer> construirMapeo(List<String> encabezadosExcel) {

        Map<String, Integer> mapeo = new LinkedHashMap<>();

        for (Map.Entry<String, JComboBox<String>> entrada : combosPorCampo.entrySet()) {

            String seleccion = (String) entrada.getValue().getSelectedItem();

            if (seleccion == null || seleccion.equals(OPCION_NO_USAR)) {
                mapeo.put(entrada.getKey(), -1);
            } else {
                mapeo.put(entrada.getKey(), encabezadosExcel.indexOf(seleccion));
            }
        }

        return mapeo;
    }

    private String[] construirOpciones(List<String> encabezadosExcel) {

        String[] opciones = new String[encabezadosExcel.size() + 1];
        opciones[0] = OPCION_NO_USAR;

        for (int i = 0; i < encabezadosExcel.size(); i++) {
            opciones[i + 1] = encabezadosExcel.get(i);
        }

        return opciones;
    }

    private String buscarCoincidencia(String campo, List<String> encabezadosExcel) {

        String campoNormalizado = normalizar(campo);

        for (String encabezado : encabezadosExcel) {
            if (normalizar(encabezado).equals(campoNormalizado)) {
                return encabezado;
            }
        }

        return OPCION_NO_USAR;
    }

    private String normalizar(String texto) {

        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return sinAcentos.trim().toUpperCase();
    }

    public static Map<String, Integer> mostrar(Window parent, List<String> encabezadosExcel) {

        DialogoMapeoColumnasImportarExcel dialogo = new DialogoMapeoColumnasImportarExcel(parent, encabezadosExcel);
        dialogo.setVisible(true);

        return dialogo.resultado;
    }
}