package sistemaDistribuido.visual.clienteServidor;

import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoServidor;
import sistemaDistribuido.visual.clienteServidor.MicroNucleoFrame;
import sistemaDistribuido.visual.clienteServidor.ProcesoFrame;

public class ServidorFrame extends ProcesoFrame {
    private static final long serialVersionUID = 1;
    private ProcesoServidor proc;

    public ServidorFrame(MicroNucleoFrame frameNucleo,int alumno) {
        super(frameNucleo, "Servidor de Archivos");
        switch(alumno){
        case MicroNucleoFrame.CUELLAR_CLIENTE:
            proc = new ProcesoServidorCuellar(this);
            break;
        case MicroNucleoFrame.CORONA_CLIENTE:
            proc = new ProcesoServidorCorona(this);
            break;
        case MicroNucleoFrame.DUARTE_CLIENTE:
            proc = new ProcesoServidorDuarte(this);
            break;
        }
        fijarProceso(proc);
    }
}
