package application;

import greska.GNijeNumerik;
import greska.GNijeDatum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateFormat extends Format {

	
	private boolean testDate(int d,int m,int y) throws GNijeDatum {  //provera da li datum postoji
		if (!(1 <= m && m <= 12))
			throw new GNijeDatum();
			//return false;
		if (!(1 <= d && d <= 31))
			throw new GNijeDatum();
			//return false;
		if ((d == 31) && (m == 2 || m == 4 || m == 6 || m == 9 || m == 11))
			throw new GNijeDatum();
			//return false;
		if ((d == 30) && (m == 2))
			throw new GNijeDatum();
			//return false;
		if ((m == 2) && (d == 29) && (y % 4 != 0))
			throw new GNijeDatum();
			//return false;
		if ((m == 2) && (d == 29) && (y % 400 == 0))
			return true;
		if ((m == 2) && (d == 29) && (y % 100 == 0))
			throw new GNijeDatum();
			//return false;
		if ((m == 2) && (d == 29) && (y % 4 == 0))
			return true;

		return true;
	}
	
	@Override
	public boolean testValueForFormat(String val) throws GNijeNumerik, GNijeDatum {
		
		Pattern pattern=Pattern.compile("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}\\.");
		Matcher matcher=pattern.matcher(val);
		if (matcher.find()) {
			int dd=Integer.parseInt(val.substring(0, 2));
			int mm=Integer.parseInt(val.substring(3,5));
			int yy=Integer.parseInt(val.substring(6,8));
			return testDate(dd, mm, yy);
		}
		else {
			throw new GNijeDatum();
		}
	}
	
	
	public static void main(String[] args) {
		Format format=new DateFormat();
		try {
			System.out.println(format.testValueForFormat("31.05.1952."));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} 
	}

	@Override
	public String getSign() {
		return "date";
	}

}
