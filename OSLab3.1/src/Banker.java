import java.util.ArrayList;

public class Banker {

	static ArrayList<Integer> resourcesAva = new ArrayList<Integer>();
	static int[] releasedRe; //for all released resources in the cycle
	static ArrayList<Task> taskL = new ArrayList<Task>();
	
	static ArrayList<Task> blockL = new ArrayList<Task>();
		
	/*
	 * Banker algorithm 
	 * First try to finish requests that are not granted in previous run and check safety
	 * Then run each remaining tasks and check
	 */
	public static void run(ArrayList<Task> tasklist, ArrayList<Integer> resourcelist) {

			int cycle = 0;
			
			initialize(resourcelist, tasklist);
			
			while(!taskL.isEmpty()) {
				
				//resources pending to be added back in this run
				releasedRe = new int[resourcesAva.size()];
				for(int i = 0; i < releasedRe.length; i++) {
					releasedRe[i] = 0;
				}
				
			
				//process not granted requests first
				processBlockedTask(tasklist);
				
				//run each tasks
			for(int i = 0 ; i < taskL.size(); i++) {
					Task rT = taskL.get(i);
					
					//check if the task is aborted or terminated
					if(rT.activityList.isEmpty() || rT.processed){
						//if already processed request, set to no
						if(rT.processed){
							rT.processed = false;
						}
						continue; 
					}
					
					Activity a = rT.activityList.get(0);
					int reTy = a.resourceType;
					int numReq = a.numRequested;
					
				//run each task
					switch(a.activity){
					case "initiate":
						// if available < claim --- abort the task
						if(resourcesAva.get(reTy - 1) < numReq) {			
							Abort(rT);
							i--;
							System.out.println("Banker aborts task " + rT.ID + " before run begins:");
							System.out.println("	claim for resourse " + reTy + " (" + numReq +
									 ") exceeds number of units present (" +
									resourcesAva.get(reTy - 1) + ")");	
							break;

						}
						rT.claim[reTy - 1] = numReq;//set claim
						rT.activityList.remove(0);	
						break;
						
					case "request":
						if (a.delayA > 0) {
							a.delayA -= 1;
						}
						else {
							//check for safety
							
							// if request larger then claim --- abort
							if(numReq > rT.claim[reTy -1]) {
								//print message and abort task
								System.out.println("During cycle "+cycle+"-"+(cycle+1) +" of Banker's algorithms");
								System.out.printf("Task %d's request exceeds claim; aborted;"
										+ " %d units available next cycle \n", rT.ID, rT.currResource[reTy-1]);
								Abort(rT);
								break;
							}
							
							int[] copyRe = copyReArr();
							
							//first grant this request and check safety
							copyRe[reTy - 1] -= numReq;
							rT.claim[reTy - 1] -= numReq;
							rT.grantReq(reTy-1, numReq);

							ArrayList<Task> mockTArr = copyTaskArrL();
							
							if(checkSafety(copyRe, mockTArr)){
								//SAFE --- grant the resource to task
								resourcesAva.set(reTy - 1, resourcesAva.get(reTy - 1) - numReq);
								//finish the activity
								rT.activityList.remove(0);
							}
							else{
								//if not safe --- block
								rT.release(reTy-1, numReq);
								rT.claim[reTy - 1] += numReq;
								rT.blockTask();
								//put it in Block list
								Task blockT = new Task(reTy, numReq, rT.ID);
								blockL.add(blockT);
								
							}				
						}
						break;
					
					case "release":
						if (a.delayA > 0) {
							a.delayA -= 1;
						}
						else {
							rT.release(reTy-1, numReq);
							rT.claim[reTy -1] += numReq;
							releasedRe[reTy - 1] += numReq;
							rT.activityList.remove(0);
						}
						break;
					case "terminate":
						if (a.delayA > 0) {
							a.delayA -= 1;
						}
						else {
							rT.activityList.remove(0);
							rT.time = cycle;
							for(int j = 0; j < resourcesAva.size(); j++ ){
								releasedRe[j] +=  rT.currResource[j];
								rT.claim[j] = 0;
							}
							taskL.remove(i);
							i--;//terminate does not requires a whole cycle
						}
						break;
					}
					}
					
				
				//return all released units in this cycle back to the resource list
				for(int i = 0; i < resourcesAva.size(); i++ ){
					resourcesAva.set(i, resourcesAva.get(i) + releasedRe[i]);
				}
				
				
				cycle ++;
			}
			
			//print result
			printResult(tasklist);
		}
	
		
		
	private static void printResult(ArrayList<Task> tasklist) {
		System.out.println("	   BANKER'S");
		int totalTime = 0;
		int totalwait = 0;
		for(int j = 0;j< tasklist.size(); j++) {
			Task t = tasklist.get(j);
			if (t.aborted) {
				System.out.println("Task " + t.ID + "	aborted");
			}
			else {
				totalTime += t.time;
				totalwait += t.waitingTime;
				int percentage = (int) (Math.floor(((double)t.waitingTime/t.time)*100+ 0.5));
				System.out.println("Task " + t.ID + "	" + t.time + "	" 
										+t.waitingTime + "	" + percentage + "%");
			}
			
		}
		int totalPer = (int) (Math.floor(((double)totalwait/totalTime)*100+ 0.5));
		System.out.println("Total	" + totalTime + "	" + totalwait + "	" +
								totalPer + "%");	
	}



	private static int[] copyReArr() {
		int [] copy = new int[resourcesAva.size()];
		for(int j = 0; j < resourcesAva.size(); j ++ ){
			copy[j] = resourcesAva.get(j);
		}
			return copy;
	}

	private static ArrayList<Task> copyTaskArrL(){
		ArrayList<Task> copy = new ArrayList<Task>();
		for(Task t: taskL){
			copy.add(t);
		}
		return copy; 
	}

	
	/*
	 * Method process blocked task
	 * First check if it is safe to grant
	 * Then if safe, grant, else keep blocked 
	 */
	private static void processBlockedTask(ArrayList<Task> tasklist) {
		
		for(int i = 0; i < blockL.size(); i++){
			Task blockT = blockL.get(i);
			
			Task taskInList = tasklist.get(blockT.ID - 1);
			int resourceType = blockT.resourceT;
			int numReq = blockT.numReq;
			
			int[] mockList = copyReArr();

			//try to grant this request
			mockList[resourceType - 1] -= numReq;
			taskInList.claim[resourceType - 1] -= numReq;
			taskInList.currResource[resourceType - 1] += numReq;
			
			ArrayList<Task> mockTL = copyTaskArrL();
			
			if (!checkSafety(mockList, mockTL)) {
				//return the resources we try to grant the task
				taskInList.currResource[resourceType - 1] -= numReq;
				taskInList.claim[resourceType - 1] += numReq;
				//keep it waiting
				taskInList.blockTask();
			}
			
			else {
				//grant the resource to task
				resourcesAva.set(resourceType - 1, resourcesAva.get(resourceType - 1) - numReq);
				//finish the activity
				taskInList.activityList.remove(0);
				//remove the request from wait list
				blockL.remove(i);
				i--;
			}
			
			taskInList.processed = true;
		}
			
		}

	
	

	/* 
	 * Checks if all tasks can be finished if the request granted
	 */
		static boolean checkSafety(int[] reL, ArrayList<Task> mockTaskList) {
			//if there is no process remaining, return true
			
			if(mockTaskList.isEmpty()) {
				return true;
			}
			
			for(int i = 0; i < mockTaskList.size(); i++ ){				
				Task t = mockTaskList.get(i);
				//check if it's maximum additional request for each resource type
				//is less than or equal to remaining resource of this type
				boolean exceed = false;
				for(int j = 0; j < reL.length; j++) {
					//check if claim exceed resource available
					if(t.claim[j] > reL[j]){
						exceed = true;
						break;
					}
				}
										
				if(!exceed) {
					// if not exceed, mock granting request and check future allocations
					for(int m = 0; m < reL.length; m ++ ){
						reL[m] += t.currResource[m];
					}
					mockTaskList.remove(i--);
					//check new list	
					if(checkSafety(reL, mockTaskList)) {
						return true;
					}
					//future allocations will lead to deadlock --- NOT SAFE, put back
					else {
						for(int n = 0; n < reL.length; n ++){
							reL[n] -= t.currResource[n];
						}
						}
					} 
			}
			
			//if there is no process remaining after all future allocations mocking --- SAFE
			if(mockTaskList.isEmpty()) {
					return true;
			}
			
			return false;
		}
		
	/*
	 * Abort Task, clear activity list, remove from task list
	 * release all resources to resource list
	 */
		static void Abort(Task task) {
			task.aborted = true;
			task.activityList.clear();
			//release the resources holding by aborted task
			for(int j = 0; j< releasedRe.length; j++) {
				releasedRe[j] += task.currResource[j];
				task.currResource[j] = 0;
				task.claim[j] = 0;
			}
			taskL.remove(task);
			
		}
				
	/*
	 * Method makes populate resource list and task list in this class
	 */
		private static void initialize(ArrayList<Integer> resourcelist, ArrayList<Task> tasklist) {
			resourcesAva.clear();
			for(int i = 0; i < resourcelist.size(); i ++ ){
				resourcesAva.add(resourcelist.get(i));
			}
			
			//make a copy of the task and initialize the curResources list
			taskL.clear();
			for(int i = 0; i < tasklist.size(); i++) {
				Task t = tasklist.get(i);
				t.currResource = new int[resourcelist.size()];
				t.claim = new int[resourcelist.size()];
				int v = 0;
				while (v < resourcelist.size()) {
					t.currResource[v] = 0;
					v++;
				}
							
				taskL.add(t);
			}
		}

	}





