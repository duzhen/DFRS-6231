package dfrs.servers3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class transmodule {


	private int port = 0;
	private passengerRecord pR;
	public transmodule(int new_port,passengerRecord new_pR)
	{
		pR = new_pR;
		port= new_port;
	
	}

	public boolean execute()
	{
		 
		DatagramSocket aSocket = null;
		System.out.println("client "+port+"send transfer");
		
			try {
				aSocket = new DatagramSocket();
				String t ="2"+"$"+pR.firstName
						+"$"+pR.lastName
						+"$"+pR.address
						+"$"+pR.phoneNumber
						+"$"+pR.destination
						+"$"+pR.flightClass
						+"$"+pR.flightDate
						+"$"+pR.RecordID+"$";
				byte [] m = t.getBytes();
				InetAddress aHost = InetAddress.getByName("localhost");
				DatagramPacket request =
				 	new DatagramPacket(m, t.length(), aHost, port);
				aSocket.send(request);			                
				System.out.println("message sended in transmodule");
				
				byte[] buffer = new byte[1000];
				
				DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(reply);
				String replydata  = new String(reply.getData());
				System.out.println("Reply: " +replydata.trim());
				if(replydata.trim().equals("success"))
				{
					return true;
				}
				else
				{
					return false;
				}
			} catch ( Exception e) {
				e.printStackTrace();
				return false;
			} 
	}

}
