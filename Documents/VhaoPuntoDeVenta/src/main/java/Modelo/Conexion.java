package Modelo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Timer;
import java.util.TimerTask;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Conexion {

    private static HikariDataSource dataSource;

 static {
    try {
        Properties props = new Properties();
     InputStream input = Conexion.class.getClassLoader().getResourceAsStream("config/config.local.properties");

if (input == null) {
    throw new RuntimeException("No se encontró el archivo config/config.local.properties en el classpath.");
}
props.load(input);

        String jdbcUrl = props.getProperty("jdbc.url");
        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");

        HikariConfig config = new HikariConfig(); // Solo esta vez
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
//
        // --- CONFIGURACIÓN DEL POOL ---
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(600000);            // 10 minutos
        config.setConnectionTimeout(30000);       // 30 segundos
        config.setMaxLifetime(1800000);           // 30 minutos

        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);

        dataSource = new HikariDataSource(config);

        // Iniciar KeepAlive
        iniciarKeepAlive();

    } catch (Exception e) {
        logError("Error al inicializar pool de conexiones: " + e.getMessage());
        throw new RuntimeException("Error al inicializar pool", e);
    }
}

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private static void iniciarKeepAlive() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try (Connection con = getConnection();
                     PreparedStatement ps = con.prepareStatement("SELECT 1")) {
                    ps.execute();
                    System.out.println("[KeepAlive] SELECT 1 ejecutado");
                } catch (SQLException e) {
                    String mensaje = "[KeepAlive] Error: " + e.getMessage();
                    System.err.println(mensaje);
                    logError(mensaje);
                }
            }
        }, 0, 5 * 60 * 1000); // Cada 5 minutos
    }

    // Registro de errores en archivo
    
    private static void logError(String mensaje) {
    try {
        // Crear carpeta "logs" si no existe
        java.nio.file.Path logDir = java.nio.file.Paths.get("logs");
        if (!java.nio.file.Files.exists(logDir)) {
            java.nio.file.Files.createDirectories(logDir);
        }

        // Ruta completa del archivo de log
        String rutaLog = "logs/conexion_log.txt";

        try (FileWriter fw = new FileWriter(rutaLog, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(LocalDateTime.now() + " - " + mensaje);
        }

    } catch (IOException ex) {
        System.err.println("No se pudo escribir en el log: " + ex.getMessage());
    }
}
 // Método para probar la conexión a la base de datos
    public static void testConexion() {
        try (Connection con = getConnection()) {
            if (con != null && !con.isClosed()) {
                System.out.println("Conexión exitosa a la base de datos.");
            } else {
                System.out.println("No se pudo conectar a la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    // Método main para ejecutar la prueba
    public static void main(String[] args) {
        testConexion();
        // Cerrar datasource al terminar (opcional)
        closeDataSource();
    }
}
