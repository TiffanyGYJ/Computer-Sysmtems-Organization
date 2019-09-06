
public class Address extends Module{
	
	private String type; 
	private int addValue; 
	private String varUsedNotDef;
	
	private boolean varUsedNotDefB;
	private boolean extAddExceedUseList; 
	private boolean testAbExceed; 
	private boolean testReExceed; 

	public Address(){
		
	}
	
	public Address(String typeInput, int addressInput){
		this.type = typeInput; 
		this.addValue = addressInput;
	}

	public int getAddValue() {
		return addValue;
	}

	public void setAddValue(int addValue) {
		this.addValue = addValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVarUsedNotDef() {
		return varUsedNotDef;
	}

	public void setVarUsedNotDef(String varUsedNotDef) {
		this.varUsedNotDef = varUsedNotDef;
	}

	public boolean isVarUsedNotDefB() {
		return varUsedNotDefB;
	}

	public void setVarUsedNotDefB(boolean varUsedNotDefB) {
		this.varUsedNotDefB = varUsedNotDefB;
	}
	
	
	public boolean isExtAddExceedUseList() {
		return extAddExceedUseList;
	}

	public void setExtAddExceedUseList(boolean extAddExceedUseList) {
		this.extAddExceedUseList = extAddExceedUseList;
	}

	public boolean isTestAbExceed() {
		return testAbExceed;
	}

	public void setTestAbExceed(boolean testAbExceed) {
		this.testAbExceed = testAbExceed;
	}

	public boolean isTestReExceed() {
		return testReExceed;
	}

	public void setTestReExceed(boolean testReExceed) {
		this.testReExceed = testReExceed;
	}



}
