/* Modificado para practica 4.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;
import sistemaDistribuido.visual.rpc.DespleganteConexiones;

import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;

public class ProgramaConector {
    private DespleganteConexiones m_display;
    // Llaves que provee DespleganteConexiones
    private Hashtable<Integer, Object> m_conections;

    public ProgramaConector(DespleganteConexiones desplegante) {
        this.m_display = desplegante;
    }

    /**
     * Inicializar tablas en programa conector
     */
    public void inicializar() {
        m_conections = new Hashtable<Integer, Object>();
    }

    /**
     * Remueve tuplas visualizadas en la interfaz gr�fica registradas en tabla
     * conexiones
     */
    private void removerConexiones() {
        Set<Integer> s = m_conections.keySet();
        Iterator<Integer> i = s.iterator();
        while (i.hasNext()) {
            m_display.removerServidor((i.next()).intValue());
            i.remove();
        }
    }

    /**
     * Al solicitar que se termine el proceso, por si se implementa como tal
     */
    public void terminar() {
        removerConexiones();
        m_display.finalizar();
    }

    public int registro(String serverName, String serverVersion,
                        ParMaquinaProceso handle) {
        return 0;
    }
}
