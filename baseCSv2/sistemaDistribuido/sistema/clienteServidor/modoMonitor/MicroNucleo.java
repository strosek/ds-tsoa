/*
 * Erick Daniel Corona Garcia 210224314. TSOA D03.
 * 
 * Modificado para Practica 5.
 */

package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Hashtable;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleoBase;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoCliente;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ProcesoServidor;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ResendThread;
import sistemaDistribuido.util.IntByteConverter;

public final class MicroNucleo extends MicroNucleoBase {
    private static MicroNucleo nucleo = new MicroNucleo();
    private Hashtable<Integer, ParMaquinaProceso> m_emissionTable;
    private Hashtable<Integer, byte[]>            m_receptionTable;
    private Hashtable<Integer, RequestsMailbox>   m_mailboxesTable;

    private MicroNucleo() {
        m_emissionTable = new Hashtable<Integer, ParMaquinaProceso>();
        m_receptionTable = new Hashtable<Integer, byte[]>();
        m_mailboxesTable = new Hashtable<Integer, RequestsMailbox>();
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

    protected void sendVerdadero(int dest, byte[] message) {
        imprimeln("El proceso invocante es el " + super.dameIdProceso());

        ParMaquinaProceso pmp;
        String ip;
        int id;

        if (m_emissionTable.containsKey(new Integer(dest))) {
            ip = m_emissionTable.get(new Integer(dest)).dameIP();
            id = m_emissionTable.get(new Integer(dest)).dameID();
        } else {
            pmp = dameDestinatarioDesdeInterfaz();
            ip = pmp.dameIP();
            id = pmp.dameID();
            imprimeln("Enviando mensaje a IP=" + ip + " ID=" + id);
        }
        imprimeln("Buscando en listas locales el par (" + ip + ", " + dest
                + ")");

        imprime("Completando campos del encabezado del mensaje a ser enviado");
        setOriginBytes(message, super.dameIdProceso());
        setDestinationBytes(message, id);

        System.out.println("before sending answer: ");
        printBuffer(message);

        try {
            DatagramPacket packet = new DatagramPacket(message, message.length,
                    InetAddress.getByName(ip), nucleo.damePuertoRecepcion());
            imprime("Enviando mensaje por la red");
            nucleo.dameSocketEmision().send(packet);
        } catch (UnknownHostException e) {
            System.err.println("Error creando socket transmision: "
                    + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error creando socket transmision: "
                    + e.getMessage());
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

                origin = getOrigin(packet.getData());
                originIp = packet.getAddress().getHostAddress();
                destination = getDestination(packet.getData());
                
                System.out.println("buffer to extract origin, dest: ");
                printBuffer(packet.getData());
                System.out.println("origin: " + origin + " dest: " + destination);

                if (packet.getData()[ProcesoServidor.INDEX_STATUS] ==
                    ProcesoServidor.STATUS_TA) {
                    packet.getData()[ProcesoServidor.INDEX_STATUS] = 0;

                    System.out.println("data to resend: " + packet.getData());
                    ResendThread resender = new ResendThread(
                            dameSocketRecepcion(), packet);
                    System.out.println("resender: start");
                    resender.start();
                    System.out.println("resender: background?");
                }

                imprimeln("Buscando proceso correspondiente al campo recibido");
                process = nucleo.dameProcesoLocal(destination);
                if (process != null) {
                    if (m_receptionTable.containsKey(destination)) {
                        byte[] array = m_receptionTable.get(destination);

                        imprimeln("Copiando mensaje al espaco de proceso");
                        System.arraycopy(packet.getData(), 0, array, 0,
                                array.length);
                        m_emissionTable.put(new Integer(origin),
                                new MachineProcessPair(originIp, origin));
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
                                System.out.println("message before saving in mailbox");
                                printBuffer(packet.getData());
                                mailbox.saveMessage(packet.getData());
                            }
                            else {
                                System.out.println("buzon lleno, enviando TA");
                                imprimeln("Proceso distinatario ocupado, " + 
                                          "enviando TA");
                                buffer[ProcesoServidor.INDEX_STATUS] = 
                                        (byte)ProcesoServidor.STATUS_TA;

                                setOriginBytes(buffer, destination);
                                setDestinationBytes(buffer, origin);

                                packet = new DatagramPacket(buffer,
                                        buffer.length,
                                        InetAddress.getByName(originIp),
                                        nucleo.damePuertoRecepcion());
                                System.out.println("buffer before sending TA");
                                printBuffer(packet.getData());
                                nucleo.dameSocketEmision().send(packet);
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

                    packet = new DatagramPacket(buffer, buffer.length,
                            InetAddress.getByName(originIp),
                            nucleo.damePuertoRecepcion());
                    nucleo.dameSocketEmision().send(packet);
                }
            } catch (IOException ioe) {
                System.err.println("Error en la recepcion del paquete: " +
                                   ioe.getMessage());
            }
        }
    }

    public void registrarParMaquinaProceso(ParMaquinaProceso server) {
        m_emissionTable.put(server.dameID(), server);
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
        System.out.println();
    }
}
