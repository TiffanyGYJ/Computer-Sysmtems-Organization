import java.util.ArrayList;
import java.util.Scanner;

public class Process {

	int id;
	int pageSize; 
	int processSize; 
	int refNum; 
	
	double A; 
	double B;
	double C; 
	int refWord; //reference word
	int refPage; //reference page
	
	int pageFault; //number of page fault
	int evictionNum ; //number of eviction
	int residency;
	
	boolean terminated;
	
	ArrayList<Frame> pageTable = new ArrayList<Frame>(); 
	
	
	public void Process(){
		
	}
	
	public Process(int ID, int pageS, int processS, int NumOfRef, double A, double B, double C){
		this.id = ID;
		this.pageSize = pageS;
		this.processSize = processS;
		this.refNum = NumOfRef;
		this.A = A;
		this.B = B;
		this.C = C;
		
		this.refWord = (111 * id + this.processSize) % this.processSize;
		this.refPage = this.refWord / this.pageSize;
		
		this.pageFault = 0;
		this.evictionNum = 0;
		this.residency = 0;
		
		this.terminated = false;
	}

	
	public void placement(Frame f){
		f.proInFrame = this;
		f.page = this.refPage;
		f.loadedT = Main.time;
		f.lastUsedT = Main.time;
		this.pageTable.add(f);
	}
	
	
	//proceed to next reference word
	public void nextRef(Scanner random) {
		int randomNum = random.nextInt();
		
		double y = randomNum / (Integer.MAX_VALUE + 1d);
		if(y < A) {
			this.refWord = (refWord + 1 + this.processSize) % this.processSize;
		}
		else if(y < A+B) {
			this.refWord = (refWord - 5 + this.processSize) % this.processSize;
		}
		else if(y < A+B+C) {
			this.refWord = (refWord + 4 + this.processSize) % this.processSize;
		}
		else {
			randomNum = random.nextInt();
			this.refWord = (randomNum + this.processSize) % this.processSize;
		}

		this.refPage = this.refWord / this.pageSize;
	}
	
	
}
