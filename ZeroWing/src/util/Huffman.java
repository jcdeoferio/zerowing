package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.swing.JOptionPane;

public class Huffman {
	public static void main(String args[]){
//		String s = "aaaabbbccdefghijklmnopqrstuvwxyz1234567890!@#$%^&*()";
//		String s ="abccddeee";
//		String s = "abc";
//		String s = "aaaabbbccd";
		String s = "Some of these models of concurrency are primarily intended to support reasoning and specification, while others can be used through the entire development cycle, including design, implementation, proof, testing and simulation of concurrent systems. The proliferation of different models of concurrency has motivated some researchers to develop ways to unify these different theoretical models. For example, Lee and Sangiovanni-Vincentelli have demonstrated that a so-called \"tagged-signal\" model can be used to provide a common framework for defining the denotational semantics of a variety of different models of concurrency,[4] while Nielsen, Sassone, and Winskel have demonstrated that category theory can be used to provide a similar unified understanding of different models.[5] The Concurrency Representation Theorem in the Actor model provides a fairly general way to represent concurrent systems that are closed in the sense that they do not receive communications from outside. (Other concurrency systems, e.g., process calculi can be modeled in the Actor model using a two-phase commit protocol.[6]) The mathematical denotation denoted by a closed system S is constructed increasingly better approximations from an initial behavior called S using a behavior approximating function progressionS to construct a denotation (meaning ) for S as follows:[7]";
		
		Huffman hm = new Huffman();
		hm.exec(s);
	}
	public Huffman(){
		
	}
	public void exec(String s){

		TreeSet<HuffEnt> ts = manTree(s);
		HuffEnt he = ts.pollFirst();
		String result = "";
		result = he.toData();
//		System.out.println(result);
		HuffEnt he2 = toTree(result);
		System.out.println(he2.toData());
	}
	public HuffmanResultTuple getCoding(String s){
		TreeSet<HuffEnt> ts = manTree(s);
		HuffEnt root = ts.pollFirst();
		
		HuffmanResultTuple hrt = new HuffmanResultTuple(root, s, root.toData(), null);
		return hrt;
	}
	
	
	public TreeSet<HuffEnt> manTree(String toMan){
		HashMap<String, Integer> map;
		map = new HashMap<String, Integer>();
		char[] str = new char[toMan.length()];

		str = toMan.toCharArray();

		//extract frequency data
		for(char c:str){
			String ss = c+"";
			
			Integer i = map.get(ss);
			if(i==null){
				map.put(ss, new Integer(1));
			} else {
				i = new Integer(Integer.valueOf(i)+1);
				map.put(ss, i); // might be unnecessary, but what the hell.
			}
			
			
		}
		TreeSet<HuffEnt> ts = toCollection(map);
		
//		System.out.println("====== "+ts.size());
		if(ts.size()<2){
			return ts;
		}
		
		while(ts.size()!=2){
			HuffEnt he = ts.pollFirst();
			HuffEnt he2 = ts.pollFirst();
			
			HuffEnt he3 = new HuffEnt(he, he2);
//			System.out.println(he+" / "+he2 + " = "+he3 );
			ts.add(he3);
		}
		HuffEnt he = ts.pollFirst();
		HuffEnt he2 = ts.pollFirst();
		
		HuffEnt he3 = new HuffEnt(he, he2);
//		System.out.println(he+" / "+he2 + " = "+he3 );
		ts.add(he3);
		
		return ts;
	}
	
	/*
	 * replaced by HuffEnt.toData
	 */
	@Deprecated
	public String DFSTree(HuffEnt root, int depth, String code, HashMap<Character, String> charToCode, String result){
		if(root==null)return "";
		
//		for(int i=0;i<depth;i++)System.out.print(" ");
//		if(root.left==null)System.out.print("*");
//		System.out.println(root+ " d:"+depth);
		
		root.code = code;
		if(root.ch.length()>1){
			result = result + "(";
		} else {
			result = result + "("+root.ch+" "+root.code+")";
		}
		
//		System.out.println("result: "+result);
		
		String left  = DFSTree(root.left, depth+1, code+"0", charToCode, "");
		String right = DFSTree(root.right, depth+1, code+"1", charToCode, "");
		result = result + left + right;
		if(root.ch.length()>1)result+=")";
//		System.out.println("result!!!:" + result );
		return result;
	}
	
	
	private TreeSet<HuffEnt> toCollection(HashMap<String, Integer> map){
		TreeSet<HuffEnt> coll = new TreeSet<HuffEnt>();
		Iterator<String> iters = map.keySet().iterator();
		while(iters.hasNext()){
			String s = iters.next();
			coll.add(new HuffEnt(s, map.get(s)));
			
//			System.out.println("adding "+s+" "+coll.size() );
		}
//		System.out.println(coll.size());
		return coll;
	}
	
	public HuffEnt toTree(String src){ 
		LinkedList<Character> list = new LinkedList<Character>();
		for(char c:src.toCharArray()){
			list.addLast(c);
		}
		return toTree(list);
	}
	public HuffEnt toTreeDriver(LinkedList<Character> src){
		src.removeFirst();
		return toTree(src);
	}
	
	public HuffEnt toTree(LinkedList<Character> src){
		if(src.size()==0){
			JOptionPane.showMessageDialog(null, "ERROR! Check toTree:HuffEnt in Huffman.java");
			return null;
		}
		char c;
		HuffEnt he;
		c = src.removeFirst();
		if(c=='['){
			String s = "";
			c = src.removeFirst();
			s = s + String.valueOf(c);
			c = src.removeFirst();
			while(c!=']'){
				s = s + String.valueOf(c);
				c = src.removeFirst();
			}
			return new HuffEnt(s);
		}
		if(c=='('){
			HuffEnt left = toTree(src);
			HuffEnt right= toTree(src);
			he = new HuffEnt(left, right);
			return he; 
		}
		if(c==')')return toTree(src);
		JOptionPane.showMessageDialog(null, "ERROR: toTree unrecognized char "+c);
		return null;
	}
	
	
	public void printMap(HashMap<String, Integer> map){
		Iterator<String> iters = map.keySet().iterator();
		
		while(iters.hasNext()){
			String s = iters.next();
			Integer i = map.get(s);
			System.out.println(s+ " " + i);
		}
	}
}

