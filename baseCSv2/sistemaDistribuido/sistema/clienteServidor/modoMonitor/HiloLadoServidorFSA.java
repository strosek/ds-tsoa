package sistemaDistribuido.sistema.clienteServidor.modoMonitor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class HiloLadoServidorFSA extends Thread{
	DatagramSocket socketEmision;
	int puerto;
	int destino;// es a donde va el FSA
	int idServer;
	
	public HiloLadoServidorFSA(DatagramSocket socket,int puerto,int destino,int idServer)
	{
		socketEmision = socket;
		this.puerto = puerto;
		this.destino = destino;// es a donde va el FSA
		this.idServer = idServer;
		
		
	}
	
	public void run()
	{
		DatagramPacket paqueteFSA;
		byte[] messageFSA = new byte[1024];
		//message[9] = -1;
		short codigoFSA = -3;
		byte arrayAux[] = new byte[4];
		
		messageFSA[8] = (byte)codigoFSA;
		codigoFSA >>=8;
        messageFSA[9]= (byte)codigoFSA;
        
        arrayAux = empaquetaEntero(0); //tenia destino
		for(int i =0; i<4; i++)// primer campo es el emisor
		{
			messageFSA[i]= arrayAux[i];
		}
		arrayAux = empaquetaEntero(destino);//tenia origen
		for(int i =0; i<4; i++)// segundo campo es el receptor
		{
			messageFSA[i+4]= arrayAux[i];
		}
		arrayAux = empaquetaEntero(248);
		for(int i =0; i<4; i++)
		{
			messageFSA[i+10]= arrayAux[i];
		}
		arrayAux = empaquetaEntero(idServer);
		for(int i =0; i<4; i++)
		{
			messageFSA[i+14]= arrayAux[i];
		}
		
		
	    //socketEmision = dameSocketEmision();
	    
	    String ip="";
		try {
			 ip = InetAddress.getLocalHost().getHostAddress();	 
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    
		// quisas sea necesario dormir aqui unos milisegundos
		try
		{
			
		   paqueteFSA = new DatagramPacket(messageFSA,messageFSA.length,InetAddress.getByName(ip),puerto );
		   socketEmision.send(paqueteFSA);
		   System.out.println("MicroNucleo se envia el FSA");
		   //socketEmision.close();
		  
		}catch(SocketException e){
			System.out.println("Error iniciando socket: "+e.getMessage());
		}
      catch(IOException e){
			System.out.println("IOException: "+e.getMessage());
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
        
       // System.out.println(array[0]+" "+array[1]+" "+array[2]+" "+array[3]);
        
        return array;
    }

}
