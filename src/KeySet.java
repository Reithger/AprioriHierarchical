import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class KeySet {
	
//---  Instance Variables   -------------------------------------------------------------------

	/** Store String, Integer  mapping for runtime, invert at end when generating readable rules*/
	private HashMap<String, Integer> nameCodeMapping;

	private int id;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public KeySet() {
		nameCodeMapping = new HashMap<String, Integer>();
		id = 0;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
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
	
	public int[] getItemList() {
		int[] out = new int[nameCodeMapping.values().size()];
		int post = 0;
		for(Integer i : nameCodeMapping.values()) {
			out[post++] = i;
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
