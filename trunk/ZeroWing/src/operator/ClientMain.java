/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ClientMain.java
 *
 * Created on Feb 8, 2010, 3:59:47 PM
 */

package operator;

import java.sql.Savepoint;

import client.AddressPort;
import client.Client;

/**
 * 
 * @author jc.deoferio
 */
public class ClientMain extends javax.swing.JFrame {

	/** Creates new form ClientMain */
	public ClientMain() {
		initComponents();
		initComponents2();
	}

	public ClientMain(Client c) {
		this.c = c;
		initComponents();
		initComponents2();
	}

	private void initComponents2() {
		updatePeerListActionPerformed(null);
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
		jSeparator1 = new javax.swing.JSeparator();
		reconnectButton = new javax.swing.JButton();
		jLabel3 = new javax.swing.JLabel();
		jSeparator2 = new javax.swing.JSeparator();
		updatePeerList = new javax.swing.JButton();
		peerListComboBox = new javax.swing.JComboBox();
		syncWithPeerButton = new javax.swing.JButton();
		closeClient = new javax.swing.JButton();
		newPeerIP = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		newPeerPort = new javax.swing.JTextField();
		addPeerButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18));
		jLabel1.setText("Client Main Window");

		jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel2.setText("Tracker");

		reconnectButton.setText("Reconnect");
		reconnectButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				reconnectButtonActionPerformed(evt);
			}
		});

		jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel3.setText("Peers");

		updatePeerList.setText("Update List");
		updatePeerList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				updatePeerListActionPerformed(evt);
			}
		});

		peerListComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

		syncWithPeerButton.setText("Sync");
		syncWithPeerButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						syncWithPeerButtonActionPerformed(evt);
					}
				});

		closeClient.setText("Close");
		closeClient.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeClientActionPerformed(evt);
			}
		});

		newPeerIP.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newPeerIPActionPerformed(evt);
			}
		});

		jLabel4.setText("IP");

		jLabel5.setText("Port");

		addPeerButton.setText("Add Peer");
		addPeerButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addPeerButtonActionPerformed(evt);
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
														.addComponent(
																jSeparator1,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																380,
																Short.MAX_VALUE)
														.addComponent(jLabel1)
														.addComponent(jLabel2)
														.addComponent(
																reconnectButton)
														.addComponent(jLabel3)
														.addComponent(
																jSeparator2,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																380,
																Short.MAX_VALUE)
														.addComponent(
																peerListComboBox,
																0, 380,
																Short.MAX_VALUE)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				syncWithPeerButton)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				updatePeerList))
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addComponent(
																				jLabel4)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				newPeerIP,
																				javax.swing.GroupLayout.PREFERRED_SIZE,
																				204,
																				javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				jLabel5)
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				newPeerPort,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				134,
																				Short.MAX_VALUE))
														.addComponent(
																closeClient,
																javax.swing.GroupLayout.Alignment.TRAILING)
														.addComponent(
																addPeerButton))
										.addContainerGap()));
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
										.addComponent(jLabel2)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jSeparator1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												10,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(reconnectButton)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel3)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jSeparator2,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												10,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												peerListComboBox,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(
																syncWithPeerButton)
														.addComponent(
																updatePeerList))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel4)
														.addComponent(
																newPeerIP,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(jLabel5)
														.addComponent(
																newPeerPort,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(addPeerButton).addGap(44,
												44, 44).addComponent(
												closeClient).addContainerGap()));

		pack();
	}

	private void reconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {
		c.loginTracker();
	}

	private void closeClientActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
		System.exit(1);
	}

	private void updatePeerListActionPerformed(java.awt.event.ActionEvent evt) {
		c.requestPeerList();

		peerListComboBox.removeAllItems();

		for (AddressPort ap : c.candidates) {
			peerListComboBox.addItem(ap);
		}

		if (peerListComboBox.getItemCount() == 0)
			peerListComboBox.addItem(new AddressPort(c.getName(), "localhost",
					c.getClientPeerServerPort()));
	}

	private void syncWithPeerButtonActionPerformed(
			java.awt.event.ActionEvent evt) {
		c.ask();
	}

	private void addPeerButtonActionPerformed(java.awt.event.ActionEvent evt) {
		c.addPeerManual(newPeerIP.getText(), Integer.parseInt(newPeerPort
				.getText()));
		updatePeerListActionPerformed(null);
	}

	private void newPeerIPActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ClientMain().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.JButton addPeerButton;
	private javax.swing.JButton closeClient;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JTextField newPeerIP;
	private javax.swing.JTextField newPeerPort;
	private javax.swing.JComboBox peerListComboBox;
	private javax.swing.JButton reconnectButton;
	private javax.swing.JButton syncWithPeerButton;
	private javax.swing.JButton updatePeerList;
	private Client c;
	// End of variables declaration

}