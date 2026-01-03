package Modelo;

public class CierreTurnoModel {
    private double esperado;
    private double real;

    public CierreTurnoModel(double esperado) {
        this.esperado = esperado;
        this.real = esperado;
    }

    public double getEsperado() {
        return esperado;
    }

    public double getReal() {
        return real;
    }

    public void setReal(double real) {
        this.real = real;
    }

    public double calcularDiferencia() {
        return real - esperado;
    }

    public boolean estaCorrecto() {
        return Math.abs(calcularDiferencia()) < 0.01; // margen de error
    }
}
