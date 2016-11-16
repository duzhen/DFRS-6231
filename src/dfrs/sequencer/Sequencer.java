package dfrs.sequencer;

import java.io.BufferedInputStream;
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

import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocketOutputStream;

public class Sequencer extends Thread {

        private int port;
        public Sequencer(int port){
            this.port = port;
        }
        
    	public static void main(String[] args) {
    		Sequencer seq = new Sequencer(8888);
    		seq.start();
    		//open four connection with the four replica client. the address and the port should be defined.
    	}    
    	
        @Override
        public void run(){
            try {
                ReliableServerSocket serverSocket = new ReliableServerSocket(port);
                
                boolean isStopped = false;
                while(!isStopped){
                    Socket connectionSocket = serverSocket.accept();
                    
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    String content = inFromClient.readLine();
                    System.out.println("Server: "+content);
                    
                    // message send back to client
                    ReliableSocketOutputStream outToClient = (ReliableSocketOutputStream) connectionSocket.getOutputStream();
                    PrintWriter outputBuffer = new PrintWriter(outToClient);
                    outputBuffer.println("Processed Sentence From Server");
                    outputBuffer.flush();
                    
                    connectionSocket.close();
                }
                
                serverSocket.close();

            } catch (IOException ex) {

            } 
        }
	}

