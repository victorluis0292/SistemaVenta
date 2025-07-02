package sistemaventa;

import Vista.Login;
import javax.swing.UIManager;

public class SistemaVenta {
    public static void main(String[] args) {
        // Opcional: definir look and feel, por ejemplo Windows
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Mostrar solo la ventana login
        javax.swing.SwingUtilities.invokeLater(() -> {
            Login lg = new Login();
            lg.setVisible(true);
        });
    }
}
