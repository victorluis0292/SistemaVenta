package Modelo;
public class Productos {
    private int id;
    private String codigo;
    private String nombre;
    private String categoria;      // ✅ Nueva columna para clasificar productos
    private String imagenUrl;      // ✅ Nueva columna para la foto (NUEVO)
    private int proveedor;
    private String proveedorPro;
    private int stock;
    private double precio;         // 💰 Precio unitario (por pieza o paquete)
    private double precioKg;       // 💰 Precio por kilo
    private double preciocompra;   // Precio de compra
    private int id_empresa;        // Empresa a la que pertenece
    private Integer idCatalogoGlobal;
    private Integer idCategoria;   // ✅ Vínculo con tabla categorias (nullable, igual que idCatalogoGlobal)
    // Constructor vacío
    public Productos() {
    }
    // Constructor con parámetros básicos (por kilo)
    public Productos(String nombre, double precioKg, String categoria) {
        this.nombre = nombre;
        this.precioKg = precioKg;
        this.categoria = categoria;
    }
    // Constructor completo
    public Productos(int id, String codigo, String nombre, String categoria,
                     int proveedor, String proveedorPro,
                     int stock, double precio, double precioKg,
                     double preciocompra, int id_empresa) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.proveedor = proveedor;
        this.proveedorPro = proveedorPro;
        this.stock = stock;
        this.precio = precio;
        this.precioKg = precioKg;
        this.preciocompra = preciocompra;
        this.id_empresa = id_empresa;
    }
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public int getProveedor() { return proveedor; }
    public void setProveedor(int proveedor) { this.proveedor = proveedor; }
    public String getProveedorPro() { return proveedorPro; }
    public void setProveedorPro(String proveedorPro) { this.proveedorPro = proveedorPro; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public double getPrecioKg() { return precioKg; }
    public void setPrecioKg(double precioKg) { this.precioKg = precioKg; }
    public double getPreciocompra() { return preciocompra; }
    public void setPreciocompra(double preciocompra) { this.preciocompra = preciocompra; }
    public int getId_empresa() { return id_empresa; }
    public void setId_empresa(int id_empresa) { this.id_empresa = id_empresa; }
    // 📌 Método para calcular precio proporcional según gramos
    public double calcularPrecioPorGramos(double gramos) {
        return (gramos / 1000.0) * precioKg;
    }
    public Integer getIdCatalogoGlobal() { return idCatalogoGlobal; }
    public void setIdCatalogoGlobal(Integer idCatalogoGlobal) { this.idCatalogoGlobal = idCatalogoGlobal; }

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }
}