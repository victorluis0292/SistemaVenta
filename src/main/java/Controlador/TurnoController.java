package Controlador;

import Modelo.TurnoDAO;
import Modelo.TurnoModel;
import Vista.CorteCajeroView;
import Vista.Sistema;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TurnoController {

    private static TurnoController instancia; // Singleton
    private static TurnoModel turnoGlobal;    // Turno activo globalmente
    private final TurnoDAO dao = new TurnoDAO();
    private TurnoModel turnoActual;

    // Constructor privado
    private TurnoController(String usuario) {
        System.out.println("🔍 Buscando turno abierto para usuario: '" + usuario + "'");
        int idEmpresa = Sistema.getIdEmpresaActiva(); // ✅ Método que devuelve la empresa actual

        this.turnoActual = dao.obtenerTurnoAbierto(usuario, idEmpresa);

        if (this.turnoActual == null) {
            System.out.println("❌ No se encontró turno abierto.");
            turnoGlobal = null;
        } else {
            turnoGlobal = this.turnoActual;
            System.out.println("✅ Turno abierto encontrado con ID: " + this.turnoActual.getId());
        }
    }

    // Singleton con recarga si ya existe
    public static synchronized TurnoController getInstance(String usuario) {
        int idEmpresa = Sistema.getIdEmpresaActiva(); // ✅ Siempre obtener empresa activa

        if (instancia == null) {
            instancia = new TurnoController(usuario);
        } else {
            instancia.turnoActual = instancia.dao.obtenerTurnoAbierto(usuario, idEmpresa);
            turnoGlobal = instancia.turnoActual;
            if (turnoGlobal != null) {
                System.out.println("🔄 Turno actualizado desde getInstance, ID: " + turnoGlobal.getId());
            } else {
                System.out.println("⚠️ No se encontró turno al actualizar.");
            }
        }
        return instancia;
    }

    // Turno global estático (uso en todo el sistema)
    public static TurnoModel getTurnoGlobal() {
        if (turnoGlobal == null) {
            System.out.println("⚠️ turnoGlobal es null. Intentando recargar...");
            TurnoDAO dao = new TurnoDAO();
            String usuarioActivo = Sistema.getUsuarioActivo(); // método que ya debes tener
            int idEmpresa = Sistema.getIdEmpresaActiva(); // ✅ también obtener empresa actual

            turnoGlobal = dao.obtenerTurnoAbierto(usuarioActivo, idEmpresa);

            if (turnoGlobal != null) {
                System.out.println("🔁 Turno recargado: ID " + turnoGlobal.getId());
            } else {
                System.out.println("❌ No se pudo recargar turno para: " + usuarioActivo);
            }
        }
        return turnoGlobal;
    }

    // Setter manual (útil para login)
    public static void setTurnoGlobal(TurnoModel turno) {
        turnoGlobal = turno;
    }

    // Limpieza manual (uso en logout o cerrar turno)
    public static void limpiarTurno() {
        System.out.println("🧹 Turno global limpiado.");
        turnoGlobal = null;
    }

    // ---------------- MÉTODOS DE NEGOCIO ----------------

    public void cerrarTurno(BigDecimal ventas, BigDecimal ingresos, BigDecimal egresos, BigDecimal ganancia, CorteCajeroView vista) {
        if (turnoActual == null) return;

        BigDecimal total = getSaldoInicial()
                .add(ventas)
                .add(ingresos)
                .subtract(egresos);

        turnoActual.setTotalVentas(ventas);
        turnoActual.setIngresos(ingresos);
        turnoActual.setEgresos(egresos);
        turnoActual.setGanancia(ganancia);
        turnoActual.setTotalEnCaja(total);
        turnoActual.setFechaCierre(new Timestamp(System.currentTimeMillis()));

        dao.cerrarTurno(turnoActual);

        // 🧹 Limpiar datos en memoria
        TurnoController.limpiarTurno();             
        Sistema.setUsuarioActivo(null);             

        // 🧼 Cerrar ventana principal
        if (Sistema.getInstancia() != null) {
            Sistema.getInstancia().dispose();
        }

        // ✅ Mostrar mensaje y volver al login
        if (vista != null) {
            vista.mostrarMensaje("Turno cerrado correctamente.");
            vista.cerrarVentana();
            vista.volverAlLogin();
        }
    }

    public Timestamp getFechaInicioTurno() {
        return (turnoActual != null) ? turnoActual.getFechaInicio() : null;
    }

    public BigDecimal getSaldoInicial() {
        if (turnoActual == null) return BigDecimal.ZERO;
        return turnoActual.getSaldoInicial() != null ? turnoActual.getSaldoInicial() : BigDecimal.ZERO;
    }

    public BigDecimal calcularVentasDelTurno() {
        return (turnoActual != null) ? dao.obtenerVentas(turnoActual.getId()) : BigDecimal.ZERO;
    }

    public BigDecimal calcularIngresosDelTurno() {
        return (turnoActual != null) ? dao.obtenerIngresos(turnoActual.getId()) : BigDecimal.ZERO;
    }

    public BigDecimal calcularEgresosDelTurno() {
        return (turnoActual != null) ? dao.obtenerEgresos(turnoActual.getId()) : BigDecimal.ZERO;
    }

    public BigDecimal calcularGananciaDelTurno() {
        return (turnoActual != null) ? dao.obtenerGanancia(turnoActual.getId()) : BigDecimal.ZERO;
    }

    public TurnoModel getTurnoActual() {
        return turnoActual;
    }
}
