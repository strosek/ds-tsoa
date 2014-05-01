/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 2.
 */

package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.IntByteConverter;
import sistemaDistribuido.util.Pausador;
import sistemaDistribuido.visual.clienteServidor.ClienteFrame;


public class ProcesoServidor extends Proceso {
	public static final int INDEX_STATUS =     8;
	public static final int INDEX_MESSLENGTH = 9;
	public static final int INDEX_MESSAGE =    10;

	public static final int STATUS_SUC_READ =   0;
	public static final int STATUS_SUC_WRITE =  1;
	public static final int STATUS_SUC_CREATE = 2;
	public static final int STATUS_SUC_DELETE = 3;
	public static final int STATUS_ERR_READ =   4;
	public static final int STATUS_ERR_WRITE =  5;
	public static final int STATUS_ERR_CREATE = 6;
	public static final int STATUS_ERR_DELETE = 7;
	public static final int STATUS_AU =         8;
	
	public static final int SIZE_PACKET = 1024;
	
	private String m_requestMessage;
	private String m_responseMessage;
	private int    m_status;

	public ProcesoServidor(Escribano esc) {
		super(esc);
		
		imprimeln("Inicio de proceso...");
		start();
	}

	public void run() {
		m_request = new byte[ProcesoCliente.SIZE_PACKET];
		m_response = new byte[SIZE_PACKET];

		String fileName;
		String argument;
		while(continuar()) {
			imprimeln("Invocando a receive...");
			Nucleo.receive(dameID(),m_request);
			
			imprimeln("Procesando peticion recibida del cliente...");
			m_requestMessage = new String(m_request, 
					ProcesoCliente.INDEX_MESSAGE, 
					(int)m_request[ProcesoCliente.INDEX_MESSAGELENGTH]);
			
			switch (m_request[ProcesoCliente.INDEX_OPCODE]) {
			case ClienteFrame.CODOP_CREATE :
				imprimeln("Creando archivo: " + m_requestMessage + "...");
				createFile();
				break;
			case ClienteFrame.CODOP_DELETE :
				imprimeln("Eliminando archivo: " + m_requestMessage + "...");
				deleteFile();
				break;
			case ClienteFrame.CODOP_WRITE :
				fileName = m_requestMessage.split(":")[0];
				argument = m_requestMessage.split(":")[1];
				imprimeln("Escribiendo archivo: " + fileName + "...");
				writeToFile(fileName, argument);
				break;
			case ClienteFrame.CODOP_READ :
				fileName = m_requestMessage.split(":")[0];
				argument = m_requestMessage.split(":")[1];
				imprimeln("Leyendo archivo: " + fileName);
				m_responseMessage = readFromFile(fileName, 
						                         Integer.parseInt(argument));
				break;
			default :
				imprimeln("Codigo de operacion invalido");
				break;
			}

			imprimeln("Generando mensaje a ser enviado," +
					  " llenando los campos necesarios...");
			pack();

			imprimeln("Senhalamiento al nucleo para envio de mensaje...");
			// avoid server's send() before client's receive()
			Pausador.pausa(2000);

			int origin = IntByteConverter.toInt(
					Arrays.copyOfRange(m_request, ProcesoCliente.INDEX_ORIGIN,
					ProcesoCliente.INDEX_ORIGIN +
					IntByteConverter.SIZE_INT));
			int destination = IntByteConverter.toInt(
					Arrays.copyOfRange(m_request, 
					ProcesoCliente.INDEX_DESTINATION,
					ProcesoCliente.INDEX_DESTINATION +
					IntByteConverter.SIZE_INT));

			byte[] originBytes = IntByteConverter.toBytes(origin);
			byte[] destinationBytes = IntByteConverter.toBytes(destination);
			for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
				m_response[ProcesoCliente.INDEX_ORIGIN + i] =
						destinationBytes[i];
				m_response[ProcesoCliente.INDEX_DESTINATION + i] =
						originBytes[i];
			}
			System.out.println("enviando respuesta a proceso: " + origin);
			System.out.println("enviando respuesta desde proceso : " +
						       destination);
			Nucleo.send(origin, m_response);
		}
	}
	
	private void createFile() {
		String fileName = m_requestMessage;
		try {
			imprimeln("Nombre archivo " + fileName);
			File myFile = new File(fileName);
			if (myFile.createNewFile()) {
				m_status = STATUS_SUC_CREATE;
				imprimeln("Archivo creado!");
			}
			else {
				m_status = STATUS_ERR_CREATE;
				imprimeln("Error creando archivo!");
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	private void deleteFile() {
		String fileName = m_requestMessage;
		try {
			File myFile = new File(fileName);
			if ( myFile.delete() ) {
				m_status = STATUS_SUC_DELETE;
				imprimeln("Archivo eliminado!");
			}
			else {
				m_status = STATUS_ERR_DELETE;
				imprimeln("Error eliminando archivo!");
			}
		}
		catch (SecurityException ioe ) {
			ioe.printStackTrace();
		}
	}
	private String readFromFile(String fileName, int lineNo) {
		String contents = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line;
			int i = 1;
			while ((line = in.readLine()) != null) {
				if (lineNo == i) {
					contents = line + "\n";
				}
				++i;
			}
			in.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		if (contents == null) {
			m_status = STATUS_ERR_READ;
		}
		else {
			m_status = STATUS_SUC_READ;
		}
		
		return contents;
	}
	private void writeToFile(String fileName, String contents) {
		try {
			PrintWriter out = new PrintWriter(fileName);
			out.print(contents);
			out.close();
			m_status = STATUS_SUC_WRITE;
		} catch (FileNotFoundException fnfe) {
			m_status = STATUS_ERR_WRITE;
			fnfe.printStackTrace();
		}
	}

	private void pack() {
		m_response[INDEX_STATUS] = (byte)m_status;
		
		if (m_responseMessage != null) {
			byte[] messageBytes = m_responseMessage.getBytes();
			int messageLength = messageBytes.length;
			m_response[INDEX_MESSLENGTH] = (byte)messageLength;
			for (int i = 0; i < messageLength; ++i) {
				m_response[INDEX_MESSAGE + i] = messageBytes[i];
			}
		}
		else {
			m_response[INDEX_MESSLENGTH] = 0;
		}
	}
}
