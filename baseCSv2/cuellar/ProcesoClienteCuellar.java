package cuellar;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.awt.Button;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;

/**
 * Hector Jeronimo Cuellar Villalobos
 * 206514472
 */
public class ProcesoClienteCuellar extends Proceso{

    private String codop = "";
    private String mensaje = "";
    public String avisodelHiloLSA ="";
    private Button botonSolicitud;
    public byte[] solCliente=new byte[1024]; // considerar quitar de aqui estos arreglos porq en cada vuelta se crea un nuevo objeto
    public byte[] respCliente=new byte[1024];


    public ProcesoClienteCuellar(Escribano esc,Button boton){
        super(esc);
        botonSolicitud = boton;
        banderaSend = false;
        start();
    }

    public void capturarSolicitud(String operacion, String datos)
    {
        codop = operacion;
        mensaje = datos;
    }

    public void run(){
        imprimeln("Proceso cliente en ejecucion.");
        imprimeln("Esperando datos para continuar.");

        while(continuar())// aqui deberia ir una bandera o lo de continuar()
        {
            Nucleo.suspenderProceso();
            imprimeln("Generando mensaje");
            solCliente[0]=(byte)10;  //aqui va a enpaquetarse el mensaje
            empaquetarMensaje(solCliente);
            imprimeln("Enviando mensaje");

            System.out.println("Cliente convoca a send");
            Nucleo.establecerCliente(this);
            banderaSend = true;
            Nucleo.send(248,solCliente);
            banderaSend = false;

            imprimeln("Se convoco a receive");
            System.out.println("Cliente se puso en receive(1)");
            Nucleo.receive(dameID(),respCliente);
            System.out.println("Cliente salio del receive()");

            String mensajeDeLaRed = desempaquetarMensaje(respCliente);

            while( mensajeDeLaRed.equals("FSA")  || mensajeDeLaRed.equals("AU"))
            {
                System.out.println("Cliente se puso en receive(2)");
                if(mensajeDeLaRed.equals("AU"))
                {
                    System.out.println("Cliente convoca a send");
                    banderaSend = true;
                    Nucleo.send(248,solCliente); 
                    banderaSend = false;
                }

                Nucleo.receive(dameID(),respCliente);
                System.out.println("Cliente salio del receive()");
                mensajeDeLaRed = desempaquetarMensaje(respCliente);

            }

            if(avisodelHiloLSA.equals(""))
                imprimeln("el servidor me envio:  "+  mensajeDeLaRed);
            else
            {
                imprimeln(avisodelHiloLSA);
                avisodelHiloLSA = "";
            }

            // aqui deberiamos volver hacer verdadero el boton
            botonSolicitud.setEnabled(true);

        }
    }


    public void empaquetarMensaje(byte []paquete)// no necesita return porque solo recibimos el puntero a array
    {
        byte arrayAux[];
        short numeroCheckSum;

        arrayAux = mensaje.getBytes();
        numeroCheckSum = (short)arrayAux.length;

        paquete[10] = (byte)numeroCheckSum;
        numeroCheckSum >>=8;
        paquete[11]= (byte)numeroCheckSum;


        if(codop.equals("Crear"))
        {
            paquete[9] = 1;
        }
        else
            if(codop.equals("Eliminar"))
            {
                paquete[9] = 2;
            }
            else
                if(codop.equals("Leer"))
                {
                    paquete[9] = 3;	
                }
                else
                    if(codop.equals("Escribir"))
                    {
                        paquete[9] = 4;
                    }

        for (int i=12,j=0; i<paquete.length && j<arrayAux.length ;i++,j++)
        {
            paquete[i] = arrayAux[j];
        }
    }

    public String desempaquetarMensaje(byte [] paquete){
        System.out.println("Cliente Recivi algo");
        String mensaje="";
        byte numeroenEnBytes[] = new byte[2];
        short tamMensaje;
        numeroenEnBytes[0] = paquete[8];
        numeroenEnBytes[1] = paquete[9];
        tamMensaje = construyeShort(numeroenEnBytes);

        if(tamMensaje == -1)
        {
            System.out.println("Cliente el cliente recivio un AU");
            int ultimoIdProcesoQueSeLeEnvioSolicitud = construyeInt(Arrays.copyOfRange(paquete,  0, 4));
            System.out.println("Cliente el cliente recivio un AU del PROCESO "+ ultimoIdProcesoQueSeLeEnvioSolicitud);
            Nucleo.eliminarDatosProcesoRemoto(ultimoIdProcesoQueSeLeEnvioSolicitud,248);
            return "AU";
        }
        else
            if(tamMensaje == -3) // se recibio un FSA
            {
                System.out.println("Cliente el cliente recivio un FSA");
                int idServer = construyeInt(Arrays.copyOfRange(paquete,  14, 18));
                String ip ="";
                // registre 248 y la ip local por no programar para obtener realmnte esos datos
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();	 
                } catch (UnknownHostException e) {

                }

                Nucleo.registrarProcesoRemoto(248, ip, idServer);
                return "FSA";
            }
            else
            {
                System.out.println("Cliente el cliente recivio un mensaje normal");
                for(int i=0,j=10; i<tamMensaje; i++,j++)
                {
                    mensaje += (char)paquete[j];
                }

                return mensaje;
            }
    }

    public byte[] ordenaArray(byte [] array)
    {   
        byte[] destino=new byte[array.length];
        int j=destino.length;
        for(int i=0;i<array.length;i++){
            j--;
            destino[j]=array[i];
        }

        return destino;
    }


    public  short construyeShort(byte arreglo [])
    { 
        short valorOriginal;
        short aux;

        arreglo = ordenaArray(arreglo);

        valorOriginal = (short)(arreglo[0] | (short)0x00);
        valorOriginal <<= 8;
        valorOriginal = (short) (valorOriginal & (short)0xFF00);

        aux = (short)(arreglo[1] | (short)0x00);
        aux = (short) (aux & (short)0x00FF);
        valorOriginal = (short) (valorOriginal | aux);


        return valorOriginal;	
    }

    public int construyeInt(byte arreglo[]){
        int valorOriginal = 0;

        arreglo = ordenaArray(arreglo);

        for(int i= 0 ; i< 4; i++)
        {

            valorOriginal = (int)( (arreglo[i] & 0xFF) | valorOriginal);          
            if( i != 3 ){
                valorOriginal = valorOriginal << 8;
            }
        }
        return valorOriginal;


    }
}
