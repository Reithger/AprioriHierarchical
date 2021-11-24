package database;

import itemset.Itemset;

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
	
	public boolean supported(int id) {
		return itemset.contains(id);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public int getTID() {
		return TID;
	}
	
	public Itemset getItemset() {
		return itemset;
	}
	
}
