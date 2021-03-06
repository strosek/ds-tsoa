/* Modificado para practica 3.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.Pausador;

public class ProcesoCliente extends Proceso {
    private Libreria m_mathLib;
    private int[]    m_summationArgs;
    private int[]    m_maxArgs;
    private int[]    m_minArgs;
    private int      m_cubeArg;

    public ProcesoCliente(Escribano esc) {
        super(esc);

        // luego con esta comentando la anterior, para subrutina servidor remota
        m_mathLib = new LibreriaCliente(esc);

        start();
    }

    public void setSummationArgs(int[] args) {
        m_summationArgs = args;
    }
    public void setMaxArgs(int[] args) {
        m_maxArgs = args;
    }
    public void setMinArgs(int[] args) {
        m_minArgs = args;
    }
    public void setCubeArg(int arg) {
        m_cubeArg = arg;
    }

    public void run() {
        imprimeln("Proceso cliente en ejecucion.");
        imprimeln("Esperando datos para continuar.");
        Nucleo.suspenderProceso();
        imprimeln("Salio de suspenderProceso");

        int result;
        result = m_mathLib.summation(m_summationArgs);
        imprimeln("Resultado sumatoria: " + result);
        Pausador.pausa(500);

        result = m_mathLib.max(m_maxArgs);
        imprimeln("Resultado maximo: " + result);
        Pausador.pausa(500);
        
        result = m_mathLib.min(m_minArgs);
        imprimeln("Resultado minimio: " + result);
        Pausador.pausa(500);

        result = m_mathLib.cube(m_cubeArg);
        imprimeln("Resultado cubo: " + result);

        imprimeln("Fin del cliente.");
    }

}
