package test.netfree.modules;

public class Edge {
	Node a;
	Node b;
	boolean directed = false;
	public Edge(Node a, Node b){
		this.a = a;
		this.b = b;
	}
	public Edge(Node a, Node b, boolean directed){
		this.a = a;
		this.b = b;
		this.directed = directed;
	}
	
}
