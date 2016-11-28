package dfrs.servers2;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlightRecords {
	CopyOnWriteArrayList<FlightRecord> flightRecords;
	
	public FlightRecords(){
		flightRecords = new CopyOnWriteArrayList<>();
	}
	
	public void addFlightRecord(FlightRecord fr){
		flightRecords.add(fr);
	}
	
	public void removeFlightRecord(FlightRecord fr){
		flightRecords.remove(fr);
	}

	public CopyOnWriteArrayList<FlightRecord> getFlightRecords() {
		return flightRecords;
	}
}
