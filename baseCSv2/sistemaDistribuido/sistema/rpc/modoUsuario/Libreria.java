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

    /**
     * Servidor suma verdadera generable por un compilador estandar o resguardo
     * de la misma por un compilador de resguardos.
     */
    protected abstract void suma();

    public int summation(int[] m_summationArgs) {
        // TODO: Poner en la pila y hacer llamada a libreria implementadora.
        imprimeln("Resultado : ");
        return 0;
    }

    public int max(int[] m_maxArgs) {
        return 0;
    }

    public int min(int[] m_minArgs) {
        return 0;
    }

    public int cube(int m_cubeArg) {
        return 0;
    }
}
