
public class Frame {

	int page;
	Process proInFrame;
	
	int loadedT; 
	int evictT;
	int lastUsedT; 
	
	public Frame(){
		
	}
	
	public void evict() {
		this.evictT = Main.time;	
		proInFrame.evictionNum ++;
		proInFrame.residency += (this.evictT - this.loadedT);
		proInFrame.pageTable.remove(this);
	}
	
}
