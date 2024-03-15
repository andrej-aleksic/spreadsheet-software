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

public class JSONParser extends Parser {

	public JSONParser(Table t) {
		super(t);
	}

	@Override
	public void loadTable(String path)
			throws FileNotFoundException, GNepostojiFajl, GNijeNumerik, GNijeDatum, GNepravilnaEkstenzija {
		Pattern p1 = Pattern.compile(".{1,}\\.json");
		Matcher m1 = p1.matcher(path);
		if (!m1.find())
			throw new GNepravilnaEkstenzija(path, ".json");
		File inputFile = new File(path);

		if (inputFile.exists()) {
			System.out.println("Ucitao sam fajl");
			Scanner reader = new Scanner(inputFile);
			String line = reader.nextLine();
			Pattern pattern = Pattern
					.compile("\\{\"rowNum\":\"([0-9]{1,})\",\"columnNum\":\"([0-9]{1,})\",\"Table\":\\[");
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {

				table.rows = Integer.parseInt(matcher.group(1));
				table.columns = Integer.parseInt(matcher.group(2));
			}
			// System.out.println(table.rows);
			// System.out.println(table.columns);
			Pattern p2 = Pattern.compile(
					"\\{\"row\":\"([0-9]{1,})\",\"column\\\":\"([0-9]{1,})\",\"value\":\"(.*?)\",\"format\":\"(.*?)\",\"decimals\":\"([0-9])\"\\},");
			while (reader.hasNextLine()) {
				matcher = p2.matcher(reader.nextLine());
				if (matcher.find()) {
					int row = Integer.parseInt(matcher.group(1));
					int column = Integer.parseInt(matcher.group(2));
					String value = matcher.group(3);
					String format = matcher.group(4);

					int d = Integer.parseInt(matcher.group(5));

					Cell c = new Cell(value);
					Format f;
					if (format.equals("text")) {
						f = new TextFormat();
					} else if (format.equals("date")) {
						f = new DateFormat();
					} else {
						f = new NumberFormat(d);
					}
					c.setFormat(f);
					table.cells.add(c);
				}
			}
			reader.close();
		} else {
			throw new GNepostojiFajl(path);
		}

	}

	@Override
	public void saveTable(String path) throws IOException, GNepravilnaEkstenzija {
		Pattern p1 = Pattern.compile(".{1,}\\.json");
		Matcher m1 = p1.matcher(path);
		if (!m1.find())
			throw new GNepravilnaEkstenzija(path, ".json");
		File outputFile = new File(path);
		FileWriter writer = new FileWriter(outputFile);
		writer.write("{\"rowNum\":\"" + table.rows + "\",\"columnNum\":\"" + table.columns + "\",\"Table\":[\n");

		for (int r = 0; r < table.rows; r++) {
			for (int c = 0; c < table.columns; c++) {
				int index = r * table.columns + c;
				String strFormat = table.cells.get(index).getFormat().getSign();
				int decimals = 0;
				writer.write("{\"row\":\"" + r + "\",\"column\":\"" + c + "\",\"value\":\""
						+ table.cells.get(index).getCellValue().getValue() + "\",\"format\":\"" + strFormat
						+ "\",\"decimals\":\"" + decimals + "\"},\n");
			}
		}
		writer.write("]}");
		writer.close();

	}

	public static void main(String[] args) throws IOException, GNijeNumerik, GNijeDatum {
		Table t = new Table();
		Parser p = new JSONParser(t);
		try {
			p.loadTable("saveProba.json");
		} catch (FileNotFoundException e) {
			System.out.println("Nema fajla");
		} catch (GNepostojiFajl e) {
			System.out.println("Nema fajla");
		} catch (GNepravilnaEkstenzija e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.printTable();
	}
}
