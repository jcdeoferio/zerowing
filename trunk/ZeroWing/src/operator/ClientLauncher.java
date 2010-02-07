package operator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.*;

import client.*;

import settings.SystemSettings;
import signal.ServerConnection;


//zerodba zerodbb zerodbc
//zerodbs1 zerodbs2

@SuppressWarnings("serial")
public class ClientLauncher extends JFrame implements ClientInterface {
	ServerConnection serverConn;
	JMenuBar menuBar;
	JPanel panel;
	JTextArea updatesList;
	JComboBox peerList;
	Client c;
	JTextField name;
	JTextField ip;
	JTextField port;
	JTextField peerIP;
	JTextField peerPort;
	JTextField dbName;
	JComboBox dbType;
	JTextField dbIP;
	JTextField dbPort;
	JTextField dbUser;
	JPasswordField dbPassword;
	
	JTextField[] fieldArray;
	
	JPanel changeUnitControlPanel;
	JTextField column;
	JTextField table;
	JTextField changeUnit;

	// setDB(String dbType, String ip, int port, String dbName,String dbUser,
	// String dbPassword) {

	JButton startClient;
	JButton addPeer;
	JButton refreshPeerList;
	JButton sync;
	JButton trackerConnect;
	
	JButton setChangeUnit;

	static String previousLaunch = "clientStartupSettings.txt"; 
	
	public ClientLauncher() {
		panel = new JPanel();
		startClient = new JButton("Start Client");

		startClient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					saveSettings();
					startClient();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		name = new JTextField("");
		ip = new JTextField("127.0.0.1");
		port = new JTextField("8888");
		dbName = new JTextField("zerodb");
		
		dbType = new JComboBox(new String[]{"postgresql", "mysql"});
		dbIP = new JTextField("localhost");
		dbPort = new JTextField("5432");
		dbUser = new JTextField("postgres");
		
		menuBar = new JMenuBar();
		menuBar.add(new JMenu("Menu"));
		menuBar.setSize(500, 10);
		
		//fill field array.
		fillFields();

		panel.add(new JLabel("name:"));
		panel.add(name);
		name.requestFocus();
		panel.add(new JLabel("tracker ip:"));
		panel.add(ip);
		panel.add(new JLabel("tracker port:"));
		panel.add(port);
		panel.add(new JLabel("db type:"));
		panel.add(dbType);
		panel.add(new JLabel("db ip:"));
		panel.add(dbIP);
		panel.add(new JLabel("db port:"));
		panel.add(dbPort);
		panel.add(new JLabel("db name:"));
		panel.add(dbName);
		panel.add(new JLabel("db user:"));
		panel.add(dbUser);
		panel.add(new JLabel("db password:"));
		panel.add(dbPassword = new JPasswordField("password"));
		panel.add(startClient);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(panel);
		this.setJMenuBar(menuBar);

		this.setSize(500, 700);
		// this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Main");
		this.setVisible(true);
		
		loadFromSettings();
	}

	/* (non-Javadoc)
	 * @see operator.ClientInterface#startClient()
	 */
	public void startClient() throws SQLException {
		this.setLayout(new BorderLayout());
		this.remove(panel);
		this.add(panel, BorderLayout.NORTH);
		panel.removeAll();

		updatesList = new JTextArea(5,20);
		updatesList.setText("");
		updatesList.setEditable(false);
		JButton updateButton = new JButton("Data");
		final JTextField updateInput = new JTextField();
		JPanel updatesListInput = new JPanel(new BorderLayout());
		JScrollPane jps;

		panel.add(new JLabel("Add peer ip:"));
		panel.add(peerIP = new JTextField());
		panel.add(new JLabel("port:"));
		panel.add(peerPort = new JTextField());
		panel.add(addPeer = new JButton("Add Peer"));
		panel.add(new JLabel("Peer List:"));
		panel.add(peerList = new JComboBox());
		panel.add(refreshPeerList = new JButton("Refresh Peer List"));
		panel.add(sync = new JButton("Sync"));
		panel.add(trackerConnect = new JButton("Connect to Tracker"));

		peerIP.requestFocus();
//		panel.add(updateButton = new JButton("Data"));
		updatesListInput.add(updateButton, BorderLayout.WEST);
		updatesListInput.add(updateInput, BorderLayout.CENTER);
		updateInput.setToolTipText("Format of query must satisfy this format:\n SELECT * FROM [YOUR QUERY]");
		
		changeUnitControlPanel = new JPanel();
		changeUnitControlPanel.setLayout(new BoxLayout(changeUnitControlPanel,BoxLayout.Y_AXIS));
		column = new JTextField();
		table = new JTextField();
		changeUnit = new JTextField();
		
		
		
		setChangeUnit = new JButton("Set New Change Unit");
		
		 changeUnitControlPanel.add(column);
		 changeUnitControlPanel.add(table);
		 changeUnitControlPanel.add(changeUnit);
		
		 column.setToolTipText("Column Name");
		 table.setToolTipText("Table Name");
		 changeUnit.setToolTipText("Change Unit to be Acquired.");
		 
		setChangeUnit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				callSetChangeUnit(column.getText(),
						table.getText(), changeUnit.getText());
			}
		});
		 
		panel.add(changeUnitControlPanel);
		panel.add(setChangeUnit);
		panel.add(updatesListInput);
		panel.add(new JLabel("Updates list"));
		jps = new JScrollPane();
		//jps.setPreferredSize(new Dimension(150,100));
		jps.setSize(new Dimension(150,100));
		jps.setViewportView(updatesList);
		jps.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jps.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.add(jps,BorderLayout.CENTER);


		updateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try {
					updatesList.setText(c.dumpTable(updateInput.getText()));
				} catch (SQLException e) {
					e.printStackTrace();
					updatesList.setText("SQL Exception: "+e.getLocalizedMessage());
				}
			}});
		

		updateInput.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try {
					updatesList.setText(c.dumpTable(updateInput.getText()));
				} catch (SQLException e) {
					e.printStackTrace();
					updatesList.setText("SQL Exception: "+e.getLocalizedMessage());
				}
			}});
		
		peerList.setEditable(false);
		
		trackerConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectToTracker();
			}
		});
		
		sync.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sync();
			}
		});

		addPeer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addPeer();
			}
		});

		refreshPeerList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				refreshPeerList();
			}
		});

		c = Client.getClient(name.getText(), ip.getText(), Integer.parseInt(port
				.getText()), (String)dbType.getSelectedItem(), dbIP.getText(), Integer
				.parseInt(dbPort.getText()), dbName.getText(),
				dbUser.getText(), new String(dbPassword.getPassword()));
		if(c==null){
			System.out.println("Possible SQL Error, see console log for details.");
			setButtonEnableness(panel, false, 0);
		} else{
			c.startClient();
			this.setTitle(c.getDBName()+": Logged in as: "+c.getDBUser());
			refreshPeerList();
		}
		
	}
	private void callSetChangeUnit(String column, String table, String cuid){
		JOptionPane.showMessageDialog(this, "Called Set Change Unit: of col ["+column+"] tab ["+table+"] id: "+cuid);
		if(column==null || table == null || cuid == null)return;
		if(column.equals("") || table.equals("") || cuid.equals("")) return;
		try {
			c.getDatabase().addToChangeUnit(column, table, cuid);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void setButtonEnableness(Component c,boolean enabled, int depth){
//		for(int i=0;i<depth;i++)System.out.print(" ");
		if(c instanceof Container){
			Container b = (Container)c;
			Component[] comps = b.getComponents();
//			System.out.println(comps.length + " " + c);
			
			for(Component cc : comps){
				setButtonEnableness( cc, enabled, depth+1);
				cc.setEnabled(enabled);
			}
		} else {
			c.setEnabled(enabled);
		}
	}

	/* (non-Javadoc)
	 * @see operator.ClientInterface#refreshPeerList()
	 */
	public void refreshPeerList() {
		c.requestPeerList();

		while(peerList.getItemCount()>0)
			peerList.removeItemAt(0);
		
		for (AddressPort ap : c.candidates) {
			peerList.addItem(ap);
		}

		if(peerList.getItemCount()==0)peerList.addItem(new AddressPort(c.getName(),"localhost",c.getClientPeerServerPort()));
	}
	
	public void addPeer(){
		addPeer(peerIP.getText(), Integer.parseInt(peerPort.getText()));
	}

	public void addPeer(String ip, int port ) {
		c.addPeerManual(ip, port);
		refreshPeerList();
	}
	
	public void sync(){
		c.ask();
	}
	
	/* (non-Javadoc)
	 * @see operator.ClientInterface#connectToTracker()
	 */
	public void connectToTracker(){
		c.loginTracker();
	}

	public static void main(String args[]) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unused")
		ClientInterface m = new ClientLauncher();
	}
		
	private void fillFields(){
		fieldArray = new JTextField[]{
				name,
				ip,
				port,
				dbName,
				dbIP,
				dbPort,
				dbUser
			};
	}
	private void loadFromSettings(){
		String[] fieldData = 
			SystemSettings.getSystemSettingsArray(ClientLauncher.previousLaunch);
		
		loadFields(fieldData);
	}
	private void saveSettings(){
		System.out.println("ClientLauncher.saveSettings(): attempting to save this launch.");
		String[] toSave = new String[fieldArray.length];
		for(int i=0; i< fieldArray.length;i++){
			toSave[i] = fieldArray[i].getText();
		}
		SystemSettings.insertSettingsAsArray(toSave, ClientLauncher.previousLaunch);
	}
	
	private void loadFields(String[] fieldData){
		if(fieldData==null){
			return;
		}
		if(fieldArray.length==fieldData.length){
			for(int i=0; i< fieldArray.length;i++){
				
				fieldArray[i].setText(fieldData[i]);
			}
		}
	}
	
}
