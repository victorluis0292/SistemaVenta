package Vista;

import javax.swing.*;
import java.awt.*;

/**
 * Contenedor de la pestaña "Config" del menú lateral. Por ahora solo aloja
 * el panel de Dispositivos (Báscula, Impresora, etc.), pero si más adelante
 * agregas más secciones de configuración (Usuarios, Empresa, etc.), aquí
 * es donde se organizan con un CardLayout o JTabbedPane interno.
 */
public class OpcionConfig extends JPanel {

    public OpcionConfig() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // De momento, Config = Dispositivos. Si luego agregas más secciones,
        // aquí se pondría un sub-menú o pestañas internas.
        add(new PanelDispositivos(), BorderLayout.CENTER);
    }
}