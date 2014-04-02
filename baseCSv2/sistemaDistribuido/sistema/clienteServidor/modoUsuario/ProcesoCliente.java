/*
 * Erick Daniel Corona Garcia D03.
 * 
 * Modificado para Practica 1.
 */

package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;


public class ProcesoCliente extends Proceso{
	public static final int INDEX_CODOP =          9;
	public static final int INDEX_MESSAGELENGTH = 10;
	public static final int INDEX_MESSAGE =       11;
	
	public static final int SIZE_PACKET = 1024;
	
	private byte   m_codop;
	private String m_message;
	
	public ProcesoCliente(Escribano esc){
		super(esc);
		start();
	}
	
	public void setCodop(int codop) {
		m_codop = (byte)codop;
	}
	
	public void setMessage(String message) {
		m_message = message;
	}

	public void run() {
		imprimeln("Proceso cliente en ejecucion.");
		imprimeln("Esperando datos para continuar.");
		Nucleo.suspenderProceso();

		byte[] solCliente =new byte[SIZE_PACKET];
		byte[] respCliente =new byte[ProcesoServidor.SIZE_PACKET];

		solCliente[INDEX_CODOP] = m_codop;
		solCliente[INDEX_MESSAGELENGTH] = (byte)m_message.length();
		
		packMessage(solCliente);
		
		Nucleo.send(248,solCliente);
		Nucleo.receive(dameID(), respCliente);
		
		switch (respCliente[ProcesoServidor.INDEX_STATUS]) {
		case ProcesoServidor.STATUS_SUC_CREATE :
			imprimeln("Creacion exitosa");
			break;
		case ProcesoServidor.STATUS_SUC_DELETE :
			imprimeln("Eliminacion exitosa");
			break;
		case ProcesoServidor.STATUS_SUC_READ :
			imprimeln("Lectura exitosa");
			imprimeln("Contenido: " + new String(respCliente, 
					ProcesoServidor.INDEX_MESSAGE, 
					(int)respCliente[ProcesoServidor.INDEX_MESSLENGTH]));
			break;
		case ProcesoServidor.STATUS_SUC_WRITE :
			imprimeln("Escritura exitosa");
			break;
		case ProcesoServidor.STATUS_ERR_CREATE :
			imprimeln("Error al leer el archivo");
			break;
		case ProcesoServidor.STATUS_ERR_DELETE :
			imprimeln("Error al eliminar en archivo");
			break;
		case ProcesoServidor.STATUS_ERR_READ :
			imprimeln("Error al leer archivo");
			break;
		case ProcesoServidor.STATUS_ERR_WRITE :
			imprimeln("Error al elcribir el archivo");
			break;
		}
	}

	private void packMessage(byte[] packet) {
		byte[] array = m_message.getBytes();
		for (int i = 0; i < array.length; ++i)
			packet[INDEX_MESSAGE + i] = array[i];
	}
}
