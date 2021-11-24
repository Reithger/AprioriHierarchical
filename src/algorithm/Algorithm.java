package algorithm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import database.Dataset;
import database.Transaction;
import itemset.Itemset;
import itemset.KeySet;

/**
 * 
 * TODO
 * Split this up for a base Apriori class and a Hierarchical Apriori class
 * Implement a Hierarchy tree you can customly define
 * Associate items with the Hierarchy
 * Program equivalence of items within Hierarchy
 * Where itemsets fail to be supported, bump their composite items up the hierarchy and test again
 * 
 * 
 * Problems Posed:
 *  - What is the intent of applying a hierarchy of items? Itemsets with insufficient support can find
 *  support by having some of their items made more generic; less specific rules, but they may catch
 *  interesting associations that would otherwise be missed due to many Transactions having similar but
 *  technically different items.
 *    - This implies the need for even one-item itemsets that aren't large to be made generic so as not
 *    to lose their relevancy as they merge to make candidate sets in the event that a category is supported.
 *    - However, we still want specificity, and very general rules when nearly identical but more specific
 *    ones exist could be seen as irrelevant. Suppose an itemset Cheddar, x, y, z was supported, but Parmesan, x, y, z
 *    and Brie, x, y, z were not. Do we want a Cheese, x, y, z rule when the Cheddar one already exists?
 *    
 *    
 *  - How do we decide which item in an itemset to make generic when the itemset is not supported? This
 *  speaks to the intent of this extension on Apriori; are we trying to generate all possible itemsets
 *  with a key set that is expanded by the hierarchical terms, or simply make non-supported rules more
 *  general so that only relevant entries are considered in the first place?
 *    - Inclusion of generics in our one-item itemsets implies that categorical generics should be
 *    considered their own items. These may be very trivial, however, and give us non-interesting rules.
 * 	  - But this still works with our idea of generating new rules, and the relevant itemsets are those
 *    that have been combined with other items, so there is still a reducing factor and the relative size
 *    of the hierarchy items to the Keyset items is very small; we may end up with very general rules at
 *    the end, but they can still be of interest or easily ignored.
 *    
 *  - Ongoing conclusions: consider the hierarchy components as their own items and include them in the initial
 *  generation of large 1-item itemsets. When a rule fails, ascend the least supported item that has a parent,
 *  test it again.
 *    - Acknowledge that we don't want
 *    - Wait do we even need to ascend unsupported items if the hierarchy items are in our initial 1-item large itemsets?
 *    
 *  - Even more ongoing conclusions: Treat categories as their own items and include them from the start, should capture
 *  all cases where we would have 'ascended' an item in an itemset and avoid the duplication issue.
 *   - So all we need to do then is integrate item id equivalency within its categories, add hierarchy/category implementation
 *   into Keyset, and add all hierarchy/category terms as individual items whose ids are their prefixes for other items.
 * 
 * 
 * 
 * 
 * Order of Operations:
 *  - Set up Hierarchy if applicable (fully and completely)
 *  - Add relevant items as they appear in the Hierarchy
 *  - Submit your database of transactions
 *  - Run Apriori
 * 
 * 
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
	
	private double categoryFriction;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public Algorithm(int maxId) {
		keySet = new KeySet(maxId);
		Itemset.assignKeySet(keySet);
		AssociationRule.assignKeySet(keySet);
		database = new Dataset();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	public void populateDatabase(String filePath) {
		
	}
	
	public void populateDatabase(String[][] input) {
		database = new Dataset(input);
	}
	
	public ArrayList<Itemset> candidateGen(ArrayList<Itemset> L){
		//System.out.println("Large Itemsets: \n" + L);
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
		//System.out.println("Candidates: \n" + candidate);
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
				if(hasSupport(countSup.get(i), i.countCategories())){
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
				if(hasSupport(supMap.get(it), it.countCategories())) {
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
					AssociationRule ar = new AssociationRule(pt, it.getRemainder(pt));
					if(!out.contains(ar)) {
						out.add(ar);
					}
				}
			}
		}
		return out;
	}

	private ArrayList<Itemset> getSizeOneLargeItemsets(){
		ArrayList<Itemset> out = new ArrayList<Itemset>();
		int[] keyItems = keySet.getItemList();
		for(int i = 0; i < keyItems.length; i++) {
			if(hasSupport(database.getSupport(keyItems[i]), 0)) {
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
		if(items.contains(check.getParentOne()) && items.contains(check.getParentTwo())) {
			return true;
		}
		return false;
	}
	
//-- Hierarchy Managing  --------------------------------------
	
	public void addNode(String node, String newChild) {
		keySet.addNode(node, newChild);
	}
	
	public int retrievePrefix(String ref) {
		return keySet.retrievePrefix(ref);
	}
	
	public void registerItemAsCategory(String item, String category) {
		keySet.registerItem(item, category);
	}
	
	public void registerItemsAsCategory(String[] items, String category) {
		for(String t : items) {
			registerItemAsCategory(t, category);
		}
	}
	
	public int getItemId(String item) {
		return keySet.get(item);
	}
	
	public int getHierarchySize() {
		return keySet.getHierarchySize();
	}
	
//-- Database Managing  ---------------------------------------
	
	public void addTransaction(String[] in) {
		database.addTransaction(in);
	}
	
	public void removeTransaction(int id) {
		database.removeTransaction(id);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public Dataset getDatabase() {
		return database;
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void assignSupportConfidence(double supp, double conf, double frict) {
		support = supp;
		confidence = conf;
		categoryFriction = frict;
	}
	
	private boolean hasSupport(int count, int categ) {
		double supUse = support;
		for(int i = 0; i < categ; i++) {
			supUse += (1 - supUse) * categoryFriction;
		}
		return ((double) count / database.getSize()) >= supUse;
	}
	
}
