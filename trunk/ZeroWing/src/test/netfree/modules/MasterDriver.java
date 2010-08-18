package test.netfree.modules;

/**
 * A <code>MasterDriver</code> instance handles multiple <code>TestControl</code> instances.
 * 
 * @author kevinzana
 *
 */
public class MasterDriver {
	public static void main(String[] args){
		for (int i=0 ;i<1;i++){
			System.out.println("==================== start test "+i);
			TestControl tc;
			tc = TestControl.getTestControl();
			
			System.out.println("==================== end test "+i);
		}
		System.out.println("all tests done");
		
	}
	
	public void displayln(String msg){
		System.out.println("[MasterDriver]"+msg);
	}
}
