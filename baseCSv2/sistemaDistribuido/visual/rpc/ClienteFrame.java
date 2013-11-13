package sistemaDistribuido.visual.rpc;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.rpc.modoUsuario.ProcesoCliente;
import sistemaDistribuido.visual.clienteServidor.ProcesoFrame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Label;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClienteFrame extends ProcesoFrame{
	private static final long serialVersionUID=1;
	private ProcesoCliente proc;
	private TextField campo1,campo2,campo3,campo4;
	private Button botonSolicitud;

	public ClienteFrame(RPCFrame frameNucleo){
		super(frameNucleo,"Cliente de Archivos");
		add("South",construirPanelSolicitud());
		validate();
		proc=new ProcesoCliente(this);
		fijarProceso(proc);
	}

	public Panel construirPanelSolicitud(){
		Panel pSolicitud,pcodop1,pcodop2,pcodop3,pcodop4,pboton;
		pSolicitud=new Panel();
		pcodop1=new Panel();
		pcodop2=new Panel();
		pcodop3=new Panel();
		pcodop4=new Panel();
		pboton=new Panel();
		campo1=new TextField(10);
		campo2=new TextField(10);
		campo3=new TextField(10);
		campo4=new TextField(10);
		pSolicitud.setLayout(new GridLayout(5,1));

		pcodop1.add(new Label("CREAR >> "));
		pcodop1.add(new Label("Param 1:"));
		pcodop1.add(campo1);

		pcodop2.add(new Label("LEER >> "));
		pcodop2.add(new Label("Param 1:"));
		pcodop2.add(campo2);

		pcodop3.add(new Label("ESCRIBIR >> "));
		pcodop3.add(new Label("Param 1:"));
		pcodop3.add(campo3);

		pcodop4.add(new Label("ELIMINAR >> "));
		pcodop4.add(new Label("Param 1:"));
		pcodop4.add(campo4);

		botonSolicitud=new Button("Solicitar");
		pboton.add(botonSolicitud);
		botonSolicitud.addActionListener(new ManejadorSolicitud());

		pSolicitud.add(pcodop1);
		pSolicitud.add(pcodop2);
		pSolicitud.add(pcodop3);
		pSolicitud.add(pcodop4);
		pSolicitud.add(pboton);

		return pSolicitud;
	}

	class ManejadorSolicitud implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			String com=e.getActionCommand();
			if (com.equals("Solicitar")){
				botonSolicitud.setEnabled(false);
				//...
				Nucleo.reanudarProceso(proc);
			}
		}
	}
}
