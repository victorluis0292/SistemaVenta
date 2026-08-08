package Pruebas;

import com.fazecast.jSerialComm.SerialPort;
import java.io.OutputStream;

/**
 * Simulador de báscula: envía un peso de prueba por el puerto virtual
 * (Connector COM4 en VSPE, el mismo que usa BasculaListener).
 * Solo para pruebas mientras no tienes báscula física.
 */
public class SimuladorBascula {

    public static void main(String[] args) throws Exception {
        SerialPort puerto = SerialPort.getCommPort("COM4");
        puerto.setBaudRate(9600);
        puerto.setNumDataBits(8);
        puerto.setNumStopBits(SerialPort.ONE_STOP_BIT);
        puerto.setParity(SerialPort.NO_PARITY);

        if (!puerto.openPort()) {
            System.out.println("No se pudo abrir COM4. ¿Está creado el Connector en VSPE?");
            return;
        }

        System.out.println("Simulador de báscula enviando datos por COM4...");
        OutputStream out = puerto.getOutputStream();

        double peso = 0.500;
        while (true) {
            String linea = String.format("+%06.3fkg\r\n", peso);
            out.write(linea.getBytes());
            out.flush();
            System.out.println("Enviado: " + linea.trim());

            Thread.sleep(1500);
            peso += 0.100;
        }
    }
}