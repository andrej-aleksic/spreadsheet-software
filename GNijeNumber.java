package greska;

public class GNijeNumber extends Exception {

	public GNijeNumber(String s) {
		super("Celija "+s+ " nije u number formatu!");
	}
}
