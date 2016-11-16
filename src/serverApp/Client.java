package serverApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

public class Client extends Thread{
        
    String host;
    int port;
    String content;
    
    public Client(String host, int port, String content){
        this.host = host;
        this.port = port;
        this.content = content;
    }
        
     @Override
    public void run(){
    try {
        ReliableSocket clientSocket = new ReliableSocket(host, port);
        ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) clientSocket.getOutputStream();
        PrintWriter outputBuffer = new PrintWriter(outToServer);
        outputBuffer.println(content);
        outputBuffer.flush();
        
        // message receive from server
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Client receive the reply: " + inFromServer.readLine());

        clientSocket.close();

    } catch (IOException ex) {

    }
}
}
	    