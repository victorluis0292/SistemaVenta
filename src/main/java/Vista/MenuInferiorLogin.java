package Vista;

import Utilidades.ConfigApp;
import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MenuInferiorLogin extends JPanel {

    public MenuInferiorLogin() {
        // 🔹 Espaciado horizontal y vertical del menú inferior
        setLayout(new FlowLayout(FlowLayout.CENTER, 25, 10));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(400, 70));

        // 🔹 Botones (icono + texto)
        add(crearBoton("", "/Img/icon_netkey.png"));
        add(crearBoton("", "/Img/icon_codi.png"));
        add(crearBoton("", "/Img/icon_dimo.png"));
        add(crearBoton("Facebook", "/Img/icon_facebook.png"));
        add(crearBoton("Soporte", "/Img/icon_soporte.png"));
    }

    private JPanel crearBoton(String texto, String rutaIcono) {
        JPanel panelBoton = new JPanel();
        panelBoton.setLayout(new BorderLayout(0, 5)); // Espacio entre ícono y texto
        panelBoton.setPreferredSize(new Dimension(55, 50));
        panelBoton.setBackground(Color.WHITE);
        panelBoton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 🔹 Ícono centrado y redimensionado
        JLabel icono = new JLabel("", SwingConstants.CENTER);
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(rutaIcono));
            Image img = originalIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            icono.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            icono.setText("");
        }

        // 🔹 Texto debajo del ícono
        JLabel textoLabel = new JLabel(texto, SwingConstants.CENTER);
        textoLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        textoLabel.setForeground(new Color(60, 60, 60));

        // 🔹 Añadir icono y texto
        panelBoton.add(icono, BorderLayout.NORTH);
        panelBoton.add(textoLabel, BorderLayout.SOUTH);

        // 🔹 Efecto hover visual y eventos
        panelBoton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panelBoton.setBackground(new Color(240, 240, 240));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panelBoton.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // 🔸 Opción Soporte (WhatsApp)
                if (texto.equalsIgnoreCase("Soporte")) {
                    try {
                        int idEmpresa = ConfigApp.getIdEmpresa();
                        String correo = ConfigApp.getCorreo();

                        // Mensaje codificado para URL (WhatsApp)
                        String mensaje = String.format(
                            "Hola%%2C%%20soy%%20el%%20comercio%%0A" +
                            "Número%%20de%%20empresa%%3A%%20%d%%0A" +
                            "Correo%%3A%%20%s%%0A" +
                            "¿Podría%%20ayudarme%%3F",
                            idEmpresa, correo
                        );

                        String url = "https://wa.me/5218139939981?text=" + mensaje;
                        Desktop desktop = Desktop.getDesktop();

                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(new java.net.URI(url));
                        } else {
                            JOptionPane.showMessageDialog(null, "No se puede abrir el navegador en este sistema.");
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "No se pudo abrir el chat de soporte en WhatsApp.");
                        ex.printStackTrace();
                    }
                }

                // 🔸 Opción Facebook
                else if (texto.equalsIgnoreCase("Facebook")) {
                    try {
                        String url = "https://www.facebook.com/profile.php?id=61583088501480";
                        Desktop desktop = Desktop.getDesktop();

                        if (desktop.isSupported(Desktop.Action.BROWSE)) {
                            desktop.browse(new java.net.URI(url));
                        } else {
                            JOptionPane.showMessageDialog(null, "No se puede abrir el navegador en este sistema.");
                        }

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "No se pudo abrir la página de Facebook.");
                        ex.printStackTrace();
                    }
                }
            }
        });

        return panelBoton;
    }
}
