package Modelo;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

public class AbrirCajaEfectivo {
    public static void main(String[] args) {
        try {
            // Comando ESC/POS para abrir la caja de dinero
            byte[] openDrawerCommand = {27, 112, 0, (byte) 25, (byte) 250};

            // Buscar todas las impresoras disponibles
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

            if (services.length == 0) {
                System.out.println("⚠️ No hay impresoras instaladas.");
                return;
            }

            // Opción 1: usar la impresora por defecto del sistema
            PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();

            // Si hay impresora por defecto, usarla
            PrintService selectedPrinter = (defaultPrinter != null) ? defaultPrinter : services[0];

            // Crear el trabajo de impresión
            DocPrintJob job = selectedPrinter.createPrintJob();
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            Doc doc = new SimpleDoc(openDrawerCommand, flavor, null);

            // Enviar comando para abrir el cajón
            job.print(doc, null);
            System.out.println("✅ Caja de efectivo abierta en: " + selectedPrinter.getName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
