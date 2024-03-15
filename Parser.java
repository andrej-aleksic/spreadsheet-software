package application;

import java.io.FileNotFoundException;
import java.io.IOException;

import greska.GNepostojiFajl;
import greska.GNepravilnaEkstenzija;
import greska.GNijeDatum;
import greska.GNijeNumerik;

public abstract class Parser {
	
	protected Table table;
	
	public  Parser(Table t) {
		this.table=t;
	}
	
	public abstract void loadTable(String path) throws FileNotFoundException, GNepostojiFajl, GNijeNumerik, GNijeDatum, GNepravilnaEkstenzija;
	public abstract void saveTable(String path) throws IOException, GNepravilnaEkstenzija;
	
}
