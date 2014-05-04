package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.sistema.rpc.modoUsuario.Libreria;
import sistemaDistribuido.util.Escribano;

public class LibreriaServidor extends Libreria {

    public LibreriaServidor(Escribano esc) {
        super(esc);
    }

    /**
     * Ejemplo de servidor suma verdadera
     */
    protected void suma() {
        // saca parametros de pila

        // devuelve valor izquierdo
    }

    public int summation() {
        int[] addends = getParametersFromStack();
        int sum = 0;

        for (int i = 0; i < addends.length; ++i) {
            sum += addends[i];
        }

        return sum;
    }

    public int max() {
        int[] numbers = getParametersFromStack();
        int max = numbers[0];
        for (int i = 1; i < numbers.length; ++i) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }

        return max;
    }

    public int min() {
        int[] numbers = getParametersFromStack();
        int min = numbers[0];
        for (int i = 1; i < numbers.length; ++i) {
            if (numbers[i] < min) {
                min = numbers[i];
            }
        }

        return min;
    }

    public int cube() {
        int a = getParameterFromStack();
        return a * a * a;
    }

    private int getParameterFromStack() {
        return 0;
    }

    private int[] getParametersFromStack() {
        return null;
    }
}
