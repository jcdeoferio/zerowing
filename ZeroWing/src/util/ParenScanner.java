package util;

import java.util.NoSuchElementException;

/**
 * Parses input in the form (input1)(input2)(input3)...
 * Everything in between parenthesised inputs is ignored.
 * Currently, parentheses within the inputs must be matched correctly (within the input).
 */
public class ParenScanner {
	private String input;
	private int cur;
	private String nextStr;
	private int nextCur;
	
	public ParenScanner(String input){
		this.input = input;
		this.cur = 0;
		this.nextStr = null;
		this.nextCur = -1;
	}
	
	private void getNext(){
		if(nextStr != null)
			return;
		
		nextCur = cur;
		
		while(nextCur < input.length() && input.charAt(nextCur) != '(')
			nextCur++;
		
		if(nextCur >= input.length()){
			nextStr = null;
			nextCur = -1;
			return;
		}
		
		int endParenDex = Utility.findMatchingParen(input, nextCur);
		
		if(!(endParenDex < 0)){
			nextStr = input.substring(nextCur+1, endParenDex);
			nextCur = endParenDex;
		}
		else{
			nextStr = null;
			nextCur = -1;
		}
	}
	
	public boolean hasNext(){
		if(nextStr == null)
			getNext();
		
		return(nextStr != null);
	}
	
	public String next(){
		if(!hasNext())
			throw new NoSuchElementException();
		
		String next = nextStr;
		
		cur = nextCur;
		
		nextStr = null;
		nextCur = -1;
		
		return(next);
	}
}
