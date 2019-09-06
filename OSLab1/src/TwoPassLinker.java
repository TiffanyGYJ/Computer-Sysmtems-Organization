import java.util.*;
import java.io.*;
import java.io.File;
import java.io.File;

public class TwoPassLinker {

	/*
	 * Program reads file name in the command line
	 * Symbol is Variable
	 */
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
	try{
		
		File input = new File(args[0]);
		Scanner scanner = new Scanner(new FileInputStream(input));
		
		//File input = new File("test8.txt");
		//Scanner scanner = new Scanner(input);
		
		int numOfNewVariable;
		int numOfVariableUsed; 
		int numOfAddress; 
		
		ArrayList<Variable> variableArray;
		String[] usage; 
		Address[] addressArray; 
		ArrayList<Variable> varInModule; 
		
		ArrayList<Address> allAddress = new ArrayList<Address>(); 
		ArrayList<Variable> allVarArray = new ArrayList<Variable>();
		ArrayList<Variable> allUsedVarArray = new ArrayList<Variable>();
		
		ArrayList<Variable> inListNotUsed = new ArrayList<Variable>();
		
		int numModule = scanner.nextInt();
		Module[] module = new Module[numModule]; 
		
		/* Create and initialize Modules */
		
		while(scanner.hasNext()){
			
			for(int m = 0; m < module.length; m ++ ){

			numOfNewVariable = scanner.nextInt();
			//System.out.println("sjidjfs");
			variableArray = new ArrayList<Variable>();
			Variable add;
			//Add new Variable & check if is Multiple defined
			
			for(int i = 0; i < numOfNewVariable; i ++){
				add = new Variable (scanner.next(), scanner.nextInt(),m); 
				variableArray.add(add);
				for(Variable a:allVarArray){
					int find = variableArray.indexOf(add);
					if(a.getName().equals(add.getName())){
						a.setDefinedMul(true);
						add.setDefinedMul(true);
						add.setValue(a.getValue());
						variableArray.remove(find);
					}else{
						variableArray.get(find).setDefinedMul(false);
					}
				}
			}
			
			varInModule = new ArrayList<Variable>();
			numOfVariableUsed = scanner.nextInt();
			usage = new String[numOfVariableUsed];
			for(int i = 0; i < usage.length; i++){
				usage[i] = scanner.next();
			}
			
			numOfAddress = scanner.nextInt();
			addressArray = new Address[numOfAddress];
			for(int i = 0; i < addressArray.length; i ++){
				addressArray[i] = new Address(scanner.next(),scanner.nextInt());
			}
			
			module[m] = new Module(variableArray, usage, addressArray, m, varInModule); 
		
			if(m == 0){
				module[m].setRelativePosition(0);
			}else{
				module[m].setRelativePosition(module[m-1].getRelativePosition() 
											+ module[m-1].getAddressArray().length); 
			}
			

			//change the value of variables according to the relative position of the module */
			//check if variable defined exceeds the size of the module (number of addresses in module)	
			module[m].setAdjustedVariable(variableArray);
			allVarArray.addAll(module[m].getAdjustedVariable());
			
		}
			
	}
		
		
		/*
		 * Match usage variable with Variable Value
		 */
		
		for(int m = 0; m < module.length; m ++){		
			usage = module[m].getUsage();
		
			for(int i = 0; i < usage.length; i++){
					String varName = usage[i];
					boolean find = false; 
					for(int k = 0; k < allVarArray.size(); k ++){
						Variable a = allVarArray.get(k);
						if(a.getName().equals(varName)){
							module[m].varUsedModule.add(a); 
							find = true;
							if(!allUsedVarArray.contains(allVarArray.get(k))){
							allUsedVarArray.add(allVarArray.get(k));
							}
							break;
						}
					}
					//Used But Not Defined
					if(find == false){
							Variable temp = new Variable(varName,0,m);
							temp.setUsedNotDef(true);
							module[m].varUsedModule.add(temp);
							allUsedVarArray.add(temp);
							allVarArray.add(temp);
							break;
						}
			}
		
		}		
		
		/*
		 * change Address based on type and print the result
		 */
		
		//iterate through all modules
		
		for(int m = 0; m < module.length; m ++ ){
			
			ArrayList<Variable> varUsedInModule = module[m].getVarUsedModule();
			
			addressArray = module[m].getAddressArray();
			usage = module[m].getUsage();
			//iterate through the address array inside a module
			for(int i = 0; i < module[m].getNumOfAddress(); i++){
				
				if(addressArray[i].getType().equals("R")){	
					int valOfE = addressArray[i].getAddValue() % 1000;
					int firstDigOfE = addressArray[i].getAddValue() / 1000; 
					
					if(valOfE > addressArray.length){
						addressArray[i].setAddValue(firstDigOfE*1000);
						addressArray[i].setTestReExceed(true);
					}
					else{
					addressArray[i].setAddValue(addressArray[i].getAddValue() + module[m].getRelativePosition());
					}
				}
				
				else if(addressArray[i].getType().equals("E")){
									
					int valOfE = addressArray[i].getAddValue() % 1000;
					int firstDigOfE = addressArray[i].getAddValue() / 100; 
					
					//variable used to modify address is in the range
					if(valOfE <= module[m].getNumOfVariableUsed()){
						//variable used but not defined
						if(varUsedInModule.get(valOfE).isUsedNotDef()){
							int newAddVal = varUsedInModule.get(valOfE).getValue() + (firstDigOfE * 100);					
							addressArray[i].setAddValue(newAddVal);
							addressArray[i].setVarUsedNotDefB(true);
							addressArray[i].setVarUsedNotDef(varUsedInModule.get(valOfE).getName());
							
							//variable in use list and used, only update the var array inside module
							//not allVar in main
							varUsedInModule.get(valOfE).setUsed(true);
							
						}
						else{
						int newAddVal = varUsedInModule.get(valOfE).getValue() + (firstDigOfE * 100);					
						addressArray[i].setAddValue(newAddVal);
						varUsedInModule.get(valOfE).setUsed(true);
						}
					}					
					else{
					//If an external address is too large to reference an entry in the use list, 
					//print an error message and treat the address as immediate, No change in E Value
						addressArray[i].setExtAddExceedUseList(true);
					}
				
				}
				else if(addressArray[i].getType().equals("I")){
					
				}
				else if(addressArray[i].getType().equals("A")){
					int valOfE = addressArray[i].getAddValue() % 1000;
					int firstDigOfE = addressArray[i].getAddValue() / 1000; 
					if(valOfE >= 200){
						addressArray[i].setAddValue(firstDigOfE*1000);
						addressArray[i].setTestAbExceed(true); 
					}
					
				}
				allAddress.add(addressArray[i]);
			}
			module[m].setAddressArray(addressArray);
			
			module[m].setVarUsedModule(varUsedInModule);
			
			for(Variable a: module[m].varUsedModule){
				if(!a.isUsed()){
					a.setModuleNum(m);
					inListNotUsed.add(a);
				}
			}
			
			
		}
		
		/*
		 * Print Variable Value, loop through modules, calling Module printVariable function
		 */		
		System.out.println("Symbol Table");
		for(int m = 0; m < module.length ; m ++ ){
			module[m].printVariableInModule();
		}
					
		/*
		 * Print Final Address
		 */
		System.out.println();
		System.out.println("Memory Map");
		for(Address addPrint: allAddress){
			if(allAddress.indexOf(addPrint) < 10){
				if(addPrint.isVarUsedNotDefB()){
					System.out.println(allAddress.indexOf(addPrint) + ":  "+ addPrint.getAddValue()
					+ " Error: "+addPrint.getVarUsedNotDef()+" is not defined; zero used.");
				}
				else if(addPrint.isExtAddExceedUseList()){
					System.out.println(allAddress.indexOf(addPrint) + ":  "+ addPrint.getAddValue()
					+ " Error: External address exceeds length of use list; treated as immediate.");
				}
				else if(addPrint.isTestAbExceed()){
					System.out.println(allAddress.indexOf(addPrint) + ":  "+ addPrint.getAddValue()
					+ " Error: Absolute address exceeds machine size; zero used.");
				}
				else if(addPrint.isTestReExceed()){
					System.out.println(allAddress.indexOf(addPrint) + ":  "+ addPrint.getAddValue()
					+ " Error: Relative address exceeds module size; zero used.");
				}
				else{
					System.out.println(allAddress.indexOf(addPrint) + ":  "+ addPrint.getAddValue());
				}
			}
			else{
				if(addPrint.isVarUsedNotDefB()){
					System.out.println(allAddress.indexOf(addPrint) + ": "+ addPrint.getAddValue()
					+ " Error: "+addPrint.getVarUsedNotDef()+" is not defined; zero used.");
				}
				else if(addPrint.isExtAddExceedUseList()){
					System.out.println(allAddress.indexOf(addPrint) + ": "+ addPrint.getAddValue()
					+ " Error: External address exceeds length of use list; treated as immediate.");
				}
				else if(addPrint.isTestAbExceed()){
					System.out.println(allAddress.indexOf(addPrint) + ": "+ addPrint.getAddValue()
					+ " Error: Absolute address exceeds machine size; zero used.");
				}
				else if(addPrint.isTestReExceed()){
					System.out.println(allAddress.indexOf(addPrint) + ": "+ addPrint.getAddValue()
					+ " Error: Relative address exceeds module size; zero used.");
				}
				else{
					System.out.println(allAddress.indexOf(addPrint) + ": "+ addPrint.getAddValue());
					}
				}
			}

		
		
		
		/*
		 * Error Warnings: Variable Appeared in the use list but was not actually used.
		 */
					
		System.out.println();
		for(Variable a : inListNotUsed){
				System.out.println("Warning: In module "+a.getModuleNum()+" "+a.getName()+
									" appeared in the use list but was not actually used.");
		}
		
		
		
		
		/*
		 * Error Warnings Defined but Not Used
		 */
		System.out.println();
			for(Variable defined: allVarArray){
				for(Variable used: allUsedVarArray){
					if(defined.CompareTo(used)){
						defined.setDefNotUsed(false);
						break;
					}
					defined.setDefNotUsed(true);	
				}		
				if(defined.isDefNotUsed()){
					System.out.println("Warning: "+ defined.getName() + " was defined in module " + 
											defined.getModuleNum() + " but never used");
				}
		}
		
		
		/*
		 * Error Warnings: If an address appearing in a definition exceeds the size of the module,
		 *  print an error message and treat the address as 0 (relative).
		 */
		System.out.println();
		for(int m = 0; m < module.length; m++){
			module[m].printDefExceedsModuleSize(); 
		}
			
	}
	catch (Exception e){
		e.printStackTrace();
		System.out.println("read file problem");
	}
		
	}
	
}
