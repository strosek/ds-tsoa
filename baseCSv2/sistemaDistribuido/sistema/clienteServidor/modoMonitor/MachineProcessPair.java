package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

public class MachineProcessPair implements ParMaquinaProceso {
	private String m_ip;
	private int    m_id;
	
	public MachineProcessPair() {
	}

	public MachineProcessPair(String ip, int id) {
		m_ip = ip;
		m_id = id;
	}
	
	public void setIp(String ip) {
		m_ip = ip;
	}
	public void setId(int id) {
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
