package util;

import javax.swing.JOptionPane;

public class HuffEnt implements Comparable<HuffEnt>{
	HuffEnt left;
	HuffEnt right;
	String ch;
	Integer value;
	Integer sumValue;
	String code;
	
	public HuffEnt(String ch, Integer value){
		this.ch = ch;
		this.value = Integer.valueOf(value);
		sumValue = Integer.valueOf(this.value);
	}
	public HuffEnt(String ch){
		this.ch = ch;
		this.sumValue = 0;
	}
	public HuffEnt(HuffEnt left, HuffEnt right){
		this.left = left;
		this.right = right;
		this.sumValue = left.getSumValue()+right.getSumValue();
		this.value = this.sumValue;
		this.ch = left.ch + right.ch;
	}
	
	public int compareTo(HuffEnt he2) {
		int a = this.getSumValue();
		int b = he2.getSumValue();
		if(a==b){
			return ch.compareTo(he2.ch);
		}
		else if(a<b) return -1;
		return 1;
		
	}
	
	public void setCode(String code){
		this.code = code;
	}
	
	public int getSumValue(){
		return Integer.valueOf(sumValue);
	}
	
	public String toData(){
		if(left==null && right == null){
			return "["+ch+"]";
		}
		else if(left!=null && right!=null){
			return "(" + left.toData()+ right.toData()+ ")"; 
		}
		else {
			JOptionPane.showMessageDialog(null, "Something bad seriously happened. locate in HuffEnt.java");
			return null;
		}
		
		
	}
	
	public String toString(){
		return "["+ch+","+value+":"+sumValue+"]";
	}
	
}
