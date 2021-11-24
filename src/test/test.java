package test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import algorithm.Algorithm;
import algorithm.AssociationRule;
import itemset.Itemset;
import itemset.Node;

public class test {

	/**
	 * 
	 * Expected output for baseline:
	 *  Apriori
		Large Itemsets: [[3, 5], [2, 3], [2, 5], [1, 3], [2, 3, 5]]
		Rules: 
		[2] => [5]
		[5] => [2]
		[1] => [3]
		[2, 3] => [5]
		[3, 5] => [2]
		AprioriTID
		Large Itemsets: [[3, 5], [2, 3], [1, 3], [2, 5], [2, 3, 5]]
		Rules: 
		[1] => [3]
		[2] => [5]
		[5] => [2]
		[2, 3] => [5]
		[3, 5] => [2]
	 * 
	 * @param args
	 */
	
	public static void main(String[] args) {
		int transactionLimit = 70;
		Algorithm a = new Algorithm(transactionLimit);
		a.assignSupportConfidence(.4, .75, .5);
		
		String[][] items = new String[][] {
				{"Chocolate Milk", "2% Milk", "Strawberry Milk"}, 
				{"Cheddar", "Brie", "Parmesan"},
				{"Chicken Wing", "Chicken Breast", "Chicken Leg"}, 
				{"Pork Tenderloin", "Porkchops", "Pork Sausage"},
				{"Brocolli", "Leak", "Bell Pepper"}, 
				{"Apple", "Orange", "Strawberry"}
			};
		
		String[] categories = new String[] {"Milk", "Cheese", "Chicken", "Pork", "Vegetable", "Fruit"};
				
		a.addNode("Product", "Dairy");
		a.addNode("Product", "Meat");
		a.addNode("Product", "Produce");
		a.addNode("Dairy", "Milk");
		a.addNode("Dairy", "Cheese");
		a.addNode("Meat", "Chicken");
		a.addNode("Meat", "Pork");
		a.addNode("Produce", "Vegetable");
		a.addNode("Produce", "Fruit");
		
		for(int i = 0; i < categories.length; i++) {
			a.registerItemsAsCategory(items[i], categories[i]);
		}

		/*
		 * 
		 * Add randomized sets of items that are related in that when an item gets picked
		 * during transaction generation, there is a moderate likelihood of pulling another
		 * item from that set so we can simulate actual associations
		 * 
		 * Try to get a rule with 3 items that isn't trivial
		 * 
		 * 
		 */
		Random rand = new Random();
		
		int numAssocSets = 5;
		int maxSizeSets = 3;
		String[][] assocSets = new String[numAssocSets][];
		for(int i = 0; i < numAssocSets; i++) {
			String[] use = new String[rand.nextInt(maxSizeSets) + 3];
			int posit = 0;
			while(posit < use.length) {
				int ind = rand.nextInt(items.length);
				String item = items[ind][rand.nextInt(items[ind].length)];
				if(indexOf(use, item) == -1) {
					use[posit++] = item;
				}
			}
			assocSets[i] = use;
		}
		
		System.out.println(Arrays.deepToString(assocSets));

		int maxTransactionSize = 10;
		double assocChance = .75;
		
		for(int i = 0; i < transactionLimit; i++) {
			String[] submit = new String[2 + rand.nextInt(maxTransactionSize)];
			int posit = 0;
			int category = -1;
			top:
			while(posit < submit.length) {
				if(posit > 0 && rand.nextDouble() <= assocChance) {
					String last = submit[posit - 1];
					for(int j = 0; j < assocSets.length; j++) {
						int ind = indexOf(assocSets[j], last);
						if(ind != -1) {
							for(int k = 0; k < assocSets[j].length; k++) {
								if(indexOf(submit, assocSets[j][k]) == -1) {
									submit[posit++] = assocSets[j][k];
									continue top;
								}
							}
						}
					}
				}
				category = rand.nextInt(categories.length);
				int prod = rand.nextInt(items[category].length);
				int bail = 0;
				while(indexOf(submit, items[category][prod]) != -1) {
					prod = rand.nextInt(items[category].length);
					bail++;
					if(bail > 100) {
						continue top;
					}
				}
				submit[posit++] = items[category][prod];
			}
			a.addTransaction(submit);
			//System.out.println(Arrays.toString(submit));
		}
		System.out.println("Apriori");
		//testApriori(a);
		System.out.println("AprioriTID");
		testAprioriTID(a);
	
	}
	
	private static int indexOf(String[] array, String key) {
		for(int i = 0; i < array.length; i++) {
			if(array[i] != null && array[i].equals(key)) {
				return i;
			}
		}
		return -1;
	}
	
	private static void testBaseline() {
		Algorithm a = new Algorithm(9);
		a.populateDatabase(new String[][] {{"1", "2", "3", "4", "5"}, {"1", "3", "4"}, {"2", "3", "5"}, {"1", "2", "3", "5"}, {"2", "5"}});
		a.assignSupportConfidence(.5, .75, .5);
		a.removeTransaction(0);
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
	
	private static void testItemsetCategoricalEquivalence() {
		Itemset t = new Itemset(new int[] {211, 4, 6});
		System.out.println(t.contains(210));
	}
	
	private static void testAlgorithmHierarchySetup() {
		Algorithm a = new Algorithm(9);
		a.addNode("Product", "Meat");
		a.addNode("Product", "Dairy");
		a.addNode("Dairy", "Cheese");
		a.addNode("Dairy", "Milk");
		System.out.println(a.retrievePrefix("Cheese"));
		System.out.println(a.retrievePrefix("Milk"));
		a.registerItemAsCategory("Cheddar", "Cheese");
		System.out.println(a.getItemId("Cheddar"));
		System.out.println(a.getHierarchySize());
	}
	
	private static void testNode() {
		Node n = new Node("Product");
		n.addChild("Product", "Meat");
		n.addChild("Product", "Dairy");
		n.addChild("Dairy", "Cheese");
		n.addChild("Dairy", "Milk");

		System.out.println(n.retrievePrefix("Cheese"));
		System.out.println(n.retrievePrefix("Milk"));
	}

	private static void testPowerSet() {
		Itemset e = new Itemset(new int[] {3, 1, 2, 4});
		System.out.println(e.generatePowerSet());
		for(Itemset i : e.generatePowerSet()) {
			System.out.println(i.toString());
		}
	}
	
}
