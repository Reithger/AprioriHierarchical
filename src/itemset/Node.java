package itemset;

import java.util.ArrayList;

public class Node {

	private String name;
	private ArrayList<Node> children;
	
	public Node(String in) {
		name = in;
		children = new ArrayList<Node>();
	}
	
	public void addChild(String in) {
		children.add(new Node(in));
	}
	
	public void addChild(String node, String newChild) {
		findNode(node).addChild(newChild);
	}
	
	public int getSize() {
		int count = 1;
		for(Node n : children) {
			count += n.getSize();
		}
		return count;
	}
	
	public int getDepth() {
		int deepest = 0;
		for(Node n : children) {
			int cont = n.getDepth();
			if(cont > deepest) {
				deepest = cont;
			}
		}
		return 1 + deepest;
	}
	
	public ArrayList<String> getNames(){
		ArrayList<String> names = new ArrayList<String>();
		names.add(name);
		for(Node n : children) {
			names.addAll(n.getNames());
		}
		return names;
	}
	
	public Node findNode(String in) {
		if(matches(in)) {
			return this;
		}
		for(Node n : children) {
			if(n.matches(in)) {
				return n;
			}
		}
		for(Node n : children) {
			Node res = n.findNode(in);
			if(res != null) {
				return res;
			}
		}
		return null;
	}
	
	public boolean matches(String test) {
		return name.equals(test);
	}
	
	public int retrievePrefix(String test) {
		if(matches(test)) {
			return 1;
		}
		for(int i = 0; i < children.size(); i++) {
			if(children.get(i).matches(test)) {
				return i + 1;
			}
			int result = children.get(i).retrievePrefix(test);
			if(result != -1) {
				return (i + 1) * ((result / 10) + 1) * 10 + result;
			}
		}
		return -1;
	}
	
}
