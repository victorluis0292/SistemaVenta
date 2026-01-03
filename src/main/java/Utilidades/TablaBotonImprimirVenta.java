package Utilidades;

import Modelo.VentaDao;
import Modelo.ImprimirTicket;

import javax.swing.*;
import java.awt.*;

public class TablaBotonImprimirVenta {

    public static JButton crearBotonReimprimir(Component parentComponent) {
        JButton btnReimprimirTicket = new JButton("Reimprimir Ticket");
        btnReimprimirTicket.setBackground(Color.RED);
        btnReimprimirTicket.setForeground(Color.WHITE);
        btnReimprimirTicket.setFont(new Font("Tahoma", Font.PLAIN, 14));

        btnReimprimirTicket.addActionListener(e -> {
          });

        return btnReimprimirTicket;
    }
}
