package Utilidades;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

public class SelectorImagenCloudinary {

    public static String mostrarSelectorVisual(JFrame parent, List<String> listaUrls) {
        JDialog dialog = new JDialog(parent, "Seleccionar Imagen de Cloudinary", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        JPanel panelContenedor = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(panelContenedor);
        
        final String[] urlSeleccionada = {null};

        for (String urlImg : listaUrls) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            try {
                URL url = new URL(urlImg);
                ImageIcon iconOriginal = new ImageIcon(url);
                Image imagenEscalada = iconOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                JButton btnImagen = new JButton(new ImageIcon(imagenEscalada));
                
                btnImagen.addActionListener(e -> {
                    urlSeleccionada[0] = urlImg;
                    dialog.dispose();
                });

                itemPanel.add(btnImagen, BorderLayout.CENTER);
                JLabel lblInfo = new JLabel("Seleccionar", JLabel.CENTER);
                itemPanel.add(lblInfo, BorderLayout.SOUTH);

            } catch (Exception e) {
                // Manejar error de URL si alguna falla al cargar
            }

            panelContenedor.add(itemPanel);
        }

        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.add(btnCancelar, BorderLayout.SOUTH);

        dialog.setVisible(true);
        return urlSeleccionada[0];
    }
}