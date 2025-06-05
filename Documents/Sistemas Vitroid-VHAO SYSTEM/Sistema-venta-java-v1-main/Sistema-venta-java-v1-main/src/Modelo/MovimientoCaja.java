// Modelo/MovimientoCaja.java
package Modelo;

import java.util.Date;

public class MovimientoCaja {
    private String tipo;      // "Ingreso" o "Egreso"
    private String concepto;
    private String nota;
    private double monto;
    private String gasto;     // puede ser null o "" si no aplica
    private Date fecha;

    // Getters y setters
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getGasto() { return gasto; }
    public void setGasto(String gasto) { this.gasto = gasto; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
}
