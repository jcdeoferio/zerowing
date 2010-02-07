package util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Pair<A extends Comparable<A>, B extends Comparable<B>> implements Comparable<Pair<A, B>>{
	public A first;
	public B second;
	
	public Pair(){
		this(null, null);
	}
	
	public Pair(A first, B second){
		this.first = first;
		this.second = second;
	}
	
	public int compareTo(Pair<A, B> other) {
		int diff = this.first.compareTo(other.first);
		
		if(diff != 0)
			return(diff);
		else
			return(this.second.compareTo(other.second));
	}
	
	public static <A extends Comparable<A>, B extends Comparable<B>> Set<A> setOfFirst(Collection<Pair<A, B>> pairList){
		Set<A> set = new HashSet<A>();
		
		Iterator<Pair<A, B>> iter = pairList.iterator();
		while(iter.hasNext()){
			Pair<A, B> p = iter.next();
			
			set.add(p.first);
		}
		
		return(set);
	}
	
	public static <B extends Comparable<B>> Set<B> setOfSecond(Collection<Pair<?, B>> pairList){
		Set<B> set = new HashSet<B>();
		
		Iterator<Pair<?, B>> iter = pairList.iterator();
		while(iter.hasNext()){
			Pair<?, B> p = iter.next();
			
			set.add(p.second);
		}
		
		return(set);
	}
}
