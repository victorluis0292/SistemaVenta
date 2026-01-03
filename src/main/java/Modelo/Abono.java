package Modelo;

import java.util.Date;

public class Abono {
    private double monto;
    private Date fecha;
    private int idVenta;     // ID de la venta asociada
    private int idEmpresa;   // ID de la empresa asociada
    private boolean aplicado; // Si el abono ya fue aplicado

    // Constructor completo
    public Abono(double monto, Date fecha, int idVenta, int idEmpresa, boolean aplicado) {
        this.monto = monto;
        this.fecha = fecha;
        this.idVenta = idVenta;
        this.idEmpresa = idEmpresa;
        this.aplicado = aplicado;
    }

    // Constructor simplificado (para casos rápidos)
    public Abono(double monto, Date fecha) {
        this(monto, fecha, 0, 0, true);
    }

    public double getMonto() {
        return monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public boolean isAplicado() {
        return aplicado;
    }
}
