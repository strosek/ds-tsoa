package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;


public class ProcesoCliente extends Proceso{
	public static final int INDEX_CODOP =          9;
	public static final int INDEX_MESSAGELENGTH = 10;
	public static final int INDEX_MESSAGE =       11;
	
	public static final int SIZE_REQPACKET = 1024;
	public static final int SIZE_REPPACKET = 1024;
	
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
		imprimeln("Hola =)");
		byte[] solCliente=new byte[SIZE_REQPACKET];
		byte[] respCliente=new byte[SIZE_REPPACKET];
		byte dato;
		solCliente[0]=(byte)10;
		solCliente[INDEX_CODOP] = m_codop;
		solCliente[INDEX_MESSAGELENGTH] = (byte)m_message.length();
		packMessage(solCliente);
		Nucleo.send(248,solCliente);
		Nucleo.receive(dameID(),respCliente);
		dato=respCliente[0];
		imprimeln("el servidor me envio un "+dato);
	}
	
	private void packMessage(byte[] packet) {
		byte[] array = m_message.getBytes();
		for (int i = 0; i < array.length; ++i)
			packet[INDEX_MESSAGE + i] = array[i];
	}
}
