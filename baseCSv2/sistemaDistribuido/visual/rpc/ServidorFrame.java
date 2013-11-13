package sistemaDistribuido.visual.rpc;

import sistemaDistribuido.sistema.rpc.modoUsuario.ProcesoServidor;
import sistemaDistribuido.visual.clienteServidor.ProcesoFrame;

public class ServidorFrame extends ProcesoFrame{
  private static final long serialVersionUID=1;
  private ProcesoServidor proc;
  
  public ServidorFrame(RPCFrame frameNucleo){
    super(frameNucleo,"Servidor de Archivos");
    proc=new ProcesoServidor(this);
    fijarProceso(proc);
  }
}
