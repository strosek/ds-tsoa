/* Modificado para practica 4.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.sistema.rpc.modoMonitor.RPC;
import java.util.Arrays;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.rpc.modoUsuario.Libreria;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.IntByteConverter;

public class LibreriaCliente extends Libreria {
    private byte[] m_requestBuffer = new byte[SIZE_PACKET];
    private byte[] m_responseBuffer = new byte[SIZE_PACKET];

    public LibreriaCliente(Escribano esc) {
        super(esc);
    }
    
    public void summation() {
        packParameters();
        m_requestBuffer[INDEX_OPCODE] = OPCODE_SUMMATION;

        int destinationHandle = RPC.importarInterfaz("CandyServer", "1.1");
        Nucleo.send(destinationHandle, m_requestBuffer);

        Nucleo.receive(Nucleo.dameIdProceso(), m_responseBuffer);
        m_parametersStack.push(Integer.valueOf(unpackResponse()));
        m_parametersStack.push(Integer.valueOf(1));
    }
    public void max() {
        packParameters();
        m_requestBuffer[INDEX_OPCODE] = OPCODE_MAX;

        int destinationHandle = RPC.importarInterfaz("CandyServer", "1.1");
        Nucleo.send(destinationHandle, m_requestBuffer);

        Nucleo.receive(Nucleo.dameIdProceso(), m_responseBuffer);
        m_parametersStack.push(Integer.valueOf(unpackResponse()));
        m_parametersStack.push(Integer.valueOf(1));
    }
    public void min() {
        packParameters();
        m_requestBuffer[INDEX_OPCODE] = OPCODE_MIN;

        int destinationHandle = RPC.importarInterfaz("CandyServer", "1.1");
        Nucleo.send(destinationHandle, m_requestBuffer);

        Nucleo.receive(Nucleo.dameIdProceso(), m_responseBuffer);
        m_parametersStack.push(Integer.valueOf(unpackResponse()));
        m_parametersStack.push(Integer.valueOf(1));
    }
    public void cube() {
        packParameters();
        m_requestBuffer[INDEX_OPCODE] = OPCODE_CUBE;

        int destinationHandle = RPC.importarInterfaz("CandyServer", "1.1");
        Nucleo.send(destinationHandle, m_requestBuffer);

        Nucleo.receive(Nucleo.dameIdProceso(), m_responseBuffer);
        m_parametersStack.push(Integer.valueOf(unpackResponse()));
        m_parametersStack.push(Integer.valueOf(1));
    }

    private void packParameters() {
        int nParameters = m_parametersStack.pop().intValue();
        m_requestBuffer[INDEX_DATALENGTH] = (byte)nParameters;
        for (int i = nParameters - 1; i >= 0; --i) {
            byte[] parameterBytes = IntByteConverter.toBytes(
                    m_parametersStack.pop().intValue());
            for (int j = 0; j < IntByteConverter.SIZE_INT; ++j) {
                m_requestBuffer[
                        INDEX_DATA + i * IntByteConverter.SIZE_INT + j] =
                        parameterBytes[j];
            }
        }
    }

    private int unpackResponse() {
        return IntByteConverter.toInt(Arrays.copyOfRange(m_responseBuffer,
                INDEX_DATA, INDEX_DATA + IntByteConverter.SIZE_INT));
    }
}
