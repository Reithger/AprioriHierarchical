
public class Transaction {

//---  Instance Variables   -------------------------------------------------------------------
	
	private int TID;
	private Itemset itemset;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Transaction(int id, String[] in) {
		TID = id;
		itemset = new Itemset(in);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public boolean supported(Itemset in) {
		return itemset.contains(in);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getTID() {
		return TID;
	}
	
	public Itemset getItemset() {
		return itemset;
	}
	
}
