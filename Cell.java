package application;

import greska.GNepostojiCelija;
import greska.GNijeDatum;
import greska.GNijeNumber;
import greska.GNijeNumerik;

public class Cell {
	
	private Format format;
	private CellValue cellValue;
	
	public Cell(String s) {
		this.cellValue=new CellValue(s);
	}
	
	public void setFormat(Format f) throws GNijeNumerik, GNijeDatum {
		System.out.println("testiram: "+cellValue.getValue());
		if (f.testValueForFormat(cellValue.getValue())) {
			format=f;			
		}
	}
	
	public Format getFormat() {
		return format;
	}
	
	public void setValue(String val,Table t) throws GNijeNumerik, GNijeDatum, GNepostojiCelija, GNijeNumber {
		
		System.out.println("usao sam u proveru");
		if (val.charAt(0)=='=' && format.getSign() =="number") {
			cellValue.setValue(t.calcFormula(val));
		}
		else if (format.testValueForFormat(val)) {
			
			cellValue.setValue(val);
		}
	}
	
	public CellValue getCellValue() {
		return cellValue;
	}
	
}
