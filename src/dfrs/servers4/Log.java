package dfrs.servers4;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Log {
	public static final String LOG_DIR = "./";
	
	public static void i(String file, String log) {
		FileWriter fw;
		try {
			fw = new FileWriter(LOG_DIR+file, true);
			fw.write("\r\n"+new Date().toString()+"-----ACTION:"+log);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void e(String file, String log) {
		FileWriter fw;
		try {
			fw = new FileWriter(LOG_DIR+file, true);
			fw.write("\r\n"+new Date().toString()+"-----ERROR:"+log);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public static void createLogDir(String d) {
		try {
			File dir = new File(d);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
