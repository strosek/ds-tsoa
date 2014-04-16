/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 2.
 */

package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import java.util.Hashtable;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleoBase;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.IntByteConverter;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoCliente;


public final class MicroNucleo extends MicroNucleoBase{
	private static MicroNucleo m_kernel = new MicroNucleo();
	private Hashtable<Integer, ParMaquinaProceso> m_emissionTable;
	private Hashtable<Integer, ParMaquinaProceso> m_receptionTable;

	private MicroNucleo() {
	}

	public final static MicroNucleo obtenerMicroNucleo() {
		return m_kernel;
	}

	/* Metodos para probar el paso de mensajes entre los procesos cliente y 
	 * servidor en ausencia de datagramas. Esta es una forma incorrecta de
	 * programacion "por uso de variables globales" (en este caso atributos de
	 * clase) ya que, para empezar, no se usan ambos parametros en los metodos 
	 * y fallaria si dos procesos invocaran simultaneamente a receiveFalso() 
	 * al reescriir el atributo mensaje
	 */
	byte[] mensaje;

	public void sendFalso(int dest,byte[] message){
		System.arraycopy(message, 0, mensaje, 0, message.length);
		
		// Reanuda la ejecucion del proceso que haya invocado a receiveFalso()
		notificarHilos();
	}

	public void receiveFalso(int addr,byte[] message){
		mensaje=message;
		suspenderProceso();
	}

	protected boolean iniciarModulos() {
		return true;
	}

	protected void sendVerdadero(int dest, byte[] message) {
		// TODO: add real action when not found (send AU packet).
		if (m_emissionTable.containsKey(new Integer(dest)))
		{
			imprimeln("No se encontro");
		}
		
		// TODO: get a valid origin ID
		int originId = 248;

		byte[] originBytes = IntByteConverter.toBytes(originId);
		for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
			message[ProcesoCliente.INDEX_ORIGIN + i] = originBytes[i];
		}

		ParMaquinaProceso pmp = dameDestinatarioDesdeInterfaz();
		imprimeln("Enviando mensaje a IP=" + pmp.dameIP() + " ID=" +
				  pmp.dameID());
		byte[] destinationBytes = IntByteConverter.toBytes(pmp.dameID());
		for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
			message[ProcesoCliente.INDEX_DESTINATION + i] = destinationBytes[i];
		}

		sendFalso(dest,message);
		imprimeln("El proceso invocante es el "+super.dameIdProceso());

		
		// esta invocacion depende de si se requiere bloquear al hilo de control 
		// invocador
		suspenderProceso();
	}

	protected void receiveVerdadero(int addr,byte[] message){
		receiveFalso(addr,message);
		//el siguiente aplica para la practica #2
		//suspenderProceso();
	}

	/**
	 * Para el(la) encargad@ de direccionamiento por servidor de nombres en 
	 * practica 5  
	 */
	protected void sendVerdadero(String dest, byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en practica 5
	 */
	protected void sendNBVerdadero(int dest, byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en practica 5
	 */
	protected void receiveNBVerdadero(int addr, byte[] message){
	}

	public void run(){

		while(seguirEsperandoDatagramas()){
			/* Lo siguiente es reemplazable en la practica #2,
			 * sin esto, en practica #1, segun el JRE, puede incrementar el 
			 * uso de CPU
			 */ 
			// TODO: remove this to have fast feedback of processes.
			try{
				sleep(60000);
			}catch(InterruptedException e){
				System.out.println("InterruptedException");
			}
		}
	}
}
