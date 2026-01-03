package Utilidades;

import java.awt.Desktop;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import javax.swing.JOptionPane;

/**
 * Clase utilitaria para generar un PDF informativo al registrar una empresa.
 * Genera el archivo en la carpeta Descargas del usuario y guarda una copia interna
 * en "archivos/registro/" dentro del proyecto.
 * 
 * Autor: vic
 */
public class generarPdfRegistro {

    /**
     * Genera un PDF visual con la información del registro de empresa.
     * @param nombreEmpresa Nombre de la empresa registrada.
     * @param idEmpresa ID asignado a la empresa.
     */
    public static void generarPdfRegistro(String nombreEmpresa, int idEmpresa) {
        try {
            // 📂 Carpeta Descargas del usuario
            String userHome = System.getProperty("user.home");
            File carpetaDescargas = new File(userHome + File.separator + "Downloads");
            if (!carpetaDescargas.exists()) carpetaDescargas.mkdirs();

            File archivoPdf = new File(carpetaDescargas, "RegistroEmpresa_" + idEmpresa + ".pdf");

            // 🧾 Crear documento PDF
            Document documento = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(documento, new FileOutputStream(archivoPdf));
            documento.open();

            // 🔷 Encabezado visual
            Paragraph encabezado = new Paragraph("Registro de Empresa Exitoso",
                    new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, new BaseColor(0, 102, 204)));
            encabezado.setAlignment(Element.ALIGN_CENTER);
            documento.add(encabezado);

            documento.add(new Paragraph(" "));
            LineSeparator linea = new LineSeparator();
            linea.setLineColor(new BaseColor(0, 102, 204));
            documento.add(linea);
            documento.add(new Paragraph(" "));

            // 🧾 Bloque informativo con fondo gris claro
            PdfPTable tablaInfo = new PdfPTable(1);
            tablaInfo.setWidthPercentage(100);

            PdfPCell celda = new PdfPCell();
            celda.setPadding(15);
            celda.setBackgroundColor(new BaseColor(245, 245, 245));

            Font textoNormal = new Font(Font.FontFamily.HELVETICA, 12);
            Font textoResaltado = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, new BaseColor(0, 102, 204));

            Paragraph contenido = new Paragraph();
            contenido.add(new Phrase("🎉 Felicidades, acabas de registrar tu empresa\n\n", textoResaltado));
            contenido.add(new Phrase("Nombre de la empresa: ", textoNormal));
            contenido.add(new Phrase(nombreEmpresa + "\n", textoResaltado));
            contenido.add(new Phrase("ID Empresa: ", textoNormal));
            contenido.add(new Phrase(String.valueOf(idEmpresa) + "\n\n", textoResaltado));
            contenido.add(new Phrase("Accede con las credenciales que creaste (correo y contraseña).\n", textoNormal));
            contenido.add(new Phrase("\nGuarda este documento para futuras referencias.", textoNormal));

            celda.addElement(contenido);
            tablaInfo.addCell(celda);
            documento.add(tablaInfo);

            // Espacio final
            documento.add(new Paragraph(" "));

            // 🩵 Pie de página
            Paragraph pie = new Paragraph("Gracias por usar VHAO System punto de venta.",
                    new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, new BaseColor(100, 100, 100)));
            pie.setAlignment(Element.ALIGN_CENTER);
            documento.add(pie);

            documento.close();

            // 🗂️ Guardar copia interna (archivos/registro)
            File carpetaInterna = new File("archivos/registro");
            if (!carpetaInterna.exists()) carpetaInterna.mkdirs();

            File copiaPdf = new File(carpetaInterna, "RegistroEmpresa_" + idEmpresa + ".pdf");
            Files.copy(archivoPdf.toPath(), copiaPdf.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Intentar abrir automáticamente el PDF
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivoPdf);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "📄 PDF generado en: " + archivoPdf.getAbsolutePath() + "\nNo se puede abrir automáticamente.",
                            "PDF Creado", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "📄 PDF generado en: " + archivoPdf.getAbsolutePath() + "\nNo se pudo abrir automáticamente.",
                        "PDF Creado", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar el PDF: " + e.getMessage(),
                    "Error PDF", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
