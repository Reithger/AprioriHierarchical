import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 
 * TODO
 * Split this up for a base Apriori class and a Hierarchical Apriori class
 * Implement a Hierarchy tree you can customly define
 * Associate items with the Hierarchy
 * Program equivalence of items within Hierarchy
 * Where itemsets fail to be supported, bump their composite items up the hierarchy and test again
 * 
 * @author Borinor
 *
 */

public class Algorithm {
	
//---  Instance Variables   -------------------------------------------------------------------

	private Dataset database;
	private KeySet keySet;
	
	private double support;
	private double confidence;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Algorithm(String databaseFilePath) {
		keySet = new KeySet();
		Itemset.assignKeySet(keySet);
		AssociationRule.assignKeySet(keySet);
		//need to read in massive csv file, generate String[][] from that, and make our database with it
	}
	
	public Algorithm(String[][] input) {
		keySet = new KeySet();
		Itemset.assignKeySet(keySet);
		AssociationRule.assignKeySet(keySet);
		database = new Dataset(input);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public ArrayList<Itemset> candidateGen(ArrayList<Itemset> L){
		Collections.sort(L);
		ArrayList<Itemset> candidate = new ArrayList<Itemset>();
		for(int i = 0; i < L.size(); i++) {
			for(int j = i + 1; j < L.size(); j++) {
				Itemset pot = L.get(i).blend(L.get(j));
				if(pot != null && subsetSupport(L, pot)) {
					candidate.add(pot);
				}
			}
		}
		return candidate;
	}

	public ArrayList<Itemset> apriori(){
		ArrayList<Itemset> out = new ArrayList<Itemset>();
		ArrayList<Itemset> Li = getSizeOneLargeItemsets();
		while(Li.size() > 0) {
			ArrayList<Itemset> Ck = candidateGen(Li);
			HashMap<Itemset, Integer> countSup = database.getSupport(Ck);
			Li.clear();
			for(Itemset i : countSup.keySet()) {
				if(hasSupport(countSup.get(i))){
					Li.add(i);
				}
			}
			out.addAll(Li);
		}
		return out;
	}
	
	public ArrayList<Itemset> aprioriTID(){
		ArrayList<Itemset> out = new ArrayList<Itemset>();
		ArrayList<Itemset> Li = getSizeOneLargeItemsets();
		HashMap<Integer, ArrayList<Itemset>> tidMap = new HashMap<Integer, ArrayList<Itemset>>();
		for(Transaction t : database.getTransactions()) {
			tidMap.put(t.getTID(), new ArrayList<Itemset>());
			for(Itemset i : Li) {
				if(t.supported(i)) {
					tidMap.get(t.getTID()).add(i);
				}
			}
		}
		while(Li.size() != 0) {
			ArrayList<Itemset> Ck = candidateGen(Li);
			HashMap<Itemset, Integer> supMap = new HashMap<Itemset, Integer>();
			
			ArrayList<Integer> keys = new ArrayList<Integer>(tidMap.keySet());
			for(int i : keys) {
				boolean relevant = false;
				ArrayList<Itemset> use = tidMap.get(i);
				tidMap.put(i, new ArrayList<Itemset>());
				for(Itemset it : Ck) {
					if(candidateSupport(use, it)) {
						supMap.put(it, supMap.get(it) == null ? 1 : (supMap.get(it) + 1));
						tidMap.get(i).add(it);
						relevant = true;
					}
				}
				if(!relevant) {
					tidMap.remove(i);
				}
			}
			
			Li.clear();
			for(Itemset it : supMap.keySet()) {
				if(hasSupport(supMap.get(it))) {
					Li.add(it);
				}
			}
			out.addAll(Li);
		}
		
		return out;
	}

	public ArrayList<AssociationRule> deriveRules(ArrayList<Itemset> largeItems){
		/*
		 * For every itemset l, get powerset of l and test rule a \in P(l) => (l - a)
		 * 
		 * sup(l) / sup(a) >= c ==> sup(l) / c >= sup(a)
		 * 
		 */
		ArrayList<AssociationRule> out = new ArrayList<AssociationRule>();
		for(Itemset it : largeItems) {
			int supL = database.getSupport(it);
			ArrayList<Itemset> powSet = it.generatePowerSet();
			for(Itemset pt : powSet) {
				if(pt.getSize() == it.getSize()) {
					continue;
				}
				if((double)supL / (double)database.getSupport(pt) >= confidence) {
					out.add(new AssociationRule(pt, it.getRemainder(pt)));
				}
			}
		}
		return out;
	}

	private ArrayList<Itemset> getSizeOneLargeItemsets(){
		ArrayList<Itemset> out = new ArrayList<Itemset>();
		int[] keyItems = keySet.getItemList();
		for(int i = 0; i < keyItems.length; i++) {
			if(hasSupport(database.getSupport(keyItems[i]))) {
				out.add(new Itemset(new int[] {keyItems[i]}));
			}
		}
		return out;
	}
	
	private boolean subsetSupport(ArrayList<Itemset> L, Itemset pot) {
		for(Itemset l : pot.generateSizeDownSet()) {
			if(!L.contains(l)) {
				return false;
			}
		}
		return true;
	}

	private boolean candidateSupport(ArrayList<Itemset> items, Itemset check) {
		int[] a = new int[check.getSize() - 1];
		int[] b = new int[check.getSize() - 1];
		
		int[] copy = check.getItems();
		
		for(int i = 0; i < copy.length - 1; i++) {
			a[i] = copy[i];
			b[i] = copy[i];
		}
		b[b.length - 1] = copy[copy.length - 1];
		if(items.contains(new Itemset(a)) && items.contains(new Itemset(b))) {
			return true;
		}
		return false;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public Dataset getDatabase() {
		return database;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void assignSupportConfidence(double supp, double conf) {
		support = supp;
		confidence = conf;
	}
	
	private boolean hasSupport(int count) {
		return ((double) count / database.getSize()) >= support;
	}
	
}
