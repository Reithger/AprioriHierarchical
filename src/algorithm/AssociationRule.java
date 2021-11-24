package algorithm;

import java.util.Comparator;

import itemset.Itemset;
import itemset.KeySet;

public class AssociationRule implements Comparator<AssociationRule>, Comparable<AssociationRule>{

	private static KeySet keySet;
	
	private Itemset consequent;
	private Itemset determinant;
	
	public AssociationRule(Itemset X, Itemset Y) {
		determinant = X;
		consequent = Y;
	}
	
	public static void assignKeySet(KeySet in) {
		keySet = in;
	}
	
	@Override
	public String toString() {
		return determinant.toString() + " => " + consequent.toString();
	}
	
	public String toStringVerbose() {
		return keySet.convertItemset(determinant) + " => " + keySet.convertItemset(consequent);
	}

	@Override
	public int compareTo(AssociationRule o) {
		return toString().compareTo(o.toString());
	}

	@Override
	public int compare(AssociationRule o1, AssociationRule o2) {
		return o1.compareTo(o2);
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			AssociationRule ot = (AssociationRule)o;
			return this.compareTo(ot) == 0;
			
		}
		catch(Exception e) {
			return false;
		}
	}
	
}
