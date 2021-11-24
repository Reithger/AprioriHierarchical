package itemset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class KeySet {
	
//---  Instance Variables   -------------------------------------------------------------------

	/** Store String, Integer  mapping for runtime, invert at end when generating readable rules*/
	private HashMap<String, Integer> nameCodeMapping;

	private int id;
	
	private Node hierarchy;
	
	private int maxId;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public KeySet(int itemCap) {
		nameCodeMapping = new HashMap<String, Integer>();
		id = 1;
		maxId = 0;
		while(itemCap > 0) {
			maxId++;
			itemCap /= 10;
		}
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void addNode(String node, String newChild) {
		if(hierarchy == null) {
			hierarchy = new Node(node);
		}
		hierarchy.addChild(node, newChild);
	}
	
	public int retrievePrefix(String in) {
		return hierarchy.retrievePrefix(in);
	}
	
	public int getHierarchySize() {
		return hierarchy.getSize();
	}
	
	public void registerItem(String item, String category) {
		int pref = (int)(retrievePrefix(category) * getMaxItemId());
		pref += id++;
		nameCodeMapping.put(item, pref);
	}
	
	public HashMap<Integer, String> invertKeysetMapping(){
		HashMap<Integer, String> out = new HashMap<Integer, String>();
		for(String s : nameCodeMapping.keySet()) {
			out.put(nameCodeMapping.get(s), s);
		}
		return out;
	}
	
	public ArrayList<String> convertItemset(Itemset in){
		ArrayList<String> out = new ArrayList<String>();
		HashMap<Integer, String> map = invertKeysetMapping();
		for(int i : in.getItems()) {
			out.add(map.get(i));
		}
		return out;
	}

//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getMaxItemId() {
		return (int)(Math.pow(10, maxId));
	}
	
	public int[] getItemList() {
		int[] out = new int[nameCodeMapping.values().size() + getHierarchySize()];
		int post = 0;
		for(Integer i : nameCodeMapping.values()) {
			out[post++] = i;
		}
		int depth = hierarchy.getDepth() - 1;
		for(String t : hierarchy.getNames()) {
			int prefix = hierarchy.retrievePrefix(t);
			int adj = 0;
			int copy = prefix;
			while(copy > 0) {
				adj++;
				copy /= 10;
			}
			prefix *= Math.pow(10, depth - adj);
			out[post++] = prefix * getMaxItemId();
			nameCodeMapping.put(t, prefix * getMaxItemId());
		}
		Arrays.sort(out);
		return out;
	}
	
	public int get(String in) {
		if(nameCodeMapping.get(in) == null) {
			nameCodeMapping.put(in, id++);
		}
		return nameCodeMapping.get(in);
	}
	
}
