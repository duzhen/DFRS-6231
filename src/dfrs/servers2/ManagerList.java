package dfrs.servers2;

import java.util.ArrayList;

public class ManagerList {
	private ArrayList<Manager> managerList;
	
	public ManagerList(){
		managerList = new ArrayList<>();
	}
	
	public void addManager(Manager m){
		managerList.add(m);
	}
	
	public void removeManager(Manager m){
		managerList.remove(m);
	}

	public ArrayList<Manager> getManagerList() {
		return managerList;
	}

	

}
