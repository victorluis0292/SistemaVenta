package Modelo;

import java.io.*;
import java.time.*;
import java.util.Properties;

public class licenciadeprograma {
    private static final String ARCHIVO_LICENCIA = "licencia.properties";

    public static boolean licenciaValida() {
        File archivo = new File(ARCHIVO_LICENCIA);
        LocalDate hoy = LocalDate.now();

        try {
            if (!archivo.exists()) {
                Properties props = new Properties();
                props.setProperty("fechaInicio", hoy.toString());
                try (FileOutputStream out = new FileOutputStream(archivo)) {
                    props.store(out, "Fecha de inicio de la licencia");
                }
                return true;
            }

            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(archivo)) {
                props.load(in);
            }

            LocalDate fechaInicio = LocalDate.parse(props.getProperty("fechaInicio"));
            LocalDate fechaExpiracion = fechaInicio.plusMonths(1);

            return !hoy.isAfter(fechaExpiracion);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}