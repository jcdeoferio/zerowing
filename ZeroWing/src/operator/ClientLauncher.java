/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ClientStarter.java
 *
 * Created on Feb 8, 2010, 3:46:32 PM
 */

package operator;

import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import settings.SystemSettings;

import client.Client;

/**
 * 
 * @author jc.deoferio
 */
public class ClientLauncher extends javax.swing.JFrame {
	private static final long serialVersionUID = -7368758110714928467L;

	private void fillFields() {
		fieldArray = new JTextField[] { nodeName, trackerIP, trackerPort,
				dbName, dbIP, dbPort, dbUser };
		// fieldArray = new JTextField[] { name, ip, port, dbName, dbIP, dbPort,
		// dbUser };
	}

	private void loadFromSettings() {
		String[] fieldData = SystemSettings
				.getSystemSettingsArray(previousLaunch);

		loadFields(fieldData);
	}

	private void saveSettings() {
		System.out
				.println("ClientLauncher.saveSettings(): attempting to save this launch.");
		String[] toSave = new String[fieldArray.length];
		for (int i = 0; i < fieldArray.length; i++) {
			toSave[i] = fieldArray[i].getText();
		}
		SystemSettings.insertSettingsAsArray(toSave, previousLaunch);
	}

	private void loadFields(String[] fieldData) {
		if (fieldData == null) {
			return;
		}
		if (fieldArray.length == fieldData.length) {
			for (int i = 0; i < fieldArray.length; i++) {

				fieldArray[i].setText(fieldData[i]);
			}
		}
	}

	/** Creates new form ClientStarter */
	public ClientLauncher() {
		initComponents();
		initComponents2();
		fillFields();
		loadFromSettings();
	}

	private void initComponents2() {
		dbTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "postgresql", "mysql" }));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jSeparator2 = new javax.swing.JSeparator();
		nodeName = new javax.swing.JTextField();
		trackerIP = new javax.swing.JTextField();
		trackerPort = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		dbTypeComboBox = new javax.swing.JComboBox();
		dbIP = new javax.swing.JTextField();
		dbPort = new javax.swing.JTextField();
		jLabel9 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jLabel11 = new javax.swing.JLabel();
		dbName = new javax.swing.JTextField();
		jLabel12 = new javax.swing.JLabel();
		dbUser = new javax.swing.JTextField();
		dbPassword = new javax.swing.JPasswordField();
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18));
		jLabel1.setText("Client Start");

		jLabel2.setText("Name");

		jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
		jLabel3.setText("Tracker");

		jLabel4.setText("IP");

		jLabel5.setText("Port");

		jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
		jLabel6.setText("Database");

		nodeName.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				nodeNameActionPerformed(evt);
			}
		});

		jLabel7.setText("Type");

		dbTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "postgresql", "mysql" }));

		jLabel9.setText("Port");

		jLabel10.setText("IP");

		jLabel8.setText("Name");

		jLabel11.setText("User");

		jLabel12.setText("Password");

		dbUser.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dbUserActionPerformed(evt);
			}
		});

		okButton.setText("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				okButtonActionPerformed(evt);
			}
		});

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabel1)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel2)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												nodeName,
																												javax.swing.GroupLayout.DEFAULT_SIZE,
																												349,
																												Short.MAX_VALUE)))
																		.addContainerGap())
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel3)
																		.addContainerGap(
																				346,
																				Short.MAX_VALUE))
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jSeparator1,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				380,
																				Short.MAX_VALUE)
																		.addContainerGap())
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel4)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				trackerIP,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				205,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabel5)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				trackerPort,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				133,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addContainerGap())
														.addComponent(jLabel6)
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jSeparator2,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				380,
																				Short.MAX_VALUE)
																		.addContainerGap())
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel7)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				dbTypeComboBox,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addContainerGap(
																				306,
																				Short.MAX_VALUE))
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel10)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				dbIP,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				205,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabel9)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				dbPort,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				133,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addContainerGap())
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel8)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				dbName,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				349,
																				Short.MAX_VALUE)
																		.addContainerGap())
														.addGroup(
																javax.swing.GroupLayout.Alignment.TRAILING,
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel11)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				dbUser,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				173,
																				Short.MAX_VALUE)
																		.addGap(
																				6,
																				6,
																				6)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.TRAILING)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addComponent(
																												jLabel12)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												dbPassword,
																												javax.swing.GroupLayout.PREFERRED_SIZE,
																												125,
																												javax.swing.GroupLayout.PREFERRED_SIZE))
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addComponent(
																												okButton)
																										.addPreferredGap(
																												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																										.addComponent(
																												cancelButton)))
																		.addContainerGap()))));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jLabel1)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel2)
														.addComponent(
																nodeName,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jLabel3,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												14,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jSeparator1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												10,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel4)
														.addComponent(jLabel5)
														.addComponent(
																trackerIP,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																trackerPort,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel6)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jSeparator2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												10,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel7)
														.addComponent(
																dbTypeComboBox,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel10)
														.addComponent(jLabel9)
														.addComponent(
																dbIP,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																dbPort,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel8)
														.addComponent(
																dbName,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel11)
														.addComponent(
																dbUser,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(
																dbPassword,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel12))
										.addGap(18, 18, 18)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																cancelButton)
														.addComponent(okButton))
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		pack();
	}

	private void dbUserActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void nodeNameActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		c = Client.getClient(nodeName.getText(), trackerIP.getText(), Integer
				.parseInt(trackerPort.getText()), (String) dbTypeComboBox
				.getSelectedItem(), dbIP.getText(), Integer.parseInt(dbPort
				.getText()), dbName.getText(), dbUser.getText(), new String(
				dbPassword.getPassword()));
		if (c == null) {
			System.out
					.println("Possible SQL Error, see console log for details.");
			JOptionPane.showMessageDialog(this,
					"Cannot Contact Database. Information might be incorrect",
					"Cannot Contact Database", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		c.startClient();
		saveSettings();
		setVisible(false);
		try {
			if (c.db.newSystemTables) {
				int ans = JOptionPane
						.showConfirmDialog(
								this,
								"Do you want ZeroWing to Automatically add the Change Units?",
								"ZeroWing", JOptionPane.YES_NO_OPTION);
				if (ans == JOptionPane.YES_OPTION) {
					c.db.createChangeUnitsPerTable();
				} else {
					String a[] = c.db.getTables();
					if(a.length == 0){
						c.db.createChangeUnitsPerTable();
					}else{
						new NewChangeUnitForm_TableList(c).setVisible(true);
					}
				}
			}
		} catch (SQLException e) {
			System.out.println("Change Units not created");
			e.printStackTrace();
		}
		new ClientMain(c).setVisible(true);
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(1);
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ClientLauncher().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.JButton cancelButton;
	private javax.swing.JTextField dbIP;
	private javax.swing.JTextField dbName;
	private javax.swing.JPasswordField dbPassword;
	private javax.swing.JTextField dbPort;
	private javax.swing.JComboBox dbTypeComboBox;
	private javax.swing.JTextField dbUser;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JTextField nodeName;
	private javax.swing.JButton okButton;
	private javax.swing.JTextField trackerIP;
	private javax.swing.JTextField trackerPort;
	private Client c;
	private JTextField[] fieldArray;
	static String previousLaunch = "clientStartupSettings.txt";
	// End of variables declaration

}
