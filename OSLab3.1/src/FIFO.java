import java.util.ArrayList;

public class FIFO {
	static ArrayList<Integer> resourcesAvailable = new ArrayList<Integer>(); //resources available
	static int[] releasedRe;
	static ArrayList<Task> taskL = new ArrayList<Task>();
	
	static ArrayList<Task> blockL = new ArrayList<Task>();
	
	/*
	 * FIFO algorithm 
	 * First check for deadlock, then finish undone requests 
	 * then run remaining tasks
	 */
	public static void run(ArrayList<Task> tasklist, ArrayList<Integer> resourcelist) {
		int cycle = 0;
		
		initialize(resourcelist, tasklist);
	
		while(!taskL.isEmpty()) {

			//initialize the resources that needed to be added back in this cycle
			releasedRe = new int[resourcesAvailable.size()];
			for(int i = 0; i < releasedRe.length; i ++ ){
				releasedRe[i] = 0; 
			}
			
			//detect deadlock
			int[] copyRe = copyReArr();
			detectDeadlock(copyRe);

			
			//process requests not finished first
			processBlockedTask(tasklist);
			
			//run each tasks
			for(int i = 0; i < taskL.size(); i++) {
				Task task = taskL.get(i);

				if(task.processed || task.activityList.isEmpty()){
					if(task.processed){
						task.processed = false;
					}
					continue;
				}
				
				Activity act = task.activityList.get(0);
				switch(act.activity){
				case "initiate":
					task.activityList.remove(0);
					break;
					
				case "request":
					if(act.delayA > 0){
						act.delayA -= 1;
					}
					else{
						//check if resources are enough for request
						if(resourcesAvailable.get(act.resourceType-1) >= act.numRequested){
							task.grantReq(act.resourceType-1, act.numRequested);
							resourcesAvailable.set(act.resourceType-1, 
									resourcesAvailable.get(act.resourceType-1) - act.numRequested);
							task.activityList.remove(0);
						}
						else{
							//not enough, request go into wait list 
							task.blockTask();
							Task blockT = new Task(act.resourceType, act.numRequested, task.ID);
							blockL.add(blockT);
							
						}
					}
					break;
					
				case "release":
					if(act.delayA > 0){
						act.delayA -= 1;
					}
					else{
						task.release(act.resourceType-1, act.numRequested);
						releasedRe[act.resourceType-1] += act.numRequested;
						task.activityList.remove(0);
					}
					break;
				
				case "terminate":
					if(act.delayA > 0){
						act.delayA -= 1;
					}
					else{
						task.activityList.remove(0);
						task.terminate(cycle);
						//put back all terminate task's resources 
						for(int update = 0; update < resourcesAvailable.size(); update ++){
							releasedRe[update] += task.currResource[update];
						}
						taskL.remove(i);
						i--; //terminate does not take a whole cycle
					}
					break;
				}

			}
			
			//return released units in this cycle back to the resource list
			for(int i = 0; i < resourcesAvailable.size(); i ++ ){
				resourcesAvailable.set(i, resourcesAvailable.get(i) + releasedRe[i]);
			}	
				
			cycle ++;
		}
		
		printresult(tasklist);
	}
	
	
    private static void printresult(ArrayList<Task> tasklist) {
    	System.out.println("  	   FIFO");
		int totaltime = 0;
		int totalwait = 0;
		for(int i = 0; i < tasklist.size(); i++) {
			Task t = tasklist.get(i);
			if(t.aborted) {
				System.out.println("Task " + t.ID + "	aborted");
			}
			else {
				totaltime += t.time;
				totalwait += t.waitingTime;
				int percentage = (int) (Math.floor(((double)t.waitingTime/t.time)*100+ 0.5));
				System.out.println("Task "+ t.ID + "	" + t.time + "	" +
						t.waitingTime + "	" + percentage + "%");
			}
			
		}
		int totalPer = (int) (Math.floor(((double)totalwait/totaltime)*100+ 0.5));
		System.out.println("Total	" + totaltime +"	" + totalwait +"	"+ totalPer + "%");
		
	}
	
    /*
     * Process Blocked Task
     * First check if task is aborted, then check resources are enough to grant
     */

	private static void processBlockedTask(ArrayList<Task> tasklist) {

		for(int count = 0; count < blockL.size(); count ++ ){
	
			Task blocked = blockL.get(count);
			int resourceType = blocked.resourceT;
			int numReq = blocked.numReq;
			
			Task taskInList = tasklist.get(blocked.ID - 1);
			//not aborted task
			if(!taskInList.aborted) {
				//available < requested --- not run, block
				if(resourcesAvailable.get(resourceType-1) < numReq){
					taskInList.blockTask();
				}
				else {
					//if the resources are available, allocate them to task and finish the activity
					taskInList.grantReq(resourceType-1, numReq);
					resourcesAvailable.set(resourceType -1 , 
											resourcesAvailable.get(resourceType - 1) - numReq);
					taskInList.activityList.remove(0);
					//remove the request from wait list
					blockL.remove(count);
					count--;
				}

				//mark the task as checked
				taskInList.processed = true;
			}
		}				
		
	}
	
	

	private static int[] copyReArr() {
		int[] copy = new int[resourcesAvailable.size()];
		for(int i = 0 ; i < resourcesAvailable.size(); i++ ){
			copy[i] = resourcesAvailable.get(i);
		}
		return copy;
	}


	/*
	 * Method makes populate resource list and task list in this class
	 */
	private static void initialize(ArrayList<Integer> resourcelist, ArrayList<Task> tasklist) {
		resourcesAvailable.clear();	
		for(int i = 0; i < resourcelist.size(); i++ ){
			resourcesAvailable.add(resourcelist.get(i));
		}
		
		//make a copy of the task and initialize the curResources list
		taskL.clear();
		for(int i = 0; i < tasklist.size(); i++) {
			Task t = tasklist.get(i);
			taskL.add(t);
			t.currResource= new int[resourcelist.size()];
			for(int j = 0; j < resourcelist.size(); j ++){
				t.currResource[j] = 0;
			}
		}	
		
	}

	/*
	 * By checking if the activities remain are all requesting 
	 * resources that the system cannot grant
	 */
	static void detectDeadlock(int[] resourcesAva ) {
		boolean deadlock = true;
	
		//first check if asking more then have 
		for(int i=0; i< taskL.size(); i++) {
			Task t = taskL.get(i);
			if(t.activityList.isEmpty()){
				deadlock = false;
				break;
			}
			//activity list not empty
			else{
				Activity a = t.activityList.get(0);
				if(a.activity.equals("request")){
					if(resourcesAva[a.resourceType-1] >= a.numRequested){
						deadlock = false;
						break;
					}
				}
				//not requesting
				else{
					deadlock = false;
					break;
				}
			}
		}
		
		if(deadlock) {
			Task abort = taskL.get(0);
			abort.aborted = true;
			abort.activityList.clear();
			//release the resources the aborted task holds
			for(int i = 0; i < resourcesAva.length; i ++){
				resourcesAva[i] += abort.currResource[i];
				releasedRe[i] += abort.currResource[i];
			}

			taskL.remove(0);	
			if(!taskL.isEmpty()) {
				detectDeadlock(resourcesAva);
			}
		}
	}
	
}
