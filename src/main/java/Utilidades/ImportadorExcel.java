package Utilidades;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportadorExcel {

    public static final String[] CAMPOS = {
            "Código", "Descripción", "Categoría",
            "Proveedor", "Stock", "Precio Venta", "Precio Compra"
    };

    public static List<String> leerEncabezados(File archivo) throws Exception {

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook libro = new XSSFWorkbook(fis)) {

            Sheet hoja = libro.getSheetAt(0);
            Row filaEncabezado = hoja.getRow(0);

            List<String> encabezados = new ArrayList<>();

            if (filaEncabezado != null) {
                for (Cell celda : filaEncabezado) {
                    encabezados.add(obtenerTexto(celda));
                }
            }

            return encabezados;
        }
    }

    public static List<FilaImportada> leerFilas(File archivo, Map<String, Integer> mapeo) throws Exception {

        List<FilaImportada> filas = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook libro = new XSSFWorkbook(fis)) {

            Sheet hoja = libro.getSheetAt(0);
            int ultimaFila = hoja.getLastRowNum();

            for (int i = 1; i <= ultimaFila; i++) {

                Row fila = hoja.getRow(i);

                if (fila == null) {
                    continue;
                }

                FilaImportada datos = new FilaImportada();

                datos.codigo = obtenerValor(fila, mapeo.get("Código"));
                datos.descripcion = obtenerValor(fila, mapeo.get("Descripción"));
                datos.categoria = obtenerValor(fila, mapeo.get("Categoría"));
                datos.proveedor = obtenerValor(fila, mapeo.get("Proveedor"));

                datos.stock = parsearEntero(obtenerValor(fila, mapeo.get("Stock")));
                datos.precioVenta = parsearDecimal(obtenerValor(fila, mapeo.get("Precio Venta")));
                datos.precioCompra = parsearDecimal(obtenerValor(fila, mapeo.get("Precio Compra")));

                boolean sinCodigo = datos.codigo == null || datos.codigo.trim().isEmpty();
                boolean sinDescripcion = datos.descripcion == null || datos.descripcion.trim().isEmpty();

                if (sinCodigo || sinDescripcion) {
                    continue; // fila incompleta, se descarta
                }

                filas.add(datos);
            }
        }

        return filas;
    }

    private static String obtenerValor(Row fila, Integer indiceColumna) {

        if (indiceColumna == null || indiceColumna < 0) {
            return null;
        }

        return obtenerTexto(fila.getCell(indiceColumna));
    }

    private static String obtenerTexto(Cell celda) {

        if (celda == null) {
            return "";
        }

        switch (celda.getCellType()) {

            case STRING:
                return celda.getStringCellValue().trim();

            case NUMERIC:
                double valor = celda.getNumericCellValue();
                if (valor == Math.floor(valor)) {
                    return String.valueOf((long) valor);
                }
                return String.valueOf(valor);

            case BLANK:
                return "";

            default:
                return celda.toString().trim();
        }
    }

    private static int parsearEntero(String texto) {
        try {
            return (int) Double.parseDouble(texto.trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    private static double parsearDecimal(String texto) {
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public static class FilaImportada {
        public String codigo;
        public String descripcion;
        public String categoria;
        public String proveedor;
        public int stock;
        public double precioVenta;
        public double precioCompra;
    }
}