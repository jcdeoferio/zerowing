package test.netfree.modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JList;



import util.DBUtility;
import util.Utility;

import mechanic.DBConnection;

/**
 * Each instance of <code>TestControl</code> manages a set of <code>Nodes</code>.
 * Specifically, <code>TestControl</code> calls the methods of each individual <code>Node</code> to
 * simulate real-world operation and interaction of <code>Nodes</code>.
 * <p>
 * Each <code>TestControl</code> instance must have a unique name. Specifically, this name must be unique
 * within the set of <code>TestControl</code> tests being run on a given database. Should <code>TestControl</code>
 * take up a minimal amount of resources, multiple tests can be safely run in parallel.
 * <p>
 * <code>TestControl</code> is responsible for setting up the graph architecture of the <code>Nodes</code>.
 * <p>
 * Threads may be used to reduce testing time. However, multiple threads using the same <code>Node</code>
 * are not allowed in order to maintain the integrity of the resulting data. 
 * In such cases, <code>TestControl</code> will wait for the thread 
 * using the similar <code>Node</code> to finish. 
 * 
 * @author kevinzana
 *
 */
public class TestControl {
	Node[] nodes;
	Edge[] edges;
	int syncs;
	
	public static TestControl getTestControl(){
		return new TestControl();
	}
	private TestControl(){
		defaultTest();
		
	}
	/**
	 * Default test setup. Uses 2 nodes, but your mileage may vary.
	 */
	private void defaultTest(){
		nodes = new Node[3];
		edges = new Edge[1];
		syncs = 5;
		try{
			for(int i=0;i<nodes.length;i++){
				Node a = Node.getCleanNode(""+i, "127.0.0.1", 3306, 
						"mysql",
						"test"+i+"_dev", 
						"root", "password");
				nodes[i] = a;
				displayln("[defaultTest]: NODE CREATION: "+a.toString());
				clearDB(a);
				schematizeDBFromFile(a, "test");
				startDB(a);
				populateDBFromFile(a, "test");
				displayln("[defaultTest]: NODE CREATION Done. ->"+a.toString());
			}
		} catch (SQLException sqle){
			displayError("[defaultTest] sqlexception in making test node.");
			System.out.println(sqle.getLocalizedMessage());
		} catch (IOException e) {
			displayError("[defaultTest] IOException");
			e.printStackTrace();
		}
		runRandomTest(syncs);
		
	}
	
	/**
	 * <code>runTest</code> runs a test using the <code>edges</code> variable.
	 */
	public void runTest(){
		
	}
	/**
	 * By RandomTest, this test means random edges.
	 */
	public void runRandomTest(int syncs){
		if(nodes.length<2){
			displayError("[runRandomTest]: Running a sync test on one or fewer nodes would result in" +
					" no data, because it would only sync with itself.");
			return;
		} else if(syncs < 1){
			displayError("[runRandomTest]: Running a sync test with "+syncs+ " syncs would be weird. Seriously.");
			return;
		}
		displayln("[runRandomTest]: Running syncs test on "+nodes.length+" nodes.");
		displayln("[runRandomTest]: Testing nodes with "+syncs+" syncs.");
		int len = nodes.length;
		int[] arr = generateNodeSequence(len);
		arr = rearrangeNodeSequence(arr);
		int marker = 0;
		
		Node getter;
		Node giver;
		ZeroWingTestModem testModem = new ZeroWingTestModem();
		for(int run = 0; run < syncs; run++){
			try {
				
				displayln("[runRandomTest][run]:Test run "+run+".");
				// ======= GET INDEX OF CURRENT NODES BEING SYNCED
				int marker2 = marker++;
				if(marker>=len)marker = marker%len;
				if(marker2>=len)marker2 = marker2%len;
				// ======= marker and marker2 are the indexes of arr to get node.
				getter = nodes[marker];
				giver = nodes[marker2];
				
				// ======= BRUNT OF DRIVER:
				String giverName = giver.getPeerName();
				List<String> updates = testModem.getUpdateList(getter,giver);
				for(int i=0;i<updates.size();i++){
					String updateString = updates.get(i);
					getter.db.insertUpdate(updateString, giverName);
//					displayln("[runRandomTest][run][data]:        "+updates.get(i));
				}
				// ======= DISPLAYERS
				displayln("[runRandomTest][run]:    Using request string >> "+testModem.constructUpdateRequest(getter.db));
				displayln("[runRandomTest][run]:    "+updates.size()+" updates syncing: "+getter.getPeerName()+
						" and " + giver.getPeerName());
			} catch (SQLException e) {
				displayError("[runRandomTest][run "+run+"] terminated due to sql error "+e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}
	/**
	 * receives schema
	 * @param a
	 * @param data
	 */
	public void schematizeDB(Node a, String[] data){
		displayln("[schematizeDB]"+a);
		try {
			for(int i=0;i<data.length;i++){
				displayln("    [sDB]"+data[i]);
				DBConnection dbc = a.getConnection();
				dbc.execute(data[i]);
			}
		} catch (SQLException e) {
			displayError("    [sDB][ERROR]: error executing command.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Call's the <code>startDB()</code> function of the given <code>Node</code>.
	 * <code>startDB()</code> starts the initial Zero Wing code on databases. On new databases,
	 * this places triggers. On old databases, this checks the changelog for new information.
	 * @param a
	 * @throws SQLException
	 */
	public void startDB(Node a) throws SQLException{
		a.startDB();
	}
	
	/**
	 * Populate the given <code>Node</code>'s database using the given commands.
	 * @param a
	 * @param data
	 * @throws SQLException 
	 */
	public void populateDB(Node a, LinkedList<String> data) throws SQLException{
		DBConnection dbc = a.getConnection();
//		
		if(data.size() < 1 ){
			displayError("[populateDB]: loaded file for "+a.getPeerName()+" contained corrupt info.");
			return;
		}
		DBConnection dbConn = a.getConnection();
		while(data.size()>0){
			int tables = Integer.parseInt(data.removeFirst());
			displayln("[populateDB]: tables: "+tables);
			for (int i=0;i<tables;i++){
				String tableName = data.removeFirst();
				displayln("[populateDB]: table["+i+"]: "+tableName);
				String[] columnsArray = data.removeFirst().split(" ");
				String[] typeArray = data.removeFirst().split(" ");
				boolean[] types = new boolean[typeArray.length];
				LinkedList<String> columns = new LinkedList<String>();
				// Grab columns.
				for(int j=0;j<columnsArray.length;j++)
					columns.addLast(columnsArray[j]);
				for(int j=0;j<typeArray.length;j++)
					types[j] = typeArray[j].equals("1");
				
				PreparedStatement ps = 
					prepareKeylessInsertStatement(tableName, columns, dbConn);
				
				int tableElems = Integer.parseInt(data.removeFirst()); 
				
				for (int elem = 0; elem < tableElems;elem++){
					String[] entryData = data.removeFirst().split("	");
					for(int entry =0;entry<entryData.length;entry++){
						String attrib = columnsArray[entry];
						if(types[entry]){
							ps.setString(entry+1, entryData[entry]);
						} else {
							ps.setObject(entry+1, entryData[entry]);
						}
					}
//					displayln("        result: "+ps.toString());
					ps.execute();
				}	
			}
		}
	}
	/**
	 * Calls <code>grabStringListFile</code> and then calls <code>populateDB</code> with the
	 * values returned.
	 * 
	 * @param a
	 * @param fileName
	 * @throws IOException
	 * @throws SQLException
	 */
	public void populateDBFromFile(Node a, String fileName) throws IOException, SQLException{
		LinkedList<String> data = grabStringListFile(
				"src/test/netfree/modules/"+fileName+"_"+a.getPeerName()+"_data.sql");
		populateDB(a, data);
	}
	
	/**
	 * Clears all tables and views.<p>Throws an SQLException for <code>DBConnection</code>
	 * and <code>DBUtility</code> bottom-level code.
	 * @param a
	 * @throws SQLException
	 */
	public void clearDB(Node a) throws SQLException{
		try {
			DBConnection dbc = a.getConnection();
			DBUtility dbu = new DBUtility(dbc);
			ResultSet results;
			
			displayln("[clearDB] clearing database("+a.dbName+") for "+a.peerName);
			results = dbu.getTables();
			int removed = 0;
			while(results.next()){
				String tableName = results.getString(3);
//				displayln("[clearDB-tables] "+tableName);
				dbc.execute("DROP TABLE "+tableName);
				removed++;
			}
			displayln("[clearDB]"+removed+" tables removed.");
			results = dbu.getViews();
			removed = 0;
			while(results.next()){
				String viewName = results.getString(3);
//				displayln("[clearDB-views] "+viewName);
				dbc.execute("DROP VIEW "+viewName);
				removed++;
			}
			displayln("[clearDB]"+removed+" views removed.");
		} catch (SQLException e) {
			displayError("[clearDB-error]"+a.peerName+" sql exception during clearDB.");
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Helper function for <code>schematizeDB</code>.<p>Allows single
	 * String containing multiple newlines and multiple commands into a
	 * String array.
	 * 
	 * @param a
	 * @param data
	 */
	public void schematizeDB(Node a, String data){
		String[] commands = convertToDBCommandSet(data, ";");
		for(int i=0;i<commands.length;i++){
			commands[i] = commands[i]+";";
		}
		schematizeDB(a, commands);
	}
	/**
	 * Helper function for simplicity of <code>schematizeDB</code> calls. 
	 * Directly calls <code>schematizeDB</code> once
	 * data is extracted from parameter <code>fileName</code>. Uses <code>grabStringFromFile</code>,
	 * which handles files, which throws <code>IOException</code>.<p>
	 * Automatically uses the directory <code>src/test/netfree/modules/FILENAME_schema.sql</code>.
	 * @param a
	 * @param fileName
	 * @throws IOException
	 */
	public void schematizeDBFromFile(Node a,String fileName) throws IOException{
		String data = grabStringFromFile("src/test/netfree/modules/"+fileName+"_schema.sql");
		schematizeDB(a, data);
	}
	/**
	 * Helper function for any code that uses database command strings.
	 * Replaces all newlines in strings to spaces, splits the string on semicolons.
	 * Useful in cases wherein multiple commands are in a single string, and the
	 * string contains newlines.
	 * @param data
	 * @return
	 */
	public String[] convertToDBCommandSet(String data, String regex){
		data.replaceAll("\n", " ");
		String[] newData= data.split(regex);
		return newData;
	}
	/**
	 * Gets a single multi-line String from the given <code>fileName</code>.
	 * Extension must be provided manually as part of the given <code>fileName</code>.
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String grabStringFromFile(String fileName) throws IOException{
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File(fileName)));
			String all = "";
			String s = br.readLine();
			displayln("[grabStringFromFile][read file]-start("+fileName+")");
			while(s!=null){
//				displayln("    [grabStringFromFile][read file]"+s);
				all = all+s;
				s = br.readLine();
			}
			br.close();
			displayln("[grabStringFromFile][data]"+all);
			return all;
			
		} catch (FileNotFoundException e) {
			displayError("[grabStringFromFile]file "+fileName+" not found.");
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			displayError("[grabStringFromFile]IOException on "+fileName+".");
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * Grabs all the lines from the given file <code>fileName</code>. Each line
	 * (defined by newlines) is put into a different index on the array, in the same
	 * order as in the file.
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public String[] grabStringArrayFromFile(String fileName) throws IOException{
		LinkedList<String> linkedData = grabStringListFile(fileName);
		String[] data = new String[linkedData.size()];
		for(int i=0;i<data.length;i++){
			data[i] = linkedData.removeFirst();
		}
		return data;
		
	}
	/**
	 * Grabs a <code>LinkedList</code> of type <code>String</code> where each element
	 * is a newline-split of the whole file.
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public LinkedList<String> grabStringListFile(String fileName) throws IOException{
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File(fileName)));
			LinkedList<String> linkedData = new LinkedList<String>();
			
			displayln("[grabStringFromFile][read file]-start("+fileName+")");
			String s = br.readLine();
			while(s!=null){
//				displayln("    [grabStringFromFile][read file]"+s);
				linkedData.add(s);
				s = br.readLine();
			}
			br.close();
			return linkedData;
			
		} catch (FileNotFoundException e) {
			displayError("[grabStringFromFile]file "+fileName+" not found.");
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			displayError("[grabStringFromFile]IOException on "+fileName+".");
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * Copy of Keyless <code>prepareInsertStatement</code> as defined by @JC's <code>DBUtility</code>. 
	 * @param tablename
	 * @param columns
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement prepareKeylessInsertStatement(String tablename, List<String> columns, DBConnection dbConn) throws SQLException{
		//insertQuery = INSERT INTO tablename (column1, column2, ..., key) VALUES (?, ?, ..., ?)
		String insertQuery = "INSERT INTO "+tablename+" ("+Utility.commaSeparate(columns)+") VALUES ("+nQuestionMarks(columns.size())+")";
		
		PreparedStatement insertPS = dbConn.getConnection().prepareStatement(insertQuery);
		
		return(insertPS);
	}
	/**
	 * Copy of Keyless <code>nQuestionMarks</code> as defined by @JC's <code>DBUtility</code>. 
	 * @param tablename
	 * @param columns
	 * @param key
	 * @return
	 * @throws SQLException
	 */
	public String nQuestionMarks(int n){
		String str = "";
		
		for(int i = 0; i < n; i++)
			str += "?, ";
		
		return(str.substring(0, str.length()-2)); //2 length of dangling ", "
	}
	/**
	 * Generate an array with elements containing <code>0..len</code>.
	 * @param len
	 * @return
	 */
	public int[] generateNodeSequence(int len){
		int[] array = new int[len];
		for(int i=0;i<len;i++){
			array[i] = i;
		}
		return array;
	}
	/**
	 * Rearranges the given node sequence, no side effects.
	 * @param orig
	 * @return
	 */
	public int[] rearrangeNodeSequence(int[] orig){
		int[] newArray = new int[orig.length];
		Random r = new Random();
		for(int i=0;i<newArray.length;i++){
			newArray[i] = orig[i];
		}
		int next, prev;
		int max = newArray.length;
		for(int i=0;i<newArray.length;i++){
			next = r.nextInt(max);
			prev = newArray[i];
			newArray[i] = newArray[next];
			newArray[next] = prev;
		}
//		//CHECK
//		int sum = 0;
//		for(int i=0;i<newArray.length;i++)sum += newArray[i];
//		if (sum != (max*(max-1))/2) System.out.println("NOT OK!");
		return newArray;
	}
	
	public void displayln(String msg){
		System.out.println("[TestControl]"+msg);
	}
	public void displayError(String errorMsg){
		displayln("[Error]"+errorMsg);
	}
}




























