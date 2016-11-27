package dfrs.servers3;

import java.io.Serializable;

public class managerRecord implements Serializable {
	public		String managerID;
	public		String destination;
	public		String flightDate;
	public		int economy = 55;
	public		int economyLeft = 55;
	public		int business = 20;
	public		int businessLeft = 20;
	public		int firstclass = 15;
	public		int firstclassLeft = 15;
	public managerRecord(){}
}
