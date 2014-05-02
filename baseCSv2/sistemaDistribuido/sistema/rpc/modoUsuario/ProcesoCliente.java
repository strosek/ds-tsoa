package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;


public class ProcesoCliente extends Proceso {
	private Libreria m_mathLib;

	public ProcesoCliente(Escribano esc) {
		super(esc);

		// primero debe funcionar con esta para subrutina servidor local
		m_mathLib = new LibreriaServidor(esc);

		// luego con esta comentando la anterior, para subrutina servidor remota
		// m_mathLib = new LibreriaCliente(esc);

		start();
	}

	public void run() {
		imprimeln("Proceso cliente en ejecucion.");
		imprimeln("Esperando datos para continuar.");
		Nucleo.suspenderProceso();
		imprimeln("Salio de suspenderProceso");

		int result;
		result = m_mathLib;

		imprimeln("Fin del cliente.");
	}
}
