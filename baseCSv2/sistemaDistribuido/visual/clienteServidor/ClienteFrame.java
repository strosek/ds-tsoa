package sistemaDistribuido.visual.clienteServidor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoCliente;
import sistemaDistribuido.visual.clienteServidor.MicroNucleoFrame;
import sistemaDistribuido.visual.clienteServidor.ProcesoFrame;
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
    private ProcesoCliente proc;
    private Choice codigosOperacion;
    private TextField campoMensaje;
    private Button botonSolicitud;
    private String codop1, codop2, codop3, codop4;

    public ClienteFrame(MicroNucleoFrame frameNucleo, int alumno) {
        super(frameNucleo, "Cliente de Archivos");
        add("South", construirPanelSolicitud());
        validate();
        
        switch(alumno){
        case MicroNucleoFrame.CUELLAR_CLIENTE:
            proc = new ProcesoClienteCuellar(this);
            break;
        case MicroNucleoFrame.CORONA_CLIENTE:
            proc = new ProcesoClienteCorona(this);
            break;
        case MicroNucleoFrame.DUARTE_CLIENTE:
            proc = new ProcesoClienteDuarte(this);
            break;
        }
        
        fijarProceso(proc);
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
                proc.setCodop(codigosOperacion.getSelectedIndex());
                imprimeln("Mensaje a enviar: " + campoMensaje.getText());
                proc.setMessage(campoMensaje.getText());
                Nucleo.reanudarProceso(proc);
            }
        }
    }
}
