package reliableudpsample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketOutputStream;

/**
 *
 * @author yucunli
 */
public class ReliableUDPSample {

    // mr-udp is using UDP to implement TCP like socket, so
    // you need use it as you use TCP socket, not like UDP
    
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
                System.out.println("Client: " + inFromServer.readLine());

                clientSocket.close();

            } catch (IOException ex) {

            }
        }
    }
    
    public class Server extends Thread{
        int port;
        
        public Server(int port){
            this.port = port;
        }
        
        @Override
        public void run(){
            try {
                ReliableServerSocket serverSocket = new ReliableServerSocket(port);
                
                boolean isStopped = false;
                while(!isStopped){
                    Socket connectionSocket = serverSocket.accept();
                    
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    System.out.println("Server: "+inFromClient.readLine());
                    
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
    
    public void test(){
        Server server = new Server(8888);
        server.start();
        
        Client client = new Client("localhost", 8888, "msg to send");
        client.run();
    }
    
    
    public static void main(String[] args) {
        ReliableUDPSample s = new ReliableUDPSample();
        s.test();
    }
    
}
