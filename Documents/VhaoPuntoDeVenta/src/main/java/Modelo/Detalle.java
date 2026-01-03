
package Modelo;

import java.math.BigDecimal;

public class Detalle {
    private int id;
    private int id_pro;
    private String nombre; // se metio
    private int cantidad;
    private double precio;
   private double total;
    private int id_venta;
    private String cliente;
     private int dni;//se agrego para creditocliente
    private String fecha;//se agrego para creditocliente
       private String descripcion;
    private BigDecimal subtotal;
    
    public Detalle(){
        
    }

    public Detalle(int id, int id_pro,String nombre_product, int cantidad, double precio, double total, int id_venta, String cliente, int dni, String fecha) {
        this.id = id;
        this.id_pro = id_pro;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.total = total;
        this.id_venta = id_venta;
         this.cliente = cliente;
          this.dni = dni;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_pro() {
        return id_pro;
    }

    public void setId_pro(int id_pro) {
        this.id_pro = id_pro;
    }
    
  
    
      public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
      public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
  public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }
    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }
     public String getFecha() {
        return fecha;
    }

    public void setFecha(String  fecha) {
        this.fecha = fecha;
    }
    
  
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
