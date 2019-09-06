import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<Integer> resourceArray = new ArrayList<Integer>();//number of each resource types
		
		ArrayList<Task> taskArray = new ArrayList<Task>();
		ArrayList<Task> taskArrayBanker = new ArrayList<Task>();
	
		int totalTaskNum;
		int totalResourceNum;

	
		File inputF = new File(args[0]);
		
		if(!inputF.exists()) {
			System.out.println("Error: file does not exist.");
			System.exit(1);
		}
		if(!inputF.canRead()) {
			System.out.println("Error: cannot read file.");
			System.exit(1);
		}
		
		
		//File inputF  = new File("input1.txt");
		Scanner scanner = new Scanner(inputF);
		
		/*
		 * Read first line of input, construct resource array
		 */
		totalTaskNum = scanner.nextInt();
		totalResourceNum = scanner.nextInt();
		
		for(int i = 0; i < totalResourceNum; i ++){
			resourceArray.add(scanner.nextInt());
		}
		
		// Initiate task array for FIFO and banker
		for(int i=1;i <= totalTaskNum; i++){
					
			Task t1 = new Task(totalResourceNum, i); 
			Task t2 = new Task(totalResourceNum, i);
			
			taskArray.add(t1);
			taskArrayBanker.add(t2);
		}
		
				
		/*
		 *	Read Activities and create Activity objects
		 */
		while(scanner.hasNext()){
			
			String act = scanner.next();
			int taskNum; int delay; int resourceType; int numRequested;
			Activity a1; Activity a2; 
			
			switch(act){
			case "initiate": 
				taskNum = scanner.nextInt();
				delay = scanner.nextInt();
				resourceType = scanner.nextInt();
				numRequested = scanner.nextInt();
				a1 =new Activity (act, taskNum, delay, resourceType, numRequested);
				a2 =new Activity (act, taskNum, delay, resourceType, numRequested);
				
				//Populate FIFO list
				taskArray.get(taskNum-1).activityList.add(a1);
				//taskArray.get(taskNum-1).claim[resourceType-1] = numRequested;
				//Populate Banker's list
				taskArrayBanker.get(taskNum-1).activityList.add(a2);
				//taskArrayBanker.get(taskNum-1).claim[resourceType-1] = numRequested;
				
				break;
				
			case "request":
				taskNum = scanner.nextInt();
				delay = scanner.nextInt();
				resourceType = scanner.nextInt();
				numRequested = scanner.nextInt();
				a1 =new Activity (act, taskNum, delay, resourceType, numRequested);
				a2 =new Activity (act, taskNum, delay, resourceType, numRequested);
				
				//Populate Task Lists of both Managers
				taskArray.get(taskNum-1).activityList.add(a1);
				taskArrayBanker.get(taskNum-1).activityList.add(a2);
				
				break;
			
			case "release":
				taskNum = scanner.nextInt();
				delay = scanner.nextInt();
				resourceType = scanner.nextInt();
				numRequested = scanner.nextInt();
				a1 =new Activity (act, taskNum, delay, resourceType, numRequested);
				a2 =new Activity (act, taskNum, delay, resourceType, numRequested);
				
				//Populate Task Lists of both Managers
				taskArray.get(taskNum-1).activityList.add(a1);
				taskArrayBanker.get(taskNum-1).activityList.add(a2);
				
				break;
			
			case "terminate":
				taskNum = scanner.nextInt();
				delay = scanner.nextInt();
				resourceType = scanner.nextInt();
				numRequested = scanner.nextInt();
				a1 =new Activity (act, taskNum, delay, resourceType, numRequested);
				a2 =new Activity (act, taskNum, delay, resourceType, numRequested);
				
				//Populate Task Lists of both Managers
				taskArray.get(taskNum-1).activityList.add(a1);
				taskArrayBanker.get(taskNum-1).activityList.add(a2);

				break;
			}
		
	}

		FIFO.run(taskArray, resourceArray);
		System.out.println();
		Banker.run(taskArrayBanker, resourceArray);

	}

}
