package sistemaDistribuido.visual.rpc;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.rpc.modoUsuario.ProcesoCliente;
import sistemaDistribuido.visual.clienteServidor.ProcesoFrame;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.Button;
import java.awt.Label;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ClienteFrame extends ProcesoFrame {
    private static final long serialVersionUID = 1;

    private ProcesoCliente m_proc;
    private TextField      m_summationField;
    private TextField      m_maxField;
    private TextField      m_minField;
    private TextField      m_cubeField;
    private Button         m_botonSolicitud;

    public ClienteFrame(RPCFrame frameNucleo) {
        super(frameNucleo, "Cliente de Archivos");
        add("South", construirPanelSolicitud());
        validate();
        m_proc = new ProcesoCliente(this);
        fijarProceso(m_proc);
    }

    public Panel construirPanelSolicitud() {
        Panel pSolicitud, pcodop1, pcodop2, pcodop3, pcodop4, pboton;
        pSolicitud = new Panel();
        pcodop1 = new Panel();
        pcodop2 = new Panel();
        pcodop3 = new Panel();
        pcodop4 = new Panel();
        pboton = new Panel();
        m_maxField = new TextField(10);
        m_minField = new TextField(10);
        m_cubeField = new TextField(10);
        m_summationField = new TextField(10);
        pSolicitud.setLayout(new GridLayout(5, 1));

        pcodop1.add(new Label("MAXIMO"));
        pcodop1.add(new Label("Params (n):"));
        pcodop1.add(m_maxField);

        pcodop2.add(new Label("MINIMO"));
        pcodop2.add(new Label("Params (n):"));
        pcodop2.add(m_minField);

        pcodop3.add(new Label("CUBO"));
        pcodop3.add(new Label("Params (1):"));
        pcodop3.add(m_cubeField);

        pcodop4.add(new Label("SUMATORIA"));
        pcodop4.add(new Label("Param (n):"));
        pcodop4.add(m_summationField);

        m_botonSolicitud = new Button("Solicitar");
        pboton.add(m_botonSolicitud);
        m_botonSolicitud.addActionListener(new ManejadorSolicitud());

        pSolicitud.add(pcodop1);
        pSolicitud.add(pcodop2);
        pSolicitud.add(pcodop3);
        pSolicitud.add(pcodop4);
        pSolicitud.add(pboton);

        return pSolicitud;
    }

    class ManejadorSolicitud implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String com = e.getActionCommand();
            if (com.equals("Solicitar")) {
                m_proc.setSummationArgs(
                        getArgsFromString(m_summationField.getText()));
                m_proc.setMaxArgs(getArgsFromString(m_maxField.getText()));
                m_proc.setMinArgs(getArgsFromString(m_minField.getText()));
                m_proc.setCubeArg(Integer.parseInt(m_cubeField.getText()));

                m_botonSolicitud.setEnabled(false);

                Nucleo.reanudarProceso(m_proc);
            }
        }

        public int[] getArgsFromString(String inputString) {
            String[] argsStringArray = inputString.split(" ");
            int[] argsArray = new int[argsStringArray.length];
            for (int i = 0; i < argsArray.length; ++i) {
                argsArray[i] = Integer.parseInt(argsStringArray[i]);
            }

            return argsArray;
        }
    }
}
