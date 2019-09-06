
public class Variable extends Module{
	
	private String name;
	private int value; 
	private int moduleNum; 
	
	private boolean definedMul; 
	private boolean usedNotDef;
	private boolean defExceedSize;
	private boolean used; 
	private boolean defNotUsed;
	private boolean mulDefChange;
	
	public Variable (){
		
	}
	
	public Variable(String name, int value, int moduleN){
		this.setName(name);
		this.setValue(value); 
		this.setModuleNum(moduleN); 
		
		this.usedNotDef = false; 
		this.setUsed(false);
		//this.mulDefChange =false;
	}

	public boolean CompareTo(Variable v){
		if(v.getName().equals(this.name) && (v.getValue() == this.value)){
			return true; 
		}else{
			return false;
		}	
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getModuleNum() {
		return moduleNum;
	}

	public void setModuleNum(int moduleNum) {
		this.moduleNum = moduleNum;
	}

	public boolean isDefinedMul() {
		return definedMul;
	}

	public void setDefinedMul(boolean defined) {
		this.definedMul = defined;
	}

	public boolean isUsedNotDef() {
		return usedNotDef;
	}

	public void setUsedNotDef(boolean usedNotDef) {
		this.usedNotDef = usedNotDef;
	}

	public boolean isDefExceedSize() {
		return defExceedSize;
	}

	public void setDefExceedSize(boolean defExceedSize) {
		this.defExceedSize = defExceedSize;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	public boolean isDefNotUsed() {
		return defNotUsed;
	}

	public void setDefNotUsed(boolean defNotUsed) {
		this.defNotUsed = defNotUsed;
	}

	public boolean isMulDefChange() {
		return mulDefChange;
	}

	public void setMulDefChange(boolean mulDefChange) {
		this.mulDefChange = mulDefChange;
	}
	
}
