package Utilidades;

import Modelo.Productos;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
public class ExportadorExcel {

    private static final String[] ENCABEZADOS = {
            "ID", "Código", "Descripción", "Categoría",
            "Proveedor", "Stock", "Precio Venta", "Precio Compra"
    };

    public static void exportarProductos(Component parent, List<Productos> productos) {

        if (productos == null || productos.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No hay productos para exportar.",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte de productos");
        File carpetaDescargas = new File(System.getProperty("user.home"), "Downloads");
        if (carpetaDescargas.exists()) {
    selector.setCurrentDirectory(carpetaDescargas);
        }

        selector.setSelectedFile(new File("Productos.xlsx"));

        if (selector.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".xlsx")) {
            archivo = new File(archivo.getAbsolutePath() + ".xlsx");
        }

        try (XSSFWorkbook libro = new XSSFWorkbook()) {

            Sheet hoja = libro.createSheet("Productos");
            CellStyle estiloEncabezado = crearEstiloEncabezado(libro);

            Row filaEncabezado = hoja.createRow(0);
            for (int i = 0; i < ENCABEZADOS.length; i++) {
                Cell celda = filaEncabezado.createCell(i);
                celda.setCellValue(ENCABEZADOS[i]);
                celda.setCellStyle(estiloEncabezado);
            }

            int numFila = 1;
            for (Productos p : productos) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(p.getId());
                fila.createCell(1).setCellValue(p.getCodigo());
                fila.createCell(2).setCellValue(p.getNombre());
                fila.createCell(3).setCellValue(p.getCategoria());
                fila.createCell(4).setCellValue(p.getProveedorPro());
                fila.createCell(5).setCellValue(p.getStock());
                fila.createCell(6).setCellValue(p.getPrecio());
                fila.createCell(7).setCellValue(p.getPreciocompra());
            }

            for (int i = 0; i < ENCABEZADOS.length; i++) {
                hoja.autoSizeColumn(i);
            }

            try (FileOutputStream salida = new FileOutputStream(archivo)) {
                libro.write(salida);
            }

            JOptionPane.showMessageDialog(parent,
                    "Reporte exportado correctamente:\n" + archivo.getAbsolutePath(),
                    "Exportación completa",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent,
                    "Error al exportar a Excel:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static CellStyle crearEstiloEncabezado(Workbook libro) {
    CellStyle estilo = libro.createCellStyle();
    org.apache.poi.ss.usermodel.Font fuente = libro.createFont();
    fuente.setBold(true);
    fuente.setColor(IndexedColors.WHITE.getIndex());
    estilo.setFont(fuente);
    estilo.setFillForegroundColor(IndexedColors.BLUE.getIndex());
    estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    return estilo;
}
}