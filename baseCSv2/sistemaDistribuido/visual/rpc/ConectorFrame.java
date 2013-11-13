package sistemaDistribuido.visual.rpc;

import sistemaDistribuido.sistema.rpc.modoUsuario.ProgramaConector;
import sistemaDistribuido.sistema.rpc.modoMonitor.RPC;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.CheckboxGroup;
import java.awt.Checkbox;
import java.awt.Button;
import java.awt.TextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

/**
* 
*/
class PanelConexion extends Panel{
  private static final long serialVersionUID=1;
  private Checkbox seleccion;
  private int id;
  
  /**
  * 
  */
  public PanelConexion(Checkbox seleccion,String nombreServidor,String version,String ip,String id,int identificacionUnica){
    Panel panelDescripcion=new Panel();
    this.seleccion=seleccion;
    this.id=identificacionUnica;
    setLayout(new BorderLayout());
    panelDescripcion.setLayout(new GridLayout(1,4));
    panelDescripcion.add(new Label(nombreServidor,Label.LEFT));
    panelDescripcion.add(new Label(version,Label.LEFT));
    panelDescripcion.add(new Label(ip,Label.LEFT));
    panelDescripcion.add(new Label(id,Label.LEFT));
    add(BorderLayout.WEST,seleccion);
    add(BorderLayout.CENTER,panelDescripcion);
  }
  
  /**
  * 
  */
  public Checkbox dameCheckbox(){
    return seleccion;
  }
  
  /**
  * 
  */
  public int dameIdPanel(){
    return id;
  }
}

public class ConectorFrame extends Frame implements DespleganteConexiones,WindowListener{
  private static final long serialVersionUID=1;
  private RPCFrame frameNucleo;
  private Panel panelConexiones=new Panel();
  private CheckboxGroup grupoConexiones=new CheckboxGroup();
  private Hashtable<Integer,PanelConexion> tablaConexiones=new Hashtable<Integer,PanelConexion>();
  private Hashtable<Checkbox,PanelConexion> tablaCheckbox=new Hashtable<Checkbox,PanelConexion>();
  private GridLayout layout;
  private String agregarStr="Agregar";
  private String removerStr="Remover de conector";
  private TextField campoNombre,campoVersion,campoIP,campoID;
  private ProgramaConector conector;
  private int claveEntrada=0;
  
  /**
  * 
  */
  public ConectorFrame(RPCFrame frameNucleo){
    super("Conector");
    this.frameNucleo=frameNucleo;
    panelConexiones.setLayout(layout=new GridLayout(1,1));
    add(BorderLayout.NORTH,construirPanelNorte());
    add(BorderLayout.CENTER,panelConexiones);
    add(BorderLayout.SOUTH,construirPanelSur());
    setSize(500,250);
    addWindowListener(this);
    conector=new ProgramaConector(this);
    RPC.asignarConector(conector);
  }
  
  /**
  * 
  */
  private Panel construirPanelNorte(){
    Panel panelNorte=new Panel();
    Panel panelDescripcion=new Panel();
    panelNorte.setLayout(new BorderLayout());
    panelDescripcion.setLayout(new GridLayout(1,4));
    panelDescripcion.add(new Label("SERVIDOR",Label.LEFT));
    panelDescripcion.add(new Label("VERSION",Label.LEFT));
    panelDescripcion.add(new Label("ASA IP",Label.LEFT));
    panelDescripcion.add(new Label("ASA ID",Label.LEFT));
    panelNorte.add(BorderLayout.WEST,new Label("  "));
    panelNorte.add(BorderLayout.CENTER,panelDescripcion);
    return panelNorte;
  }
  
  /**
  * 
  */
  private Panel construirPanelSur(){
    Panel panelSur=new Panel();
    Panel panelCampos=new Panel();
    Panel panelBotones=new Panel();
    Button botonAgregar=new Button(agregarStr);
    Button botonRemover=new Button(removerStr);
    ManejadorBotones mb=new ManejadorBotones();
    campoNombre=new TextField(10);
    campoVersion=new TextField(10);
    campoIP=new TextField(10);
    campoID=new TextField(10);
    panelSur.setLayout(new GridLayout(2,1));
    panelCampos.setLayout(new BorderLayout());
    Panel p=new Panel();
    p.setLayout(new GridLayout(1,4));
    p.add(campoNombre);
    p.add(campoVersion);
    p.add(campoIP);
    p.add(campoID);
    panelCampos.add(BorderLayout.WEST,new Label("  "));
    panelCampos.add(BorderLayout.CENTER,p);
    botonAgregar.addActionListener(mb);
    botonRemover.addActionListener(mb);
    panelBotones.add(botonAgregar);
    panelBotones.add(botonRemover);
    panelSur.add(panelCampos);
    panelSur.add(panelBotones);
    return panelSur;
  }
  
  private class ManejadorBotones implements ActionListener{
    
    /**
    * 
    */
    public void actionPerformed(ActionEvent e){
      String com=e.getActionCommand();
      
      if (com.equals(agregarStr)){
        agregarDesdeInterfazGrafica(campoNombre.getText(),campoVersion.getText(),campoIP.getText(),campoID.getText());
        campoNombre.setText("");
        campoVersion.setText("");
        campoIP.setText("");
        campoID.setText("");
      }
      else if (com.equals(removerStr)){
        removerDesdeInterfazGrafica();
      }
    }
  }
  
  /**
  * 
  */
  private int agregar(String nombreServidor,String version,String ip,String id){
    Checkbox check=new Checkbox("",true,grupoConexiones);
    PanelConexion p=new PanelConexion(check,nombreServidor,version,ip,id,++claveEntrada);
    Integer clave=new Integer(claveEntrada);
    int cant=panelConexiones.getComponentCount();
    tablaConexiones.put(clave,p);
    tablaCheckbox.put(check,p);
    if (cant>0){
      layout.setRows(cant+1);
    }
    panelConexiones.add(p);
    panelConexiones.validate();
    validate();
    return claveEntrada;
  }
  
  /**
  * 
  */
  private void remover(PanelConexion p){
    int cant=panelConexiones.getComponentCount();
    panelConexiones.remove(p);
    if (cant>1){
      layout.setRows(cant-1);
    }
    panelConexiones.validate();
    validate();
  }
  
  /**
  * 
  */
  private void agregarDesdeInterfazGrafica(String nombreServidor,String version,String ip,String idProceso){
    agregar(nombreServidor,version,ip,idProceso);   //devuelve un contador secuencial de agregados a la interfaz gr�fica
    //aqui se debe hacer el registro en el programa conector
  }
  
  /**
  * 
  */
  private void removerDesdeInterfazGrafica(){
    Checkbox check=grupoConexiones.getSelectedCheckbox();
    PanelConexion p=(PanelConexion)tablaCheckbox.remove(check);
    if (p!=null){
      tablaConexiones.remove(new Integer(p.dameIdPanel()));
      remover(p);
    }
    //aqui se debe hacer el deregistro en el programa conector
  }
  
  /**
  * 
  */
  public int agregarServidor(String nombreServidor,String version,String ip,String idProceso){
    return agregar(nombreServidor,version,ip,idProceso);
  }
  
  /**
  * 
  */
  public void removerServidor(int llaveInterfaz){
    PanelConexion p=(PanelConexion)tablaConexiones.remove(new Integer(llaveInterfaz));
    if (p!=null){
      tablaCheckbox.remove(p.dameCheckbox());
      remover(p);
    }
  }
  
  /**
  * 
  */
  public void finalizar() {
    frameNucleo.cerrarFrameConector();
  }
  
  /**
  * 
  */
  public void windowClosing(WindowEvent e) {
    conector.terminar();
  }
  
  public void windowDeactivated(WindowEvent e) {
  }
  public void windowClosed(WindowEvent e) {
  }
  public void windowDeiconified(WindowEvent e) {
  }
  public void windowOpened(WindowEvent e) {
  }
  public void windowIconified(WindowEvent e) {
  }
  public void windowActivated(WindowEvent e) {
  }
}
