import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class Scheduler {
	
	static ArrayList<Integer> randomNum;
	static ArrayList<Process> processList;
	static ArrayList<Process> cycleList = new ArrayList<Process>();
	
	static int position; 
	static int cycle;
	static int finishTime;
	static int totalTurnaround;
	static int totalIO;
	static int totalWait;
	static int totalCPU;
	static int totalProcessNum;
	
	static boolean runner;
	static boolean verbose; 
	
	public static void main(String[] args) throws FileNotFoundException {
		
		processList = new ArrayList<Process>();
		String inputFileName;
		
		if(args[0].equals("--verbose")){
			verbose = true;
			inputFileName = args[1];
		}
		else {
			inputFileName = args[0];
		}
		
		//read input file
		//File inputF  = new File("input7.txt");
		File inputF = new File(inputFileName);
		if(!inputF.exists()) {
			System.err.println("Error: No such file");
			System.exit(0);
		}
		if(!inputF.canRead()) {
			System.err.println("Error: Cannot read file");
			System.exit(0);
		}
		
		//Scan file and create list
		Scanner input = new Scanner(new FileInputStream(inputF));
		int n = input.nextInt();
		for(int i = 0; i < n; i++){
			int A = input.nextInt();
			int B = input.nextInt();
			int C = input.nextInt();
			int IO = input.nextInt();
			
			Process p = new Process(A,B,C,IO);
			processList.add(p);	
		}
		
		
		System.out.print("The original input was: "+ n);
		for(Process p: processList){
			System.out.print("  "+p.A+" "+ p.B+ " "+ p.C+ " "+ p.IO);
		}
		
		
		//Sort input
		Collections.sort(processList, Process.Comparator1());
		System.out.println();
		System.out.print("The (sorted) input is: "+ n);
		for(Process p: processList){
			System.out.print("  "+p.A+" "+ p.B+ " "+ p.C+ " "+ p.IO);
		}
		System.out.println();
		
		
		//initialize the randomNum arrayList
		File ranNum = new File("randomNum.txt");
		Scanner scanner = new Scanner(new FileInputStream(ranNum));	
		randomNum = new ArrayList<Integer>();
		while(scanner.hasNext()){
			randomNum.add(scanner.nextInt());
		}
		
		position = 0; 
		FCFS();
		position = 0; 
		RR();
		position = 0; 
		UniP();
		position = 0; 
		PSJF();
			
	}


	/*
	 * First Come First Serve
	 */
	private static void FCFS() {
		initialize();
		cycle = 0;
		
		ArrayList<Process> readyL = new ArrayList<Process>();
		boolean done = false;
		runner = true;
		
		System.out.println();
		if(verbose){
			System.out.println("This detailed printout gives the state and remaining burst for each process");
		}
		System.out.println();
		
		while(!done){
			
			if(verbose){
				printVerbose();
			}
			
			countCPUbNum();
			countIObNum();
			
			//start next cycle
			for(Process p: cycleList){
				p.nextCycle();
			}
			
			//check and change process status 
			for(Process p: cycleList){
				switch(p.currState){
				//if not started, check if the arrival time reached
				case 0:	
					if(p.A == cycle){
						p.start();
						readyL.add(p);
					}
					break;
				//if process is running, check if it should be terminate or blocked	
				case 2:	 
					if(p.totalrCpuTime == 0){
						p.terminates();
						p.setfinishTime(cycle);
						runner = true; 
					}
					else if(p.rcpuBTime == 0){
						p.block();
						runner = true;
					}
					break;
				//if process if blocked, check if should unblock 	
				case 3:	
					if(p.riobTime == 0){
						p.unblock();
						readyL.add(p);
					}
					break;
				}
				
			}
			
			
			//Run first one in the ready list
			if(runner && !readyL.isEmpty()){
				Process p = readyL.remove(0);
				p.run();
				runner = false;
			}
			
			//if all done, stop scheduling
			for(Process p: cycleList){
				if(p.currState != Process.TERMINATED){
					done = false; 
					break;
				}
				done = true; 
				finishTime = cycle; 
			}
			
			cycle++; 
		}
		
		System.out.println();
		System.out.println("The scheduling algorithm used was First Come First Served");
		System.out.println();
		printSummary();
	}
	
	/*
	 * Round Robin
	 */
	private static void RR() {
		initialize();
		cycle = 0;
		
		for(Process p: cycleList){
			p.quantum = 2;
			p.quantumRemain = 2;
		}
		
		ArrayList<Process> readyL = new ArrayList<Process>();
		boolean done = false;
		runner = true;
		
		System.out.println();
		if(verbose){
			System.out.println("This detailed printout gives the state and remaining burst for each process");
		}
		System.out.println();
		
		while(!done){
			
			if(verbose){
				printVerbose();
			}
			
			countCPUbNum();
			countIObNum();
			
			//start next cycle
			for(Process p: cycleList){
				p.nextCycle();
			}
			
			//check and change process status 
			for(Process p: cycleList){
				switch(p.currState){
				//if not started, check if the arrival time reached
				case 0: 
					if(p.A == cycle){
						p.start();
						readyL.add(p);
					}
					break;
				//if process is running, check if it should be terminate or blocked	
				case 2:	 
					if(p.totalrCpuTime == 0){
						p.terminates();
						p.setfinishTime(cycle);
						runner = true; 
					}
					else if(p.rcpuBTime == 0){
						p.block();
						runner = true;
					}
					//CPU burst reach quantum
					else if(p.rb == 0){
						p.preempted();
						readyL.add(p);
						//System.out.println();
						runner = true;
					}
					break;
				//if process if blocked, check if should unblock 	
				case 3:	
					if(p.riobTime == 0){
						p.unblock();
						readyL.add(p);
					}
					break;
				}
				
			}
			
			
			//Run the first process in the ready list
			if(runner && !readyL.isEmpty()){
				Process p = readyL.remove(0);
				p.runRR();
				runner = false;
			}

				
			//if all done, stop scheduling
			for(Process p: cycleList){
				if(p.currState != Process.TERMINATED){
					done = false; 
					break;
				}
				done = true; 
				finishTime = cycle; 
			}
			
			cycle++; 
		}
		
		System.out.println();
		System.out.println("The scheduling algorithm used was Round Robin");
		System.out.println();
		printSummary();
	}

	
	
	/*
	 * One done another start
	 */
	private static void UniP() {
		initialize();
		cycle = 0;
		
		ArrayList<Process> readyL = new ArrayList<Process>();
		boolean done = false;
		System.out.println();

		runner = true;
		
		if(verbose){
			System.out.println("This detailed printout gives the state and remaining burst for each process");
		}
		
		System.out.println();
		while(!done){
			
			if(verbose){
				printVerbose();
			}
			
			countCPUbNum();
			countIObNum();
			
			//start next cycle
			for(Process p: cycleList){
				p.nextCycle();
			}
			
			//check and change process status 
			for(Process p: cycleList){
				switch(p.currState){
				//if not started, check if the arrival time reached
				case 0: 
					if(p.A == cycle){
						p.start();
						readyL.add(p);
					}
					break;
				//if process is running, check if it should be terminate or blocked	
				case 2:	 
					if(p.totalrCpuTime == 0){
						p.terminates();
						p.setfinishTime(cycle);
						runner = true;
					}
					else if(p.rcpuBTime == 0){
						p.block();
					}
					break;
				//if process is blocked, check if should run again 	
				case 3:	
					if(p.riobTime == 0){
						p.run();
					}
					break;
				}
				
			}
		
			//Run next process in the ready list
			if(runner && !readyL.isEmpty()){
					Process p = readyL.remove(0);
					p.run();
					runner = false;
			}
		
			
			//if all done, stop scheduling
			for(Process p: cycleList){
				if(p.currState != Process.TERMINATED){
					done = false; 
					break;
				}
				done = true; 
				finishTime = cycle; 
			}
			
			cycle++; 
		}
		
		System.out.println();
		System.out.println("The scheduling algorithm used was Uniprocessor");
		System.out.println(); 
		printSummary();
		
	}
	
	
	/*
	 * Shortest Job First Scheduling Algorithm
	 */
	private static void PSJF() {
		initialize();
		cycle = 0;
	
		ArrayList<Process> readyL = new ArrayList<Process>();
		boolean done = false;
		Process curProcess = new Process();
		runner = true;
		
		System.out.println();
		if(verbose){
			System.out.println("This detailed printout gives the state and remaining burst for each process");
		}
		System.out.println();
		
		while(!done){
			
			if(verbose){
				printVerbose();
			}
			
			countCPUbNum();
			countIObNum();
			
			//start next cycle
			for(Process p: cycleList){
				p.nextCycle();
			}
			
			//check and change process status 
			for(Process p: cycleList){
				switch(p.currState){
				//if not started, check if the arrival time reached
				case 0: 
					if(p.A == cycle){
						p.start();
						readyL.add(p);
					}
					break;
				//if process is running, check if it should be terminate or blocked	
				case 2:	 
					if(p.totalrCpuTime == 0){
						p.terminates();
						p.setfinishTime(cycle);
						runner = true; 
					}
					else if(p.rcpuBTime == 0){
						p.block();
						runner = true;
					}
					else{
						curProcess = p;
					}
					break;
				//if process if blocked, check if should unblock 	
				case 3:	
					if(p.riobTime == 0){
						p.unblock();
						readyL.add(p);
					}
					break;
				}
				
			}
			
			if(!readyL.isEmpty()){
				//sort first by input position
				//then by remaining total CPU time
				Collections.sort(readyL, Process.Comparator3());
				Collections.sort(readyL, Process.Comparator2());
				
				//run first one in the ready list
				if(runner){
					Process p = readyL.remove(0);
					p.run();
					runner = false;
				}
				//Block current process and Run the shortest CPU remaining time process
				else if(readyL.get(0).totalrCpuTime < curProcess.totalrCpuTime){
					Process p = readyL.remove(0);
					p.run();
					curProcess.preempted();
					readyL.add(curProcess);
					runner = false;
				}
				
			}
			
			//if all done, stop scheduling
			for(Process p: cycleList){
				if(p.currState != Process.TERMINATED){
					done = false; 
					break;
				}
				done = true; 
				finishTime = cycle; 
			}
			cycle++; 
		}
		
		System.out.println();
		System.out.println("The scheduling algorithm used was Preemptive Shortest Job First");
		System.out.println();
		printSummary();
		
	}
	
	
	
	//Initialize cycle running list and all variables
	public static void initialize(){
		cycleList.clear();
		for(Process p : processList) {
			Process temp = new Process(p);
			temp.setPositionInput(processList.indexOf(p));
			cycleList.add(temp);
		}
		cycle = 0;
		finishTime = 0;
		totalTurnaround = 0;
		totalWait = 0;
		totalCPU = 0;
		totalIO = 0;
		totalProcessNum = cycleList.size();
	}
	
	
	//Calculate IOBurst when block and CPUBurst when run
	//U is the upper bound for (0,U]
	public static int burstOS(int U){	
		int pick = randomNum.get(position);
		position ++;
		return 1+ (pick % U);
	}
	
	
	
	public static void countCPUbNum(){
		for(Process p: cycleList){
			if(p.currState == Process.RUNNING){
				totalCPU++;
				break;
			}
		}
	}
	
	
	public static void countIObNum(){
		for(Process p: cycleList){
			if(p.currState == Process.BLOCKED){
				totalIO++;
				break;
			}
		}
	}
	
	public static void printSummary() {
		//Each Process
		for(int i = 0; i < cycleList.size(); i++) {
			System.out.println();
			Process p = cycleList.get(i);
			System.out.println("Process " + i+":");
			System.out.println("\t(A,B,C,IO) = ("+p.A+","+p.B+","+p.C+","+p.IO+")");
			System.out.println("\tFinishing time: "+ p.finishTime);
			System.out.println("\tTurnaround time: "+ p.turnaroundTime);
			System.out.println("\tI/O time: "+p.tIoTime);
			System.out.println("\tWaiting time: "+ p.twaitTime);
		
			totalTurnaround += p.turnaroundTime;
			totalWait += p.twaitTime;
		}
		System.out.println();
		//Summary for all
		double cpuUtilization = (double)totalCPU/finishTime;
		double ioUtilization = (double)totalIO/finishTime;
		double throughput = (double)100/finishTime*totalProcessNum;
		double aveTurnaround = (double)totalTurnaround/totalProcessNum;
		double aveWait = (double)totalWait/totalProcessNum;
		
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: "+ finishTime);
		System.out.printf("\tCPU Utilization: %.6f%n", cpuUtilization);
		System.out.printf("\tI/O Utilization: %.6f%n", ioUtilization);
		System.out.printf("\tThroughput: %.6f processes per hundred cycles%n", throughput);
		System.out.printf("\tAverage turnaround time: %.6f%n", aveTurnaround);
		System.out.printf("\tAverage waiting time: %.6f%n", aveWait);
		System.out.println();
	}
	
	
	public static void printVerbose() {
		System.out.printf("%-12s%5d:", "Before cycle", cycle);
		for(Process p: cycleList) {
			String state = "";
			switch(p.currState) {
				case 0: state = "unstarted";
				break;
				case 1: state = "ready";
				break;
				case 2: state = "running";
				break;
				case 3: state = "blocked";
				break;
				case 4: state = "terminated";
				break;
			}
			System.out.printf("%12s%3d", state, p.rb);
		}
		System.out.println();
	}
}
