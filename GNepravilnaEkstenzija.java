package greska;

public class GNepravilnaEkstenzija extends Exception {

	public GNepravilnaEkstenzija(String path,String ect) {
		super("Putanja "+path+" nema "+ect+" ekstenziju!");
	}
	
}
