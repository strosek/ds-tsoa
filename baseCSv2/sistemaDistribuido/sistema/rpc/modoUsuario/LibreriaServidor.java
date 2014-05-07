/* Modificado para practica 3.
 * 
 * Erick Daniel Corona Garcia. 210224314. TSOA03.
 */

package sistemaDistribuido.sistema.rpc.modoUsuario;

import sistemaDistribuido.sistema.rpc.modoUsuario.Libreria;
import sistemaDistribuido.util.Escribano;

public class LibreriaServidor extends Libreria {

    public LibreriaServidor(Escribano esc) {
        super(esc);
    }


    public void summation() {
        int[] addends = getParametersFromStack();
        int sum = 0;

        for (int i = 0; i < addends.length; ++i) {
            sum += addends[i];
            System.out.println("libreriaServidor: parameter " + i + " "+ addends[i]);
        }
        System.out.println("libreriaServidor: result " + sum);
        m_parametersStack.push(Integer.valueOf(sum));
        m_parametersStack.push(Integer.valueOf(1)); // Size of result
    }
    public void max() {
        int[] numbers = getParametersFromStack();
        int max = numbers[0];
        for (int i = 1; i < numbers.length; ++i) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }
        m_parametersStack.push(Integer.valueOf(max));
        m_parametersStack.push(Integer.valueOf(1)); // Size of result
    }
    public void min() {
        int[] numbers = getParametersFromStack();
        int min = numbers[0];
        for (int i = 1; i < numbers.length; ++i) {
            if (numbers[i] < min) {
                min = numbers[i];
            }
        }
        m_parametersStack.push(Integer.valueOf(min));
        m_parametersStack.push(Integer.valueOf(1)); // Size of result
    }
    public void cube() {
        int[] a = getParametersFromStack();
        m_parametersStack.push(Integer.valueOf(a[0] * a[0] * a[0]));
        m_parametersStack.push(Integer.valueOf(1)); // Size of result
    }
}
