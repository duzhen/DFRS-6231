package dfrs.sequencer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import dfrs.utils.Config;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocketOutputStream;

public class Sequencer extends Thread {


	public static int counter=0;
    private int port;
    
    public Sequencer(int port){
        this.port = port;
    }
	
    @Override
    public void run(){
//    		content ="2"+"$"+currentCity
//    				+"$"+Integer.toString(firstclass)+"$";
    	System.out.println("Sequencer begin to run");
        try {
            ReliableServerSocket serverSocket = new ReliableServerSocket(port);//8888
            
            boolean isStopped = false;
            while(!isStopped){
            	
            	//create connect and read string
                Socket connectionSocket = serverSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String content = inFromClient.readLine();

                //add counter
                StringBuilder sb = new StringBuilder(content);
                sb.insert(1, "$");
                sb.insert(2, counter);
                content = sb.toString();
                counter++;
                System.out.println("Receive Request: "+content);
                
              //open four connection with the four clusterManager and send content
                SE2CMMulticast mc = new SE2CMMulticast(content);
                mc.initial();
                mc.execute();
                
                // message send back to client
                ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
                PrintWriter outputBuffer = new PrintWriter(outToClient);
                outputBuffer.println("sequencer Processed datagram and multicast");
                outputBuffer.flush();
                
                connectionSocket.close();
            }
            serverSocket.close();
        } catch (IOException ex) {
        	System.out.println("sequence exception ");
        } 
    }
    
	public static void main(String[] args) {
		Sequencer seq = new Sequencer(Config.SE_RECEIVER_FE_UDP_PROT);//initial data
		seq.start();
	}   
}

