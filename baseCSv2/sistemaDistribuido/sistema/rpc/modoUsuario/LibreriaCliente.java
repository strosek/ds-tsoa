/* Modificado para practica 3.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

// import sistemaDistribuido.sistema.rpc.modoMonitor.RPC;  //para practica 4
import java.util.Arrays;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.rpc.modoUsuario.Libreria;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.IntByteConverter;

public class LibreriaCliente extends Libreria {
    private static final int DESTINATION_DEFAULT = 248;
    private byte[] m_requestBuffer = new byte[SIZE_PACKET];
    private byte[] m_responseBuffer = new byte[SIZE_PACKET];

    public LibreriaCliente(Escribano esc) {
        super(esc);
    }
    
    public void summation() {
        packParameters();
        m_requestBuffer[INDEX_OPCODE] = OPCODE_SUMMATION;

        // TODO: para practica 4
        // asaDest=RPC.importarInterfaz(nombreServidor, version)
        Nucleo.send(DESTINATION_DEFAULT, m_requestBuffer);

        Nucleo.receive(Nucleo.dameIdProceso(), m_responseBuffer);
        m_parametersStack.push(Integer.valueOf(unpackResponse()));
    }
    public void max() {
        packParameters();
        m_requestBuffer[INDEX_OPCODE] = OPCODE_MAX;
        Nucleo.send(DESTINATION_DEFAULT, m_requestBuffer);

        Nucleo.receive(Nucleo.dameIdProceso(), m_responseBuffer);
        m_parametersStack.push(Integer.valueOf(unpackResponse()));
    }
    public void min() {
        packParameters();
        m_requestBuffer[INDEX_OPCODE] = OPCODE_MIN;
        Nucleo.send(DESTINATION_DEFAULT, m_requestBuffer);

        Nucleo.receive(Nucleo.dameIdProceso(), m_responseBuffer);
        m_parametersStack.push(Integer.valueOf(unpackResponse()));
    }
    public void cube() {
        packParameters();
        m_requestBuffer[INDEX_OPCODE] = OPCODE_CUBE;
        Nucleo.send(DESTINATION_DEFAULT, m_requestBuffer);

        Nucleo.receive(Nucleo.dameIdProceso(), m_responseBuffer);
        m_parametersStack.push(Integer.valueOf(unpackResponse()));
    }

    private void packParameters() {
        int nParameters = m_parametersStack.pop().intValue();
        m_requestBuffer[INDEX_DATALENGTH] = (byte)nParameters;
        for (int i = 0; i < nParameters; ++i) {
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
