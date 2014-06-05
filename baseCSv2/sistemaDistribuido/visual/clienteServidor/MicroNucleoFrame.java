/*
 * Hector Jeronimo Cuellar Villalobos 206514472.
 * Alejandro Duarte Sanchez 206587844.
 * Erick Daniel Corona Garcia 210224314. (D03)
 * 
 * TSOA D04.
 * 
 * Modificado para Proyecto Final.
 */

package sistemaDistribuido.visual.clienteServidor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.Pausador;
import sistemaDistribuido.visual.clienteServidor.ProcesoFrame;
import sistemaDistribuido.visual.clienteServidor.ClienteFrame;
import sistemaDistribuido.visual.clienteServidor.ServidorFrame;
import sistemaDistribuido.visual.util.PanelInformador;
import sistemaDistribuido.visual.util.PanelIPID;
import microKernelBasedSystem.util.WriterManager;
import microKernelBasedSystem.util.Writer;

import java.awt.Button;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MicroNucleoFrame extends Frame implements WindowListener,Escribano,ParMaquinaProceso{
    private static final long serialVersionUID=1;
    private Panel panelBotones;
    private ProcesoFrame procesos[]=new ProcesoFrame[2];
    protected PanelInformador informador;
    protected PanelIPID destinatario;
    private WriterManager writerMan=new WriterManager(this);
    public Button [][]botones;
    public static final int CUELLAR_CLIENTE  = 0;
    public static final int CUELLAR_SERVIDOR = 1;
    public static final int CORONA_CLIENTE   = 2;
    public static final int CORONA_SERVIDOR  = 3;
    public static final int DUARTE_CLIENTE   = 4;
    public static final int DUARTE_SERVIDOR  = 5;

    public MicroNucleoFrame(){
        super("Proyecto Final");
        setLayout(new BorderLayout());
        informador=new PanelInformador();
        destinatario=new PanelIPID();
        add("North",destinatario);
        add("Center",informador);
        add("South",construirPanelSur());
        setSize(500,300);
        addWindowListener(this);
    }

    public void imprime(String s){
        informador.imprime(s);
    }

    public void imprimeln(String s){
        informador.imprimeln(s);
    }

    public String dameIP(){
        return destinatario.dameIP();
    }

    public int dameID(){
        int i=0;
        try{
            i=Integer.parseInt(destinatario.dameID());
        }catch(NumberFormatException e){
            imprimeln(e.getMessage());
        }
        return i;
    }

    protected Panel construirPanelSur(){

        botones = new Button[3][2];
        panelBotones = new Panel();

        panelBotones.setLayout(new GridLayout(3, 3));

        /*
         * Inicializa los botones de Alumnos
         */
        for( int i = 0 ; i < 3 ; i++ )
        {
            botones[i][0] = new Button("Cliente");
            botones[i][1] = new Button("Servidor");
        }

        panelBotones.add(new Label("Cuellar"));
        panelBotones.add(botones[0][0]);
        panelBotones.add(botones[0][1]);
        botones[0][0].addActionListener(new ManejadorBotones(CUELLAR_CLIENTE));
        botones[0][1].addActionListener(new ManejadorBotones(CUELLAR_SERVIDOR));

        panelBotones.add(new Label("Corona"));
        panelBotones.add(botones[1][0]);
        panelBotones.add(botones[1][1]);
        botones[1][0].addActionListener(new ManejadorBotones(CORONA_CLIENTE));
        botones[1][1].addActionListener(new ManejadorBotones(CORONA_SERVIDOR));

        panelBotones.add(new Label("Duarte"));
        panelBotones.add(botones[2][0]);
        panelBotones.add(botones[2][1]);
        botones[2][0].addActionListener(new ManejadorBotones(DUARTE_CLIENTE));
        botones[2][1].addActionListener(new ManejadorBotones(DUARTE_SERVIDOR));

        return panelBotones;

    }

    protected void levantarProcesoFrame(ProcesoFrame p){
        ProcesoFrame temp[];
        boolean encontro=false;
        int i;
        for(i=0;i<procesos.length;i++){
            if (procesos[i]==null){
                procesos[i]=p;
                encontro=true;
                imprimeln("Ventana de proceso "+procesos[i].dameIdProceso()+" iniciada.");
                break;
            }
        }
        if (!encontro){
            temp=new ProcesoFrame[procesos.length+1];
            for(i=0;i<procesos.length;i++){
                temp[i]=procesos[i];
            }
            temp[i]=p;
            procesos=temp;
        }
    }

    class ManejadorBotones implements ActionListener{
        private int boton;
        public ManejadorBotones(int solicitud){
            this.boton = solicitud;
        }
        public void actionPerformed(ActionEvent e){
            switch( boton ){
            case CUELLAR_CLIENTE:
                levantarProcesoFrame(new ClienteFrame(MicroNucleoFrame.this,CUELLAR_CLIENTE));
                break;
            case CUELLAR_SERVIDOR:
                levantarProcesoFrame(new ServidorFrame(MicroNucleoFrame.this,CUELLAR_SERVIDOR));
                break;
            case CORONA_CLIENTE:
                levantarProcesoFrame(new ClienteFrame(MicroNucleoFrame.this,CORONA_CLIENTE));
                break;
            case CORONA_SERVIDOR:
                levantarProcesoFrame(new ServidorFrame(MicroNucleoFrame.this,CORONA_SERVIDOR));
                break;
            case DUARTE_CLIENTE:
                levantarProcesoFrame(new ClienteFrame(MicroNucleoFrame.this,DUARTE_CLIENTE));
                break;
            case DUARTE_SERVIDOR:
                levantarProcesoFrame(new ServidorFrame(MicroNucleoFrame.this,DUARTE_SERVIDOR));
                break;
            }
        }
    }

    public void cerrarFrameProceso(ProcesoFrame pf){
        synchronized(procesos){
            for(int i=0;i<procesos.length;i++){
                if (procesos[i]!=null && procesos[i].equals(pf)){
                    imprimeln("Cerrando ventana del proceso "+pf.dameIdProceso());
                    Pausador.pausa(2000);
                    pf.setVisible(false);
                    procesos[i]=null;
                    break;
                }
            }
        }
    }

    public void finalizar(){
        imprimeln("Terminando ventana del micronucleo...");
        synchronized(procesos){
            for(int i=0;i<procesos.length;i++){
                if (procesos[i]!=null){
                    cerrarFrameProceso(procesos[i]);
                }
            }
        }
        Pausador.pausa(2000);
        System.exit(0);
    }

    public void windowClosing(WindowEvent e){
        Nucleo.cerrarSistema();
    }

    public void windowActivated(WindowEvent e){
    }
    public void windowClosed(WindowEvent e){
    }
    public void windowDeactivated(WindowEvent e){
    }
    public void windowDeiconified(WindowEvent e){
    }
    public void windowIconified(WindowEvent e){
    }
    public void windowOpened(WindowEvent e){
    }

    public void finish() {
        finalizar();
    }

    public void print(String s) {
        imprime(s);
    }

    public void println(String s) {
        imprimeln(s);
    }

    public WriterManager getWriterManager() {
        return writerMan;
    }

    public void appendWriter(Writer w){
    }

    public static void main(String args[]){
        MicroNucleoFrame mnf=new MicroNucleoFrame();
        ServidorNombresFrame sn = new ServidorNombresFrame();

        mnf.setVisible(true);
        sn.setVisible(true);

        mnf.imprimeln("Ventana del micronucleo iniciada.");
        Nucleo.iniciarSistema(mnf,2001,2002,mnf);
    }
}
