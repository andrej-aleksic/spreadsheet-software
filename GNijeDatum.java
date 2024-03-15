package greska;

public class GNijeDatum extends Exception{
	
	public GNijeDatum() {
		super("Zadata vrednost ne predstavlja datum u odgovarajucem formatu!\nMozda prvo treba potvrditi promenu vrednosti u celiji!\n");
	}
}
