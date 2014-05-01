/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 2.
 */

package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Hashtable;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleoBase;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoCliente;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoServidor;
import sistemaDistribuido.util.IntByteConverter;


public final class MicroNucleo extends MicroNucleoBase{
	private static MicroNucleo                     nucleo = new MicroNucleo();
	private Hashtable<Integer, MachineProcessPair> m_emissionTable;
	private Hashtable<Integer, byte[]>             m_receptionTable;

	private MicroNucleo() {
		m_emissionTable = new Hashtable<Integer, MachineProcessPair>();
		m_receptionTable = new Hashtable<Integer, byte[]>();
	}

	public final static MicroNucleo obtenerMicroNucleo() {
		return nucleo;
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
		System.arraycopy(message,0,mensaje,0,message.length);

		//Reanuda la ejecucion del proceso que haya invocado a receiveFalso()
		notificarHilos();
	}

	public void receiveFalso(int addr,byte[] message) {
		mensaje=message;
		suspenderProceso();
	}

	protected boolean iniciarModulos() {
		return true;
	}

	protected void sendVerdadero(int dest,byte[] message){
		imprimeln("El proceso invocante es el " + super.dameIdProceso());

		ParMaquinaProceso pmp;
		String ip;
		int id;
		
		if (m_emissionTable.containsKey(new Integer(dest))) {
			ip = m_emissionTable.get(new Integer(dest)).dameIP();
			id = m_emissionTable.get(new Integer(dest)).dameID();
		}
		else {
			pmp = dameDestinatarioDesdeInterfaz();
			ip = pmp.dameIP();
			id = pmp.dameID();
			imprimeln("Enviando mensaje a IP=" + ip + " ID=" + id);
		}
		imprimeln("Buscando en listas locales el par (" + ip + ", " +
				  dest + ")");

		byte[] originBytes = IntByteConverter.toBytes(super.dameIdProceso());
		byte[] destinationBytes = IntByteConverter.toBytes(id);

		imprime("Completando campos del encabezado del mensaje a ser enviado");
		for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
			message[ProcesoCliente.INDEX_ORIGIN + i] = originBytes[i];
			message[ProcesoCliente.INDEX_DESTINATION + i] = destinationBytes[i];
		}

		try {
			DatagramPacket packet = new DatagramPacket(message, message.length,
					InetAddress.getByName(ip), nucleo.damePuertoRecepcion());
			imprime("Enviando mensaje por la red");
			nucleo.dameSocketEmision().send(packet);
		}
		catch(UnknownHostException e) {
			System.err.println("Error creando socket transmision: " +
							   e.getMessage());
		}
		catch (IOException e) {
			System.err.println("Error creando socket transmision: " +
							   e.getMessage());
		}
	}

	protected void receiveVerdadero(int addr,byte[] message) {
		m_receptionTable.put(new Integer(addr), message);
		imprimeln("Recibido mensaje proviniente de la red");
		imprimeln("Recibido mensaje que contiene la ubicacion: " + addr);
		suspenderProceso();
	}

	/**
	 * Para el(la) encargad@ de direccionamiento por servidor de nombres en 
	 * pr�ctica 5  
	 */
	protected void sendVerdadero(String dest,byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en pr�ctica 5
	 */
	protected void sendNBVerdadero(int dest,byte[] message){
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en pr�ctica 5
	 */
	protected void receiveNBVerdadero(int addr,byte[] message){
	}

	public void run(){
		byte[] buffer = new byte[ProcesoCliente.SIZE_PACKET];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		int origin, destination;
		String originIp;
		Proceso process;

		while (seguirEsperandoDatagramas()) {
			try {
				nucleo.dameSocketRecepcion().receive(packet);
				
				origin = IntByteConverter.toInt(
						Arrays.copyOfRange(packet.getData(),
						ProcesoCliente.INDEX_ORIGIN,
						ProcesoCliente.INDEX_ORIGIN +
						IntByteConverter.SIZE_INT));
				originIp = packet.getAddress().getHostAddress();

				destination = IntByteConverter.toInt(
						Arrays.copyOfRange(packet.getData(),
						ProcesoCliente.INDEX_DESTINATION,
						ProcesoCliente.INDEX_DESTINATION + 
						IntByteConverter.SIZE_INT));

				imprimeln("Buscando proceso correspondiente al campo recibido");
				process = nucleo.dameProcesoLocal(destination);
				if (packet.getData()[ProcesoServidor.INDEX_STATUS] ==
					ProcesoServidor.STATUS_AU) {
					imprimeln("Enviando AU. Servidor desconocido.");
					nucleo.reanudarProceso(process);
					continue;
				}

				if (process != null) {
					if (m_receptionTable.containsKey(destination)) {
						byte[] array = m_receptionTable.get(destination);
						
						imprimeln("Copiando mensaje al espaco de proceso");
						System.arraycopy(packet.getData(), 0, array, 0,
								         array.length);
						m_emissionTable.put(new Integer(origin),
								new MachineProcessPair(originIp, origin));
						m_receptionTable.remove(destination);
						nucleo.reanudarProceso(process);
					}
				}
				else {
					imprimeln("Proceso distinatario no encontrado segun el " +
							  "campo dest recibido");
					buffer[ProcesoServidor.INDEX_STATUS] = 
							(byte)ProcesoServidor.STATUS_AU;
					packet = new DatagramPacket(buffer, buffer.length,
							InetAddress.getByName(originIp),
							nucleo.damePuertoRecepcion());
					nucleo.dameSocketEmision().send(packet);
				}
			}
			catch (IOException e) {
				System.err.println("Error en la recepcion del paquete: " +
								   e.getMessage());
			}
		}
	}
}
