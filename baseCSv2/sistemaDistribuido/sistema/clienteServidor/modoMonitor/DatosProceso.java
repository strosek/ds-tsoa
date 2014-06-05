package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

public class DatosProceso {
    private int servicio;
    private String ip;
    private int id;

    public DatosProceso(int servicio,String ip, int id)
    {
        this.servicio = servicio;
        this.ip = ip;
        this.id = id;
    }

    public int dameNumdeServicio()
    {
        return servicio;
    }

    public String dameIP()
    {
        return ip;
    }

    public int dameID()
    {
        return id;
    }
}
