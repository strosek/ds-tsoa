package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.visual.rpc.DespleganteConexiones;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;

public class ProgramaConector{
	private DespleganteConexiones desplegante;
	private Hashtable<Object,Object> conexiones;   //las llaves que provee DespleganteConexiones

	/**
	 * 
	 */
	public ProgramaConector(DespleganteConexiones desplegante){
		this.desplegante=desplegante;
	}

	/**
	 * Inicializar tablas en programa conector
	 */
	public void inicializar(){
		conexiones=new Hashtable<Object,Object>();
	}

	/**
	 * Remueve tuplas visualizadas en la interfaz gr�fica registradas en tabla conexiones
	 */
	private void removerConexiones(){
		Set<Object> s=conexiones.keySet();
		Iterator<Object> i=s.iterator();
		while(i.hasNext()){
			desplegante.removerServidor(((Integer)i.next()).intValue());
			i.remove();
		}
	}

	/**
	 * Al solicitar que se termine el proceso, por si se implementa como tal
	 */
	public void terminar() {
		removerConexiones();
		desplegante.finalizar();
	}
}
