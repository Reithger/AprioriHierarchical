public class Itemset {
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static KeySet keySet;
	
	private int[] items;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Itemset(String[] in) {
		items = new int[in.length];
		for(int i = 0; i < in.length; i++) {
			items[i] = getMapping(in[i]);
		}
	}
	
	public Itemset(int[] in) {
		items = in;
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
	
}
