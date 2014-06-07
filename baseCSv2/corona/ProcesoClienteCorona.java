/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 5.
 */

package corona;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;

public class ProcesoClienteCorona extends Proceso {
    public static final int INDEX_ORIGIN = 0;
    public static final int INDEX_DESTINATION = 4;
    public static final int INDEX_OPCODE = 8;
    public static final int INDEX_MESSAGELENGTH = ProcesoServidorCorona.INDEX_MESSLENGTH;
    public static final int INDEX_MESSAGE = ProcesoServidorCorona.INDEX_MESSAGE;

    public static final int SIZE_PACKET = 1024;

    private byte m_opcode;
    private String m_message;
    public boolean banderaSend = false;
    public String  avisodelHiloLSA = "";

    public ProcesoClienteCorona(Escribano esc) {
        super(esc);

        imprimeln("Inicio de proceso...");
        start();
    }

    public void setCodop(int opcode) {
        m_opcode = (byte) opcode;
    }

    public void setMessage(String message) {
        m_message = message;
    }

    public void run() {
        imprimeln("Proceso cliente en ejecucion, " +
                  "esperando datos para continuar...");
        Nucleo.suspenderProceso();

        m_request = new byte[SIZE_PACKET];
        m_response = new byte[ProcesoServidorCorona.SIZE_PACKET];

        m_request[INDEX_OPCODE] = m_opcode;
        m_request[INDEX_MESSAGELENGTH] = (byte) m_message.length();

        imprimeln("Generando mensaje a ser enviado," +
                  " llenando los campos necesarios...");
        packMessage();

        imprimeln("Senhalamiento al nucleo para envio de mensaje...");
        Nucleo.send("ServidorCorona", m_request);

        imprimeln("Invocando a receive...");
        Nucleo.receive(dameID(), m_response);

        imprimeln("Procesando respuesta recibida del sevidor...");
        switch (m_response[ProcesoServidorCorona.INDEX_STATUS]) {
        case ProcesoServidorCorona.STATUS_SUC_CREATE:
            imprimeln("Creacion exitosa");
            break;
        case ProcesoServidorCorona.STATUS_SUC_DELETE:
            imprimeln("Eliminacion exitosa");
            break;
        case ProcesoServidorCorona.STATUS_SUC_READ:
            imprimeln("Lectura exitosa");
            imprimeln("Contenido: "
                    + new String(m_response, ProcesoServidorCorona.INDEX_MESSAGE,
                            (int) m_response[ProcesoServidorCorona.INDEX_MESSLENGTH]));
            break;
        case ProcesoServidorCorona.STATUS_SUC_WRITE:
            imprimeln("Escritura exitosa");
            break;
        case ProcesoServidorCorona.STATUS_ERR_CREATE:
            imprimeln("Error al leer el archivo");
            break;
        case ProcesoServidorCorona.STATUS_ERR_DELETE:
            imprimeln("Error al eliminar en archivo");
            break;
        case ProcesoServidorCorona.STATUS_ERR_READ:
            imprimeln("Error al leer archivo");
            break;
        case ProcesoServidorCorona.STATUS_ERR_WRITE:
            imprimeln("Error al elcribir el archivo");
            break;
        case ProcesoServidorCorona.STATUS_AU:
            imprimeln("Error al enviar peticion: direccion desconocida.");
            break;
        case ProcesoServidorCorona.STATUS_TA:
            imprimeln("Error al enviar peticion: servidor ocupado");
            break;
        default:
            imprimeln("invalid status");
            break;
        }
    }

    private void packMessage() {
        byte[] messageBytes = m_message.getBytes();
        for (int i = 0; i < messageBytes.length; ++i)
            m_request[INDEX_MESSAGE + i] = messageBytes[i];
    }
}
