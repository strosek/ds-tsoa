/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 2.
 */

package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Hashtable;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleoBase;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.IntByteConverter;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoCliente;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoServidor;


public final class MicroNucleo extends MicroNucleoBase {
	private static MicroNucleo m_kernel = new MicroNucleo();
	private Hashtable<Integer, ParMaquinaProceso> m_emissionTable;
	private Hashtable<Integer, byte[]>            m_receptionTable;

	private MicroNucleo() {
		m_emissionTable = new Hashtable<Integer, ParMaquinaProceso>();
		m_receptionTable = new Hashtable<Integer, byte[]>();
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

	public void sendFalso(int dest, byte[] message) {
		System.arraycopy(message, 0, mensaje, 0, message.length);
		
		// Reanuda la ejecucion del proceso que haya invocado a receiveFalso()
		notificarHilos();
	}

	public void receiveFalso(int addr, byte[] message){
		mensaje = message;
		suspenderProceso();
	}

	protected boolean iniciarModulos() {
		return true;
	}

	protected void sendVerdadero(int dest, byte[] message) {
		ParMaquinaProceso pmp;
		
		int origin = super.dameIdProceso();
		int id;
		String ip;
		
		if (m_emissionTable.containsKey(new Integer(dest)))
		{
			ip = m_emissionTable.get(new Integer(dest)).dameIP();
			id = m_emissionTable.get(new Integer(dest)).dameID();
		}
		else
		{
			pmp = dameDestinatarioDesdeInterfaz();
			imprimeln("Enviando mensaje a IP=" + pmp.dameIP() + " ID=" +
					  pmp.dameID());
			ip = pmp.dameIP();
			id = pmp.dameID();
		}

		byte[] originBytes = IntByteConverter.toBytes(origin);
		for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
			message[ProcesoCliente.INDEX_ORIGIN + i] = originBytes[i];
		}

		byte[] destinationBytes = IntByteConverter.toBytes(id);
		for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
			message[ProcesoCliente.INDEX_DESTINATION + i] =
					destinationBytes[i];
		}

		DatagramPacket packet;
		DatagramSocket socket = m_kernel.dameSocketEmision();
		try
		{
			packet = new DatagramPacket(message, message.length,
					InetAddress.getByName(ip),
					m_kernel.damePuertoRecepcion());
			socket.send(packet);
		}
		catch(UnknownHostException e)
		{
			System.err.println("Error creando socket transmision: " +
					e.getMessage());
		} catch (IOException e) {
			System.err.println("Error creando socket transmision: " +
					e.getMessage());
		}

		imprimeln("El proceso invocante es el " + super.dameIdProceso());
	}

	protected void receiveVerdadero(int addr, byte[] message) {
		m_receptionTable.put(new Integer(addr), message);
		suspenderProceso();
	}

	/**
	 * Para el(la) encargad@ de direccionamiento por servidor de nombres en 
	 * practica 5  
	 */
	protected void sendVerdadero(String dest, byte[] message) {
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en practica 5
	 */
	protected void sendNBVerdadero(int dest, byte[] message) {
	}

	/**
	 * Para el(la) encargad@ de primitivas sin bloqueo en practica 5
	 */
	protected void receiveNBVerdadero(int addr, byte[] message) {
	}

	public void run(){
        DatagramSocket rxSocket;
        DatagramPacket packet;
        byte[] buffer = new byte[ProcesoCliente.SIZE_PACKET];

        String originIp;
        int destination;
        int origin;
        Proceso process;

		while(seguirEsperandoDatagramas()) {
			try
		    {
				rxSocket = m_kernel.dameSocketRecepcion();
				packet = new DatagramPacket(buffer, buffer.length);
				rxSocket.receive(packet);
				origin = IntByteConverter.toInt(
						Arrays.copyOfRange(packet.getData(), 0,
								IntByteConverter.SIZE_INT - 1));
				destination = IntByteConverter.toInt(
						Arrays.copyOfRange(packet.getData(),
								IntByteConverter.SIZE_INT,
								IntByteConverter.SIZE_INT * 2 - 1));
				originIp = packet.getAddress().getHostAddress();

				process = m_kernel.dameProcesoLocal(destination);
				if (process != null)
				{
					if (m_receptionTable.containsKey(destination))
					{
						byte[] array = m_receptionTable.get(destination);
						System.arraycopy(packet.getData(), 0, array, 0,
								array.length);
						m_emissionTable.put(new Integer(origin),
								new MachineProcessPair(originIp, origin));
						m_receptionTable.remove(destination);
						m_kernel.reanudarProceso(process);
					}
				}
				else
				{
					buffer[ProcesoServidor.INDEX_STATUS] = 
							(byte)ProcesoServidor.STATUS_AU;
					packet = new DatagramPacket(buffer, buffer.length);
					DatagramSocket socket = m_kernel.dameSocketEmision();
					socket.send(packet);
				}
		    }
			catch (SocketException e)
			{
				System.out.println("Error iniciando socket recepcion: " +
						e.getMessage());
			}
			catch (IOException e) {
				System.out.println("Error iniciando socket recepcion: " +
						e.getMessage());
			}		
		}
	}
}
