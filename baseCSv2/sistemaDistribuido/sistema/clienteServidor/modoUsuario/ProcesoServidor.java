package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.Pausador;
import sistemaDistribuido.visual.clienteServidor.ClienteFrame;


public class ProcesoServidor extends Proceso{
	private String m_message;

	public ProcesoServidor(Escribano esc){
		super(esc);
		start();
	}

	public void run(){
		imprimeln("Proceso servidor en ejecucion.");
		byte[] solServidor=new byte[ProcesoCliente.SIZE_REQPACKET];
		byte[] respServidor = new byte[ProcesoCliente.SIZE_REPPACKET];
		byte dato;
		while(continuar()){
			Nucleo.receive(dameID(),solServidor);
			
			dato=solServidor[0];
			imprimeln("El cliente envio un "+ dato);
			
			m_message = new String(solServidor, ProcesoCliente.INDEX_MESSAGE, 
					(int)solServidor[ProcesoCliente.INDEX_MESSAGELENGTH]);
			switch (solServidor[ProcesoCliente.INDEX_CODOP]) {
			case ClienteFrame.CODOP_CREATE :
				imprimeln("Creando archivo: " + m_message);
				createFile();
				break;
			case ClienteFrame.CODOP_DELETE :
				imprimeln("Eliminando archivo: " + m_message);
				deleteFile();
				break;
			case ClienteFrame.CODOP_WRITE :
				imprimeln("Escribiendo archivo: " + m_message);
				writeToFile("Hola Perro");
				break;
			case ClienteFrame.CODOP_READ :
				imprimeln("Leyendo archivo: " + m_message);
				imprimeln("Contenido: " + readFromFile());
				break;
			default :
				imprimeln("Codigo de operacion invalido");
				break;
			}
			respServidor=new byte[20];
			respServidor[0]=(byte)(dato*dato);
			
			// avoid server's send() before client's receive()
			Pausador.pausa(1000);
			imprimeln("enviando respuesta");
			Nucleo.send(0,respServidor);
		}
	}
	
	private void createFile() {
		String fileName = m_message;
		try {
			imprimeln("Nombre archivo " + fileName);
			File myFile = new File(m_message);
			if (myFile.createNewFile()) {
				imprimeln("Archivo creado!");
			}
			else {
				imprimeln("Error creando archivo!");
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private void deleteFile() {
		String fileName = m_message;
		try {
			File myFile = new File(fileName);
			if ( myFile.delete() ) {
				imprimeln("Archivo creado!");
			}
			else {
				imprimeln("Error creando archivo!");
			}
		}
		catch (SecurityException ioe ) {
			ioe.printStackTrace();
		}
	}
	
	private String readFromFile() {
		String fileName = m_message;
		String contents = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = in.readLine()) != null)
				contents += line + "\n";
			in.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return contents;
	}

	private void writeToFile(String contents) {
		String fileName = m_message;
		try {
			PrintWriter out = new PrintWriter(fileName);
			out.print(contents);
			out.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}
}
