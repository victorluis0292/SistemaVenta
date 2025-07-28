package Modelo;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

public class PdfGeneratorCredito {

public static void generarPdfCredito(String nombreCliente, double totalPagar, TableModel modeloTabla, JFrame parent) {
    if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
        JOptionPane.showMessageDialog(parent, "Nombre del cliente no válido para generar PDF.");
        return;
    }
    if (modeloTabla.getRowCount() == 0) {
        JOptionPane.showMessageDialog(parent, "No hay datos para generar el PDF.");
        return;
    }

    try {
        // Nombre base para archivo
        String clienteArchivo = nombreCliente.replaceAll(" ", "_");
        String nombreBase = "Credito_" + clienteArchivo;
        String extension = ".pdf";

        // Carpeta Descargas
        String home = System.getProperty("user.home");
        String carpetaDescargas = home + (System.getProperty("os.name").toLowerCase().contains("windows") ? "\\Downloads" : "/Downloads");
        File dirDescargas = new File(carpetaDescargas);
        if (!dirDescargas.exists()) dirDescargas.mkdirs();

        // Configurar JFileChooser
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar PDF de crédito");
        chooser.setCurrentDirectory(dirDescargas);

        // Archivo predeterminado sin sufijo
        File archivoPredeterminado = new File(dirDescargas, nombreBase + extension);
        chooser.setSelectedFile(archivoPredeterminado);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivo PDF", "pdf"));

        int userSelection = chooser.showSaveDialog(parent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return; // usuario canceló
        }

        File archivoSeleccionado = chooser.getSelectedFile();

        // Añadir extensión si falta
        String pathArchivo = archivoSeleccionado.getAbsolutePath();
        if (!pathArchivo.toLowerCase().endsWith(".pdf")) {
            pathArchivo += ".pdf";
            archivoSeleccionado = new File(pathArchivo);
        }

        // Verificar existencia y añadir sufijo numérico para no sobreescribir
        File archivoFinal = archivoSeleccionado;
        int contador = 1;
        while (archivoFinal.exists()) {
            archivoFinal = new File(archivoSeleccionado.getParent(),
                    nombreBase + contador + extension);
            contador++;
        }

        Document documento = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(archivoFinal));
        writer.setPageEvent(new PiePagina());

        documento.open();

        // Título
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("DETALLE DE CRÉDITO", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);
        documento.add(new Paragraph(" "));

        // Info cliente con zona fija UTC-6 sin horario verano
        Font fontInfo = new Font(Font.FontFamily.HELVETICA, 11);
        ZoneId zonaSinHorarioVerano = ZoneId.of("Etc/GMT+6");
        ZonedDateTime ahora = ZonedDateTime.now(zonaSinHorarioVerano);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        Paragraph datosCliente = new Paragraph();
        datosCliente.setLeading(13f);
        datosCliente.add(new Phrase("Cliente: " + nombreCliente + "\n", fontInfo));
        datosCliente.add(new Phrase("Total a pagar: $" + new DecimalFormat("0.00").format(totalPagar) + "\n", fontInfo));
        datosCliente.add(new Phrase("Fecha: " + ahora.format(formatter), fontInfo));
        documento.add(datosCliente);

        documento.add(new Paragraph(" "));

        // Tabla principal
        int columnas = modeloTabla.getColumnCount();
        int columnasVisibles = 0;
        for (int i = 0; i < columnas; i++) {
            String colName = modeloTabla.getColumnName(i);
            if (!colName.equalsIgnoreCase("Eliminar") &&
                !colName.equalsIgnoreCase("ID") &&
                !colName.equalsIgnoreCase("ID Prod")) {
                columnasVisibles++;
            }
        }

        PdfPTable tabla = new PdfPTable(columnasVisibles);
        tabla.setWidthPercentage(100);

        // Encabezados
        for (int i = 0; i < columnas; i++) {
            String encabezado = modeloTabla.getColumnName(i);
            if (encabezado.equalsIgnoreCase("Eliminar") ||
                encabezado.equalsIgnoreCase("ID") ||
                encabezado.equalsIgnoreCase("ID Prod")) continue;

            if (encabezado.equalsIgnoreCase("Nombre")) encabezado = "Descripción";
            if (encabezado.equalsIgnoreCase("Cantidad")) encabezado = "Cant.";
            if (encabezado.equalsIgnoreCase("Precio")) encabezado = "Precio Unit.";
            if (encabezado.equalsIgnoreCase("Total")) encabezado = "Subtotal";

            PdfPCell celda = new PdfPCell(new Phrase(encabezado));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setBackgroundColor(new BaseColor(230, 230, 230));
            celda.setPadding(5);
            tabla.addCell(celda);
        }

        // Filas de datos
        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            boolean esAbono = false;
            Object nombre = null;

            // Buscar columna "Nombre" para detectar "ABONO"
            for (int c = 0; c < columnas; c++) {
                if (modeloTabla.getColumnName(c).equalsIgnoreCase("Nombre")) {
                    nombre = modeloTabla.getValueAt(fila, c);
                    break;
                }
            }
            if (nombre != null && nombre.toString().toUpperCase().contains("ABONO")) {
                esAbono = true;
            }

            for (int col = 0; col < columnas; col++) {
                String encabezado = modeloTabla.getColumnName(col);
                if (encabezado.equalsIgnoreCase("Eliminar") ||
                    encabezado.equalsIgnoreCase("ID") ||
                    encabezado.equalsIgnoreCase("ID Prod")) continue;

                Object valor = modeloTabla.getValueAt(fila, col);

                if (esAbono && encabezado.equalsIgnoreCase("Cantidad")) {
                    valor = "1";
                }

                PdfPCell celda = new PdfPCell(new Phrase(valor != null ? valor.toString() : ""));
                celda.setPadding(5);
                celda.setHorizontalAlignment(esNumero(valor) ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);

                if (esAbono) {
                    celda.setBackgroundColor(new BaseColor(255, 245, 225));
                    Font fontBold = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLDITALIC);
                    celda.setPhrase(new Phrase(valor != null ? valor.toString() : "", fontBold));
                }

                tabla.addCell(celda);
            }
        }

        documento.add(tabla);

        // Saldo total
        documento.add(new Paragraph(" "));
        Paragraph resumen = new Paragraph("Saldo pendiente: $" + new DecimalFormat("0.00").format(totalPagar));
        resumen.setAlignment(Element.ALIGN_RIGHT);
        resumen.setSpacingBefore(10);
        documento.add(resumen);

        documento.close();

        JOptionPane.showMessageDialog(parent, "PDF generado correctamente en:\n" + archivoFinal.getAbsolutePath());

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(parent, "Error al generar PDF: " + e.getMessage());
    }
}

private static boolean esNumero(Object valor) {
    if (valor == null) return false;
    try {
        Double.parseDouble(valor.toString());
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
}



    private static class PiePagina extends PdfPageEventHelper {
        Font pieFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase pie = new Phrase("Página " + writer.getPageNumber(), pieFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    pie,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }
}
