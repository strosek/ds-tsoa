/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 2.
 */

package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

public class MachineProcessPair implements ParMaquinaProceso {
    private String m_ip;
    private int m_id;

    public MachineProcessPair() {
    }

    public MachineProcessPair(String ip, int id) {
        m_ip = ip;
        m_id = id;
    }

    @Override
    public String dameIP() {
        return m_ip;
    }

    @Override
    public int dameID() {
        return m_id;
    }
}
