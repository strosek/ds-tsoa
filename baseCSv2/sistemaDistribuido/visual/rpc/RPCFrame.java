package sistemaDistribuido.visual.rpc;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.visual.clienteServidor.MicroNucleoFrame;
import sistemaDistribuido.visual.rpc.PanelClienteServidorConector;
import sistemaDistribuido.visual.rpc.ClienteFrame;
import sistemaDistribuido.visual.rpc.ServidorFrame;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class RPCFrame extends MicroNucleoFrame{
	private static final long serialVersionUID=1;
	private PanelClienteServidorConector panelBotones;
	private ConectorFrame conector;

	public RPCFrame(){
		setTitle("Practicas 3 y 4 - Llamadas a Procedimientos Remotos (RPC)");
		conector=new ConectorFrame(this);
	}

	protected Panel construirPanelSur(){
		panelBotones=new PanelClienteServidorConector();
		panelBotones.agregarActionListener(new ManejadorBotones());
		return panelBotones;
	}

	class ManejadorBotones implements ActionListener{

		public void actionPerformed(ActionEvent e){
			String com=e.getActionCommand();
			if (com.equals("Cliente")){
				levantarProcesoFrame(new ClienteFrame(RPCFrame.this));
			}
			else if (com.equals("Servidor")){
				levantarProcesoFrame(new ServidorFrame(RPCFrame.this));
			}
			else if (com.equals("Conector")){
				conector.setVisible(true);
				panelBotones.dameBotonConector().setEnabled(false);
			}
		}
	}

	public void cerrarFrameConector(){
		conector.setVisible(false);
		panelBotones.dameBotonConector().setEnabled(true);
	}

	public static void main(String args[]){
		RPCFrame rpcf=new RPCFrame();
		rpcf.setVisible(true);
		rpcf.imprimeln("Ventana del micronucleo iniciada.");
		Nucleo.iniciarSistema(rpcf,2001,2002,rpcf);
	}
}
