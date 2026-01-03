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
        if (!archivo.exists()) {
            LocalDate fechaInternet = obtenerFechaInternet();
            LocalDate fechaInicio = (fechaInternet != null) ? fechaInternet : LocalDate.now();

            props.setProperty("fechaInicio", fechaInicio.toString());
            props.setProperty("ultimaFechaUso", fechaInicio.toString());
            props.setProperty("activada", "false");  // <-- nueva propiedad
            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                props.store(fos, null);
            }
            return true;
        }

        try (FileInputStream fis = new FileInputStream(archivo)) {
            props.load(fis);
        }

        // Revisar si ya está activada
        if ("true".equalsIgnoreCase(props.getProperty("activada", "false"))) {
            return true; // Licencia activada, siempre válida
        }

        LocalDate fechaInicio = LocalDate.parse(props.getProperty("fechaInicio"));
        LocalDate ultimaFechaUso = LocalDate.parse(props.getProperty("ultimaFechaUso"));
        LocalDate hoy = LocalDate.now();

        if (hoy.isBefore(ultimaFechaUso)) {
            System.out.println("⚠️ Fecha del sistema modificada.");
            return false;
        }

        long diasUsados = ChronoUnit.DAYS.between(fechaInicio, hoy);
        if (diasUsados > 30) {
            System.out.println("⛔ Licencia caducada.");
            return false;
        }

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

   public static void activarLicencia() {
    File archivo = new File(FILE_NAME);
    Properties props = new Properties();

    try {
        if (!archivo.exists()) {
            System.out.println("No existe el archivo de licencia para activar.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(archivo)) {
            props.load(fis);
        }

        props.setProperty("activada", "true");

        try (FileOutputStream fos = new FileOutputStream(archivo)) {
            props.store(fos, null);
        }

        System.out.println("Licencia activada permanentemente.");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public static boolean estaActivada() {
    File archivo = new File(FILE_NAME);
    Properties props = new Properties();

    if (!archivo.exists()) return false;

    try (FileInputStream fis = new FileInputStream(archivo)) {
        props.load(fis);
        return "true".equalsIgnoreCase(props.getProperty("activada", "false"));
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
        if (!archivo.exists()) return 30;
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(archivo)) {
            props.load(fis);
            LocalDate fechaInicio = LocalDate.parse(props.getProperty("fechaInicio"));
            long dias = ChronoUnit.DAYS.between(fechaInicio, LocalDate.now());
            return 30 - dias;
        } catch (Exception e) {
            return 0;
        }
    }
}
