package sistemaDistribuido.sistema.rpc.modoUsuario;

import java.util.Stack;

import sistemaDistribuido.util.Escribano;

public abstract class Libreria {
    private Escribano        esc;
    protected Stack<Integer> m_parametersStack;

    public Libreria(Escribano esc) {
        this.esc = esc;
    }

    protected void imprime(String s) {
        esc.imprime(s);
    }
    protected void imprimeln(String s) {
        esc.imprimeln(s);
    }

    /**
     * Ejemplo para el paso intermedio de parametros en pila. Esto es lo que
     * esta disponible como interfaz al usuario programador
     */

    protected abstract void summation();
    protected abstract void max();
    protected abstract void min();
    protected abstract void cube();

    protected int getParameterFromStack() {
        return m_parametersStack.pop().intValue();
    }

    protected int[] getParametersFromStack() {
        int nParameters = m_parametersStack.pop().intValue();

        int[] parameters = new int[nParameters];
        for (int i = 0; i < nParameters; ++i) {
            parameters[i] = m_parametersStack.pop().intValue();
        }

        return parameters;
    }
}
