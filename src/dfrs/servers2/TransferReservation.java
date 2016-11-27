package dfrs.servers2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransferReservation {
	private static TransferReservation instance;
	private HashMap<String, List<ITransaction>> data;
	private HashMap<String, Result> netPackage;
	
	private TransferReservation() {
		data = new HashMap<String, List<ITransaction>>();
		netPackage = new HashMap<String, Result>();
	}

	public static synchronized TransferReservation getInstance() {
		if (instance == null) {
			instance = new TransferReservation();
		}
		return instance;
	}
	
	public synchronized boolean initTransaction(String id, ITransaction t) {
		if(data.containsKey(id)) {
			return false;
		} else {
			ArrayList<ITransaction> list = new ArrayList<ITransaction>();
			list.add(t);
			data.put(id, list);
			return true;
		}
	}
	
	public boolean addTransactionOperation(String id, ITransaction t) {
		if(data.containsKey(id)) {
			List<ITransaction> list = data.get(id);
			list.add(t);
			return true;
		} else {
			return false;
		}
	}
	
	public Result doTransaction(String id) {
		Result result = new Result();
		List<ITransaction> list = data.get(id);
		if(list == null) {
			return result;
		}
		try {
			for(ITransaction l:list) {
				l.doCommit();
			}
			result.success = removeTransaction(id);
			if(!result.success) {
				throw new Exception("Time out");
			}
			result.content = "Success";
		} catch(Exception e) {
			for(ITransaction l:list) {
				l.backCommit();
			}
			result.success = false;
			result.content = e.getMessage();
		} 
//		finally {
//			data.remove(id);
//		}
		return result;
	}
	
	public synchronized boolean removeTransaction(String id) {
		if(data.containsKey(id)) {
			data.remove(id);
			return true;
		}
		return false;
	}
	
	public Result popNetPackage(String key) {
		if(key == null)
			return null;
		return netPackage.remove(key);
	}
	
	public void pushNetPackage(String key, Result value) {
		if(key == null || value == null)
			 return;
		netPackage.put(key, value);
	}
}
