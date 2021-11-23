
public class AssociationRule {

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
	
}
