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
		int start = 0;
		for (int i=0 ;i<200;i++){
			System.out.println("==================== start test "+i);
			TestControl tc;
			tc = TestControl.getTestControl(start+i);
			
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
