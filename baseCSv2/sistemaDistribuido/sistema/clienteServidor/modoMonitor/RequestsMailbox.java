/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 5.
 */

package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import java.util.LinkedList;


public class RequestsMailbox {
    private final int SIZE_MAILBOX = 3;

    LinkedList<byte[]> m_container;


    public RequestsMailbox() {
        m_container = new LinkedList<byte[]>();
    }

    public void saveMessage(byte[] message) {
        byte[] localMessage = new byte[message.length];

        System.arraycopy(message, 0, localMessage, 0, message.length);
        m_container.offer(localMessage);
        System.out.println("mailbox size: " + m_container.size());
    }

    public byte[] getOldestMessage() {
        return m_container.poll();
    }

    public boolean hasSpace() {
        if (m_container.size() >= SIZE_MAILBOX) {
            return false;
        }

        return true;
    }

    public boolean isEmpty() {
        return m_container.isEmpty();
    }
}
