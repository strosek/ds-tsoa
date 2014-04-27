/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 2.
 */

package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

public class MachineProcessPair implements ParMaquinaProceso {
	private String ip;
	private int    id;
	
	public MachineProcessPair() {
	}
	public MachineProcessPair(String ip, int id) {
	}

	@Override
	public String dameIP() {
		return ip;
	}

	@Override
	public int dameID() {
		return id;
	}
}
