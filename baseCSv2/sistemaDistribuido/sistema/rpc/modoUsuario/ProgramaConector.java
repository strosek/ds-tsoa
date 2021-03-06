/* Modificado para practica 4.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;
import sistemaDistribuido.sistema.rpc.modoMonitor.ServerData;
import sistemaDistribuido.visual.rpc.DespleganteConexiones;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;

public class ProgramaConector {
    private DespleganteConexiones m_display;
    private Hashtable<Integer, ServerData> m_connections;

    public ProgramaConector(DespleganteConexiones desplegante) {
        this.m_display = desplegante;
    }

    /**
     * Inicializar tablas en programa conector
     */
    public void inicializar() {
        m_connections = new Hashtable<Integer, ServerData>();
    }

    /**
     * Remueve tuplas visualizadas en la interfaz gr�fica registradas en tabla
     * conexiones
     */
    private void removerConexiones() {
        Set<Integer> s = m_connections.keySet();
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
        int uniqueId = m_display.agregarServidor(serverName, serverVersion,
                handle.dameIP(), Integer.toString(handle.dameID()));
        m_connections.put(Integer.valueOf(uniqueId), new ServerData(serverName, 
                serverVersion, handle, uniqueId));

        return uniqueId;
    }

    public boolean deregistro(String name, String version, int uniqueId) {
        boolean isServerRemoved = false;

        ServerData server;
        Enumeration<ServerData> servers = m_connections.elements();
        while (servers.hasMoreElements() && !isServerRemoved) {
            server = servers.nextElement();
            if (server.getName().equals(name) &&
                server.getVersion().equals(version)) {
                m_connections.remove(uniqueId);
                m_display.removerServidor(uniqueId);
                isServerRemoved = true;
            }
        }

        return isServerRemoved;
    }

    public ParMaquinaProceso busqueda(String name, String version) {
        Vector<ParMaquinaProceso> results = new Vector<ParMaquinaProceso>();

        ServerData server;
        Enumeration<ServerData> servers = m_connections.elements();
        while (servers.hasMoreElements()) {
            server = servers.nextElement();
            if (server.getName().equals(name) &&
                server.getVersion().equals(version)) {
                results.add(server.getHandle());
            }
        }

        ParMaquinaProceso result = null;
        int vectorIndex = 0;

        if (results.size() > 1) {
            Random random = new Random();
            vectorIndex = random.nextInt() % results.size();
            if (vectorIndex < 0) {
                vectorIndex = -vectorIndex;
            }

            System.out.println("size: " + results.size());
            System.out.println("index: " + vectorIndex);
        }

        result = results.get(vectorIndex);

        return result;
    }
}
