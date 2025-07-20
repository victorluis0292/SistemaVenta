/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

/**
 *
 * @author vic
 */
public class LoaderDialog {
   public JDialog mostrarLoader(java.awt.Window parent) {
    JDialog dialog = new JDialog(parent); // esto lo vincula al padre
    dialog.setUndecorated(true);
    dialog.setSize(250, 180);
    dialog.setLocationRelativeTo(parent); // lo centra sobre la ventana que lo llamó
    dialog.setModal(false);
    dialog.setAlwaysOnTop(true);

    ImageIcon icono = new ImageIcon(getClass().getResource("/Img/spinner.gif"));
    JLabel lbl = new JLabel("Procesando pago...", icono, JLabel.CENTER);
    lbl.setHorizontalTextPosition(SwingConstants.CENTER);
    lbl.setVerticalTextPosition(SwingConstants.BOTTOM);
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    lbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    dialog.add(lbl);

    SwingUtilities.invokeLater(() -> dialog.setVisible(true));

    return dialog;
}
}
