package sistemaDistribuido.sistema.rpc.modoUsuario;

//import sistemaDistribuido.sistema.rpc.modoMonitor.RPC;   //para práctica 4
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;

/**
 * 
 */
public class ProcesoServidor extends Proceso{
	//private LibreriaServidor ls;   //para práctica 3

	/**
	 * 
	 */
	public ProcesoServidor(Escribano esc){
		super(esc);
		//ls=new LibreriaServidor(esc);   //para práctica 3
		start();
	}

	/**
	 * Resguardo del servidor
	 */
	public void run(){
		imprimeln("Proceso servidor en ejecucion.");
		//idUnico=RPC.exportarInterfaz("FileServer", "3.1", asa)  //para práctica 4

		while(continuar()){
			Nucleo.receive(dameID(),null);
		}

		//RPC.deregistrarInterfaz(nombreServidor, version, idUnico)  //para práctica 4
	}
}
