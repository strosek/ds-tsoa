package sistemaDistribuido.util;

import microKernelBasedSystem.util.Writer;

public interface Escribano extends Writer{
	public void imprime(String s);
	public void imprimeln(String s);
	public void finalizar();
}
