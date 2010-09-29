package test.netfree.modules;

import java.sql.SQLException;

/**
 * A <code>MasterDriver</code> instance handles multiple <code>TestControl</code> instances.
 * 
 * @author kevinzana
 *
 */
public class MasterDriver {
	public static void main(String[] args){
		String baseTest = "randomString";
		
//		MasterDriver md = new MasterDriver("syncs-8nodes/", 181, 8, 100, 60, baseTest);
//		
//		md.runAllTests();
		
		MasterDriver md10 = new MasterDriver("syncs-10nodes/", 34, 10, 100, 240, baseTest);
		
		md10.runAllTests();
		
		
		
	}
	int start = 0;
	int nodeCount = 4;
	int inserts = 100;
	String namePrefix = "testerr/";
	int repeats = 1;
	String randomString;
	public MasterDriver(String namePrefix, int start, int nodeCount, int inserts, int repeats, String randomString){
		this.namePrefix = namePrefix;
		this.start = start;
		this.nodeCount = nodeCount;
		this.inserts = inserts;
		this.repeats = repeats;
		this.randomString = randomString;
	}
	public void runAllTests(){
		for (int i=0 ;i<repeats;i++){
			System.out.println("==================== start test "+i);
			TestControl tc;
			tc = TestControl.getTestControl(namePrefix, start+i, nodeCount, inserts, randomString);
			
			System.out.println("==================== end test "+i);
			
			Node[] nodeSet = tc.nodes;
			for(Node n: nodeSet){
				try {
					n.getConnection().getConnection().close();
				} catch (SQLException e) {
					System.out.println("ERRRO: closing didn't work. or sumth. in MasterDriver");
					e.printStackTrace();
				}
			}
		}
		System.out.println("all tests done");
	}
	
	
	public void displayln(String msg){
		System.out.println("[MasterDriver]"+msg);
	}
}
