package dfrs.servers2;

import java.util.HashMap;

public class ServerInfo {
	
	
	private static HashMap<String, Integer> serverUDPMaps;
    static {
    	serverUDPMaps = new HashMap<>();
        serverUDPMaps.put("Montreal", 1111);
        serverUDPMaps.put("Washington", 2222);
        serverUDPMaps.put("NewDelhi", 3333);
    }
    

	public static HashMap<String, Integer> getServerMaps() {
		// TODO Auto-generated method stub
		return serverUDPMaps;
	}
    
    
	
}
