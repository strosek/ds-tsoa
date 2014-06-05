package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import sistemaDistribuido.sistema.clienteServidor.modoUsuario.*;
import sistemaDistribuido.util.IntByteConverter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;

public class HiloLadoCliente  extends Thread{
    int idorigen;
    DatagramSocket socketEmision;
    int puerto;
    byte [] mensajeCliente = new byte[1024];
    LinkedList<DatosProceso> TablaProcesosRemotos;
    MicroNucleo nucleo;
    ProcesoCliente cliente;

    public HiloLadoCliente(int idorigen, DatagramSocket socket,
            int puerto, LinkedList<DatosProceso> tabla, byte [] mensajeOriginal,
            MicroNucleo nucleo,Proceso cliente)
    {
        this.idorigen = idorigen;
        socketEmision = socket;
        this.puerto = puerto;
        TablaProcesosRemotos = tabla;
        System.arraycopy(mensajeOriginal, 0, mensajeCliente, 0, mensajeCliente.length);
        this.nucleo = nucleo;
        this.cliente =  (ProcesoCliente) cliente;
    }

    public void run()
    {
        byte [] messageLSA = new byte[1024];
        DatagramPacket paqueteLSA;
        byte codigoLSA = -2;
        byte arrayAux[] = new byte[4];

        messageLSA[9]= codigoLSA;

        nucleo.setOriginBytes(messageLSA, idorigen);
        nucleo.setDestinationBytes(messageLSA, 0);

        arrayAux = IntByteConverter.toBytes(248);
        for (int i =0; i<4; i++)// segundo campo es el receptor
        {
            messageLSA[ProcesoServidor.INDEX_SERVICE + i]= arrayAux[i];
        }

        Iterator<DatosProceso> lista;
        DatosProceso datos;
        boolean banderaEncontrarServer = false;
        int iddestino =0;
        String ip ="";

        int oportunidad = 0;
        while((oportunidad < 3) && (banderaEncontrarServer == false))
        {
            try {
                paqueteLSA = new DatagramPacket(messageLSA, messageLSA.length,
                        InetAddress.getByName(
                        InetAddress.getLocalHost().getHostAddress()),
                        puerto);
                System.out.println("HILO CLIENTE mensaje LSA enviado a la red");
                socketEmision.send(paqueteLSA);
            } 
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }


            try {
                sleep(5000);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            lista = TablaProcesosRemotos.iterator();
            banderaEncontrarServer = false;

            while( lista.hasNext( ) && (banderaEncontrarServer == false) )
            {
                datos = lista.next();
                if(  248 == datos.dameNumdeServicio()  )
                {
                    System.out.println("HILO CLIENTE se encotraron datos en " +
                            "tabla de procesos remotos despues de los " +
                            "envios LSA");
                    banderaEncontrarServer = true;
                    iddestino = datos.dameID();
                    ip = datos.dameIP();
                }
            }

            if(banderaEncontrarServer)
            {
                //hacer envio de la solicitud al server
                nucleo.setOriginBytes(mensajeCliente, idorigen);
                nucleo.setDestinationBytes(mensajeCliente, iddestino);

                DatagramPacket dp;
                try
                {
                    dp = new DatagramPacket(mensajeCliente,
                            mensajeCliente.length,
                            InetAddress.getByName(ip), puerto);
                    System.out.println("HILO CLIENTE  se jalan los datos de " +
                            "la tabla procesos remotos y se hace el envio se" +
                            "obtuvieron gracias a los LSA");
                    socketEmision.send(dp); 
                }
                catch(SocketException exce){
                    System.err.println("Error iniciando socket: "+
                                       exce.getMessage());
                }
                catch(UnknownHostException exce){
                    System.err.println("UnknownHostException: "+
                                       exce.getMessage());
                }
                catch(IOException exce){
                    System.err.println("IOException: "+exce.getMessage());
                }
            }
            else
            {
                oportunidad++;
            }



        }//fin de while de tres oportunidades de 5 sec.


        // AQUI HACER UNA CONDICION SI SE ENCONTRO O NO UN SERVER SI
        // NO SE ENCONTRO NINGUN SERVER
        // TENEMOS QUE DESPERTAR AL CLIENTE AVISANDOLE QUE NO HAY SERVIDOR
        if(banderaEncontrarServer == false)
        {
            cliente.avisodelHiloLSA = "POR EL MOMENTO NO SE ENCUENTRAN " +
                                      "SERVIDORES INTENTA EN OTRO MOMENTO";
            cliente.m_response[8] = 0;
            cliente.m_response[9] = 0;
            nucleo.reanudarProceso(cliente);
        }
    }

    public byte[] empaquetaEntero(int numero)
    {
        byte [] array = new byte[4];

        array[0] = (byte)numero;
        numero >>=8;
                array[1]= (byte)numero;
                numero >>=8;
        array[2]= (byte)numero;
        numero >>=8;
        array[3]= (byte)numero;

        return array;
    }
}
