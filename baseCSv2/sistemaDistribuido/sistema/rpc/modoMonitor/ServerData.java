package sistemaDistribuido.sistema.rpc.modoMonitor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MachineProcessPair;

public class ServerData {
    String             m_name;
    String             m_version;
    MachineProcessPair m_handle;
    int                m_uniqueId;

    public ServerData() {
    }

    public ServerData(String name, String version, MachineProcessPair handle,
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
    public MachineProcessPair getHandle() {
        return m_handle;
    }
    public int getUniqueId() {
        return uniqueId;
    }

    public set() {
        m_
    }
}
