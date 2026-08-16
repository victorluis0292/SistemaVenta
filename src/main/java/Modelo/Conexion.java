package Modelo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Conexion {

    private static HikariDataSource dataSource;
    private static boolean inicializado = false;

    // 🔹 Entorno por defecto
    private static String environment = "local";

    // 🔹 Cambiar el entorno dinámicamente
    public static void setEnvironment(String env) {
        environment = env;
    }

    public static String getEnvironment() {
        return environment;
    }

    // 🔹 Inicializa el pool HikariCP
    private static void inicializarPool() {
        if (dataSource != null) return;

        synchronized (Conexion.class) {
            if (dataSource != null) return;

            try {
                String file = environment.equalsIgnoreCase("local")
                        ? "config/config.prod.properties"
                        : "config/config.local.properties";

                try (InputStream input = Conexion.class.getClassLoader().getResourceAsStream(file)) {
                    if (input == null)
                        throw new IOException("Archivo de configuración no encontrado: " + file);

                    Properties props = new Properties();
                    props.load(input);

                    HikariConfig config = new HikariConfig();
                    config.setJdbcUrl(props.getProperty("jdbc.url"));
                    config.setUsername(props.getProperty("db.user"));
                    config.setPassword(props.getProperty("db.password"));

                    // Opciones recomendadas
                    config.setMaximumPoolSize(10);
                    config.setMinimumIdle(2);
                    config.setIdleTimeout(600_000);          // 10 min
                    config.setConnectionTimeout(30_000);     // 30 seg
                    config.setMaxLifetime(1_800_000);       // 30 min
                    config.setValidationTimeout(5_000);     // 5 seg
                    config.setConnectionTestQuery("SELECT 1");

                    dataSource = new HikariDataSource(config);
                    inicializado = true;
                    logInfo("Pool HikariCP inicializado con entorno: " + environment);
                }

            } catch (Exception e) {
                logError("Error al inicializar pool", e);
            }
        }
    }

    // 🔹 Obtiene una conexión
    public static Connection getConnection() throws SQLException {
        inicializarPool();
        if (dataSource == null)
            throw new SQLException("Pool no inicializado.");
        return dataSource.getConnection();
    }

    // 🔹 Verifica conexión
    public static boolean hayConexion() {
        try {
            inicializarPool();
            try (Connection con = dataSource.getConnection()) {
                return con != null && !con.isClosed();
            }
        } catch (SQLException e) {
            logError("Sin conexión", e);
            return false;
        }
    }

    // 🔹 Cierra el pool
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logInfo("Pool cerrado correctamente.");
        }
    }

    // 🔹 Logging simple
    public static void logInfo(String mensaje) {
        writeLog("INFO", mensaje);
    }
public static boolean hayInternetRapido() {
    try {
        java.net.URL url = new java.net.URL("https://www.google.com");
        java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
        con.setConnectTimeout(2000); // 2 segundos
        con.connect();
        return con.getResponseCode() == 200;
    } catch (Exception e) {
        return false;
    }
}
    public static void logError(String mensaje, Exception e) {
        writeLog("ERROR", mensaje + " - " + (e != null ? e.getMessage() : ""));
    }

    private static void writeLog(String tipo, String mensaje) {
        try {
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }

            try (FileWriter fw = new FileWriter("logs/conexion_log.txt", true);
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.println(LocalDateTime.now() + " - " + tipo + " - " + mensaje);
            }

        } catch (IOException ex) {
            System.err.println("No se pudo escribir en el log: " + ex.getMessage());
        }
    }

    // 🔹 Método de utilidad para saber si es ambiente local
    public static boolean esLocal() {
        return environment.equalsIgnoreCase("local");
    }
}
