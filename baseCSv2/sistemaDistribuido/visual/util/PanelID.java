package sistemaDistribuido.visual.util;

import java.awt.Panel;
import java.awt.Label;
import java.awt.TextField;

public class PanelID extends Panel{
  private static final long serialVersionUID=1;
  private TextField campoID;

  public PanelID(){
    campoID=new TextField(4);
    campoID.setEditable(false);
    add(new Label("ID de Proceso:"));
    add(campoID);
  }
  
  public String dameID(){
    return campoID.getText();
  }
  
  public void fijarID(int id){
    campoID.setText((new Integer(id)).toString());
  }
}