
public class Algorithm {
	
//---  Instance Variables   -------------------------------------------------------------------

	private Dataset database;
	private KeySet keySet;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Algorithm(String databaseFilePath) {
		keySet = new KeySet();
		Itemset.assignKeySet(keySet);
		//need to read in massive csv file, generate String[][] from that, and make our database with it
	}
	
}
