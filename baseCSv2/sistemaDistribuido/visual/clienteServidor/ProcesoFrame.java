package sistemaDistribuido.visual.clienteServidor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.visual.clienteServidor.MicroNucleoFrame;
import sistemaDistribuido.visual.util.PanelInformador;
import sistemaDistribuido.visual.util.PanelID;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import microKernelBasedSystem.util.WriterManager;
import microKernelBasedSystem.util.Writer;

public class ProcesoFrame extends Frame implements WindowListener,Escribano{
	private static final long serialVersionUID=1;
	private Proceso proc;
	private MicroNucleoFrame frameNucleo;
	private WriterManager writerMan=new WriterManager(this);
	protected PanelInformador informador;
	protected PanelID identificador;

	public ProcesoFrame(MicroNucleoFrame frameNucleo,String nombre){
		super(nombre);
		this.frameNucleo=frameNucleo;
		informador=new PanelInformador();
		identificador=new PanelID();
		setLayout(new BorderLayout());
		add("North",identificador);
		add("Center",informador);
		addWindowListener(this);
		setSize(420,300);
		setVisible(true);
	}

	protected void fijarProceso(Proceso proc){
		this.proc=proc;
		identificador.fijarID(proc.dameID());
	}

	public void imprime(String s){
		informador.imprime(s);
	}

	public void imprimeln(String s){
		informador.imprimeln(s);
	}

	public final String dameIdProceso(){
		return identificador.dameID();
	}

	public void finalizar(){
		frameNucleo.cerrarFrameProceso(this);
	}

	public void windowClosing(WindowEvent e){
		if (proc!=null){
			Nucleo.terminarHilo(proc);
		}
		else{
			finalizar();
		}
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
}