package dfrs.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dfrs.utils.Config;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocketOutputStream;

public class FEServer  extends Thread {

    public int port;
    public CompareResult CR;
    public FEServer(int port, CompareResult new_CR){
        this.port = port;
        CR = new_CR;
    }
    
    public void run(){
        try {
            ReliableServerSocket serverSocket = new ReliableServerSocket(port);
            
            boolean isStopped = false;
            while(!isStopped){
                Socket connectionSocket = serverSocket.accept();
                
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String reply= inFromClient.readLine().toString();
                System.out.println("Server: "+reply+" port "+port);
                
                if(port==Config.FE_RECEIVE_SERVER_PORT_1){
                	CR.CM[0] = reply;
                }else if(port==Config.FE_RECEIVE_SERVER_PORT_2){
                	CR.CM[1] = reply;
                }else if(port==Config.FE_RECEIVE_SERVER_PORT_3){
                	CR.CM[2] = reply;
                }else if(port==Config.FE_RECEIVE_SERVER_PORT_4){
                	CR.CM[3]  = reply;
                }
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
