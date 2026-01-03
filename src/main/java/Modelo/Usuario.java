package Modelo;

public class Usuario {
    private int id;
    private String nombre;
    private String correo;
    private String pass;      
    private String rol;        // reemplaza idRol, coincide con la columna 'rol' de la tabla
    private int idEmpresa;     // FK hacia la tabla empresa
    private String clave;      // clave para autorizaciones especiales
    
    // Campos extra (rellenados solo cuando hagas JOIN en los DAO)
    private String nombreEmpresa;

    public Usuario() {}

    public Usuario(String nombre, String correo, String pass, String rol, int idEmpresa, String clave) {
        this.nombre = nombre;
        this.correo = correo;
        this.pass = pass;
        this.rol = rol;
        this.idEmpresa = idEmpresa;
        this.clave = clave;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
}
