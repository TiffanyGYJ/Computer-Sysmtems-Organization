import java.util.ArrayList;

public class Task {

	int[] currResource;
	int[] claim; 
	int waitingTime;
	int time; 
	int ID;  
	
int resourceT;
int numReq;
	
	boolean aborted;
	boolean processed;
	boolean blocked; 
	
	ArrayList<Activity> activityList = new ArrayList<Activity>();
	
	public Task(){
		
	}
	
	public Task(int totalResourceTypeNum, int Id){
		this.ID = Id; 
		this.waitingTime = 0; 
		this.aborted = false;
		this.processed = false;
		
	}
	
public Task(int resourceType, int numReq, int id){
		this.ID = id;
		this.resourceT = resourceType;
		this.numReq = numReq;
}
	
	public void blockTask() {
		this.waitingTime ++;
		this.blocked = true;
	}

	public void grantReq(int resourceIndex, int numRequested) {
		this.currResource[resourceIndex] += numRequested; 
	}

	public void release(int resourceIndex, int numRequested) {
		this.currResource[resourceIndex] -= numRequested;
	}

	
	public void printActivityList(){

		for(int i=0; i< this.activityList.size();i++){
			this.activityList.get(i).printActivity();
		}
	}

	public void terminate(int cycle) {
		this.time = cycle;
		
	}

}
