import java.util.Comparator;

public class Process {
	
	int A; //arrival time
	int B; //CPU burst upper bound
	int C; //total CPU time
	int IO; //IO burst upper bound
	
	int finishTime;
	int turnaroundTime;
	int tIoTime; //total IO time
	int twaitTime; //total waiting time
	
	int cpuBTime; //CPU burst time
	int rcpuBTime; //remaining CPU burst time
	int totalrCpuTime; //total remaining CPU time
	
	int iobTime; //IO burst time
	int riobTime; //remaining IO burst time
	
	int rb; //remaining burst at current cycle, either IO or CPU; for output PRINT
	
	int quantum; 
	int quantumRemain; 
	
	int positionInput; //record the position on the input list
	
	int currState = UNSTARTED; //current state
	
	
	public static final int UNSTARTED = 0;
	public static final int READY = 1;
	public static final int RUNNING = 2;
	public static final int BLOCKED = 3;
	public static final int TERMINATED = 4;
	
	
	
	public Process(){
	
	}
	
	public Process(int A, int B, int C, int IO){
			this.A = A;
			this.B = B;
			this.C = C;
			this.IO = IO;
			
			this.totalrCpuTime = C;
			this.tIoTime = 0;
			this.twaitTime = 0;
			this.quantum = -1;
			this.quantumRemain = -1;
		}
	
	public Process(Process p) {
			this.A = p.A;
			this.B = p.B;
			this.C = p.C;
			this.IO = p.IO;
			
			this.totalrCpuTime = p.totalrCpuTime;
			this.tIoTime = 0;
			this.twaitTime = 0;
			this.quantum = -1;
			this.quantumRemain = -1;
		}
	
	
	
	public void start(){
		this.currState = READY; 
	}
	
	
	
	/*
	 * Match output for FCFS, PSJF, Uni
	 */
	public void run(){
		this.currState = RUNNING; 
		
		if(this.rcpuBTime == 0){
			this.cpuBTime = Scheduler.burstOS(this.B);
			this.rb = this.cpuBTime; 
			//when more CPU burst time then needed
			if(this.cpuBTime > this.totalrCpuTime){
				this.cpuBTime = this.totalrCpuTime;
			}
			this.rcpuBTime = this.cpuBTime;
		}
		else{
			this.rb = this.rcpuBTime;
		}
	
	}
	
	/*
	 * Match output for RR
	 */
	public void runRR(){
		this.currState = RUNNING; 
		
		if(this.rcpuBTime == 0){
			this.cpuBTime = Scheduler.burstOS(this.B);
			//when more CPU burst time then needed
			if(this.cpuBTime > this.totalrCpuTime){
				this.cpuBTime = this.totalrCpuTime;
			}
			this.rcpuBTime = this.cpuBTime;
			this.rb = this.cpuBTime; 
			//RR case, make remain burst 2 if larger
			if(this.quantum == 2){
				if(this.cpuBTime > 2){
				if(this.rb > 2){
					this.rb = 2; 
				}
				}
			}
		}
		else{
			this.rb = this.rcpuBTime;
			//RR case, make remain burst 2 if larger
			if(this.quantum == 2){
				if(this.cpuBTime > 2){
				if(this.rb > 2){
					this.rb = 2; 
				}
				}
				if(this.rcpuBTime == 1){
					this.rb = 1;
				}
			}
		}
	}
	
	
	public void block(){
		this.currState = BLOCKED;
		this.iobTime = Scheduler.burstOS(this.IO);
		this.rb = this.iobTime;
		this.riobTime = this.iobTime;
	}
	
	
	public void unblock(){
		this.currState = READY; 
	}
	
	public void preempted(){
		this.currState = READY; 
	}
	
	public void terminates(){
		this.currState = TERMINATED; 
		this.rb = 0;
	}
	
	
	
	//changes each cycle
	public void nextCycle(){
		if(this.currState == RUNNING){
			this.totalrCpuTime --; 
			this.rcpuBTime --;
			this.rb --;
			this.quantumRemain --; 
			
		}
		if(this.currState == BLOCKED){
			this.riobTime --;
			this.rb --;
			this.tIoTime ++;
		}
		if(this.currState == READY){
			this.twaitTime ++;
		}
	}
	
	
	//set finish and turn around
	public void setfinishTime(int t) {
		this.finishTime = t;
		this.setturnaroundTime();
	}
	
	private void setturnaroundTime() {
		this.turnaroundTime = this.finishTime - this.A;
	}
	
	
	public void setPositionInput(int p){
		this.positionInput = p;
	}
	
	public int getPositionInput(){
		return this.positionInput;
	}
		
	
	//sorter
	public static Comparator<Process> Comparator1(){
		return new Comparator<Process>(){
			public int compare(Process p1, Process p2){
				return Integer.compare(p1.A, p2.A);
			}
		};
	}
	
	
	public static Comparator<Process> Comparator2() {
		return new Comparator<Process>() {
			public int compare(Process p1, Process p2) {
				return Integer.compare(p1.totalrCpuTime, p2.totalrCpuTime);
			}
		};
	}
	
	public static Comparator<Process> Comparator3() {
		return new Comparator<Process>() {
			public int compare(Process p1, Process p2) {
				return Integer.compare(p1.positionInput, p2.positionInput);
			}
		};
	}
		
}
