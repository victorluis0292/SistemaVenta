package Controlador;

import Modelo.CashInBox;
import Modelo.CashInBoxDAO;
import Vista.CashInBoxForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Modelo.login;  // 👈 asegúrate de tener este import
import Vista.Sistema;
public class CashInBoxController {
    private CashInBoxForm view;
    private CashInBoxDAO dao;
    private login loginUsuario;

 public CashInBoxController(CashInBoxForm view, CashInBoxDAO dao, login usuario) {
    this.view = view;
    this.dao = dao;
    this.loginUsuario = usuario;

    this.view.btnRegistrar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            registrarSaldoInicial();
        }
    });
       // ✅ ENTER en txtAmount también registra
    this.view.txtAmount.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            registrarSaldoInicial();
        }
    });
}


    public void iniciar() {
        view.setVisible(true);
    }

  private void registrarSaldoInicial() {
    try {
        double amount = view.txtAmount.getDoubleValue();

        CashInBox cash = new CashInBox(amount);
        boolean registrado = dao.registrarSaldoInicial(cash);

       if (registrado) {
    JOptionPane.showMessageDialog(view, "Saldo inicial registrado.");
    view.dispose();  // ✅ SOLO cerrar, no crear Sistema aquí
} else {
            JOptionPane.showMessageDialog(view, "Error al registrar el saldo.");
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(view, "Monto inválido.");
    }
}

}
