package application;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import greska.GNepostojiCelija;
import greska.GNepostojiFajl;
import greska.GNijeDatum;
import greska.GNijeNumber;
import greska.GNijeNumerik;

public class Table {

	ArrayList<Cell> cells = new ArrayList<>();
	int rows = 0;
	int columns = 0;
	int columnWidth[] = new int[30];
	private Stack<Action> actionsToUndo;
	private Stack<Action> actionsToRedo;

	public static native String infixToPostfix(String formula);
	
	/**
	 * Getter for number of table rows
	 * @return number of rows
	 */
	public int getRows() {
		return rows;
	}

	
	/**
	 * Getter for number of table columns
	 * @return number of columns
	 */
	public int getColumns() {
		return columns;
	}

	public Stack<Action> getActionsToUndo() {
		return actionsToUndo;
	}

	public Stack<Action> getActionsToRedo() {
		return actionsToRedo;
	}

	
	/**
	 * Prints table in console
	 */
	public void printTable() {
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				int index = r * columns + c;
				System.out.print(cells.get(index).getCellValue().getValue() + " | ");
			}
			System.out.println();
		}

	}

	
	/**
	 * 
	 * @param selectInput - CellID of cell/cells to be formated
	 * @param format - name of format to be set
	 * @param dec - number of decimal places if format is a number
	 * @throws GNijeNumerik - throws if a value of cell is not a number
	 * @throws GNijeDatum - throws if a value of cell is not a date
	 */
	public void formatTable(String selectInput, String format, int dec) throws GNijeNumerik, GNijeDatum {
		
		ArrayList<Cell> cellsToFormat=new ArrayList<>();  //lista celija koje treba formatirati
		
		if (selectInput.length() > 1) { // Ako je jedna celija--> ima kolonu i red
			Pattern pattern = Pattern.compile("([A-Z]{1})([1-9]{1}[0-9]{0,})");
			Matcher matcher = pattern.matcher(selectInput);
			if (matcher.find()) {
				char cc=matcher.group(1).charAt(0);
				int column=cc-'A';
				int row=Integer.parseInt(matcher.group(2))-1;			
				int index = row * columns + column;
				cellsToFormat.add(cells.get(index));
			}
		}
		else {
			
			char cc=selectInput.charAt(0);
			int column=cc-'A';
			for (int i=column;i<columns*rows;i+=columns) {
				cellsToFormat.add(cells.get(i));
			}
			
		}
		System.out.println(cellsToFormat.size());
		//postavljanje novog formata za sve celije koje treba da se formatiraju
		if (format=="text") {  
			System.out.println("text");
			for(int i=0;i<cellsToFormat.size();i++) {
				cellsToFormat.get(i).setFormat(new TextFormat());
			}
		}
		else if (format=="date") {
			System.out.println("date");
			for(int i=0;i<cellsToFormat.size();i++) {
				cellsToFormat.get(i).setFormat(new DateFormat());
			}
		}
		else{
			System.out.println("number");
			for(int i=0;i<cellsToFormat.size();i++) {
				cellsToFormat.get(i).setFormat(new NumberFormat(dec));
			}
		}
		
	}
	
	
	/**
	 * Returns calculated value when formula applied
	 * @param formula - formula as a string value
	 * @return - calculated value based on formula
	 * @throws GNepostojiCelija - throws if a cell in formula doesnt exist
	 * @throws GNijeNumber - throws if a cell from formula is not in number format
	 */
	public String calcFormula(String formula) throws GNepostojiCelija, GNijeNumber {
		
		
		String postfix=Table.infixToPostfix(formula.toUpperCase());
		Stack<Double> s=new Stack<>();
		String operand="";
		
		for (char c : postfix.toCharArray()) {
			if (c == ' ') {
				if (operand.charAt(0) >= 'A' && operand.charAt(0) <= 'Z') {
					//CellIdentifier temp_id((int)operand.at(1) - '1', (int)operand.at(0) - 'A');
					int row=Integer.parseInt(operand.substring(1))-1;
					int column=(int)(operand.charAt(0) - 'A');
					int index=row*columns+(int)(operand.charAt(0) - 'A');
					//int index= (int)(operand.charAt(1) - '1')*columns+(int)(operand.charAt(0) - 'A');
					if (row<0 || column<0) throw new GNepostojiCelija(""+operand.charAt(0)+row);
					if (row>rows || column>columns) throw new GNepostojiCelija(""+operand.charAt(0)+row);
					if (index>columns*rows-1) throw new GNepostojiCelija("");
					if (!cells.get(index).getFormat().getSign().equals("number")) throw new GNijeNumber(""+operand.charAt(0)+row);
					//int index = r * table.columns + c;
					//s.push(get_value_from_table((int)operand.at(1) - '1', (int)operand.at(0) - 'A', tabela));
					s.push(Double.parseDouble(cells.get(index).getCellValue().getValue()));
				}
				else {
					s.push(Double.parseDouble(operand));
				}

				operand = "";
			}
			else if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.') {
				operand += c;
			}
			else {
				double a = s.peek(); s.pop();
				double b = s.peek(); s.pop();

				switch (c) {
				case '+':
					s.push(b + a);
					break;
				case '-':
					s.push(b - a);
					break;
				case '*':
					s.push(b * a);
					break;
				case '/':
					s.push(b / a);
					break;
				}
			}
		}
		
		return s.peek().toString();
	}
	
	
}
