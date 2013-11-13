package sistemaDistribuido.visual.util;

import java.awt.Panel;
import java.awt.TextArea;
import java.awt.BorderLayout;

public class PanelInformador extends Panel{
  private static final long serialVersionUID=1;
  private TextArea areaSucesos;

  public PanelInformador(){
    setLayout(new BorderLayout());
    areaSucesos=new TextArea();
    areaSucesos.setEditable(false);
    add("Center",areaSucesos);
  }
  
  public void imprime(String s){
    areaSucesos.append(s);
  }
  
  public void imprimeln(String s){
    areaSucesos.append(s+"\n");
  }
}