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
		MasterDriver md = new MasterDriver("syncs-4nodes/", 0, 4, 100, 240);
		
		md.runAllTests();
	}
	int start = 0;
	int nodeCount = 4;
	int inserts = 100;
	String namePrefix = "testerr/";
	int repeats = 1;
	public MasterDriver(String namePrefix, int start, int nodeCount, int inserts, int repeats){
		this.namePrefix = namePrefix;
		this.start = start;
		this.nodeCount = nodeCount;
		this.inserts = inserts;
		this.repeats = repeats;
	}
	public void runAllTests(){
		for (int i=0 ;i<repeats;i++){
			System.out.println("==================== start test "+i);
			TestControl tc;
			tc = TestControl.getTestControl(namePrefix, start+i, nodeCount, inserts);
			
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
