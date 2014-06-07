/*
 * Hector Jeronimo Cuellar Villalobos 206514472.
 * Alejandro Duarte Sanchez 206587844.
 * Erick Daniel Corona Garcia 210224314. (D03)
 * 
 * TSOA D04.
 * 
 * Modificado para Proyecto Final.
 */

package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleoBase;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoCliente;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoServidor;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ResendThread;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ServidorNombres;
import sistemaDistribuido.util.IntByteConverter;

public final class MicroNucleo extends MicroNucleoBase {
    private static MicroNucleo nucleo = new MicroNucleo();
    private static Hashtable<Integer, ParMaquinaProceso> m_emissionTable;
    private Hashtable<Integer, byte[]>            m_receptionTable;
    private Hashtable<Integer, RequestsMailbox>   m_mailboxesTable;
    private LinkedList<DatosProceso> TablaDireccionamientoProcesosRemotos;
    private LinkedList<DatosProceso> TablaDireccionamientoProcesosLocales;
    private Proceso referenciaACliente;

    private MicroNucleo() {
        m_emissionTable = new Hashtable<Integer, ParMaquinaProceso>();
        m_receptionTable = new Hashtable<Integer, byte[]>();
        m_mailboxesTable = new Hashtable<Integer, RequestsMailbox>();
        TablaDireccionamientoProcesosLocales = new LinkedList<DatosProceso>();
        TablaDireccionamientoProcesosRemotos = new LinkedList<DatosProceso>();
    }

    public final static MicroNucleo obtenerMicroNucleo() {
        return nucleo;
    }

    /*
     * Metodos para probar el paso de mensajes entre los procesos cliente y
     * servidor en ausencia de datagramas. Esta es una forma incorrecta de
     * programacion "por uso de variables globales" (en este caso atributos de
     * clase) ya que, para empezar, no se usan ambos parametros en los metodos y
     * fallaria si dos procesos invocaran simultaneamente a receiveFalso() al
     * reescriir el atributo mensaje
     */
    byte[] mensaje;

    public void sendFalso(int dest, byte[] message) {
        System.arraycopy(message, 0, mensaje, 0, message.length);

        // Reanuda la ejecucion del proceso que haya invocado a receiveFalso()
        notificarHilos();
    }

    public void receiveFalso(int addr, byte[] message) {
        mensaje = message;
        suspenderProceso();
    }

    protected boolean iniciarModulos() {
        return true;
    }

    protected void sendVerdadero(int dest,byte[] message){
        imprimeln("\nEl proceso invocante es el "+super.dameIdProceso()+"\n");
        int idorigen = 0;
        int iddestino = 0;
        String ip = "";

        if ( m_emissionTable.containsKey(new Integer(dest))) {
            System.out.println("MicroNucleo se encontraron datos en " + 
                               "Tabla de Emision");
            ParMaquinaProceso datos;
            datos = m_emissionTable.get( new Integer(dest)  );
            m_emissionTable.remove(dest);
            ip = datos.dameIP();
            idorigen = super.dameIdProceso();
            iddestino = dest;


            // Envio del mensaje real
            setOriginBytes(message, idorigen);
            setDestinationBytes(message, iddestino);

            DatagramPacket dp;
            DatagramSocket socketEmision;
            try
            {
                dp = new DatagramPacket(message,
                        message.length, InetAddress.getByName(ip),
                        damePuertoRecepcion() );
                socketEmision = dameSocketEmision();
                System.out.println("MicroNucleo se encuentran los datos en " +
                                   "tabla emision y se hace envio");
                socketEmision.send(dp); 
            }
            catch (SocketException se){
                System.err.println("Error iniciando socket: " +
                                    se.getMessage());
            }
            catch (UnknownHostException uhe){
                System.err.println("UnknownHostException: " + uhe.getMessage());
            }
            catch (IOException ioe){
                System.err.println("IOException: " + ioe.getMessage());
            }
        }
        else {
            if(referenciaACliente != null && referenciaACliente.banderaSend)
            {
                Iterator<DatosProceso> lista =
                        TablaDireccionamientoProcesosRemotos.iterator();
                DatosProceso datos;
                idorigen = super.dameIdProceso();
                boolean banderaEncontrarServer = false;

                while( lista.hasNext( ) && (banderaEncontrarServer == false) )
                {
                    datos = lista.next();
                    if(  dest == datos.dameNumdeServicio()  )
                    {
                        System.out.println("MicroNucleo Se encontron datos " +
                                           "en Tabla de Procesos Remotos");
                        banderaEncontrarServer = true;
                        iddestino = datos.dameID();
                        ip = datos.dameIP();
                    }
                }

                if(banderaEncontrarServer)
                {
                    // hacer el envio

                    setOriginBytes(message, idorigen);
                    setDestinationBytes(message, iddestino);

                    DatagramPacket dp;
                    DatagramSocket socketEmision;
                    try
                    {
                        dp = new DatagramPacket(message,
                                message.length, InetAddress.getByName(ip),
                                damePuertoRecepcion());
                        socketEmision = dameSocketEmision();
                        System.out.println("MicroNucleo se jalan los "+
                                "datos de la tabla procesos remotos y " +
                                "se hace el envio");
                        socketEmision.send(dp); 
                    }
                    catch(SocketException se){
                        System.err.println("Error iniciando socket: " +
                                se.getMessage());
                    }
                    catch(UnknownHostException uhe){
                        System.err.println("UnknownHostException: " +
                                           uhe.getMessage());
                    }
                    catch(IOException exce){
                        System.err.println("IOException: "+exce.getMessage());
                    }
                }
                else // No se encontro servidor en la tabla de procesos remotos
                {
                    HiloLadoCliente hilo;
                    try {
                        System.out.println("MicroNucleo se crea hilo " +
                                "cronometro para hacer paquetes LSA");
                        hilo = new HiloLadoCliente(idorigen,
                                new DatagramSocket(), damePuertoRecepcion(),
                                TablaDireccionamientoProcesosRemotos, message,
                                this, dameProcesoLocal(super.dameIdProceso()));
                        hilo.start();
                    } catch (SocketException se) {
                        se.printStackTrace();
                    }
                }
            }
        }
    }


    protected void receiveVerdadero(int addr, byte[] message) {
        imprimeln("Recibido mensaje proviniente de la red");
        imprimeln("Recibido mensaje que contiene la ubicacion: " + addr);

        RequestsMailbox mailbox = getRequestsMailbox(super.dameIdProceso());
        if (mailbox != null) { // Process is a server.
            imprimeln("Procesando receive de servidor");
            if (mailbox.isEmpty()) {
                imprimeln("Buzon vacio");
                m_receptionTable.put(Integer.valueOf(addr), message);
                suspenderProceso();
            }
            else {
                imprimeln("Sacando mensaje del buzon");
                byte[] mailboxMessage = mailbox.getOldestMessage();

                System.arraycopy(mailboxMessage, 0, message, 0, 
                                 mailboxMessage.length);

                m_receptionTable.put(Integer.valueOf(addr), message);
            }
        }
        else { // Process is a client.
            imprimeln("Registrando proceso cliente");
            m_receptionTable.put(Integer.valueOf(addr), message);
            suspenderProceso();
        }
    }

    /**
     * Para el(la) encargad@ de direccionamiento por servidor de nombres en
     * practica 5
     */
    protected void sendVerdadero(String dest, byte[] message) {
        DatagramPacket dp;
        DatagramSocket socketEmision;
        String ip = new String();

        imprimeln("El proceso invocante es el "+super.dameIdProceso());

        imprimeln("Enviando mensaje de búsqueda del servidor");
        int destino = ServidorNombres.buscar(dest);
        imprimeln("Buscando en listas locales el par (máquina, proceso) que " +
                  "corresponde al parámetro dest de la llamada a send");
        if (m_emissionTable.containsKey(destino)) {
            imprimeln("Completando campos de encabezado del mensaje a enviar");

            setOriginBytes(message, super.dameIdProceso());
            setDestinationBytes(message, destino);
            ip = m_emissionTable.get(destino).dameIP();
            imprimeln("Enviando mensaje a IP="+ip+" ID="+dameIdProceso());
            m_emissionTable.remove(destino);
        }
        try {
            dp = new DatagramPacket(message, message.length, 
                                    InetAddress.getByName(ip),
                                    damePuertoRecepcion());
            socketEmision=dameSocketEmision();
            imprimeln("Enviando mensaje por la red");
            socketEmision.send(dp);
        } catch (UnknownHostException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    /**
     * Para el(la) encargad@ de primitivas sin bloqueo en practica 5
     */
    protected void sendNBVerdadero(int dest, byte[] message) {
    }

    /**
     * Para el(la) encargad@ de primitivas sin bloqueo en practica 5
     */
    protected void receiveNBVerdadero(int addr, byte[] message) {
    }

    public void run() {
        byte[] buffer = new byte[ProcesoCliente.SIZE_PACKET];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        int origin, destination;
        String originIp;
        Proceso process;

        while (seguirEsperandoDatagramas()) {
            try {
                nucleo.dameSocketRecepcion().receive(packet);
            } catch (IOException ioe) {
                System.err.println("Error en la recepcion del paquete: " +
                        ioe.getMessage());
            }

            origin = getOrigin(packet.getData());
            originIp = packet.getAddress().getHostAddress();
            destination = getDestination(packet.getData());

            if (packet.getData()[ProcesoServidor.INDEX_STATUS] ==
                    ProcesoServidor.STATUS_TA) {
                packet.getData()[ProcesoServidor.INDEX_STATUS] = 0;

                ResendThread resender = new ResendThread(
                        dameSocketRecepcion(), packet);
                resender.start();
            }
            else if (packet.getData()[ProcesoServidor.INDEX_STATUS] == 
                     ProcesoServidor.STATUS_LSA)
            {
                System.out.println("MicroNucleo obtiene un paquete LSA y lo empieza a tratar");
                int numServicio = IntByteConverter.toInt((Arrays.copyOfRange(buffer,  10, 14)));
                Iterator<DatosProceso> lista = TablaDireccionamientoProcesosLocales.iterator();
                boolean banderaEncontrarServer = false;
                DatosProceso datos;
                String ipServer;
                int idServer = 0;


                while( lista.hasNext( ) && (banderaEncontrarServer == false) )
                {
                    datos = lista.next();
                    if(  248 == datos.dameNumdeServicio()  )
                    {
                        System.out.println("MicroNucleo se encontraron datos en tabla de procesos locales");
                        banderaEncontrarServer = true;
                        idServer = datos.dameID();
                        ipServer = datos.dameIP();
                    }

                }
                if(banderaEncontrarServer)
                {// Enviar FSA
                    System.out.println("MicroNucleo se prepara un FSA");
                    HiloLadoServidorFSA hiloFSA = new HiloLadoServidorFSA (
                            dameSocketEmision(), damePuertoRecepcion(),
                            origin, idServer);
                    hiloFSA.start();
                }
            }
            else {
                imprimeln("Buscando proceso correspondiente al campo recibido");
                process = nucleo.dameProcesoLocal(destination);

                if (process != null) {
                    if (m_receptionTable.containsKey(destination)) {
                        byte[] array = m_receptionTable.get(destination);

                        imprimeln("Copiando mensaje al espaco de proceso");
                        System.arraycopy(packet.getData(), 0, array, 0,
                                array.length);
                        m_emissionTable.put(new Integer(origin),
                                new MachineProcessPair(originIp,
                                        origin));
                        m_receptionTable.remove(destination);
                        nucleo.reanudarProceso(process);
                    }
                    else {
                        if (m_mailboxesTable.containsKey(
                                nucleo.dameIdProceso(process))) {
                            RequestsMailbox mailbox = m_mailboxesTable.get(
                                    nucleo.dameIdProceso(process));
                            if (mailbox.hasSpace()) {
                                imprimeln("Guardando mensaje en buzon.");
                                mailbox.saveMessage(packet.getData());
                                m_emissionTable.put(new Integer(origin),
                                        new MachineProcessPair(originIp,
                                                origin));
                            }
                            else {
                                imprimeln("Proceso distinatario ocupado, " + 
                                        "enviando TA");
                                buffer[ProcesoServidor.INDEX_STATUS] = 
                                        (byte)ProcesoServidor.STATUS_TA;

                                // TODO: define if this inversion is necessary.
                                setOriginBytes(buffer, destination);
                                setDestinationBytes(buffer, origin);

                                try {
                                    packet = new DatagramPacket(buffer,
                                            buffer.length,
                                            InetAddress.getByName(originIp),
                                            nucleo.damePuertoRecepcion());
                                    nucleo.dameSocketEmision().send(packet);
                                } catch (UnknownHostException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                else {
                    imprimeln("Proceso distinatario no encontrado segun el " +
                            "campo dest recibido");
                    buffer[ProcesoServidor.INDEX_STATUS] =
                            (byte)ProcesoServidor.STATUS_AU;

                    setOriginBytes(buffer, destination);
                    setDestinationBytes(buffer, origin);

                    try {
                        packet = new DatagramPacket(buffer, buffer.length,
                                InetAddress.getByName(originIp),
                                nucleo.damePuertoRecepcion());
                        nucleo.dameSocketEmision().send(packet);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static int registrarParMaquinaProceso(ParMaquinaProceso server) {
        m_emissionTable.put(server.dameID(), server);

        return server.dameID();
    }

    private RequestsMailbox getRequestsMailbox(int pid) {
        RequestsMailbox mailbox = null;

        if (m_mailboxesTable.containsKey(Integer.valueOf(pid))) {
            mailbox = m_mailboxesTable.get(Integer.valueOf(pid));
        }

        return mailbox;
    }

    public void addNewMailbox(int pid) {
        m_mailboxesTable.put(Integer.valueOf(pid), new RequestsMailbox());
    }

    public void setOriginBytes(byte[] buffer, int origin) {
        byte[] originBytes = IntByteConverter.toBytes(origin);

        for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
            buffer[ProcesoCliente.INDEX_ORIGIN + i] = originBytes[i];
        }
    }

    public void setDestinationBytes(byte[] buffer, int destination) {
        byte[] destinationBytes = IntByteConverter.toBytes(destination);

        for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
            buffer[ProcesoCliente.INDEX_DESTINATION + i] = destinationBytes[i];
        }
    }

    public int getOrigin(byte[] buffer) {
        return IntByteConverter.toInt(Arrays.copyOfRange(buffer,
                ProcesoCliente.INDEX_ORIGIN,
                ProcesoCliente.INDEX_ORIGIN +
                IntByteConverter.SIZE_INT));
    }

    public int getDestination(byte[] buffer) {
        return IntByteConverter.toInt(Arrays.copyOfRange(buffer,
                ProcesoCliente.INDEX_DESTINATION,
                ProcesoCliente.INDEX_DESTINATION +
                IntByteConverter.SIZE_INT));
    }

    public void invertOriginDestination(byte[] buffer) {
        byte aux;
        for (int i = 0; i < IntByteConverter.SIZE_INT; ++i) {
            aux = buffer[ProcesoCliente.INDEX_ORIGIN + i];
            buffer[ProcesoCliente.INDEX_ORIGIN + i] =
                    buffer[ProcesoCliente.INDEX_DESTINATION + i];
            buffer[ProcesoCliente.INDEX_DESTINATION + i] = aux;
        }
    }

    public static void printBuffer(byte[] buffer) {
        for (int i = 0; i < 20; ++i) {
            System.out.print(buffer[i] + "|");
        }
        System.out.println("ST: " + buffer[ProcesoServidor.SIZE_PACKET - 1]);
        System.out.println();
    }

    public DatosProceso registrarServidor(int servicio, String ip, int id)
    {
        DatosProceso datos = new DatosProceso(servicio,ip,id);
        TablaDireccionamientoProcesosLocales.add(datos);
        return datos;
    }

    public boolean derregistrarServidor(DatosProceso datos)
    {
        TablaDireccionamientoProcesosLocales.remove(datos);
        return true;
    }

    public boolean registrarProcesoRemoto(int servicio, String ip, int id)
    {
        DatosProceso datos = new DatosProceso(servicio,ip,id);
        TablaDireccionamientoProcesosRemotos.add(datos);
        return true;
    }

    public boolean eliminarDatosProcesoRemoto(int id,int servicio){
        DatosProceso datos;
        for(int i=0; i < TablaDireccionamientoProcesosRemotos.size(); i++)
        {
            datos = TablaDireccionamientoProcesosRemotos.get(i);
            if( (datos.dameID() == id) && (datos.dameNumdeServicio() == servicio))
            {
                TablaDireccionamientoProcesosRemotos.remove(datos);
                return true;
            }
            
        }

        return false;
    }

    public boolean establecerCliente(Proceso cliente)
    {
        referenciaACliente = cliente;
        return true;
    }
}
