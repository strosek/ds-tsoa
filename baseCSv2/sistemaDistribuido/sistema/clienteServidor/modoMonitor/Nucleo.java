package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import microKernelBasedSystem.system.clientServer.userMode.threadPackage.SystemThread;

public class Nucleo{
	public static MicroNucleo nucleo=MicroNucleo.obtenerMicroNucleo();
	
	public static void iniciarSistema(Escribano wri, int puertoEntrada, ParMaquinaProceso pmp) {
		nucleo.iniciarSistema(wri, puertoEntrada, pmp);
	}

	public static void iniciarSistema(Escribano wri, int puertoEntrada) {
		nucleo.iniciarSistema(wri, puertoEntrada);
	}

	public static void receive(int addr, byte[] message) {
		nucleo.receive(addr, message);
	}

	public static void receiveNB(int addr, byte[] message) {
		nucleo.receiveNB(addr, message);
	}

	public static void reanudarProceso(Proceso p) {
		nucleo.reanudarProceso(p);
	}

	public static void send(int dest, byte[] message) {
		nucleo.send(dest, message);
	}

	public static void send(String dest, byte[] message) {
		nucleo.send(dest, message);
	}

	public static void sendNB(int dest, byte[] message) {
		nucleo.sendNB(dest, message);
	}

	public static void cerrarSistema() {
		nucleo.cerrarSistema();
	}

	public static void suspenderProceso() {
		nucleo.suspenderProceso();
	}
	
	public static int dameIdProceso(){
		return nucleo.dameIdProceso();
	}

	public static int dameIdHilo() {
		return nucleo.dameIdHilo();
	}

	public static void terminarHilo(SystemThread t){
		nucleo.terminarHilo(t);
	}

	/*
	 * solo para compatibilidad con versiones 2009A y anteriores
	 */
	public static void iniciarSistema(Escribano wri, int puertoEntrada, int puertoSalida, ParMaquinaProceso pmp) {
		nucleo.iniciarSistema(wri, puertoEntrada, pmp);
	}

	/*
	 * solo para compatibilidad con versiones 2009A y anteriores
	 */
	public static void iniciarSistema(Escribano wri, int puertoEntrada, int outputPort) {
		nucleo.iniciarSistema(wri, puertoEntrada);
	}
}
