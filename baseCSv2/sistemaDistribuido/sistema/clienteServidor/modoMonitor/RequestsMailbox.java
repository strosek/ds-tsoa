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
        System.out.println("insertando mensaje en buzon");
        m_container.addLast(localMessage);
        System.out.println("mailbox size: " + m_container.size());
    }

    public byte[] getOldestMessage() {
        System.out.println("sacando primer mensaje de buzon");
        byte[] firstMessage = m_container.removeFirst();
        System.out.println("mailbox size: " + m_container.size());
        MicroNucleo.printBuffer(firstMessage);
        return firstMessage;
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
