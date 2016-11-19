package dfrs.servers;

import java.util.HashMap;

public abstract class BaseServerCluster {
	
	public static final String SERVER_MTL = "Montreal";
	public static final String SERVER_WST = "Washington";
	public static final String SERVER_NDL = "New Delhi";
	public static final String[] SERVERS = new String[] {SERVER_MTL,SERVER_WST,SERVER_NDL};
	
	protected abstract void createServers(String[] servers);
	
	private HashMap<String, CorbaServer> servers;

	public BaseServerCluster() {
		this.servers = new HashMap<String, CorbaServer>();
	}

	protected CorbaServer getServer(String server) {
		return servers.get(server);
	}

	protected void registerServer(CorbaServer server) {
		if(server == null)
			return;
		CorbaServer s = getServer(server.getServerName());
		if (s != null && s.isAlive()) {
			s.shutdown(false);
		}
		if (server.isCreated()) {
			servers.put(server.getServerName(), server);
		}
	}

	protected void startServer(String[] servers) {
		if (servers == null)
			return;
		for (int i = 0; i < servers.length; i++) {
			CorbaServer s = getServer(servers[i]);
			if (s != null)
				s.start();
		}
	}

	protected void stopAllServer() {
		for (int i = 0; i < SERVERS.length; i++) {
			CorbaServer s = getServer(SERVERS[i]);
			if (s != null && s.isAlive()) {
				s.shutdown(false);
			}
		}
		HeartBeatSender.getInstance().closeSocket();
	}
}
