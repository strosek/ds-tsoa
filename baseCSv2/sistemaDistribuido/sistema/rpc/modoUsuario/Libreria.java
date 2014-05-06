/* Modificado para practica 3.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

import java.util.Stack;

import sistemaDistribuido.util.Escribano;

public abstract class Libreria {
    public static final int SIZE_PACKET = 1024;

    public static final int INDEX_OPCODE = 8;
    public static final int INDEX_DATALENGTH = INDEX_OPCODE + 1;
    public static final int INDEX_DATA = INDEX_DATALENGTH + 1;
    
    public static final byte OPCODE_SUMMATION = 0;
    public static final byte OPCODE_MAX =       1;
    public static final byte OPCODE_MIN =       2;
    public static final byte OPCODE_CUBE =      3;

    protected Stack<Integer> m_parametersStack;
    private Escribano        esc;

    public Libreria(Escribano esc) {
        this.esc = esc;
    }

    protected void imprime(String s) {
        esc.imprime(s);
    }
    protected void imprimeln(String s) {
        esc.imprimeln(s);
    }

    protected abstract void summation();
    protected abstract void max();
    protected abstract void min();
    protected abstract void cube();

    protected int[] getParametersFromStack() {
        int nParameters = m_parametersStack.pop().intValue();

        int[] parameters = new int[nParameters];
        for (int i = 0; i < nParameters; ++i) {
            parameters[i] = m_parametersStack.pop().intValue();
        }

        return parameters;
    }
}
