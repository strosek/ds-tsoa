package sistemaDistribuido.visual.rpc;

import sistemaDistribuido.visual.clienteServidor.PanelClienteServidor;
import java.awt.Button;
import java.awt.event.ActionListener;

public class PanelClienteServidorConector extends PanelClienteServidor{
  private static final long serialVersionUID=1;
  private Button botonConector;
  
  public PanelClienteServidorConector(){
    botonConector=new Button("Conector");
    add(botonConector);
  }
  
  public Button dameBotonConector(){
    return botonConector;
  }
  
  public void agregarActionListener(ActionListener al){
    super.agregarActionListener(al);
    botonConector.addActionListener(al);
  }
}
