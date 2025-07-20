package Modelo;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

public class licenciadeprograma {
    private static final String FILE_NAME = System.getProperty("user.home") + "/.licenciaaixa.dat";

    public static boolean licenciaValida() {
        File archivo = new File(FILE_NAME);
        Properties props = new Properties();

        try {
            // Si el archivo no existe, crearlo con la fecha de internet (si hay)
            if (!archivo.exists()) {
                LocalDate fechaInternet = obtenerFechaInternet();
                LocalDate fechaInicio = (fechaInternet != null) ? fechaInternet : LocalDate.now();

                props.setProperty("fechaInicio", fechaInicio.toString());
                props.setProperty("ultimaFechaUso", fechaInicio.toString());
                try (FileOutputStream fos = new FileOutputStream(archivo)) {
                    props.store(fos, null);
                }
                return true;
            }

            // Cargar archivo de licencia con try-with-resources
            try (FileInputStream fis = new FileInputStream(archivo)) {
                props.load(fis);
            }

            LocalDate fechaInicio = LocalDate.parse(props.getProperty("fechaInicio"));
            LocalDate ultimaFechaUso = LocalDate.parse(props.getProperty("ultimaFechaUso"));
            LocalDate hoy = LocalDate.now();

            // Verificación de manipulación de fecha
            if (hoy.isBefore(ultimaFechaUso)) {
                System.out.println("⚠️ Fecha del sistema modificada.");
                return false;
            }

            long diasUsados = ChronoUnit.DAYS.between(fechaInicio, hoy);
            if (diasUsados > 60) {
                System.out.println("⛔ Licencia caducada.");
                return false;
            }

            // Actualiza última fecha de uso
            props.setProperty("ultimaFechaUso", hoy.toString());
            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                props.store(fos, null);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hayInternet() {
        HttpURLConnection con = null;
        try {
            URL url = new URL("http://www.google.com");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");
            con.setConnectTimeout(3000);
            con.connect();
            int code = con.getResponseCode();
            return (code >= 200 && code <= 399);
        } catch (Exception e) {
            return false;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public static LocalDate obtenerFechaInternet() {
        HttpURLConnection con = null;
        try {
            URL url = new URL("https://worldtimeapi.org/api/timezone/America/Mexico_City");
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder json = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    json.append(inputLine);
                }
                String datetime = json.toString().split("\"datetime\":\"")[1].split("\"")[0];
                return LocalDate.parse(datetime.substring(0, 10));
            }

        } catch (Exception e) {
            return null; // Si falla, se usa fecha local
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    public static long diasRestantes() {
        File archivo = new File(FILE_NAME);
        if (!archivo.exists()) return 60;
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(archivo)) {
            props.load(fis);
            LocalDate fechaInicio = LocalDate.parse(props.getProperty("fechaInicio"));
            long dias = ChronoUnit.DAYS.between(fechaInicio, LocalDate.now());
            return 60 - dias;
        } catch (Exception e) {
            return 0;
        }
    }
}
