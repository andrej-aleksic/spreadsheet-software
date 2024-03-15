package application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import greska.GNijeNumerik;

public class NumberFormat extends Format {

	private int decimalPoints;

	public NumberFormat(int d) {
		this.decimalPoints = d;
	}

	@Override
	public boolean testValueForFormat(String val) throws GNijeNumerik {
		try {
			double d = Double.parseDouble(val);
		} catch (NumberFormatException nfe) {
			throw new GNijeNumerik(val);
		}
		return true;
	}

	public static void main(String[] args) {

		NumberFormat f = new NumberFormat(3);
		System.out.println(f.applyFormat("12"));

	}

	@Override
	public String applyFormat(String val) { //postavljanje formata na vrednost koja se nalazi u celiji
		Pattern pattern = Pattern.compile(".{1,}\\.([0-9]{1,})");
		Matcher matcher = pattern.matcher(val);
		String zeros = "";
		if (matcher.find()) { // nadjena je tacka u zapisu broja
			String decimalsStr = matcher.group(1);
			if (decimalsStr.length() < decimalPoints) {
				for (int i = 0; i < decimalPoints - decimalsStr.length(); i++)
					zeros += "0";
				return val  + zeros;
			}
			else {
				return val.substring(0,decimalPoints+val.indexOf(".")+1);
			}
		} else { // nema tacke u zapisu pa samo treba dopisati . i decimalPoints nula
			for (int i = 0; i < decimalPoints; i++)
				zeros += "0";
			if (decimalPoints!=0) return val + "." + zeros;
			else return val;
		}
	}

	@Override
	public String getSign() {
		return "number";
	}

}
