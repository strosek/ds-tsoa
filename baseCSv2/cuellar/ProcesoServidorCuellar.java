package cuellar;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.DatosProceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.GestionArchivo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.IntByteConverter;
import sistemaDistribuido.util.Pausador;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Hector Jeronimo Cuellar Villalobos
 * 206514472
 * practica 1 y 2
 */
public class ProcesoServidorCuellar extends Proceso{

    public ProcesoServidorCuellar(Escribano esc){
        super(esc);
        start();
    }

    public void run(){
        imprimeln("Proceso servidor en ejecucion.");
        byte[] solServidor=new byte[1024];
        byte[] respServidor=new byte[1024];
        GestionArchivo archivador = new GestionArchivo();
        int origen;
        String ip="";
        //String mensaje="";
        //byte dato;
        String mensajeDeLaRed;

        imprimeln("Agregando buzon");
        Nucleo.nucleo.addNewMailbox(dameID());

        try {
            ip = InetAddress.getLocalHost().getHostAddress();	 
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DatosProceso misdatos= Nucleo.levantarServidor(248, ip , dameID());

        while(continuar()){
            imprimeln("Se convoco a receive()");
            Nucleo.receive(dameID(),solServidor);
            mensajeDeLaRed = desempaquetarMensaje(solServidor);
            
            origen = IntByteConverter.toInt(Arrays.copyOfRange(solServidor,  0, 4));
            
            System.out.println("El servidor enviara al numero "+origen);

            switch(solServidor[9])
            {
            case 1:// crear archivo
                imprimeln("El cliente quiere crear un archivo que se llama " + mensajeDeLaRed);
                imprimeln("Generando mensaje");

                File archivo = new File(mensajeDeLaRed);

                if(archivo.exists())
                    empaquetarMensaje(respServidor,"El archivo que intentas crear YA EXISTE"); 
                else
                {
                    archivador.crearArchivo(new File(mensajeDeLaRed), "");
                    empaquetarMensaje(respServidor,"Se ha CREADO tu archivo exitosamente FELICITACIONES");
                }
                break;
            case 2:// eliminar archivo
                imprimeln("El cliente quiere eliminar un archivo que se llama " + mensajeDeLaRed);
                imprimeln("Generando mensaje");
                boolean resultado;


                if (! new File(mensajeDeLaRed).exists())
                {
                    empaquetarMensaje(respServidor,"El archivo no se ha creado  ");
                }
                else
                {

                    resultado = archivador.eliminarArchivo(new File(mensajeDeLaRed));

                    if(resultado)
                        empaquetarMensaje(respServidor,"Se ha ELIMINADO tu archivo exitosamente FELICITACIONES");
                    else
                        empaquetarMensaje(respServidor,"No se pudo eliminar el archivo");
                }
                break;
            case 3:// leer archivo
                imprimeln("El cliente quiere leer un archivo que se llama " + mensajeDeLaRed);
                imprimeln("Generando mensaje");
                String contenidoDelArchivo;

                if (! new File(mensajeDeLaRed).exists())
                {
                    empaquetarMensaje(respServidor,"El archivo no se ha creado  ");
                }
                else
                {
                    contenidoDelArchivo = archivador.abrirArchivo(new File(mensajeDeLaRed));
                    //System.out.println(contenidoDelArchivo);
                    empaquetarMensaje(respServidor,"ESTO CONTIENE TU ARCHIVO->  "+ contenidoDelArchivo);
                }

                break;
            case 4:// escribir en archivo
                //mensaje = desempaquetarMensaje(solServidor);
                //System.out.println(mensaje);
                StringTokenizer separador = new StringTokenizer(mensajeDeLaRed, "/");
                String nombreArchivo = separador.nextToken();
                String mensaje = separador.nextToken();

                imprimeln("El cliente quiere escribir: "+mensaje+"\nEn el archivo "+nombreArchivo);
                imprimeln("Generando mensaje");

                if (! new File(nombreArchivo).exists())
                {
                    empaquetarMensaje(respServidor,"El archivo no se ha creado  ");
                }
                else
                {
                    archivador.escribirArchivo(new File(nombreArchivo), mensaje);
                    empaquetarMensaje(respServidor,"Se ha ESCRITO tu archivo exitosamente FELICITACIONES");
                }
                break;

            }


            Pausador.pausa(5000);  //sin esta l�nea es posible que Servidor solicite send antes que Cliente solicite receive
            imprimeln("enviando respuesta");
            //Nucleo.send(0,respServidor);
            Nucleo.send(origen,respServidor);
        }

        Nucleo.tumbarServidor(misdatos);
    }

    public void empaquetarMensaje(byte[] paquete, String mensaje)
    {
        byte []arrayAux = mensaje.getBytes();
        short checkSum = (short)arrayAux.length;

        paquete[8] = (byte)checkSum;
        checkSum >>=8;
        paquete[9]= (byte)checkSum;

        for (int i=10,j=0; i<paquete.length && j<arrayAux.length ;i++,j++)
        {
            paquete[i] = arrayAux[j];
        }
    }

    public String desempaquetarMensaje(byte [] paquete){
        String mensaje="";
        byte numeroenEnBytes[] = new byte[2];
        short tamMensaje;
        numeroenEnBytes[0] = paquete[10];
        numeroenEnBytes[1] = paquete[11];
        tamMensaje = construyeShort(numeroenEnBytes);
        //System.out.println(tamMensaje);

        for(int i=0,j=12; i<tamMensaje; i++,j++)
        {
            mensaje += (char)paquete[j];
            //System.out.print((char)paquete[j]);
        }

        return mensaje;
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
