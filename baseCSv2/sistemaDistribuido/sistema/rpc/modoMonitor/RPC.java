/* Modificado para practica 4.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoMonitor;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.ParMaquinaProceso;
import sistemaDistribuido.sistema.rpc.modoUsuario.ProgramaConector;

public class RPC {
    private static ProgramaConector m_connector;

    public static void asignarConector(ProgramaConector con) {
        m_connector = con;
        m_connector.inicializar();
    }

    /**
     * Efectua la llamada de busqueda en el conector. Regresa un dest para la
     * llamada a send(dest,message).
     */
    public static int importarInterfaz(String name, String version) {
        // asa=conector.busqueda()
        return 0;
    }

    /**
     * Efectua la llamada a registro en el conector. Regresa una
     * uniqueId para el deregistro.
     */
    public static int exportarInterfaz(String name, String version,
                                       ParMaquinaProceso asa) {
        return m_connector.registro(name, version, asa);
    }

    /**
     * Efectua la llamada a deregistro en el conector. Regresa el status del
     * deregistro, true significa llevado a cabo.
     */
    public static boolean deregistrarInterfaz(String name, String version,
                                              int uniqueId) {
        boolean isServerRemoved = m_connector.deregistro(name, version,
                                                         uniqueId);
        // TODO define a place to put this code.
//        if (isServerRemoved) {
//            Nucleo.imprimenl("Exito removiendo el servidor: " + uniqueId);
//        }
//        else {
//            Nucleo.imprimenl("Error removiendo el servidor: " + uniqueId);
//        }
        return isServerRemoved;
    }
}
