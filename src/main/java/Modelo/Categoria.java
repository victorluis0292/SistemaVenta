package Modelo;

public class Categoria {

    private int idCategoria;
    private String nombre;
    private int idEmpresa;

    public Categoria() {
    }

    public Categoria(int idCategoria, String nombre, int idEmpresa) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.idEmpresa = idEmpresa;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    /** Para que se vea bonito el nombre dentro del JComboBox */
    @Override
    public String toString() {
        return nombre;
    }
}