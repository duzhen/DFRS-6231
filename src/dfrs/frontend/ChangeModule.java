package dfrs.frontend;

import dfrs.net.Client;

public class ChangeModule {
	
	public  String managerID = "";
	public  String currentCity = "";
	public  String destination ;
	public  String flightDate;
	public  int economy ;
	public  int business;
	public  int firstclass;
	public  String host;
	private int port = 0;
	private String content;
	//private passengerRecord pR;
	public ChangeModule(String new_host,int new_port,String new_currentCity,String new_managerID,String new_destination,
			String  new_flightDate,int new_economy,int new_business,int new_firstclass)
	{
		host=new_host;
		port= new_port;
		currentCity=new_currentCity;
		managerID=new_managerID;
		destination=new_destination;
		flightDate = new_flightDate;
		economy = new_economy;
		business = new_business;
		firstclass = new_firstclass;
	}

	public void execute()
	{
		content ="2"+"$"+currentCity
				+"$"+managerID
				+"$"+destination
				+"$"+flightDate
				+"$"+Integer.toString(economy)
				+"$"+Integer.toString(business)
				+"$"+Integer.toString(firstclass)+"$";
		
		System.out.println("client "+port+" connect string");
		System.out.println(content);
		
        Client client = new Client("localhost", 8888, content);
        client.run();
	}
}
