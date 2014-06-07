package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
//import java.io.PrintWriter;
import java.io.RandomAccessFile;
//import javax.swing.JOptionPane;

/**
 * Hector Jeronimo Cuellar Villalobos
 * 206514472
 */
public class GestionArchivo {
	
	public boolean eliminarArchivo(File archivo)
	{
		 if (( archivo== null ))
	        {
	            return false;
	        }
		 else
		 {
			 archivo.delete();
			 return true;
		 }
		
		 //return false;
	}
    
    
    public String abrirArchivo(File archivoDeseado)
    {
        String texto = "";
        if ( !archivoDeseado.exists() )
        {
        	//implementar este metodo para todas las operaciones
        }

        else{
            String linea = "";
           
                try
                {

                 RandomAccessFile DIS = new RandomAccessFile(archivoDeseado, "rw");//  r (modo lectura)  rw(lectura escritura)

                 while((linea = DIS.readLine())!=null)
                   {
                	 texto += linea + "\n";
                   }
                   DIS.close();
                }
                  catch(IOException e)
                   {
                   //System.out.println("no pude abrir el archivo");
                      // JOptionPane.showMessageDialog(null,"No se puede abrir el archivo","No se puede abrir el archivo",JOptionPane.ERROR_MESSAGE);
                   }


        }
        
        return texto;
    }
    
    
    public void crearArchivo(File direccion , String salida)
    {
    	String nombre = direccion.getName();
    	try
        {
            FileOutputStream tab = new FileOutputStream ( nombre );
            tab.close();
        }
        catch(IOException e)
        {
           // JOptionPane.showMessageDialog(null,"No se pudo crear el archivo TABSIM","No se pudo crear el archivo TABSIM",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    public void escribirArchivo(File direccion , String salida)
    {
    	String nombre = direccion.getName();
    	try
        {
            FileOutputStream tab=new FileOutputStream ( nombre ,true);
            PrintStream escritor = new PrintStream(tab);
            
            escritor.println(salida);
            escritor.close();
        }
        catch(IOException e)
        {
           // JOptionPane.showMessageDialog(null,"No se pudo crear el archivo TABSIM","No se pudo crear el archivo TABSIM",JOptionPane.ERROR_MESSAGE);
        }
    }

        

        
    
}