package Modelo;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BasculaListener {

    private SerialPort puerto;
    private final String nombrePuerto;
    private final Consumer<Double> onPesoRecibido;
    private final Consumer<Boolean> onEstadoConexion; // opcional: para avisar a la UI si conectó o no

    private volatile boolean escuchando = false;
    private Thread hiloReintento;

    private static final Pattern PATRON_PESO = Pattern.compile("([+-]?\\d+\\.\\d+)");

    public BasculaListener(String nombrePuerto, Consumer<Double> onPesoRecibido) {
        this(nombrePuerto, onPesoRecibido, null);
    }

    public BasculaListener(String nombrePuerto, Consumer<Double> onPesoRecibido, Consumer<Boolean> onEstadoConexion) {
        this.nombrePuerto = nombrePuerto;
        this.onPesoRecibido = onPesoRecibido;
        this.onEstadoConexion = onEstadoConexion;
    }

    /**
     * Intento único (comportamiento anterior). Lo dejamos por si lo usas en otro lado,
     * pero para el arranque normal usa iniciarConReintento().
     */
    public boolean conectar() {
        // 🔑 Forzamos refresco de la lista nativa de puertos antes de abrir.
        // Esto evita que jSerialComm se quede con una lista "vieja" si el
        // puerto virtual de VSPE apareció después de que la JVM arrancó.
        SerialPort[] puertosDisponibles = SerialPort.getCommPorts();
        SerialPort candidato = null;
        for (SerialPort p : puertosDisponibles) {
            if (p.getSystemPortName().equalsIgnoreCase(nombrePuerto)) {
                candidato = p;
                break;
            }
        }
        // Si no aparece en la lista refrescada, igual intentamos con getCommPort
        // directo por si el driver no lo reporta en la enumeración pero sí existe.
        this.puerto = (candidato != null) ? candidato : SerialPort.getCommPort(nombrePuerto);

        puerto.setBaudRate(9600);
        puerto.setNumDataBits(8);
        puerto.setNumStopBits(SerialPort.ONE_STOP_BIT);
        puerto.setParity(SerialPort.NO_PARITY);

        boolean ok = puerto.openPort();
        System.out.println("[BASCULA] Intento de conexión en " + nombrePuerto + " -> " + (ok ? "OK" : "FALLÓ"));
        return ok;
    }

    /**
     * Conexión en background con reintentos automáticos. No bloquea la UI.
     * Llama esto en vez de conectar() + iniciarEscucha() directo.
     *
     * @param intervaloMs      cada cuánto reintenta (ej. 2500)
     * @param maxIntentos      -1 para reintentar indefinidamente, o un número fijo
     */
    public void iniciarConReintento(long intervaloMs, int maxIntentos) {
        if (hiloReintento != null && hiloReintento.isAlive()) {
            return; // ya hay un intento en curso
        }

        hiloReintento = new Thread(() -> {
            int intentos = 0;
            while (!escuchando && (maxIntentos < 0 || intentos < maxIntentos)) {
                intentos++;
                boolean ok = conectar();
                if (ok) {
                    escuchando = true;
                    iniciarEscucha();
                    if (onEstadoConexion != null) onEstadoConexion.accept(true);
                    System.out.println("[BASCULA] Conectada en " + nombrePuerto + " tras " + intentos + " intento(s).");
                    return;
                }
                try {
                    Thread.sleep(intervaloMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            if (!escuchando && onEstadoConexion != null) {
                onEstadoConexion.accept(false);
            }
        }, "BasculaReintentoThread");

        hiloReintento.setDaemon(true);
        hiloReintento.start();
    }

    public void desconectar() {
        escuchando = false;
        if (hiloReintento != null) {
            hiloReintento.interrupt();
        }
        if (puerto != null && puerto.isOpen()) {
            puerto.closePort();
        }
    }

    public boolean isConectada() {
        return puerto != null && puerto.isOpen();
    }

    public void iniciarEscucha() {
        puerto.addDataListener(new com.fazecast.jSerialComm.SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return com.fazecast.jSerialComm.SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(com.fazecast.jSerialComm.SerialPortEvent event) {
                try {
                    InputStream in = puerto.getInputStream();
                    byte[] buffer = new byte[64];
                    int leidos = in.read(buffer);
                    if (leidos > 0) {
                        String texto = new String(buffer, 0, leidos).trim();
                        System.out.println("[BASCULA] Recibido crudo: " + texto);
                        Double peso = parsearPeso(texto);
                        if (peso != null && onPesoRecibido != null) {
                            onPesoRecibido.accept(peso);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[BASCULA] Error leyendo puerto: " + e.getMessage());
                    // Si se cae la conexión (ej. desconectaste la báscula), marcamos
                    // como no conectada para que un futuro reintento la retome.
                    escuchando = false;
                    if (onEstadoConexion != null) onEstadoConexion.accept(false);
                }
            }
        });
    }

    private Double parsearPeso(String textoCrudo) {
        Matcher m = PATRON_PESO.matcher(textoCrudo);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}