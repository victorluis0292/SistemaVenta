package LicenciasLocales;

import Modelo.Conexion;
import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.util.Date;

public class LicenciaValidadorLocalSerie extends JFrame {

    private static final String LIC_FOLDER = System.getProperty("user.home") + File.separator + ".miSistemaLicencia";
    private static final String LIC_FILE = LIC_FOLDER + File.separator + "licencia.dat";

    // =============================
    // OBTENER SERIAL DEL BIOS
    // =============================
    public static String obtenerSerialBIOS() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "bios", "get", "serialnumber"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.equalsIgnoreCase("SerialNumber")) {
                    return linea;
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] al obtener Serial BIOS: " + e.getMessage());
        }
        return "SERIAL_NO_DISPONIBLE";
    }

    // =============================
    // OBTENER UUID DEL EQUIPO
    // =============================
    public static String obtenerUUID() {
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "csproduct", "get", "uuid"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String linea;
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.equalsIgnoreCase("UUID")) {
                    return linea;
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] al obtener UUID: " + e.getMessage());
        }
        return "UUID_NO_DISPONIBLE";
    }

    // =============================
    // PEDIR NOMBRE Y ID EMPRESA
    // =============================
    public static String pedirNombreNegocio() {
        String nombre;
        do {
            nombre = JOptionPane.showInputDialog(null,
                    "Ingresa el nombre del negocio para activación:",
                    "Nombre del negocio", JOptionPane.QUESTION_MESSAGE);

            if (nombre == null) return null;

            nombre = nombre.trim();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (nombre.isEmpty());

        return nombre;
    }

    public static int pedirIdEmpresa() {
        int idEmpresa = -1;
        boolean valido = false;

        while (!valido) {
            String input = JOptionPane.showInputDialog(null,
                    "Ingresa el ID de la empresa asociada:",
                    "ID Empresa", JOptionPane.QUESTION_MESSAGE);

            if (input == null) return -1; // Cancelado

            try {
                idEmpresa = Integer.parseInt(input.trim());
                if (idEmpresa > 0) {
                    valido = true;
                } else {
                    JOptionPane.showMessageDialog(null, "El ID debe ser un número positivo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingresa un número válido para el ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        return idEmpresa;
    }

    // =============================
    // GENERAR ARCHIVO Y REGISTRAR EN BD
    // =============================
    public static void generarArchivoYRegistrar(String nombreNegocio, int idEmpresa, String nombreTecnico) {
        if (nombreNegocio == null || nombreNegocio.isEmpty()) return;
        if (idEmpresa <= 0) return;

        try {
            boolean registrado = registrarLicenciaEnBase(nombreNegocio, idEmpresa, nombreTecnico);
            exportarInfoLicenciaEnDescargas(nombreNegocio, idEmpresa, nombreTecnico);

            if (registrado) {
                JOptionPane.showMessageDialog(null,
                        "✅ Licencia registrada correctamente y archivo generado.",
                        "Licencia Activada", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "⚠ La licencia ya existía en la base de datos. Se actualizó el archivo.",
                        "Licencia Existente", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("[ERROR SQL] al registrar licencia: " + e.getMessage());
        }
    }

    // =============================
    // REGISTRAR LICENCIA EN BD CON TÉCNICO
    // =============================
    public static boolean registrarLicenciaEnBase(String nombreNegocio, int idEmpresa, String nombreTecnico) throws Exception {
        String serial = obtenerSerialBIOS();
        String uuid = obtenerUUID();

        try (Connection con = Conexion.getConnection()) {
            // Verificar si ya existe
            String checkSql = "SELECT COUNT(*) FROM licencias_autorizadaslocales WHERE (serial = ? OR uuid = ?) AND id_empresa = ?";
            try (PreparedStatement psCheck = con.prepareStatement(checkSql)) {
                psCheck.setString(1, serial);
                psCheck.setString(2, uuid);
                psCheck.setInt(3, idEmpresa);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }

            // Insertar licencia incluyendo técnico
            String sqlInsert = "INSERT INTO licencias_autorizadaslocales (id_empresa, serial, uuid, nombre_negocio, tecnico, fecha_registro) VALUES (?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement ps = con.prepareStatement(sqlInsert)) {
                ps.setInt(1, idEmpresa);
                ps.setString(2, serial);
                ps.setString(3, uuid);
                ps.setString(4, nombreNegocio);
                ps.setString(5, nombreTecnico);

                int filas = ps.executeUpdate();
                return filas > 0;
            }

        } catch (Exception e) {
            System.err.println("[ERROR SQL] al insertar licencia: " + e.getMessage());
            throw e;
        }
    }

    // =============================
    // EXPORTAR INFO DE LICENCIA
    // =============================
    public static void exportarInfoLicenciaEnDescargas(String nombreNegocio, int idEmpresa, String nombreTecnico) {
        String serial = obtenerSerialBIOS();
        String uuid = obtenerUUID();

        String rutaDescargas = System.getProperty("user.home") + File.separator + "Downloads";
        File archivo = new File(rutaDescargas, "datos_licencia.txt");

        try (PrintWriter writer = new PrintWriter(archivo)) {
            writer.println("==== DATOS PARA ACTIVACIÓN DEL SISTEMA ====");
            writer.println("ID Empresa: " + idEmpresa);
            writer.println("Nombre del negocio: " + nombreNegocio);
            writer.println("Nombre del técnico: " + nombreTecnico);
            writer.println("Serial BIOS: " + serial);
            writer.println("UUID: " + uuid);
            writer.println("Fecha: " + new Date());
            writer.flush();
        } catch (IOException e) {
            System.err.println("[ERROR] al generar archivo de licencia: " + e.getMessage());
        }
    }

    // =============================
    // VALIDAR LICENCIA
    // =============================
    public static boolean licenciaActiva() {
        boolean activa = false;
        String uuid = obtenerUUID().toUpperCase();
        String sql = "SELECT estado FROM licencias_autorizadaslocales WHERE uuid = ? LIMIT 1";

        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activa = rs.getBoolean("estado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activa;
    }

    // =============================
    // ACTIVAR LICENCIA
    // =============================
    public static boolean activarLicencia() {
        String uuid = obtenerUUID();
        String sql = "UPDATE licencias_autorizadaslocales SET estado = TRUE WHERE uuid = ?";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =============================
    // ACTIVAR EMPRESA POR LICENCIA
    // =============================
    public static boolean activarEmpresaPorLicencia() {
        String uuid = obtenerUUID();
        String sql = "UPDATE empresa SET estado = 1 WHERE id_empresa = (SELECT id_empresa FROM licencias_autorizadaslocales WHERE uuid = ? LIMIT 1)";
        try (Connection cn = Conexion.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, uuid);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
