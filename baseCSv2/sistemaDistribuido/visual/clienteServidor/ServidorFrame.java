package sistemaDistribuido.visual.clienteServidor;

import sistemaDistribuido.corona.ProcesoServidorCorona;
import sistemaDistribuido.cuellar.ProcesoServidorCuellar;
import sistemaDistribuido.duarte.ProcesoServidorDuarte;
import sistemaDistribuido.visual.clienteServidor.MicroNucleoFrame;
import sistemaDistribuido.visual.clienteServidor.ProcesoFrame;

public class ServidorFrame extends ProcesoFrame {
    private static final long serialVersionUID = 1;
    private ProcesoServidorCuellar proc1;
    private ProcesoServidorCorona proc2;
    private ProcesoServidorDuarte proc3;
    public ServidorFrame(MicroNucleoFrame frameNucleo,int alumno) {
        super(frameNucleo, "Servidor de Archivos");
        switch(alumno){
        case MicroNucleoFrame.CUELLAR_SERVIDOR:
            proc1 = new ProcesoServidorCuellar(this);
            fijarProceso(proc1);
            break;
        case MicroNucleoFrame.CORONA_SERVIDOR:
        	
        	proc2 = new ProcesoServidorCorona(this);
            fijarProceso(proc2);
            break;
        case MicroNucleoFrame.DUARTE_SERVIDOR:
        	
            proc3 = new ProcesoServidorDuarte(this);
            fijarProceso(proc3);
            break;
        }
        
    }
}
