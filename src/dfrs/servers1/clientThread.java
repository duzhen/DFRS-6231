package dfrs.servers1;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class clientThread extends Thread{

	private int port = 0;
	private NumberFlight query;
	public clientThread(int new_port,NumberFlight new_query)
	{
		query = new_query;
		port= new_port;
		this.start();
	}

	public void run()
	{
		 
		DatagramSocket aSocket = null;
		System.out.println("client "+port+" begin running");
		try {
			aSocket = new DatagramSocket(); 
			
			byte [] m = "1".getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");
			
			int serverPort = port;		      
			
			DatagramPacket request =
			 	new DatagramPacket(m,  "1".length(), aHost, serverPort);
			
			aSocket.send(request);			                
			
			byte[] buffer = new byte[1000];
			
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);	
			
			aSocket.receive(reply);
			String replydata  = new String(reply.getData()).trim();
			//replydata = replydata.substring(0, 1);
			
			/*
			 6789 6790 MTL
			 6791 6792 WST
			 6793 6794 NDL
			 1050 mtl
			 1051 NDL
			 1052 WST
			 */
			
			if((port==6789)||(port==6790))//accord to the port number to assign 
			{
				System.out.println("the wrong if"+replydata);
				  synchronized(replydata)
				  {
					if(replydata.equals("0"))
						query.MTL = 0;
					else
					{	query.MTL = Integer.parseInt(replydata);
						System.out.println(query.MTL );
					}
				  }
			} else
			if((port==6791)||(port==6792))
			{
				
				  synchronized(query)
				  {
					if(replydata.equals("0"))
						query.WST = 0;
					else
					{   query.WST = Integer.parseInt(replydata);
						System.out.println(query.WST );
					}
				  }
			} else
			if((port==6793)||(port==6794))
			{
				  synchronized(query)
				  {
					if(replydata.equals("0"))
						query.NDL = 0;
					else
					{
						query.NDL = Integer.parseInt(replydata);
						System.out.println(query.NDL );
					}
				  }
			}

			System.out.println("Reply: " +replydata.trim());
			
		}catch (SocketException e){
			System.out.println("Socket: " + e.getMessage());
		}catch (IOException e){
			System.out.println("IO: " + e.getMessage());
		}finally {
			if(aSocket != null) aSocket.close();
		}
	}
}
