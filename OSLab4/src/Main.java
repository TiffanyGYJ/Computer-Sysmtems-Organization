import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	public static int MachineS; 
	public static int PageS;
	public static int processS; 
	public static int JobMix; 
	public static int NumOfRefP; //number of reference for each process
	public static String RepAlgo; 
	
	public static ArrayList<Process> processL = new ArrayList<Process>();
	public static ArrayList<Process> simulateL = new ArrayList<Process>();
	
	public static int time;
	public static int numOfFrame;
	public static int quantum = 3;

	public static int freeframeNum;
	
	public static ArrayList<Frame> frameTable = new ArrayList<Frame>();
	public static ArrayList<Frame> frametableFIFO = new ArrayList<Frame>();
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		if(args.length < 6) {
			System.err.println("Error: Incomplete argument.");
			System.exit(0);
		}
		MachineS = Integer.parseInt(args[0]);
		PageS = Integer.parseInt(args[1]);
		processS = Integer.parseInt(args[2]);
		JobMix = Integer.parseInt(args[3]);
		NumOfRefP = Integer.parseInt(args[4]);
		RepAlgo = args[5];
		
/*		
		File inputF = new File("input5.txt");
		Scanner sc = new Scanner(inputF); 
		
		MachineS = sc.nextInt();
		PageS = sc.nextInt();
		processS = sc.nextInt();
		JobMix = sc.nextInt();
		NumOfRefP = sc.nextInt();
		RepAlgo = sc.next(); */
		
		File file = new File("randomNum.txt");
		if(!file.exists()) {
			System.err.println("Error: Cannot find random number file");
			System.exit(0);
		}
		Scanner random = new Scanner(file);
		
		//Print input
		System.out.println("The machine size is " + MachineS + ".");
		System.out.println("The page size is " + PageS + ".");
		System.out.println("The process size is " + processS + ".");
		System.out.println("The job mix number is " + JobMix + ".");
		System.out.println("The number of references per process is " + NumOfRefP + ".");
		System.out.println("The replacement algorithem is " + RepAlgo + ".");	
		System.out.println("The level of debugging output is " + args[6]);
		
		
		//initialize frame table
		numOfFrame = MachineS / PageS;
		freeframeNum = numOfFrame;
		for(int i = 0; i < numOfFrame; i++) {
			Frame f = new Frame();
			frameTable.add(f);
		}
		
		//create processes		
		if(JobMix == 1) {
			Process p = new Process(1, PageS, processS, NumOfRefP, 1, 0, 0);
			processL.add(p);
			simulateL.add(p);
		}
		else if (JobMix == 2) {
			for(int i = 1; i < 5; i++) {
				Process p = new Process(i, PageS, processS, NumOfRefP, 1, 0, 0);
				processL.add(p);
				simulateL.add(p);
			}
		}
		else if(JobMix == 3) {
			for(int i = 0; i < 4; i++) {
				Process p = new Process(i+1, PageS, processS, NumOfRefP, 0, 0, 0);
				processL.add(p);
				simulateL.add(p);
			}
		}
		else if(JobMix == 4) {
			Process p1 = new Process(1, PageS, processS, NumOfRefP, 0.75, 0.25, 0);
			processL.add(p1);
			simulateL.add(p1);
			Process p2 = new Process(2, PageS, processS, NumOfRefP, 0.75, 0, 0.25);
			processL.add(p2);
			simulateL.add(p2);
			Process p3 = new Process(3, PageS, processS, NumOfRefP, 0.75, 0.125, 0.125);
			processL.add(p3);
			simulateL.add(p3);
			Process p4 = new Process(4, PageS, processS, NumOfRefP, 0.5, 0.125, 0.125);
			processL.add(p4);
			simulateL.add(p4);
		}
		

		//Simulate paging
		while(!simulateL.isEmpty()) {
			Process p = simulateL.remove(0);
			for(int ref = 0; ref < quantum; ref++) {
				//still have references needed to be processed
				if(p.refNum > 0) {	
					time ++;
					
					//Page Hit
					if(!pageFault(p)){					
						
					}
					//Page Fault, replacement needed
					else{
						p.pageFault ++;

						if(freeframeNum > 0){						
							//if there are free frames, place the page to the one with highest number
							Frame frame = frameTable.get(freeframeNum - 1);	
							freeframeNum -- ;
							p.placement(frame);
							frametableFIFO.add(frame);
							
						}
						else {
							switch(RepAlgo) {
							case "fifo":
								Frame frameF = frametableFIFO.remove(0);
								frameF.evict();
								p.placement(frameF);
								frametableFIFO.add(frameF);
								break;
								
							case "random":
								int randomNum = random.nextInt();
								int index = (randomNum + numOfFrame) % numOfFrame;
								Frame frameR = frameTable.get(index);								
								frameR.evict();
								p.placement(frameR);								
								break;
							
							case "lru":
								int find = time;
								Frame frameL = null;
								for(Frame search: frameTable) {
									if(search.lastUsedT < find) {
										find = search.lastUsedT;
										frameL = search;
									}
								}
								frameL.evict();
								p.placement(frameL);
								break;
							}				
						}
					}
					
					//next reference for process p
					p.refNum --;
					p.nextRef(random);							
				}
				else{
					break;
				}
			}
			if(p.refNum > 0) {
				simulateL.add(p);
			}
		}
		//print output
		int totalFault = 0;
		int totalEviction = 0;
		int totalResidency = 0;
		
		System.out.println();
		for(int i = 0; i < processL.size();i++) {
			Process p = processL.get(i);
			totalFault += p.pageFault;
			totalEviction += p.evictionNum;
			totalResidency += p.residency;
			
			if(p.evictionNum == 0) {
				System.out.println("Process " + p.id + " has " + p.pageFault + " page faults.");
				System.out.println("	With no evictions, the average residence is undefined.");
			}
			else{
				double average = (double)p.residency/p.evictionNum;
				System.out.println("Process " + p.id + " has " + p.pageFault + " page faults and " +
						average + " average residency.");
			}
		}
		
		double aveResidency = (double)totalResidency/totalEviction;
		System.out.println();
		if(totalEviction == 0) {
			System.out.println("The total number of faults is " + totalFault +".");
			System.out.println("	With no evictions, the overall average residence is undefined.\n");
		}
		else{
			System.out.print("The total number of faults is " + totalFault);
			System.out.println(" and the overal average residency is " + aveResidency + ".");
		}
	}
	
	
	//check if there is a page fault
	private static boolean pageFault(Process p) {
		for(int i = 0; i < p.pageTable.size(); i++) {
			Frame frame = p.pageTable.get(i);
			//Page Hit
			if(frame.page == p.refPage){
				frame.lastUsedT = time;
				return false;
			}
		}
		return true;
	}

}
