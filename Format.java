package application;

import greska.GNijeDatum;
import greska.GNijeNumerik;

public abstract class Format {

	public abstract boolean testValueForFormat(String val) throws GNijeNumerik, GNijeDatum;
	public abstract String getSign();
	public String applyFormat(String val) {
		return val;
	}
}
