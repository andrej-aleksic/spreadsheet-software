package greska;

public class GNepostojiCelija extends Exception {

	public GNepostojiCelija(String s) {
		super("Celija "+s+" ne postoji u tabeli!");
	}
}
