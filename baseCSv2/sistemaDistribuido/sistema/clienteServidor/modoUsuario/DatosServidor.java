package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;

public class DatosServidor {
	String nombreServidor;
	String version;
	ParMaquinaProceso asa;
	
	public DatosServidor(String nombreServidor, String version, ParMaquinaProceso asa){
		this.nombreServidor = nombreServidor;
		this.version = version;
		this.asa = asa;
	}
	public DatosServidor(String nombreServidor, ParMaquinaProceso asa){
		this.nombreServidor = nombreServidor;
		this.asa = asa;
	}
	public void setNombreServidor(String nombreServidor){
		this.nombreServidor = nombreServidor;
	}
	public void setVersion(String version){
		this.version = version;
	}
	public void setAsa(ParMaquinaProceso asa){
		this.asa = asa;
	}
	public String getNombreServidor(){
		return this.nombreServidor;
	}
	public ParMaquinaProceso getAsa(){
		return this.asa;
	}
	public String getVersion(){
		return this.version;
	}
}
