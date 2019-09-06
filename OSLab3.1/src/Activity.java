
public class Activity {
	String activity;
	int taskNum;
	int delayA;
	int resourceType;
	int numRequested;
	int late;
	
	
	public Activity(){
		
	}
	
	public Activity (String activity, int taskNum, int delay, int resourceType, int NumRequested) {
		this.activity = activity;
		this.taskNum = taskNum;
		this.delayA = delay;
		this.resourceType = resourceType;
		this.numRequested = NumRequested;
		//this delayed = delay; 
	}
	
	public Activity (int taskID, int resourceType, int NumRequested){
		this.taskNum = taskID;
		this.resourceType = resourceType;
		this.numRequested = NumRequested;
	}
	
	public void printActivity(){
		System.out.println(activity+" "+taskNum+" "+delayA+" "+resourceType+" "+numRequested);
		
	}
}