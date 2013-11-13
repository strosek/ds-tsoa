package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;

/**
 * 
 */
public class ProcesoCliente extends Proceso{
	private Libreria lib;

	/**
	 * 
	 */
	public ProcesoCliente(Escribano esc){
		super(esc);
		lib=new LibreriaServidor(esc);  //primero debe funcionar con esta para subrutina servidor local
		//lib=new LibreriaCliente(esc);  //luego con esta comentando la anterior, para subrutina servidor remota
		start();
	}

	/**
	 * Programa Cliente
	 */
	public void run(){
		int sum1,sum2,minuendo,sustraendo,multiplicando,multiplicador,dividendo,divisor;

		sum1=8;
		sum2=7;
		minuendo=6;
		sustraendo=5;
		multiplicando=4;
		multiplicador=4;
		dividendo=2;
		divisor=1;

		imprimeln("Proceso cliente en ejecucion.");
		imprimeln("Esperando datos para continuar.");
		Nucleo.suspenderProceso();
		imprimeln("Salio de suspenderProceso");

		int resultado;
		resultado=lib.suma(sum1,sum2);
		imprimeln("suma="+resultado);
		resultado=lib.resta(minuendo,sustraendo);
		imprimeln("diferencia="+resultado);
		resultado=lib.multiplicacion(multiplicando,multiplicador);
		imprimeln("multiplicacion="+resultado);
		resultado=lib.division(dividendo,divisor);
		imprimeln("cociente="+resultado);

		imprimeln("Fin del cliente.");
	}
}
