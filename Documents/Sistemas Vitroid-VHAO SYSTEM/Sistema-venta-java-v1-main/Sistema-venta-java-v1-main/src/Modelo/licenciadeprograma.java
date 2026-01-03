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
        try {
            File archivo = new File(FILE_NAME);
            Properties props = new Properties();

            // Si el archivo no existe, crearlo con la fecha de internet (si hay)
            if (!archivo.exists()) {
                LocalDate fechaInternet = obtenerFechaInternet();
                LocalDate fechaInicio = (fechaInternet != null) ? fechaInternet : LocalDate.now();

                props.setProperty("fechaInicio", fechaInicio.toString());
                props.setProperty("ultimaFechaUso", fechaInicio.toString());
                props.store(new FileOutputStream(archivo), null);
                return true;
            }

            // Cargar archivo de licencia
            props.load(new FileInputStream(archivo));
            LocalDate fechaInicio = LocalDate.parse(props.getProperty("fechaInicio"));
            LocalDate ultimaFechaUso = LocalDate.parse(props.getProperty("ultimaFechaUso"));
            LocalDate hoy = LocalDate.now();

            // Verificación de manipulación de fecha
            if (hoy.isBefore(ultimaFechaUso)) {
                System.out.println("⚠️ Fecha del sistema modificada.");
                return false;
            }

            long diasUsados = ChronoUnit.DAYS.between(fechaInicio, hoy);
            if (diasUsados > 30) {
                System.out.println("⛔ Licencia caducada.");
                return false;
            }

            // Actualiza última fecha de uso
            props.setProperty("ultimaFechaUso", hoy.toString());
            props.store(new FileOutputStream(archivo), null);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hayInternet() {
        try {
            URL url = new URL("http://www.google.com");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");
            con.setConnectTimeout(3000);
            con.connect();
            return (con.getResponseCode() >= 200 && con.getResponseCode() <= 399);
        } catch (Exception e) {
            return false;
        }
    }

    public static LocalDate obtenerFechaInternet() {
        try {
            URL url = new URL("https://worldtimeapi.org/api/timezone/America/Mexico_City");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder json = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();

            String datetime = json.toString().split("\"datetime\":\"")[1].split("\"")[0];
            return LocalDate.parse(datetime.substring(0, 10));

        } catch (Exception e) {
            return null; // Si falla, se usa fecha local
        }
    }

    public static long diasRestantes() {
        try {
            File archivo = new File(FILE_NAME);
            if (!archivo.exists()) return 30;
            Properties props = new Properties();
            props.load(new FileInputStream(archivo));
            LocalDate fechaInicio = LocalDate.parse(props.getProperty("fechaInicio"));
            long dias = ChronoUnit.DAYS.between(fechaInicio, LocalDate.now());
            return 30 - dias;
        } catch (Exception e) {
            return 0;
        }
    }
}