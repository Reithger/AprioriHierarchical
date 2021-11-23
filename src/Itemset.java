import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Itemset implements Comparator<Itemset>, Comparable<Itemset>{
	
//---  Constants   ----------------------------------------------------------------------------
	
	private static final int DEFAULT_NON = -1;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static KeySet keySet;
	
	private int[] items;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Itemset(String[] in) {
		items = new int[in.length];
		for(int i = 0; i < in.length; i++) {
			items[i] = getMapping(in[i]);
		}
		Arrays.sort(items);
	}
	
	public Itemset(int[] in) {
		items = in.clone();
		for(int i = 0; i < in.length; i++) {
			items[i] = in[i];
		}
		Arrays.sort(items);
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void assignKeySet(KeySet in) {
		keySet = in;
	}

	public Itemset blend(Itemset o) {
		if(getSize() != o.getSize()) {
			return null;
		}
		int[] out = new int[getSize() + 1];
		int[] otems = o.getItems();
		for(int i = 0; i < items.length - 1; i++) {
			if(items[i] != otems[i]) {
				return null;
			}
			out[i] = items[i];
		}
		int last = items.length - 1;
		if(items[last] < otems[last]) {
			out[last] = items[last];
			out[last + 1] = otems[last];
			return new Itemset(out);
		}
		return null;
	}
	
	public boolean contains(Itemset o) {
		for(int i : o.getItems()) {
			if(indexOf(i) == -1) {
				return false;
			}
		}
		return true;
	}
	
	public boolean contains(int id) {
		if(indexOf(id) == -1) {
			return false;
		}
		return true;
	}

	public ArrayList<Itemset> generateSizeDownSet(){
		ArrayList<Itemset> out = new ArrayList<Itemset>();
		for(int i = 0; i < getSize(); i++) {
			int[] use = new int[getSize() - 1];
			int posit = 0;
			for(int j = 0; j < getSize(); j++) {
				if(i != j) {
					use[posit++] = items[j];
				}
			}
			out.add(new Itemset(use));
		}
		return out;
	}

	public ArrayList<Itemset> generatePowerSet(){
		ArrayList<Itemset> out = new ArrayList<Itemset>();
		for(int i = 0; i < items.length; i++) {
			powSetRecursive(out, intDefault(i+1), 0, 0);
		}
		return out;
	}
	
	private int[] intDefault(int size) {
		int[] out = new int[size];
		for(int i = 0; i < out.length; i++) {
			out[i] = DEFAULT_NON;
		}
		return out;
	}
	
	private void powSetRecursive(ArrayList<Itemset> pow, int[] carry, int indexCarry, int indexItem){
		if(indexCarry >= carry.length || indexItem == items.length) {
			return;
		}
		carry[indexCarry] = items[indexItem];
		if(indexCarry + 1 == carry.length) {
			Itemset e = new Itemset(carry);
			pow.add(e);
		}
		powSetRecursive(pow, carry, indexCarry + 1, indexItem + 1);
		powSetRecursive(pow, carry, indexCarry, indexItem + 1);
	}

	/*
	 * Assumes all elements in Itemset o are present in the Itemset calling this function. 
	 * 
	 */
	
	public Itemset getRemainder(Itemset o) {
		int[] copy = new int[getSize() - o.getItems().length];
		int posit = 0;
		for(int i = 0; i < items.length; i++) {
			if(!o.contains(items[i]))
				copy[posit++] = items[i];
		}
		return new Itemset(copy);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public int[] getItems() {
		return items;
	}

	public int getSize() {
		return items.length;
	}
	
	private int indexOf(int key) {
		for(int i = 0; i < items.length; i++) {
			if(items[i] == key) {
				return i;
			}
		}
		return -1;
	}
	
	private int getMapping(String in) {
		return keySet.get(in);
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return Arrays.toString(items);
	}
	
	public String toStringVerbose() {
		return keySet.convertItemset(this).toString();
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			Itemset i = (Itemset)o;
			return Arrays.compare(getItems(), i.getItems()) == 0;
		}
		catch(Exception e) {
			return false;
		}
	}


	@Override
	public int compare(Itemset o1, Itemset o2) {
		return o1.toString().compareTo(o2.toString());
	}

	@Override
	public int compareTo(Itemset o) {
		return toString().compareTo(o.toString());
	}
	
}
