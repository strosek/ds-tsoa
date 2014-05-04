package sistemaDistribuido.sistema.rpc.modoUsuario;

//import sistemaDistribuido.sistema.rpc.modoMonitor.RPC;  //para pr�ctica 4
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.rpc.modoUsuario.Libreria;
import sistemaDistribuido.util.Escribano;

public class LibreriaCliente extends Libreria {

    public LibreriaCliente(Escribano esc) {
        super(esc);
    }

    /**
     * Ejemplo de resguardo del cliente suma
     */
    protected void suma() {
        int asaDest = 0;

        // para pr�ctica 4
        // asaDest=RPC.importarInterfaz(nombreServidor, version)
        Nucleo.send(asaDest, null);
    }

}