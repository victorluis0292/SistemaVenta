package Utilidades;

import javax.swing.*;
import java.io.*;
import java.net.*;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class VerificadorActualizacion {

    // 📁 URL al archivo version.json
    private static final String URL_VERSION_JSON =
        "https://www.dropbox.com/scl/fi/4jl4e1pkzom7d9bacd4dd/version.json?rlkey=m39p6m87d7lmyb7ga6bkyzw25&st=nw4s5kd0&dl=1";

public static void verificar() {
    new Thread(() -> {   // 🔥 Evita congelar Swing
        System.out.println("🚀 Iniciando verificación de actualización...");

        try {
            String contenido = leerDesdeUrl(URL_VERSION_JSON);
            JSONObject json = new JSONObject(contenido);

            String versionRemota = json.getString("version");
            String notas = json.optString("notas", "Sin notas disponibles");
            String urlDescarga = json.getString("url");

            String versionLocal = ConfigApp.getVersion();

            // Si no hay versión local guardada
            if (versionLocal == null || versionLocal.isEmpty()) {
                System.out.println("📦 Primera ejecución. Guardando versión local = remota");
                ConfigApp.guardarVersion(versionRemota);
                return;
            }

            int cmp = compararVersiones(versionRemota, versionLocal);

            // 🔥 Si la versión remota es menor → ignorar actualización
            if (cmp < 0) {
                System.out.println("⚠ La versión remota (" + versionRemota +
                                   ") es menor que la local (" + versionLocal +
                                   "). Se ignora.");
                return;
            }

            // Si son iguales
            if (cmp == 0) {
                System.out.println("✅ Ya tienes la versión más reciente.");
                return;
            }

            // 🔥 Si la remota es mayor, preguntar
            SwingUtilities.invokeLater(() -> {
                int opcion = JOptionPane.showConfirmDialog(null,
                    "Hay una nueva versión disponible (" + versionRemota + ").\n\n" +
                    "Notas: " + notas + "\n\n¿Deseas descargarla ahora?",
                    "Actualización disponible",
                    JOptionPane.YES_NO_OPTION
                );

                if (opcion == JOptionPane.YES_OPTION) {
                    descargarYReemplazar(urlDescarga, versionRemota);
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Error al verificar actualización:");
            e.printStackTrace();
        }

    }).start();  // <-- FIN DEL THREAD
}

    private static String leerDesdeUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(15000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        if (conn.getResponseCode() != 200) {
            throw new IOException("Respuesta no válida del servidor: " + conn.getResponseCode());
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            return sb.toString();
        }
    }

    private static void descargarYReemplazar(String urlDescarga, String versionRemota) {
        LoaderUpdateSystemPDV loader = new LoaderUpdateSystemPDV(null, "Descargando actualización...");
        loader.mostrar();

        new Thread(() -> {
            try {
                String nombreArchivo = "VHAO PDV Online " + versionRemota + ".exe";
                File destino = new File(nombreArchivo);

                // 🔹 Eliminar cualquier versión anterior automáticamente
                File carpeta = new File(".");
                File[] archivos = carpeta.listFiles((dir, name) -> 
                        (name.startsWith("VHAO PDV Online") || name.startsWith("VHAO.PDV-")) && name.endsWith(".exe"));
                if (archivos != null) {
                    for (File f : archivos) {
                        if (f.delete()) {
                            System.out.println("🗑 Archivo antiguo eliminado: " + f.getName());
                        } else {
                            System.out.println("⚠ No se pudo eliminar: " + f.getName());
                        }
                    }
                }

                // Descargar archivo nuevo
                URL url = new URL(urlDescarga);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(20000);

                if (conn.getContentType().contains("text/html")) {
                    throw new IOException("Dropbox devolvió HTML en lugar del archivo .exe. Asegúrate de usar '?dl=1'.");
                }

                try (InputStream in = conn.getInputStream();
                     FileOutputStream out = new FileOutputStream(destino)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long total = 0;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        total += bytesRead;
                    }

                    System.out.println("✅ Descarga completada (" + total / 1024 + " KB)");
                }

                // 🔹 Guardar la nueva versión en ConfigApp
                ConfigApp.guardarVersion(versionRemota);

                // Ejecutar instalador automáticamente
Runtime.getRuntime().exec("java -jar Updater1.jar \"" + nombreArchivo + "\"");
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                        "✅ Nueva versión descargada y ejecutada.\nArchivo: " + nombreArchivo,
                        "Actualización completada", JOptionPane.INFORMATION_MESSAGE));

                // Cerrar la app actual
                System.exit(0);

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                        "❌ Error durante la descarga: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
                e.printStackTrace();
            } finally {
                loader.cerrar();
            }
        }).start();
    }

    /**
     * 🔹 Compara dos versiones tipo "1.2.3"
     * Devuelve:
     *   >0 si v1 > v2
     *    0 si son iguales
     *   <0 si v1 < v2
     */
    private static int compararVersiones(String v1, String v2) {
        String[] partes1 = v1.split("\\.");
        String[] partes2 = v2.split("\\.");
        int longitud = Math.max(partes1.length, partes2.length);

        for (int i = 0; i < longitud; i++) {
            int p1 = i < partes1.length ? Integer.parseInt(partes1[i]) : 0;
            int p2 = i < partes2.length ? Integer.parseInt(partes2[i]) : 0;
            if (p1 != p2) return p1 - p2;
        }
        return 0;
    }
}
