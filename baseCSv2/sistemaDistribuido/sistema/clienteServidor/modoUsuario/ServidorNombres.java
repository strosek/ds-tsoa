/*
 * Hector Jeronimo Cuellar Villalobos 206514472.
 * Alejandro Duarte Sanchez 206587844.
 * Erick Daniel Corona Garcia 210224314. (D03)
 * 
 * TSOA D04.
 * 
 * Modificado para Proyecto Final.
 */

package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import java.util.Enumeration;
import java.util.Hashtable;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleo;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.DatosServidor;
import sistemaDistribuido.visual.clienteServidor.MicroNucleoFrame;
import sistemaDistribuido.visual.clienteServidor.ServidorNombresFrame;

public class ServidorNombres extends MicroNucleoFrame{
        static Hashtable<Integer, DatosServidor> servidores;
        static int id;

        private static final long serialVersionUID = 1L;
        public static void inicializa(){
                ServidorNombresFrame.insertaLog("Servidor De nombres creado\n");
                servidores = new Hashtable<Integer,DatosServidor>();
                id = 0;
        }
        public static int buscar(String nombreServidor){

                Enumeration<DatosServidor> elementos = servidores.elements();
                ServidorNombresFrame.insertaLog("Buscando Servidor\n");
                while(elementos.hasMoreElements()){
                        DatosServidor datos;
                        datos = elementos.nextElement();
                        if( datos.getNombreServidor() == nombreServidor ){
                                return MicroNucleo.registrarParMaquinaProceso(datos.getAsa());
                        }
                }
                ServidorNombresFrame.insertaLog("No se encontro Servidor\n");
                return -1; //no lo encontro
        }
        public static int alta(String nombre, ParMaquinaProceso asa){
                DatosServidor datos = new DatosServidor(nombre, asa);
                servidores.put(Integer.valueOf(id),datos);
                ParMaquinaProceso e = datos.getAsa();
                ServidorNombresFrame.insertaLog("Nuevo servidor agregado\n");
                ServidorNombresFrame.insertaServidor(datos.getNombreServidor(),e.dameID(),e.dameIP());
                id++;
                return (id-1);
        }
        public static boolean baja(String servidor, int id){
                DatosServidor datos = (DatosServidor) servidores.get(Integer.valueOf(id));
                if(servidor == datos.getNombreServidor() ){
                        servidores.remove(Integer.valueOf(id));
                        ServidorNombresFrame.insertaLog("Servidor con id: "+datos.getAsa().dameID()+ " fue Eliminado\n");
                        //TODO eliminar de la interfaz
                        ServidorNombresFrame.eliminarServidor(datos.getAsa().dameID());
                        return true;
                }
                return false;
        }
}
