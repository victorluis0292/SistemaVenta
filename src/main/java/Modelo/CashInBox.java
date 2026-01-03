package Modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class CashInBox {
    private int id;
    private BigDecimal amount;
    private String usuario;
    private Timestamp fecha;
    private int idEmpresa; // <-- nuevo campo

    // Constructor principal (para registrar)
    public CashInBox(BigDecimal amount, String usuario, int idEmpresa) {
        this.amount = amount;
        this.usuario = usuario;
        this.idEmpresa = idEmpresa;
    }

    // Constructor completo (para cargar desde BD)
    public CashInBox(int id, BigDecimal amount, String usuario, Timestamp fecha, int idEmpresa) {
        this.id = id;
        this.amount = amount;
        this.usuario = usuario;
        this.fecha = fecha;
        this.idEmpresa = idEmpresa;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }
}
