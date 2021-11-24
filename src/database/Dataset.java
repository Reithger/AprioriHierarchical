package database;
import java.util.ArrayList;
import java.util.HashMap;

import itemset.Itemset;

public class Dataset {

//---  Instance Variables   -------------------------------------------------------------------
	
	private ArrayList<Transaction> transactions;
	private int tid;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Dataset(String[][] in) {
		transactions = new ArrayList<Transaction>();
		for(String[] i : in) {
			addTransaction(i);
		}
	}
	
	public Dataset() {
		transactions = new ArrayList<Transaction>();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void addTransaction(String[] in) {
		transactions.add(new Transaction(tid++, in));
	}
	
	public void removeTransaction(int tid) {
		for(int i = 0; i < transactions.size(); i++) {
			if(transactions.get(i).getTID() == tid) {
				transactions.remove(i);
				break;
			}
		}
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<Transaction> getTransactions(){
		return transactions;
	}
	
	public HashMap<Itemset, Integer> getSupport(ArrayList<Itemset> Ck){
		HashMap<Itemset, Integer> out = new HashMap<Itemset, Integer>();
		for(Transaction t : transactions) {
			for(Itemset c : Ck) {
				if(t.supported(c)) {
					if(out.get(c) == null) {
						out.put(c, 0);
					}
					out.put(c, out.get(c) + 1);
				}
			}
		}
		return out;
	}
	
	public int getSupport(Itemset i) {
		int out = 0;
		for(Transaction t : transactions) {
			if(t.supported(i)) {
				out++;
			}
		}
		return out;
	}
	
	public int getSupport(int id) {
		int out = 0;
		for(Transaction t : transactions) {
			if(t.supported(id)) {
				out++;
			}
		}
		return out;
	}
	
	public int getSize() {
		return transactions.size();
	}
	
}
