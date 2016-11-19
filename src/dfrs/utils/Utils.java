package dfrs.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Utils {
	public static String exec(String command) {
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            process.getOutputStream().close();
            reader.close();
            process.destroy();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return sb.toString();
    }
}
