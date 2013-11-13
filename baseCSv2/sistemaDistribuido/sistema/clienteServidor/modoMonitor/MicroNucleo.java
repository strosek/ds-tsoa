package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleoBase;

/**
 * 
 */
public final class MicroNucleo extends MicroNucleoBase{
	private static MicroNucleo nucleo=new MicroNucleo();

	/**
	 * 
	 */
	private MicroNucleo(){
	}

	/**
	 * 
	 */
	public final static MicroNucleo obtenerMicroNucleo(){
		return nucleo;
	}

	/*---Metodos para probar el paso de mensajes entre los procesos cliente y servidor en ausencia de datagramas.
    Esta es una forma incorrecta de programacion "por uso de variables globales" (en este caso atributos de clase)
    ya que, para empezar, no se usan ambos parametros en los metodos y fallaria si dos procesos invocaran
    simultaneamente a receiveFalso() al reescriir el atributo mensaje---*/
	byte[] mensaje;

	public void sendFalso(int dest,byte[] message){
		System.arraycopy(message,0,mensaje,0,message.length);
		notificarHilos();  //Reanuda la ejecucion del proceso que haya invocado a receiveFalso()
	}

	public void receiveFalso(int addr,byte[] message){
		mensaje=message;
		suspenderProceso();
	}
	/*---------------------------------------------------------*/

	/**
	 * 
	 */
	protected boolean iniciarModulos(){
		return true;
	}

	/**
	 * 
	 */
	protected void sendVerdadero(int dest,byte[] message){
		sendFalso(dest,message);
		imprimeln("El proceso invocante es el "+super.dameIdProceso());
		
		//lo siguiente aplica para la práctica #2
		/*ParMaquinaProceso pmp=dameDestinatarioDesdeInterfaz();
		imprimeln("Enviando mensaje a IP="+pmp.dameIP()+" ID="+pmp.dameID());
		suspenderProceso();   //esta invocacion depende de si se requiere bloquear al hilo de control invocador
		*/ 
	}

	/**
	 * 
	 */
	protected void receiveVerdadero(int addr,byte[] message){
		receiveFalso(addr,message);
		//el siguiente aplica para la práctica #2
		//suspenderProceso();
	}

	/**
	 * Para el(la) encargad@ de direccionamiento por servidor de nombres en prï¿½ctica 5  
	 */
	protected void sendVerdadero(String dest,byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en prï¿½ctica 5
	 */
	protected void sendNBVerdadero(int dest,byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en prï¿½ctica 5
	 */
	protected void receiveNBVerdadero(int addr,byte[] message){
	}

	/**
	 * 
	 */
	public void run(){

		while(seguirEsperandoDatagramas()){
			/* Lo siguiente es reemplazable en la prï¿½ctica #2,
			 * sin esto, en prï¿½ctica #1, segï¿½n el JRE, puede incrementar el uso de CPU
			 */ 
			try{
				sleep(60000);
			}catch(InterruptedException e){
				System.out.println("InterruptedException");
			}
		}
	}
}
