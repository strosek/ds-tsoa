/*
 * Hector Jeronimo Cuellar Villalobos 206514472.
 * Alejandro Duarte Sanchez 206587844.
 * Erick Daniel Corona Garcia 210224314. (D03)
 * 
 * TSOA D04.
 * 
 * Modificado para Proyecto Final.
 */

package sistemaDistribuido.visual.clienteServidor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ServidorNombres;

public class ServidorNombresFrame extends JFrame implements WindowListener{
    JPanel panel;
    static TextArea servidores;
    static TextArea log;
    public ServidorNombresFrame(){
        super("Servidor de Nombres");
        setSize(350, 500);
        setResizable(false);
        addWindowListener(this);
        setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        setContentPane(panel);
        servidores = new TextArea();
        servidores.setEditable(false);

        log = new TextArea();
        log.setEditable(false);
        log.setBackground(Color.LIGHT_GRAY);

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new GridLayout(1,3));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.add(new Label("ID",Label.LEFT));
        panelSuperior.add(new Label("Nombre",Label.LEFT));
        panelSuperior.add(new Label("IP",Label.LEFT));

        panel.add(panelSuperior,BorderLayout.NORTH);
        panel.add(servidores,BorderLayout.CENTER);
        panel.add(log,BorderLayout.SOUTH);
        ServidorNombres.inicializa();

    }
    public static void insertaServidor(String servidor, int id, String ip){
        servidores.append(id+"\t\t"+servidor+"\t\t"+ip+"\n");
    }
    public static void eliminarServidor(int id){
        int contador = 0; 
        int idInterfaz;
        int ascii = 48;
        char []aux = new char[servidores.getText().length()];

        idInterfaz = servidores.getText().charAt(0)- ascii;

        for(int i = 0 ; i < aux.length ; i++){

            if(id != idInterfaz){
                aux[contador]=servidores.getText().charAt(i);
                contador++;
            }
            if(  i < aux.length-1 && servidores.getText().charAt(i) == '\n' ){
                idInterfaz = servidores.getText().charAt(i+1)-ascii;
            }

        }
        servidores.setText(String.valueOf(aux));

    }
    public static void insertaLog(String mensaje){
        log.append(mensaje+"\n");
    }

    private static final long serialVersionUID = 1L;

    @Override
    public void windowActivated(WindowEvent arg0) {
    }

    @Override
    public void windowClosed(WindowEvent arg0) {
        System.exit(0);
    }

    @Override
    public void windowClosing(WindowEvent arg0) {
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }

}
