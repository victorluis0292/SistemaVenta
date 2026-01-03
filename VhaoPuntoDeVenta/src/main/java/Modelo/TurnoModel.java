package Modelo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TurnoModel {
    private int id;
    private String usuario;
    private Timestamp fechaInicio;
    private Timestamp fechaCierre;
    private BigDecimal saldoInicial;
    private BigDecimal totalVentas;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal ganancia;
    private BigDecimal totalEnCaja;
private String estado;
    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Timestamp fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Timestamp getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(Timestamp fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getEgresos() {
        return egresos;
    }

    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    public BigDecimal getGanancia() {
        return ganancia;
    }

    public void setGanancia(BigDecimal ganancia) {
        this.ganancia = ganancia;
    }

    public BigDecimal getTotalEnCaja() {
        return totalEnCaja;
    }

    public void setTotalEnCaja(BigDecimal totalEnCaja) {
        this.totalEnCaja = totalEnCaja;
    }
    public String getEstado() {
    return estado;
}

public void setEstado(String estado) {
    this.estado = estado;
}

}
