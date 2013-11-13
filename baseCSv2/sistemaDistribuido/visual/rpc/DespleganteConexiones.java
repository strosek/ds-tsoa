package sistemaDistribuido.visual.rpc;

public interface DespleganteConexiones{
  
  public int agregarServidor(String nombreServidor,String version,String ip,String id);
  public void removerServidor(int clave);
  public void finalizar();
}
