/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewChangeUnitForm_TableList.java
 *
 * Created on Mar 7, 2010, 2:14:43 PM
 */

package operator;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;

import client.Client;

/**
 *
 * @author jc.deoferio
 */
public class NewChangeUnitForm_TableList extends javax.swing.JFrame {
	private Client c;
    /** Creates new form NewChangeUnitForm_TableList */
    public NewChangeUnitForm_TableList() {
        initComponents();
    }

    public NewChangeUnitForm_TableList(Client c) throws SQLException {
    	this.c = c;
		initComponents();
		initComponents2();
	}

	private void initComponents2() throws SQLException {
		tableList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = c.db.getTables();
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
	}

	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tableList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        addChangeUnitsButton = new javax.swing.JButton();
        doneButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(tableList);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Create Change Units");

        jLabel2.setText("Select a table to add Change Units");

        addChangeUnitsButton.setText("Add Change Units");
        addChangeUnitsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addChangeUnitsButtonActionPerformed(evt);
            }
        });

        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addChangeUnitsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(doneButton))
                    .addComponent(jScrollPane1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addChangeUnitsButton)
                    .addComponent(doneButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void addChangeUnitsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewChangeUnitForm_TableList().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify
    private javax.swing.JButton addChangeUnitsButton;
    private javax.swing.JButton doneButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList tableList;
    // End of variables declaration

}
