package dfrs.servers;

import dfrs.utils.Config;

public class ServerCluster1 extends BaseServerCluster {
	
	public static final String SERVER_HOST = Config.getServerHost1();
	//CORBA
	public static final String SERVER_MTL_CORBA_PORT = "9050";
	public static final String SERVER_WST_CORBA_PORT = "9051";
	public static final String SERVER_NDL_CORBA_PORT = "9052";
	//HEARTBEAT
//	public static final int RM_HEARTBEAT_MTL_PORT = 7211;
//	public static final int RM_HEARTBEAT_WST_PORT = 7212;
//	public static final int RM_HEARTBEAT_NDL_PORT = 7213;
	
	public static String getCorbaPort(String server) {
		if(SERVER_MTL.equals(server)) {
			return SERVER_MTL_CORBA_PORT;
		} else if(SERVER_WST.equals(server)) {
			return SERVER_WST_CORBA_PORT;
		} else if(SERVER_NDL.equals(server)) {
			return SERVER_NDL_CORBA_PORT;
		}
		return "";
	}
	
	private static ServerCluster1 rm;
	private String[] args;
	public ServerCluster1(String[] args) {
		this.args = args;
	}

	public static void main(String[] args) {
		rm = new ServerCluster1(args);
		rm.createServers(SERVERS);
		rm.startServer(SERVERS);
//		Runtime.getRuntime().addShutdownHook(new Thread() {
//		    public void run() {
//		        try {
//		        	System.out.println("程序重启！");
//		            String restartCmd = "cmd /c start java -jar main.jar";
//		            Thread.sleep(5000);//等10秒，保证分身启动完成后，再关掉自己
//		            Utils.exec(restartCmd);
//		            System.out.println("程序重启完成！");
//		        } catch (Exception e) {
//		        	System.out.println("重启失败，原因："+e.getMessage());
//		        }
//		    }
//		});
//		 try {
//	            Thread.sleep(10000);//等10秒，保证分身启动完成后，再关掉自己
//	        } catch (Exception e) {
//	        	System.out.println("重启失败，原因："+e.getMessage());
//	        }
//		rm.stopAllServer();
//		System.exit(0);
	}

	@Override
	protected void createServers(String[] servers) {
		if(servers == null)
			return;
		for(int i=0;i<servers.length;i++) {
			registerServer(CorbaServer.createServer(this.getClass(), servers[i], args));
		}
	}
}
