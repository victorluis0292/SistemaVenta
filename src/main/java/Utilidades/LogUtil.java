package Utilidades;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtil {

    private static final String CARPETA_LOGS = "logs";
    private static final String ARCHIVO_LOG = "vhao-pdv.log";

    public static void error(String contexto, Throwable ex) {

        escribir("ERROR", contexto, ex);
    }

    public static void info(String mensaje) {

        escribir("INFO", mensaje, null);
    }

    private static synchronized void escribir(String nivel, String mensaje, Throwable ex) {

        try {

            File carpeta = new File(CARPETA_LOGS);

            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            File archivo = new File(carpeta, ARCHIVO_LOG);

            try (PrintWriter pw = new PrintWriter(new FileWriter(archivo, true))) {

                String marcaTiempo = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                pw.println("[" + marcaTiempo + "] [" + nivel + "] " + mensaje);

                if (ex != null) {

                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    pw.println(sw.toString());
                }

                pw.println("----------------------------------------");
            }

        } catch (Exception loggingEx) {

            // Si el log falla, no queremos tumbar la app por eso
            loggingEx.printStackTrace();
        }
    }
}