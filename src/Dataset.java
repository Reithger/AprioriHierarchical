import java.util.ArrayList;

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
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public ArrayList<Transaction> getTransactions(){
		return transactions;
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
	
	public int getSize() {
		return transactions.size();
	}
	
}
