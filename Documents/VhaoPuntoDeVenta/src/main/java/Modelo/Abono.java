package Modelo;

import java.util.Date;

public class Abono {
    private double monto;
    private Date fecha;

    public Abono(double monto, Date fecha) {
        this.monto = monto;
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public Date getFecha() {
        return fecha;
    }
}
