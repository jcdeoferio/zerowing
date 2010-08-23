package test.netfree.modules;

public class DataGenerator {
	public static void main(String[] args){
		DataGenerator dg = new DataGenerator(); 
		System.out.println("1\nstudents\nstudentname studentnumber");
		System.out.println("1 0");
		dg.generateData("3", 1000, 3);
	}
	public DataGenerator(){
		
	}
	/**
	 * Generates data for ( String name | int number ) entries. 
	 * @param nodeName
	 * @param basenum
	 * @param reps
	 */
	public void generateData(String nodeName, int basenum, int reps){
		System.out.println(reps);
		int max = basenum + reps;
		for(int i=basenum;i<max;i++){
			System.out.println(nodeName+"-"+i+"\t"+i);
		}
		
	}
}
