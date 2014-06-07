package sistemaDistribuido.visual.clienteServidor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.visual.clienteServidor.MicroNucleoFrame;
import sistemaDistribuido.visual.clienteServidor.ProcesoFrame;

import corona.ProcesoClienteCorona;
import cuellar.ProcesoClienteCuellar;
import duarte.ProcesoClienteDuarte;

import java.awt.Label;
import java.awt.TextField;
import java.awt.Choice;
import java.awt.Button;
import java.awt.Panel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClienteFrame extends ProcesoFrame {
    public static final int CODOP_CREATE = 0;
    public static final int CODOP_DELETE = 1;
    public static final int CODOP_READ = 2;
    public static final int CODOP_WRITE = 3;

    private static final long serialVersionUID = 1;
  
    private Choice codigosOperacion;
    private TextField campoMensaje;
    private Button botonSolicitud;
    private String codop1, codop2, codop3, codop4;
    
    private ProcesoClienteCuellar procCuellar;
    private ProcesoClienteCorona procCorona;
    private ProcesoClienteDuarte procDuarte;
    
    public ClienteFrame(MicroNucleoFrame frameNucleo, int alumno) {
        super(frameNucleo, "Cliente de Archivos");
        add("South", construirPanelSolicitud());
        validate();
        System.out.println(alumno);
        switch(alumno){
        
        case MicroNucleoFrame.CUELLAR_CLIENTE:
        	
        	procCuellar = new ProcesoClienteCuellar(this,botonSolicitud);
            fijarProceso(procCuellar);
            break;
        case MicroNucleoFrame.CORONA_CLIENTE:
        	procCorona = new ProcesoClienteCorona(this);
            fijarProceso(procCorona);
            break;
        case MicroNucleoFrame.DUARTE_CLIENTE:
        	procDuarte = new ProcesoClienteDuarte(this);
            fijarProceso(procDuarte);
            break;
        }
    
    }

    public Panel construirPanelSolicitud() {
        Panel p = new Panel();
        codigosOperacion = new Choice();
        codop1 = "Crear";
        codop2 = "Eliminar";
        codop3 = "Leer";
        codop4 = "Escribir";
        codigosOperacion.add(codop1);
        codigosOperacion.add(codop2);
        codigosOperacion.add(codop3);
        codigosOperacion.add(codop4);
        campoMensaje = new TextField(10);
        botonSolicitud = new Button("Solicitar");
        botonSolicitud.addActionListener(new ManejadorSolicitud());
        p.add(new Label("Operacion:"));
        p.add(codigosOperacion);
        p.add(new Label("Datos:"));
        p.add(campoMensaje);
        p.add(botonSolicitud);
        return p;
    }

    class ManejadorSolicitud implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String com = e.getActionCommand();
            if (com.equals("Solicitar")) {
                botonSolicitud.setEnabled(false);
                com = codigosOperacion.getSelectedItem();
                imprimeln("Solicitud a enviar: " + com);
                if( procCorona != null ){
                	procCorona.setCodop(codigosOperacion.getSelectedIndex());
                    imprimeln("Mensaje a enviar: " + campoMensaje.getText());
                    procCorona.setMessage(campoMensaje.getText());
                    Nucleo.reanudarProceso(procCorona);
                }
	            else if( procCuellar != null ){
	            	imprimeln("Solicitud a enviar: "+com);
					imprimeln("Mensaje a enviar: "+campoMensaje.getText());
					procCuellar.capturarSolicitud(com,campoMensaje.getText());
					Nucleo.reanudarProceso(procCuellar);
	            }
	            else if( procDuarte != null ){
					imprimeln("Solicitud a enviar: "+com);
					imprimeln("Mensaje a enviar: "+campoMensaje.getText());
					procDuarte.entradaInterfaz(codigosOperacion.getSelectedIndex(), campoMensaje.getText());
					Nucleo.reanudarProceso(procDuarte);
	            }
            }
        }
    }
}
