package util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import mechanic.Database;

public class Utility {
	static final private String encoding = "UTF-8";
	static final boolean huff = false;

	public static String decode(String str) {
		String decodedStr = null;
		
		if(huff)
			decodedStr = huffDecode(str);
		else
			decodedStr = urlDecode(str);
		
		return(decodedStr);
	}
	
	private static String huffDecode(String str){
		if(str.equals(Database.zwNULLEntry)){
			return null;
		} else {
			return CharacterManipulator.deconstructHuffmanMessage(str);
		}		
	}
	
	private static String urlDecode(String str){
		try {
			if(str.equals(Database.zwNULLEntry))
				return(null);
			else
				return (URLDecoder.decode(str, encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return (str);
	}
	
	public static String encode(String str) {
		String encodedStr = null;
		
		if(huff)
			encodedStr = huffEncode(str);
		else
			encodedStr = urlEncode(str);
		
		return(encodedStr);
	}
	
	private static String huffEncode(String str){
		if (str == null) {
			String ret = CharacterManipulator.constructHuffmanMessage(Database.zwNULLEntry);
			return ret;
		} else{
			String ret = CharacterManipulator.constructHuffmanMessage(str);
			return ret;
		}
	}
	
	private static String urlEncode(String str){
		try {
			if(str == null){
				return (URLEncoder.encode(Database.zwNULLEntry, encoding));
			}
			else
				return (URLEncoder.encode(str, encoding));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return (str);	
	}
	
	public static int findMatchingParen(String str, int beginParenDex){
		if(str.charAt(beginParenDex) != '(')
			return(-1);
		
		int parenCnt = 0;
		
		for(int i = beginParenDex+1; i < str.length(); i++){
			char ch = str.charAt(i);
			
			if(ch == '(')
				parenCnt++;
			else if(ch == ')'){
				if(parenCnt == 0)
					return(i);
				else
					parenCnt--;
			}
		}
		
		return(-1);
	}
	
	public static String commaSeparate(List<?> list){
		return(separatorSeparate(list, ", "));
	}
	
	public static String commaSeparate(List<String> list, List<Boolean> quote) {
		return(separatorSeparate(list, ", ", quote));
	}
	
	public static String separatorSeparate(List<?> list, String sep){
		return(separatorSeparate(list, sep, Collections.nCopies(list.size(), false)));
	}
	
	public static String separatorSeparate(List<?> list, String sep, List<Boolean> quote){
		if(list.size() != quote.size())
			throw new IllegalArgumentException("list and quote must have same length");
		
		String str = "";

		Iterator<?> listIter = list.iterator();
		Iterator<Boolean> quoteIter = quote.iterator();
		while(listIter.hasNext()){
			Object obj = listIter.next();
			boolean quoted = quoteIter.next();
			
			if(quoted)
				str += "'" + obj + "'" + sep;
			else
				str += obj + sep;
		}
		
		str = str.substring(0, str.length() - sep.length()); //remove dangling sep
		
		return(str);
	}
	
	public static String jointSeparate(String sep1, String sep2, String[]... strs){
		int length = strs[0].length;
		for(String[] strArr : strs)
			if(strArr.length != length)
				throw new IllegalArgumentException("All String arrays must have same length");
		
		List<String> outerList = new LinkedList<String>();
		
		for(int i = 0; i < length; i++){
			List<String> list = new LinkedList<String>();
			for(String[] strArr : strs)
				list.add(strArr[i]);
			
			outerList.add(separatorSeparate(list, sep1));
		}
		
		return(separatorSeparate(outerList, sep2));
	}
	
	static final String DateFormat = "yyyy/MM/dd HH:mm:ss";
	
	public static String now(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);
		return(sdf.format(cal.getTime()));
	}
	
	public static Date parseDateStr(String str){
		SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);
		
		try {
			
			return(sdf.parse(str));
			
		} catch (ParseException e) {
			
			e.printStackTrace();
			return(null);
			
		}
	}
	
	public static String timestamp(){
		final String timestampFormat = "yyyy-MM-dd-HH-mm-ss";
		return(new SimpleDateFormat(timestampFormat).format(Calendar.getInstance().getTime()));
	}
} 
