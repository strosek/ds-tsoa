/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 5.
 */

package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ResendThread extends Thread {
    byte[]         m_message;
    DatagramSocket m_socket;
    DatagramPacket m_packet;

    public ResendThread(DatagramSocket socket, DatagramPacket packet) {
        m_socket = socket;
        m_packet = packet;
    }

    public void run() {
        try {
            System.out.println("Antes de reenviar paquete por la red...");
            sleep(5000);
            m_socket.send(m_packet);
            System.out.println("Despues de reenviar paquete por la red...");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
