/* Modificado para practica 4.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoMonitor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;


public class ServerData {
    String            m_name;
    String            m_version;
    ParMaquinaProceso m_handle;
    int               m_uniqueId;

    public ServerData() {
    }

    public ServerData(String name, String version, ParMaquinaProceso handle,
                      int uniqueId) {
        m_name = name;
        m_version = version;
        m_handle = handle;
        m_uniqueId = uniqueId;
    }

    public String getName() {
        return m_name;
    }
    public String getVersion() {
        return m_version;
    }
    public ParMaquinaProceso getHandle() {
        return m_handle;
    }
    public int getUniqueId() {
        return m_uniqueId;
    }

    public void setName(String name) {
        m_name = name;
    }
    public void setVersion(String version) {
        m_version = version;
    }
    public void setHandle(ParMaquinaProceso handle) {
        m_handle = handle;
    }
    public void setUniqueId(int uniqueId) {
        m_uniqueId = uniqueId;
    }
}
