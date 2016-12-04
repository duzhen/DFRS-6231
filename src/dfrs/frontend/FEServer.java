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
    public CompareCount CC;
    public FEServer(int port, CompareResult new_CR,CompareCount new_CC){
        this.port = port;
        CR = new_CR;
        CC=new_CC;
    }
    
    public void run(){
        try {
            ReliableServerSocket serverSocket = new ReliableServerSocket(port);
            
            boolean isStopped = false;
            while(!isStopped){
                Socket connectionSocket = serverSocket.accept();
                
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                
                String reply= inFromClient.readLine().toString().trim();
                System.out.println("FE Receive RM: "+reply+" [port:"+port+"]");
                String[] params = reply.split("\\$");
                reply= params[1];
                
                if(reply.equals("MTL"))
                {
	                if(port==Config.FE_RECEIVE_SERVER_PORT_1){
	                	CC.recordCount[0][0] = Integer.parseInt(params[2]);
	                	CC.recordCount[0][1] = Integer.parseInt(params[4]);
	                	CC.recordCount[0][2] = Integer.parseInt(params[6]);
	                }else if(port==Config.FE_RECEIVE_SERVER_PORT_2){
	                	CC.recordCount[1][0] = Integer.parseInt(params[2]);
	                	CC.recordCount[1][1] = Integer.parseInt(params[4]);
	                	CC.recordCount[1][2] = Integer.parseInt(params[6]);
	                }else if(port==Config.FE_RECEIVE_SERVER_PORT_3){
	                	CC.recordCount[2][0] = Integer.parseInt(params[2]);
	                	CC.recordCount[2][1] = Integer.parseInt(params[4]);
	                	CC.recordCount[2][2] = Integer.parseInt(params[6]);
	                }else if(port==Config.FE_RECEIVE_SERVER_PORT_4)
	                {
	                	CC.recordCount[3][0] = Integer.parseInt(params[2]);
	                	CC.recordCount[3][1] = Integer.parseInt(params[4]);
	                	CC.recordCount[3][2] = Integer.parseInt(params[6]);
	                }
                }else{
	                if(port==Config.FE_RECEIVE_SERVER_PORT_1){
	                	CR.CM[0] = reply;
	                }else if(port==Config.FE_RECEIVE_SERVER_PORT_2){
	                	CR.CM[1] = reply;
	                }else if(port==Config.FE_RECEIVE_SERVER_PORT_3){
	                	CR.CM[2] = reply;
	                }else if(port==Config.FE_RECEIVE_SERVER_PORT_4){
	                	CR.CM[3] = reply;
	                }
                	
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
