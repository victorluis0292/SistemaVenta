package Modelo;

import com.fazecast.jSerialComm.SerialPort;
import java.io.*;
import java.util.*;

public class ConfiguracionDispositivos {

    private static final String ARCHIVO = "config_dispositivos.properties";
    private static final List<Runnable> listenersBascula = new ArrayList<>();

    public static class ConfigBascula {
        public boolean habilitada;
        public String modelo;
        public String puerto;
    }

    public static ConfigBascula cargarConfigBascula() {
        Properties p = cargarProperties();
        ConfigBascula c = new ConfigBascula();
        c.habilitada = Boolean.parseBoolean(p.getProperty("bascula.habilitada", "false"));
        c.modelo = p.getProperty("bascula.modelo", "");
        c.puerto = p.getProperty("bascula.puerto", "");
        return c;
    }

    public static void guardarConfigBascula(boolean habilitada, String modelo, String puerto) {
        Properties p = cargarProperties();
        p.setProperty("bascula.habilitada", String.valueOf(habilitada));
        p.setProperty("bascula.modelo", modelo == null ? "" : modelo);
        p.setProperty("bascula.puerto", puerto == null ? "" : puerto);
        guardarProperties(p);
        notificarListenersBascula(); // 👈 avisa a quien esté escuchando

    }

    private static Properties cargarProperties() {
        Properties p = new Properties();
        File f = new File(ARCHIVO);
        if (f.exists()) {
            try (FileInputStream in = new FileInputStream(f)) {
                p.load(in);
            } catch (IOException e) {
                System.out.println("[CONFIG] Error leyendo " + ARCHIVO + ": " + e.getMessage());
            }
        }
        return p;
    }

    private static void guardarProperties(Properties p) {
        try (FileOutputStream out = new FileOutputStream(ARCHIVO)) {
            p.store(out, "Configuracion de dispositivos VHAO PDV");
        } catch (IOException e) {
            System.out.println("[CONFIG] Error guardando " + ARCHIVO + ": " + e.getMessage());
        }
    }

    /** Puertos seriales que el sistema detecta actualmente (COM1, COM2, etc.) */
    public static List<String> listarPuertosDisponibles() {
        List<String> nombres = new ArrayList<>();
        for (SerialPort sp : SerialPort.getCommPorts()) {
            nombres.add(sp.getSystemPortName());
        }
        return nombres;
    }

    /** Modelos de báscula soportados. Agrega aquí conforme sumes protocolos. */
    public static List<String> listarModelosBascula() {
        return Arrays.asList(
            "Moresco 7097",
            "Torrey L-EQ",
            "CAS PD-II",
            "Rhino BAR",
            "Genérica (peso continuo)"
        );
    }
    
    /** Se llama cada vez que se guarda la config de báscula, para notificar a quien esté escuchando (ej. VerduleriaController) */
public static void agregarListenerBascula(Runnable listener) {
    listenersBascula.add(listener);
}
public static void quitarListenerBascula(Runnable listener) {
    listenersBascula.remove(listener);
}

private static void notificarListenersBascula() {
    for (Runnable l : listenersBascula) {
        l.run();
    }

}
}