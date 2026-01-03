package Modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CashInBox {
    private int id;
    private BigDecimal amount;
    private String usuario;
    private Timestamp fecha;


    // Constructor principal (para registrar)
   
    public CashInBox(BigDecimal amount, String usuario) {
        this.amount = amount;
        this.usuario = usuario;
    }

    // Constructor completo (opcional, por si necesitas cargar desde base de datos)
    public CashInBox(int id, BigDecimal amount, String usuario, Timestamp fecha) {
        this.id = id;
        this.amount = amount;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }
}
