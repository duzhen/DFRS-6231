package dfrs.servers1;

public class GenerateID {
	private static GenerateID instance;
	
	private int flightID = 0;
	private int passengerID = 0;
	
	public int getFlightID() {
		return ++flightID;
	}

	public int getPassengerID() {
		return ++passengerID;
	}

	public static synchronized GenerateID getInstance() {
		if (instance == null) {
			instance = new GenerateID();
		}
		return instance;
	}
}
