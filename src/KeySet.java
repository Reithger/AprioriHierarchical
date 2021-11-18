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

//---  Getter Methods   -----------------------------------------------------------------------
	
	public int get(String in) {
		if(nameCodeMapping.get(in) == null) {
			nameCodeMapping.put(in, id++);
		}
		return nameCodeMapping.get(in);
	}
	
}
