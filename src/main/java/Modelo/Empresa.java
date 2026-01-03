package Modelo;

public class Empresa {
    private int id_empresa;
    private String ruc;
    private String nombre;
    private String telefono;
    private String direccion;
    private String mensaje;
    private int estado;

    // Getters y Setters
    public int getId_empresa() { return id_empresa; }
    public void setId_empresa(int id_empresa) { this.id_empresa = id_empresa; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }
}
