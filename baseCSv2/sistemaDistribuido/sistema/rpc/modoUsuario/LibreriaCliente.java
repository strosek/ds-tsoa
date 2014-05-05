package sistemaDistribuido.sistema.rpc.modoUsuario;

// import sistemaDistribuido.sistema.rpc.modoMonitor.RPC;  //para pr�ctica 4
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.rpc.modoUsuario.Libreria;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.IntByteConverter;

public class LibreriaCliente extends Libreria {

    public LibreriaCliente(Escribano esc) {
        super(esc);
    }
    
    public void summation() {
        int asaDest = 0;

        byte[] buffer = packParameters();

        // TODO: para practica 4
        // asaDest=RPC.importarInterfaz(nombreServidor, version)
        Nucleo.send(asaDest, buffer);
    }
    public void max() {
        int asaDest = 0;
        byte[] buffer = packParameters();
        Nucleo.send(asaDest, buffer);
    }
    public void min() {
        int asaDest = 0;
        byte[] buffer = packParameters();
        Nucleo.send(asaDest, buffer);
    }
    public void cube() {
        int asaDest = 0;
        byte[] buffer = packParameters();
        Nucleo.send(asaDest, buffer);
    }

    private byte[] packParameters() {
        byte[] buffer = new byte[SIZE_PACKET];
        int nParameters = m_parametersStack.pop().intValue();
        for (int i = 0; i < nParameters; ++i) {
            byte[] parameterBytes = IntByteConverter.toBytes(
                    m_parametersStack.pop().intValue());
            for (int j = 0; j < IntByteConverter.SIZE_INT; ++j) {
                buffer[i * IntByteConverter.SIZE_INT + j] = parameterBytes[j];
            }
        }

        return buffer;
    }
}
