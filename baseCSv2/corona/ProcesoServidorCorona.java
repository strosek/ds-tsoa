/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 5.
 */

package corona;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MachineProcessPair;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ServidorNombres;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.Pausador;
import sistemaDistribuido.visual.clienteServidor.ClienteFrame;

public class ProcesoServidorCorona extends Proceso {
    public static final int INDEX_STATUS =      9;
    public static final int INDEX_SERVICE =    10; // +4 more bytes
    public static final int INDEX_MESSLENGTH = 15;
    public static final int INDEX_MESSAGE =    16;

    public static final int STATUS_SUC_READ =   0;
    public static final int STATUS_SUC_WRITE =  1;
    public static final int STATUS_SUC_CREATE = 2;
    public static final int STATUS_SUC_DELETE = 3;
    public static final int STATUS_ERR_READ =   4;
    public static final int STATUS_ERR_WRITE =  5;
    public static final int STATUS_ERR_CREATE = 6;
    public static final int STATUS_ERR_DELETE = 7;
    public static final int STATUS_AU =  -1;
    public static final int STATUS_LSA = -2;
    public static final int STATUS_FSA = -3;
    public static final int STATUS_TA =  -4;

    public static final int SIZE_PACKET = 1024;

    private String m_requestMessage;
    private String m_responseMessage;
    private int m_status;

    public ProcesoServidorCorona(Escribano esc) {
        super(esc);

        imprimeln("Inicio de proceso...");
        start();
    }

    public void run() {
        m_request = new byte[ProcesoClienteCorona.SIZE_PACKET];
        m_response = new byte[SIZE_PACKET];

        String fileName;
        String argument;

        imprimeln("Agregando buzon");
        Nucleo.nucleo.addNewMailbox(dameID());
        MachineProcessPair asa = null;

        try {
            asa = new MachineProcessPair(InetAddress.getLocalHost().getHostAddress(),dameID());
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }

        int id = ServidorNombres.alta("ServidorCorona", asa);
        
        while (continuar()) {
            imprimeln("Invocando a receive...");
            Nucleo.receive(dameID(), m_request);

            imprimeln("Procesando peticion recibida del cliente...");
            Pausador.pausa(5000);

            m_requestMessage = new String(m_request,
                    ProcesoClienteCorona.INDEX_MESSAGE,
                    (int) m_request[ProcesoClienteCorona.INDEX_MESSAGELENGTH]);

            switch (m_request[ProcesoClienteCorona.INDEX_OPCODE]) {
            case ClienteFrame.CODOP_CREATE:
                imprimeln("Creando archivo: " + m_requestMessage + "...");
                createFile();
                break;
            case ClienteFrame.CODOP_DELETE:
                imprimeln("Eliminando archivo: " + m_requestMessage + "...");
                deleteFile();
                break;
            case ClienteFrame.CODOP_WRITE:
                fileName = m_requestMessage.split(":")[0];
                argument = m_requestMessage.split(":")[1];
                imprimeln("Escribiendo archivo: " + fileName + "...");
                writeToFile(fileName, argument);
                break;
            case ClienteFrame.CODOP_READ:
                fileName = m_requestMessage.split(":")[0];
                argument = m_requestMessage.split(":")[1];
                imprimeln("Leyendo archivo: " + fileName);
                m_responseMessage = readFromFile(fileName,
                        Integer.parseInt(argument));
                break;
            default:
                imprimeln("Codigo de operacion invalido");
                break;
            }

            imprimeln("Generando mensaje a ser enviado,"
                    + " llenando los campos necesarios...");
            pack();

            imprimeln("Senhalamiento al nucleo para envio de mensaje...");
            // avoid server's send() before client's receive()
            Pausador.pausa(2000);

            int origin = Nucleo.nucleo.getOrigin(m_request);
            int destination = Nucleo.nucleo.getDestination(m_request);

            Nucleo.nucleo.setOriginBytes(m_response, destination);
            Nucleo.nucleo.setDestinationBytes(m_response, origin);

            Nucleo.send(origin, m_response);
        }
        ServidorNombres.baja("Servidor",id);
    }

    private void createFile() {
        String fileName = m_requestMessage;
        try {
            imprimeln("Nombre archivo " + fileName);
            File myFile = new File(fileName);
            if (myFile.createNewFile()) {
                m_status = STATUS_SUC_CREATE;
                imprimeln("Archivo creado!");
            } else {
                m_status = STATUS_ERR_CREATE;
                imprimeln("Error creando archivo!");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void deleteFile() {
        String fileName = m_requestMessage;
        try {
            File myFile = new File(fileName);
            if (myFile.delete()) {
                m_status = STATUS_SUC_DELETE;
                imprimeln("Archivo eliminado!");
            } else {
                m_status = STATUS_ERR_DELETE;
                imprimeln("Error eliminando archivo!");
            }
        } catch (SecurityException ioe) {
            ioe.printStackTrace();
        }
    }

    private String readFromFile(String fileName, int lineNo) {
        String contents = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            String line;
            int i = 1;
            while ((line = in.readLine()) != null) {
                if (lineNo == i) {
                    contents = line + "\n";
                }
                ++i;
            }
            in.close();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (contents == null) {
            m_status = STATUS_ERR_READ;
        } else {
            m_status = STATUS_SUC_READ;
        }

        return contents;
    }

    private void writeToFile(String fileName, String contents) {
        try {
            PrintWriter out = new PrintWriter(fileName);
            out.print(contents);
            out.close();
            m_status = STATUS_SUC_WRITE;
        } catch (FileNotFoundException fnfe) {
            m_status = STATUS_ERR_WRITE;
            fnfe.printStackTrace();
        }
    }

    private void pack() {
        m_response[INDEX_STATUS] = (byte) m_status;

        if (m_responseMessage != null) {
            byte[] messageBytes = m_responseMessage.getBytes();
            int messageLength = messageBytes.length;
            m_response[INDEX_MESSLENGTH] = (byte) messageLength;
            for (int i = 0; i < messageLength; ++i) {
                m_response[INDEX_MESSAGE + i] = messageBytes[i];
            }
        } else {
            m_response[INDEX_MESSLENGTH] = 0;
        }
    }
}
