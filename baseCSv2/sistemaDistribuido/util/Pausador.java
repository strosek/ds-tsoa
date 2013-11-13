package sistemaDistribuido.util;

public class Pausador{
  
  public static void pausa(long milisegundos){
    try{
      Thread.sleep(milisegundos);
    }catch(InterruptedException e){
      e.printStackTrace();
    }
  }
}
