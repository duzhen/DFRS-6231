package dfrs.sequencer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

public class ClusterManagerSender extends Thread{

	public String host;
	public int port;
	public String content;
	public ClusterManagerSender(String host, int port, String new_content) {

		this.host = host;
		this.port = port;
		this.content = new_content;
	}
	
    public void run(){
        try {
            ReliableSocket clientSocket = new ReliableSocket(host, port);
            ReliableSocketOutputStream outToServer = (ReliableSocketOutputStream) clientSocket.getOutputStream();
            PrintWriter outputBuffer = new PrintWriter(outToServer);
            outputBuffer.println(content);
            outputBuffer.flush();
            
            // message receive from server
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("ClusterManagerSender Client: " + inFromServer.readLine());

            clientSocket.close();

        } catch (IOException ex) {

        }
    }
}
