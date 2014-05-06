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
        start();
    }

     // Resguardo del servidor
    public void run() {
        imprimeln("Proceso servidor en ejecucion.");
        // idUnico=RPC.exportarInterfaz("FileServer", "3.1", asa) //para practica 4

        m_requestBuffer = new byte[Libreria.SIZE_PACKET];
        int[] parameters;
        while (continuar()) {
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

            Nucleo.send(destination, m_responseBuffer);
        }

        // RPC.deregistrarInterfaz(nombreServidor, version, idUnico) //para practica 4
    }
    
    private int[] unpackParameters() {
        int nParameters = m_requestBuffer[Libreria.INDEX_DATALENGTH];
        int[] parameters = new int[nParameters];
        int firstByte = Libreria.INDEX_DATA;
        int lastByte;
        for (int i = 0; i < nParameters; ++i) {
            firstByte += i * IntByteConverter.SIZE_INT;
            lastByte = firstByte + IntByteConverter.SIZE_INT;
            parameters[i] = IntByteConverter.toInt(Arrays.copyOfRange(
                    m_requestBuffer, firstByte, lastByte));
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
