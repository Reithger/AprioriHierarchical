package itemset;
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

	//-- Refer to Item id Equality  ---------------------------
	
	public boolean contains(Itemset o) {
		for(int i : o.getItems()) {
			if(!contains(i)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean contains(int id) {
		if((id < keySet.getMaxItemId() || id % keySet.getMaxItemId() != 0 ? indexOf(id) : indexOfCategorical(id)) == -1) {
			return false;
		}
		return true;
	}
	
	/**
	 * The blending process is not about support, so does not need to use the categorical equivalence
	 * when making new itemsets out of two parent itemsets
	 * 
	 */

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
	
	/**
	 * 
	 * Generates the set of Itemsets that are one size smaller than the current Itemset (basically
	 * the sets you get by removing 
	 * 
	 * @return
	 */

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
	 * Assumes all elements in Itemset o are present in the Itemset calling this function,
	 * also is not about support so does not use categorical equivalency.
	 * 
	 */
	
	public Itemset getRemainder(Itemset o) {
		int[] copy = new int[getSize() - o.getItems().length];
		int posit = 0;
		for(int i = 0; i < items.length; i++) {
			if(o.indexOf(items[i]) == -1)
				copy[posit++] = items[i];
		}
		return new Itemset(copy);
	}
	
	public Itemset getParentOne() {
		int[] out = new int[getSize() - 1];
		for(int i = 0; i < out.length; i++) {
			out[i] = items[i];
		}
		return new Itemset(out);
	}
	
	public Itemset getParentTwo() {
		int[] out = new int[getSize() - 1];
		for(int i = 0; i < out.length - 1; i++) {
			out[i] = items[i];
		}
		out[out.length - 1] = items[items.length - 1];
		return new Itemset(out);
	}
	
	public int countCategories() {
		int out = 0;
		for(int i : items) {
			if(i % keySet.getMaxItemId() == 0) {
				out++;
			}
		}
		return out;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------

	protected int[] getItems() {
		return items;
	}

	public int getSize() {
		return items.length;
	}
	
	private int indexOfCategorical(int key) {
		int cap = (int)(Math.pow(10, (int)Math.log10(key) - 1));
		for(int i = 0; i < items.length; i++) {
			int v = items[i] - key;
			if(v > 0 && v < cap) {
				return i;
			}
		}
		return -1;
	}
	
	protected int indexOf(int key) {
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
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public static void assignKeySet(KeySet in) {
		keySet = in;
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
