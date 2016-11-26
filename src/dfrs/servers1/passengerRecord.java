package dfrs.servers1;
import java.io.Serializable;

public class passengerRecord implements Serializable{
	public		String firstName ;
	public		String lastName;
	public		String address;
	public		String phoneNumber;
	public		String destination;
	public		String flightClass;
	public		String flightDate;
	public 		int RecordID=0;
	public	passengerRecord(){}
	@Override
	public String toString() {
		return "passengerRecord [firstName=" + firstName + ", lastName=" + lastName + ", address=" + address
				+ ", phoneNumber=" + phoneNumber + ", destination=" + destination + ", flightClass=" + flightClass
				+ ", flightDate=" + flightDate + ", RecordID=" + RecordID + "]";
	}
	
}
