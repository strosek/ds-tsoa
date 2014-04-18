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
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoCliente;


public final class MicroNucleo extends MicroNucleoBase {
	private static MicroNucleo m_kernel;
	private Hashtable<Integer, ParMaquinaProceso> m_emissionTable;
	private Hashtable<Integer, byte[]>            m_receptionTable;

	private MicroNucleo() {
		m_kernel = new MicroNucleo();
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
		if (m_emissionTable.containsKey(new Integer(dest)))
		{
		}
		else
		{
			// TODO: add real action when not found (send AU packet).
			imprimeln("No se encontro el proceso destino");
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
		
		DatagramSocket txSocket;
	    DatagramPacket packet;
	    int txPort = 52007;

	    // TODO: define a real rxPort and a real buffer.
	    int rxPort = 12345;
	    byte[] buffer = new byte[1];

	    try
	    {
	    	txSocket = new DatagramSocket(txPort);
	    	packet = new DatagramPacket(buffer, buffer.length,
	    			InetAddress.getByName(pmp.dameIP()), rxPort);
	    	txSocket.send(packet);
	    	txSocket.close();
	    }
	    catch (SocketException e)
	    {
	    	System.err.println("Error creando socket transmision: " + 
	    			e.getMessage());
	    }
	    catch(UnknownHostException e)
	    {
	    	System.err.println("Error creando socket transmision: " +
	    			e.getMessage());
	    }
	    catch(IOException e)
	    {
	    	System.err.println("Error creando socket transmision: " +
	    			e.getMessage());
	    }

	    // TODO: check if this should be here.
		sendFalso(dest,message);
		imprimeln("El proceso invocante es el " + super.dameIdProceso());

		
		// esta invocacion depende de si se requiere bloquear al hilo de control 
		// invocador
		suspenderProceso();
	}

	protected void receiveVerdadero(int addr, byte[] message) {
		m_receptionTable.put(addr, message);
		// TODO: check if this "false" call should be removed.
		receiveFalso(addr,message);
		//el siguiente aplica para la practica #2
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
		final int PORT_RX = 54321;
        DatagramSocket rxSocket;
        DatagramPacket packet;
        byte[] buffer = new byte[ProcesoCliente.SIZE_PACKET];
        
        String originIp;
        int destination;
        int origin;

		while(seguirEsperandoDatagramas()) {
			try
		    {
	          rxSocket = new DatagramSocket(PORT_RX);
		      packet = new DatagramPacket(buffer, buffer.length);
		      while (true)
		      {
		        rxSocket.receive(packet);
		        origin = IntByteConverter.toInt(
		        		Arrays.copyOfRange(packet.getData(), 0,
		        		IntByteConverter.SIZE_INT - 1));
		        destination = IntByteConverter.toInt(
		        		Arrays.copyOfRange(packet.getData(),
		        		IntByteConverter.SIZE_INT,
		        		IntByteConverter.SIZE_INT * 2 - 1));
		        originIp = packet.getAddress().getHostAddress();
		        m_kernel.dameProcesoLocal(destination);
		      }
		    }
		    catch (SocketException e)
		    {
		      System.out.println("Error iniciando socket recepcion: " + e.getMessage());
		    }
		    catch (IOException e){
		      System.out.println("Error iniciando socket recepcion: " + e.getMessage());
		    }		
		}
	}
}
