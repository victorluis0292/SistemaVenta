package Controlador;

import Modelo.CashInBox;
import Modelo.CashInBoxDAO;
import Modelo.TurnoDAO;
import Modelo.TurnoModel;
import Vista.CashInBoxForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import Modelo.login;  
import Vista.Sistema;
import java.sql.Timestamp;

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

  

// ...

private void registrarSaldoInicial() {
    try {
        // Obtener texto y limpiar cualquier carácter que no sea dígito o punto decimal
        String texto = view.txtAmount.getText().trim().replaceAll("[^\\d.]", "");

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Por favor ingrese un monto.");
            return;
        }

        if (texto.chars().filter(ch -> ch == '.').count() > 1) {
            JOptionPane.showMessageDialog(view, "Monto inválido: formato decimal incorrecto.");
            return;
        }

        BigDecimal amount = new BigDecimal(texto);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(view, "El monto debe ser mayor a cero.");
            return;
        }

        CashInBox cash = new CashInBox(amount, loginUsuario.getNombre());

        boolean registrado = dao.registrarSaldoInicial(cash);

        if (registrado) {
            // Registrar turno después de saldo inicial exitoso
            TurnoModel turno = new TurnoModel();
            turno.setUsuario(loginUsuario.getNombre());
            turno.setFechaInicio(new Timestamp(System.currentTimeMillis()));
            turno.setSaldoInicial(amount);
            turno.setEstado("abierto");

            TurnoDAO turnoDAO = new TurnoDAO();
            if (turnoDAO.abrirTurno(turno)) {
                JOptionPane.showMessageDialog(view, "Saldo inicial registrado y turno abierto correctamente.");
                view.dispose();
            } else {
                JOptionPane.showMessageDialog(view, "Saldo registrado, pero no se pudo abrir el turno.");
            }

        } else {
            JOptionPane.showMessageDialog(view, "Error al registrar el saldo inicial.");
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(view, "Monto inválido. Asegúrate de ingresar un número válido.");
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(view, "Error inesperado: " + ex.getMessage());
    }
}

    
}
