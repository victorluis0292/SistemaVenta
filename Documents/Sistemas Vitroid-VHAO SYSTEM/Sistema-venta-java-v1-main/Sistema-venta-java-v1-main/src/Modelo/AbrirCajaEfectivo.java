/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;

/**
 *
 * @author vic
 */
public class AbrirCajaEfectivo {
     public static void main(String[] args) {
        try {
            // Nombre exacto de la impresora térmica en el sistema
            String printerName = "POS-58";  

            // Comando ESC/POS para abrir la caja de dinero (con casting explícito)
            byte[] openDrawerCommand = {27, 112, 0, (byte) 25, (byte) 250};

            // Buscar la impresora en el sistema
            PrintService selectedPrinter = null;
            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

            for (PrintService service : services) {
                if (service.getName().equalsIgnoreCase(printerName)) {
                    selectedPrinter = service;
                    break;
                }
            }

            if (selectedPrinter != null) {
                // Crear el trabajo de impresión para la impresora térmica
                DocPrintJob job = selectedPrinter.createPrintJob();
                DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
                Doc doc = new SimpleDoc(openDrawerCommand, flavor, null);
                
                // Enviar comando para abrir el cajón
                job.print(doc, null);
                System.out.println("✅ ¡Caja de efectivo abierta!");
            } else {
                System.out.println("⚠️ Impresora no encontrada.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
