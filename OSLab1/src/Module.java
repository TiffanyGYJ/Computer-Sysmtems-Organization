import java.util.ArrayList;

public class Module {
	
	private int relativePosition = 0; 
	
	private int numOfNewVariable;
	private int numOfVariableUsed; 
	private int numOfAddress; 
	
	private int moduleNum;
	
	private ArrayList<Variable> adjustedVariable;
	private String[] usage; 
	private Address[] addressArray; 
	
	ArrayList<Variable> varUsedModule; 
	
	public Module(){
		
	}
	
	public Module(ArrayList<Variable> newVariable, String[] inputUsage, Address[] Address, int mNum, ArrayList<Variable> initialVarUsedModule){ 
	
		this.adjustedVariable = newVariable;
		this.setUsage(inputUsage);
		this.setAddressArray(Address); 
		
		this.setNumOfAddress(Address.length);
		this.setNumOfNewVariable(newVariable.size());
		this.setNumOfVariableUsed(inputUsage.length);
		
		this.varUsedModule = initialVarUsedModule;
		this.setModuleNum(mNum);
		
	}
	
	
	public ArrayList<Variable> getVarUsedModule(){
		return varUsedModule; 
	}
	
	public void  setVarUsedModule(ArrayList<Variable> input){
		this.varUsedModule = input; 
	}
	
	
	public ArrayList<Variable> getAdjustedVariable() {
		return adjustedVariable;
	}


	
	
	/* change the value of variables according to the relative position of the module */
	//check if variable defined exceeds the size of the module (number of addresses in module)	
	public void setAdjustedVariable(ArrayList<Variable> originalVariable) {
		
		this.adjustedVariable = originalVariable;
		
		for(int i = 0; i < originalVariable.size(); i++){
			if(originalVariable.get(i).getValue() < this.numOfAddress){
				this.adjustedVariable.get(i).setValue(originalVariable.get(i).getValue() + this.relativePosition);
				this.adjustedVariable.get(i).setDefExceedSize(false);
			}
			else{
				this.adjustedVariable.get(i).setValue(this.relativePosition);
				this.adjustedVariable.get(i).setDefExceedSize(true);
				
			}
		}
		
	}

	public String[] getUsage() {
		return usage;
	}

	public void setUsage(String[] usage) {
		this.usage = usage;
	}

	public Address[] getAddressArray() {
		return addressArray;
	}

	public void setAddressArray(Address[] addressArray) {
		this.addressArray = addressArray;
	}

	public int getRelativePosition() {
		return relativePosition;
	}

	public void setRelativePosition(int relativePosition) {
		this.relativePosition = relativePosition;
	}

	public int getNumOfNewVariable() {
		return numOfNewVariable;
	}

	public void setNumOfNewVariable(int numOfNewVariable) {
		this.numOfNewVariable = numOfNewVariable;
	}

	public int getNumOfVariableUsed() {
		return numOfVariableUsed;
	}

	public void setNumOfVariableUsed(int numOfVariableUsed) {
		this.numOfVariableUsed = numOfVariableUsed;
	}

	public int getNumOfAddress() {
		return numOfAddress;
	}

	public void setNumOfAddress(int numOfAddress) {
		this.numOfAddress = numOfAddress;
	}

	
	
	public void printVariableInModule(){
		
		for(int i = 0; i < this.adjustedVariable.size(); i++){
			if(!this.adjustedVariable.get(i).isDefinedMul()){
			System.out.println(this.adjustedVariable.get(i).getName() + "=" + this.adjustedVariable.get(i).getValue());
			}
			else{
			System.out.println(this.adjustedVariable.get(i).getName() + "=" + this.adjustedVariable.get(i).getValue()
								+ " Error: This variable is multiply defined; first value used.");
			}
		}
	}
	

	
	public void printDefExceedsModuleSize() {
		
		for(int i = 0; i < this.adjustedVariable.size(); i ++){
			if(this.adjustedVariable.get(i).isDefExceedSize()){
				System.out.println("Error: In module "+ this.adjustedVariable.get(i).getModuleNum()+
									" the def of " + this.adjustedVariable.get(i).getName()+
									" exceeds the module size; zero (relative) used.");
			}
		}
		
		
	}

	public int getModuleNum() {
		return moduleNum;
	}

	public void setModuleNum(int moduleNum) {
		this.moduleNum = moduleNum;
	}

	/*
	public void printVarInListNotUse() {
		
		for(int i = 0; i < this.getVarUsedModule().size(); i ++){
			System.out.println(this.varUsedModule.get(i).getName() + "used "+this.moduleNum+ this.varUsedModule.get(i).isUsed());
			if((!this.varUsedModule.get(i).isUsed()) && (!this.varUsedModule.get(i).isDefNotUsed())){
				System.out.println("Warning: In module "+this.moduleNum+" "+
									this.varUsedModule.get(i).getName()+" "
									+ "appeared in the use list but was not actually used.");
			}
		}
		
	}

	 */


}
