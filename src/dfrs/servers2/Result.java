package dfrs.servers2;

import java.io.Serializable;

public class Result implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 555211124072042301L;
	public boolean success;
	public String content;
	
	public Result() {
		
	}
	
	public Result(boolean success, String content) {
		this.success = success;
		this.content = content;
	}
//	public boolean isSuccess() {
//		return success;
//	}
//	public void setSuccess(boolean success) {
//		this.success = success;
//	}
//	public String getContent() {
//		return content;
//	}
//	public void setContent(String content) {
//		this.content = content;
//	}
}
