
package Modelo;

public class Combo {
  private int id;
    private String nombre;

    public Combo(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre; // Esto es lo que se muestra en el combo
    }
}
