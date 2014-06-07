/**
 * Alejandro Duarte S�nchez
 * 206587844
 * Practica 1 y 2 y 5
 */
package duarte;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MachineProcessPair;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.MicroNucleo;
import sistemaDistribuido.sistema.clienteServidor.modoMonitor.Nucleo;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.Proceso;
import sistemaDistribuido.sistema.clienteServidor.modoUsuario.ServidorNombres;
import sistemaDistribuido.util.Escribano;
import sistemaDistribuido.util.Pausador;

public class ProcesoServidorDuarte extends Proceso{
    File archivo;
    private BufferedReader br;
    /**
     * 
     * ERROR	-> -1
     * Crear 	->  0
     * Eliminar ->  1
     * Leer		->  2
     * Escribir	->  3
     */
    public ProcesoServidorDuarte(Escribano esc){
        super(esc);
        start();
    }

    public void run(){
        imprimeln("Inicio de proceso");
        imprimeln("Proceso servidor en ejecucion.");
        imprimeln("Agregando buzon");
        Nucleo.nucleo.addNewMailbox(dameID());

        byte[] solServidor=new byte[1024];
        byte[] respServidor=new byte[1024];
        byte dato;
        String respuesta = null;
        byte[] respByte = new byte[1016];
        char []aux = new char[550];
        char []aux2 = new char[550];
        String param1 = null,param2=null;
        boolean band = false;
        int tam,cont=0,cont2=0;
        int destino, origen;

        MachineProcessPair asa = null;
        try {
            asa = new MachineProcessPair(InetAddress.getLocalHost().getHostAddress(),dameID());
        } catch (UnknownHostException e1) {

            e1.printStackTrace();
        }
        int id = ServidorNombres.alta("Servidor", asa);
        solServidor[9] = -1;
        while(continuar()){
            dato = -1;
            imprimeln("Invocando a receive()");
            Nucleo.receive(dameID(),solServidor);
            MicroNucleo.printBuffer(solServidor);
            dato=solServidor[9];

            tam = (int)solServidor[10]+11;
            for(int i = 11; i < tam;i++ ){

                if( (char)solServidor[i] == '-' ){
                    band = true;

                }
                else{
                    if( !band ){
                        aux[cont] = (char)solServidor[i];
                        cont++;
                    }
                    else{
                        aux2[cont2] = (char)solServidor[i];
                        cont2++;
                    }
                }

            }
            param1 = String.copyValueOf(aux, 0, cont);
            param2 = String.copyValueOf(aux2, 0, cont2);

            cont  = 0;
            cont2 = 0;
            band = false;

            archivo = new File(param1);

            switch( dato ){
            case 0://crear
                /**Ejemplo
                 * en area de texto: perro.txt
                 */

                imprimeln("Operacion: Crear-"+" datos: "+param1);
                try {
                    if(archivo.createNewFile()){
                        respuesta ="El archivo \""+param1+"\" Fue creado";
                    }
                    else{
                        respuesta ="El archivo \""+param1+"\" No pudo ser creado";
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 1://eliminar
                /**Ejemplo
                 * en area de texto: perro.txt
                 */
                imprimeln("Operacion: Eliminar-"+" datos: "+param1);
                if(archivo.delete()){
                    respuesta ="El archivo \""+param1+"\" Fue Eliminado";
                }
                else{
                    respuesta ="El archivo \""+param1+"\" No puedo ser Eliminado";
                }
                break;
            case 2://leer
                /**Ejemplo
                 * en area de texto: perro.txt-2
                 * Separador: -
                 * Despues del separador el numero de linea a leer: 2
                 */
                imprimeln("Operacion: Leer"+" datos: "+param2);
                FileReader fr;
                try {
                    fr = new FileReader(archivo);
                    br = new BufferedReader(fr);
                    int limite = Integer.parseInt(param2);
                    for( int i = 0; i < limite ; i++ ){
                        br.readLine();
                    }
                    respuesta=br.readLine();
                    if( respuesta == null){
                        respuesta = "No se pudo leer esa linea";
                    }
                    fr .close();

                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }

                break;
            case 3://escribir
                /**Ejemplo
                 * en area de texto: perro.txt-hola
                 * Separador: -
                 * Despues del separador lo que se quiere escribir: hola
                 */
                imprimeln("Operacion: Escribir"+" datos: "+param2);
                try {
                    FileWriter file = new FileWriter(param1,true);
                    if( file != null ){
                        PrintWriter p = new PrintWriter(file);
                        p.println(param2);
                        file.close();
                        respuesta = "Se ha escrito -"+param2+"- en el archivo *"+param1+"*";
                    }


                } catch (IOException e) {
                    respuesta = "No se pudo escribir en el archivo *"+param1+"*";
                    e.printStackTrace();
                }
                break;
            default://no existe
                respuesta = "Peticion invalida";
                break;
            }
            Pausador.pausa(3000);
            imprimeln("Generando mensaje a ser enviado, llenando los campos necesarios");
            respByte = respuesta.getBytes();
            respServidor[8] = (byte)respuesta.length();
            for( int i = 9 , j = 0; i < respuesta.length()+9 ; i++  , j++){
                respServidor[i] = respByte[j];
            }
            Pausador.pausa(1000);  //sin esta l�nea es posible que Servidor solicite send antes que Cliente solicite receive
            imprimeln("enviando respuesta");
            imprimeln("Se�alamiento al n�cleo para env�o de mensaje");
            origen = solServidor[3];
            destino = solServidor[7];
            Nucleo.nucleo.setOriginBytes(respServidor, destino);
            
            Nucleo.nucleo.setDestinationBytes(respServidor, origen);
            MicroNucleo.printBuffer(respServidor);
            Nucleo.send(solServidor[0],respServidor);
        }
        ServidorNombres.baja("Servidor",id);
    }
}
