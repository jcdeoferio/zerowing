package util;

import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class HuffmanResultTuple {
	HuffEnt root;
	HashMap<String, String> mapping;
	String toData;
	String encoding;
	String toEncode;
	
	//At least 2 characters to encode. :|
	public HuffmanResultTuple(HuffEnt root, String src, String toData, String encoding){
		this.root = root;
		this.toEncode = src;
		this.toData = toData;
		this.encoding = encoding;
		mapping = new HashMap<String, String>();
		if(root.left!=null){
			traverse(root.left, "0");
			traverse(root.right, "1");
		} else {
			mapping.put(root.ch, "0");
		}
	}
	
	public String toString(){
		Iterator<String> iters = mapping.keySet().iterator();
		String s = "";
		while(iters.hasNext()){
			String nx = iters.next();
			s = s + "(" + nx+": "+mapping.get(nx) + ")";
		}
		return s;
	}
	
	private void traverse(HuffEnt root, String codeAsYet){
		if(root.left==null){
			mapping.put(root.ch, codeAsYet);
		} else {
			String lt = codeAsYet + "0";
			String rt = codeAsYet + "1";
			traverse(root.left,lt);
			traverse(root.right,rt);
		}
	}
	public String encode(){
		if(encoding == null){
			encoding = "";
			char[] cr = toEncode.toCharArray();
			for(Character c:cr){
				String st = c.toString();
				String code = mapping.get(st);
				if(code==null){
					JOptionPane.showMessageDialog(null, "Error at HuffmanResultTuple.encode, missing mapping");
				} else{
					encoding = encoding + code;
				}
				
			}
		}
		return encoding;
	}
	public String decode(){
		if(toEncode==null){
			
		}
		return toEncode;
	}
}
