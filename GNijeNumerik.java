package greska;

public class GNijeNumerik extends Exception {
	
	public GNijeNumerik(String s) {
		super("Zadata vrednost ("+ s+") ne predstavlja numericku vrednost!");
	}
	
}
