/**
 * Alejandro Duarte S�nchez
 * 206587844
 * Practica 1 y 5
 */
package sistemaDistribuido.duarte;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;

/**
 * 
 */
public class ProcesoClienteDuarte extends Proceso {

	/**
	 * Crear 	-> 0
	 * Eliminar -> 1
	 * Leer		-> 2
	 * Escribir	-> 3
	 */
	private int operacion;
	private String datos;
	
	public void entradaInterfaz(int operacion , String datos){
		this.operacion = operacion;
		this.datos = datos;
	}
	public ProcesoClienteDuarte(Escribano esc) {
		super(esc);
		start();
	}

	/**
	 * 
	 */
	public void run() {
		imprimeln("Inicio de proceso");
		imprimeln("Proceso cliente en ejecucion.");
		imprimeln("Esperando datos para continuar.");
		Nucleo.suspenderProceso();
		byte[] solCliente = new byte[1024];
		byte[] respCliente = new byte[1024];
		char []respuesta = new char[1016];
		
		imprimeln("Generando mensaje a ser enviado, llenando los campos necesarios");
		//Guardar Codigo de operacion
		solCliente[9] = (byte)operacion;
		//Guardar mensaje
		byte temp[] = new byte[1024];
		temp = datos.getBytes();
		solCliente[10] = (byte)datos.length();
		for(int i = 11 , j = 0; i < datos.length()+11 ; i++, j++){
			solCliente[i] =  temp[j];
		}
		
		imprimeln("Se�alamiento al n�cleo para env�o de mensaje");
		Nucleo.send("Servidor", solCliente);
		imprimeln("Invocando a receive()");
		Nucleo.receive(dameID(), respCliente);
		imprimeln("Procesando respuesta recibida del servidor");
		int tam = respCliente[8];
		for( int i = 9 , j = 0; i < tam+9 ; i++  , j++){
			respuesta[j]= (char)respCliente[i];
		}
		imprimeln("el servidor me envi� un :" + String.copyValueOf(respuesta));
	}
}
