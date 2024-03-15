package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import greska.GNepostojiFajl;
import greska.GNepravilnaEkstenzija;
import greska.GNijeDatum;
import greska.GNijeNumerik;

public class CSVParser extends Parser {

	public CSVParser(Table t) {
		super(t);
	}

	@Override
	public void loadTable(String path)
			throws FileNotFoundException, GNepostojiFajl, GNijeNumerik, GNijeDatum, GNepravilnaEkstenzija {
		Pattern pattern = Pattern.compile(".{1,}\\.csv");
		Matcher matcher = pattern.matcher(path);
		if (!matcher.find())
			throw new GNepravilnaEkstenzija(path, ".csv");

		File inputFile = new File(path);
		if (inputFile.exists()) {
			// System.out.println("Ucitao sam fajl");
			Scanner reader = new Scanner(inputFile);
			while (reader.hasNextLine()) {
				table.rows++;
				String rowCells[] = reader.nextLine().split(",");
				table.columns = rowCells.length;
				for (int i = 0; i < rowCells.length; i++) {
					Cell c = new Cell(rowCells[i]);
					c.setFormat(new TextFormat());
					table.cells.add(c);
					if (rowCells[i].length() > table.columnWidth[i]) {
						table.columnWidth[i] = rowCells[i].length();
					}
				}
			}
			reader.close();
		} else {
			throw new GNepostojiFajl(path);
		}

	}

	@Override
	public void saveTable(String path) throws IOException, GNepravilnaEkstenzija {
		Pattern pattern = Pattern.compile(".{1,}\\.csv");
		Matcher matcher = pattern.matcher(path);
		if (!matcher.find())
			throw new GNepravilnaEkstenzija(path, ".csv");

		File outputFile = new File(path);
		FileWriter writer = new FileWriter(outputFile);
		for (int r = 0; r < table.rows; r++) {

			for (int c = 0; c < table.columns; c++) {
				int index = r * table.columns + c;
				writer.write(table.cells.get(index).getCellValue().getValue());
				if (c != table.columns - 1) {
					writer.write(",");
				}
			}
			writer.write("\n");
		}
		writer.close();
	}

	public static void main(String[] args) {

		Pattern pattern = Pattern.compile(".{1,}\\.csv\n");
		Matcher matcher = pattern.matcher("a.csvd");
		if (!matcher.find())
			System.out.println("format fajla ne odgovara CSV");
		else
			System.out.println("Odgovara sve");
	}

}
