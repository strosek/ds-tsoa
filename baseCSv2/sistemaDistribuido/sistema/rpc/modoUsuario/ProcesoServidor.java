/* Modificado para practica 4.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

import java.util.Arrays;

import sistemaDistribuido.sistema.rpc.modoMonitor.RPC;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MachineProcessPair;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;
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

        // TODO: get a real handle
        ParMaquinaProceso handle = new MachineProcessPair();
        String serverName = "CandyServer";
        String serverVersion = "1.1";
        int uniqueId = RPC.exportarInterfaz(serverName, serverVersion, handle);

        int[] parameters;
        while (continuar()) {
            imprimeln("Invocando a receive.");
            m_requestBuffer = new byte[Libreria.SIZE_PACKET];
            Nucleo.receive(dameID(), m_requestBuffer);
            int destination = IntByteConverter.toInt(Arrays.copyOfRange(
                    m_requestBuffer, 0, IntByteConverter.SIZE_INT));
            parameters = unpackParameters();
            int result = 0;
            switch (m_requestBuffer[Libreria.INDEX_OPCODE]) {
            case Libreria.OPCODE_SUMMATION :
                imprimeln("Realizando operacion sumatoria");
                result = m_serverLib.summation(parameters);
                break;
            case Libreria.OPCODE_MAX :
                imprimeln("Realizando operacion max");
                result = m_serverLib.max(parameters);
                break;
            case Libreria.OPCODE_MIN :
                imprimeln("Realizando operacion min");
                result = m_serverLib.min(parameters);
                break;
            case Libreria.OPCODE_CUBE :
                imprimeln("Realizando operacion cubo");
                result = m_serverLib.cube(parameters[0]);
                break;
            }

            packResult(result);

            imprimeln("Enviando respuesta.");
            Nucleo.send(destination, m_responseBuffer);
        }

        RPC.deregistrarInterfaz(serverName, serverVersion, uniqueId);
    }

    private int[] unpackParameters() {
        int nParameters = m_requestBuffer[Libreria.INDEX_DATALENGTH];
        int[] parameters = new int[nParameters];

        int firstByte = Libreria.INDEX_DATA;
        int lastByte = firstByte + IntByteConverter.SIZE_INT;
        for (int i = 0; i < nParameters; ++i) {
            parameters[i] = IntByteConverter.toInt(Arrays.copyOfRange(
                    m_requestBuffer, firstByte, lastByte));

            firstByte += IntByteConverter.SIZE_INT;
            lastByte = firstByte + IntByteConverter.SIZE_INT;
        }

        return parameters;
    }
    private void packResult(int result) {
        m_responseBuffer = new byte[Libreria.SIZE_PACKET];
        m_responseBuffer[Libreria.INDEX_DATALENGTH] = 1;
        byte[] parameterBytes = IntByteConverter.toBytes(result);
        for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
            m_responseBuffer[Libreria.INDEX_DATA + i] = parameterBytes[i];
        }
    }
}
