package util;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SimpleUI extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2036486327297958625L;
	JTextArea jta;
	JTextField jtf;
	JScrollPane jsp;
	JPanel jp;
	
	public SimpleUI(String name){
		super(name);
		jta = new JTextArea();
		jtf = new JTextField();
		
		jsp = new JScrollPane(jta);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		jp = new JPanel();
		
		jtf.addKeyListener( new KeyAdapter(){
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					action();					
				}
			}
			
		});
		
		
		jp.setLayout(new BorderLayout());
		jp.add(jsp,BorderLayout.CENTER);
		jp.add(jtf,BorderLayout.SOUTH);
		
		
		add(jp);
		pack();
		setSize(320,480);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void display(String s){
		jta.append(s);
		
	}
	
	public void action(){
		String actionData = jtf.getText();
		jtf.setText("");
		onAction(actionData);
		
	}
	public void onAction(String actionString){
		
	}
	
}
