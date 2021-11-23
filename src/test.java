import java.util.ArrayList;

public class test {

	public static void main(String[] args) {
		Algorithm a = new Algorithm(new String[][] {{"0", "1", "2", "3", "4", "5"}, {"1", "3", "4"}, {"2", "3", "5"}, {"1", "2", "3", "5"}, {"2", "5"}});
		a.assignSupportConfidence(.5, .75);
		a.getDatabase().removeTransaction(0);
		System.out.println("Apriori");
		testApriori(a);
		System.out.println("AprioriTID");
		testAprioriTID(a);
	}
	
	private static void testApriori(Algorithm a) {
		ArrayList<Itemset> use = a.apriori();
		System.out.println("Large Itemsets: " + use);
		System.out.println("Rules: ");
		for(AssociationRule ar : a.deriveRules(use)) {
			System.out.println(ar.toStringVerbose());
		}
	}
	
	private static void testAprioriTID(Algorithm a) {
		ArrayList<Itemset> use = a.aprioriTID();
		System.out.println("Large Itemsets: " + use);
		System.out.println("Rules: ");
		for(AssociationRule ar : a.deriveRules(use)) {
			System.out.println(ar.toStringVerbose());
		}
	}
	
	private static void testPowerSet() {
		Itemset e = new Itemset(new int[] {3, 1, 2, 4});
		System.out.println(e.generatePowerSet());
		for(Itemset i : e.generatePowerSet()) {
			System.out.println(i.toString());
		}
	}
	
}
