package greska;

public class GNepostojiFajl extends Exception {

	public GNepostojiFajl(String s) {
		super("Fajl sa putanjom: "+s+" ne postoji!");
	}
}
