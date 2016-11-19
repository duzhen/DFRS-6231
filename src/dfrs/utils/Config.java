package dfrs.utils;

public class Config {
	private static final boolean TEST = true;
	//FE
	private static final String FE_HOST = "localhost";
	//RM
	private static final String RM_HOST_1 = "localhost";
	private static final String RM_HOST_2 = "localhost";
	private static final String RM_HOST_3 = "localhost";
	private static final String RM_HOST_4 = "localhost";
	//SERVER
	private static final String SERVER_HOST_1 = "localhost";
	private static final String SERVER_HOST_2 = "localhost";
	private static final String SERVER_HOST_3 = "localhost";
	private static final String SERVER_HOST_4 = "localhost";
	//SEQUENCER
	private static final String SE_HOST = "localhost";
	public static final int SE_RECEIVER_FE_UDP_PROT = 8888;
	
	public static final int FE_CORBA_PORT = 1050;
	public static final int FE_RECEIVE_SERVER_PORT_1 = 2001;
	public static final int FE_RECEIVE_SERVER_PORT_2 = 2002;
	public static final int FE_RECEIVE_SERVER_PORT_3 = 2003;
	public static final int FE_RECEIVE_SERVER_PORT_4 = 2004;
	
	public static String getFeHost() {
		if(TEST)
			return "localhost";
		return FE_HOST;
	}
	public static String getRmHost1() {
		if(TEST)
			return "localhost";
		return RM_HOST_1;
	}
	public static String getRmHost2() {
		if(TEST)
			return "localhost";
		return RM_HOST_2;
	}
	public static String getRmHost3() {
		if(TEST)
			return "localhost";
		return RM_HOST_3;
	}
	public static String getRmHost4() {
		if(TEST)
			return "localhost";
		return RM_HOST_4;
	}
	public static String getServerHost1() {
		if(TEST)
			return "localhost";
		return SERVER_HOST_1;
	}
	public static String getServerHost2() {
		if(TEST)
			return "localhost";
		return SERVER_HOST_2;
	}
	public static String getServerHost3() {
		if(TEST)
			return "localhost";
		return SERVER_HOST_3;
	}
	public static String getServerHost4() {
		if(TEST)
			return "localhost";
		return SERVER_HOST_4;
	}
	public static String getSeHost() {
		if(TEST)
			return "localhost";
		return SE_HOST;
	}
}
