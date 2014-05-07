/* Modificado para practica 3.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

//import sistemaDistribuido.sistema.rpc.modoMonitor.RPC;   //para practica 4
import java.util.Arrays;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.IntByteConverter;

public class ProcesoServidor extends Proceso {
    private LibreriaServidor m_serverLib;
    private byte[] m_responseBuffer;
    private byte[] m_requestBuffer;

    public ProcesoServidor(Escribano esc) {
        super(esc);
        m_serverLib = new LibreriaServidor(esc);
        m_requestBuffer = new byte[Libreria.SIZE_PACKET];
        m_responseBuffer = new byte[Libreria.SIZE_PACKET];
        start();
    }

     // Resguardo del servidor
    public void run() {
        imprimeln("Proceso servidor en ejecucion.");
        // idUnico=RPC.exportarInterfaz("FileServer", "3.1", asa) //para practica 4

        int[] parameters;
        while (continuar()) {
            imprimeln("Invocando a receive.");
            Nucleo.receive(dameID(), m_requestBuffer);
            int destination = IntByteConverter.toInt(Arrays.copyOfRange(
                    m_requestBuffer, 0, IntByteConverter.SIZE_INT));
            parameters = unpackParameters();
            int result = 0;
            switch (m_requestBuffer[Libreria.INDEX_OPCODE]) {
            case Libreria.OPCODE_SUMMATION :
                result = m_serverLib.summation(parameters);
                break;
            case Libreria.OPCODE_MAX :
                result = m_serverLib.max(parameters);
                break;
            case Libreria.OPCODE_MIN :
                result = m_serverLib.min(parameters);
                break;
            case Libreria.OPCODE_CUBE :
                result = m_serverLib.cube(parameters[0]);
                break;
            }
            packResult(result);

            imprimeln("Enviando respuesta.");
            Nucleo.send(destination, m_responseBuffer);
        }

        // RPC.deregistrarInterfaz(nombreServidor, version, idUnico) //para practica 4
    }

    private int[] unpackParameters() {
        int nParameters = m_requestBuffer[Libreria.INDEX_DATALENGTH];
        System.out.println("procesoServidor: nParameters " + nParameters);
        int[] parameters = new int[nParameters];
        int firstByte = Libreria.INDEX_DATA;
        int lastByte;
        for (int i = 0; i < nParameters; ++i) {
            firstByte += i * IntByteConverter.SIZE_INT;
            lastByte = firstByte + IntByteConverter.SIZE_INT;
            parameters[i] = IntByteConverter.toInt(Arrays.copyOfRange(
                    m_requestBuffer, firstByte, lastByte));
            System.out.println("procesoServidor: prameter " + i + " " + parameters[i]);
        }

        return parameters;
    }
    private void packResult(int result) {
        m_responseBuffer[Libreria.INDEX_DATALENGTH] = 1;
        byte[] parameterBytes = IntByteConverter.toBytes(result);
        for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
            m_responseBuffer[Libreria.INDEX_DATA + i] = parameterBytes[i];
        }
    }
}
