package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import greska.GNepostojiFajl;
import greska.GNijeDatum;
import greska.GNijeNumerik;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Main extends Application {

	private static Table t = new Table();
	private static Parser p;
	private static Scene mainScene;
	private static TextField selected;
	private String selectInput = "";
	private Label selectedInfo = new Label("");

	public static void main(String[] args) {

		System.loadLibrary("JNIFormula");
		Application.launch(args);
	}

	public GridPane populateTable() {  //formiranje same tabele u glavnom prozoru
		GridPane gridPane = new GridPane();
		TextField noneField = new TextField("");
		selected = noneField;
		noneField.setEditable(false);
		noneField.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
		noneField.setMaxWidth(30);
		selectedInfo.setTextFill(Color.GRAY);
		selectedInfo.setFont(Font.font(15));
		gridPane.add(noneField, 0, 0);

		for (int i = 0; i < t.columns; i++) {

			char c = (char) ('A' + i);
			TextField field = new TextField("" + c);
			field.setEditable(false);
			field.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
			field.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
			field.setOnMouseClicked(e -> {

				if (selected != null && selectInput.length() > 1) {
					selected.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
				} else {
					selected.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
				}
				selected = field;
				selected.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
				selectedInfo.setText(getSelected());
			});
			gridPane.add(field, i + 1, 0);
		}
		for (int r = 0; r < t.rows; r++) {

			TextField field = new TextField("" + (r + 1));
			field.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
			field.setEditable(false);
			field.setMaxWidth(30);
			field.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
			gridPane.add(field, 0, r + 1);

			for (int c = 0; c < t.columns; c++) {
				int index = r * t.columns + c;
				TextField cellView = new TextField(
						t.cells.get(index).getFormat().applyFormat(t.cells.get(index).getCellValue().getValue()));
				cellView.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, null)));
				cellView.setOnAction(e -> {
					Cell cell = t.cells.get(index);
					String input = cellView.getText();
					try {
						// cell.setFormat(new NumberFormat(2));
						System.out.println("Pokusavam da upisem: " + input);
						System.out.println("Format je: " + cell.getFormat().getSign());
						cell.setValue(input, t);
						if (input.charAt(0) == '=') {
							cellView.setText(cell.getFormat().applyFormat(cell.getCellValue().getValue()));
						}

						// ovo sam menjao
						cellView.setText(cell.getFormat().applyFormat(cell.getCellValue().getValue()));
						System.out.println("Nova Vrednost celije je: " + cell.getCellValue().getValue());

					} catch (Exception err) {
						cellView.setText(cell.getFormat().applyFormat(cell.getCellValue().getValue()));
						errorAlert(err);
					}
				});

				cellView.setOnMouseClicked(e -> {
					if (selected != null && selectInput.length() > 1) {
						selected.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
					} else {
						selected.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
					}
					selected = cellView;
					selected.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, null, null)));
					selectedInfo.setText(getSelected());
				});

				gridPane.add(cellView, c + 1, r + 1);
			}
		}

		return gridPane;
	}

	public Scene populateMainScene() { //punjenje glavnog prozora

		BorderPane borderPane = new BorderPane();
		ScrollPane scrollPane = new ScrollPane();
		// GridPane gridPane = new GridPane();
		GridPane gridPane = populateTable();
		scrollPane.setContent(gridPane);

		Scene mainScene = new Scene(borderPane);

		TextInputDialog saveDialog = new TextInputDialog();
		saveDialog.setTitle("Save AS Dialog");
		saveDialog.setContentText("Save Path:");


		borderPane.setCenter(scrollPane);
		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("File");
		MenuItem csvMenu = new MenuItem("Save as CSV");

		csvMenu.setOnAction(e -> {
			Optional<String> result = saveDialog.showAndWait();
			if (result.isPresent()) {
				try {
					Parser parser = new CSVParser(t);
					parser.saveTable(result.get());
				} catch (Exception e1) {
					errorAlert(e1);
				}
			}
		});

		MenuItem jsonMenu = new MenuItem("Save as JSON");
		jsonMenu.setOnAction(e -> {
			Optional<String> result = saveDialog.showAndWait();
			if (result.isPresent()) {
				try {
					Parser parser = new JSONParser(t);
					parser.saveTable(result.get());
				} catch (Exception e1) {
					errorAlert(e1);
				}
			}
		});

		menu.getItems().addAll(csvMenu, jsonMenu);
		menuBar.getMenus().add(menu);

		VBox formatPane = new VBox(15);
		// VBox.setMargin(formatPane, new Insets(0,20,0,0));
		formatPane.setPrefWidth(190);
		formatPane.setAlignment(Pos.TOP_CENTER);
		Button formatBtn = new Button("Format");
		formatBtn.setFont(Font.font(15));
		formatPane.getChildren().add(formatBtn);
		// formatBtn.setBorder(new Border(new
		// BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,new
		// BorderWidths(2.0))));

		ToggleGroup toggleGroup = new ToggleGroup();
		RadioButton textRbtn = new RadioButton("Text");
		textRbtn.setToggleGroup(toggleGroup);
		textRbtn.setFont(Font.font(15));
		textRbtn.setSelected(true);

		RadioButton numberRbtn = new RadioButton("Number");
		numberRbtn.setToggleGroup(toggleGroup);
		numberRbtn.setFont(Font.font(15));
		TextField decimalsTxt = new TextField();
		decimalsTxt.setEditable(false);
		decimalsTxt.setMaxWidth(140);
		numberRbtn.setOnAction(ev -> {
			decimalsTxt.setEditable(true);
		});
		textRbtn.setOnAction(ev -> {
			decimalsTxt.setEditable(false);
		});

		RadioButton dateRbtn = new RadioButton("Date");
		dateRbtn.setToggleGroup(toggleGroup);
		dateRbtn.setFont(Font.font(15));
		dateRbtn.setOnAction(ev -> {
			decimalsTxt.setEditable(false);
		});

		formatPane.getChildren().addAll(textRbtn, numberRbtn, decimalsTxt, dateRbtn);
		formatPane.setPadding(new Insets(10, 0, 0, 0));
		// formatPane.getChildren().add(selectedInfo);

		formatBtn.setOnAction(e -> {

			Toggle choice = toggleGroup.getSelectedToggle();
			String formatStr;
			try {
				if (choice == textRbtn) {
					formatStr = "text";
					t.formatTable(selectInput, "text", 0);
				} else if (choice == numberRbtn) {
					formatStr = "number";
					int dec = Integer.parseInt(decimalsTxt.getText());
					t.formatTable(selectInput, "number", dec);
					updateGridPane(gridPane);
				} else {
					formatStr = "date";
					t.formatTable(selectInput, "date", 0);
				}

				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Successful formating!");
				alert.setHeaderText("Successfully formated " + selectInput + " into format: " + formatStr);

				alert.showAndWait();


			} catch (Exception err) {
				errorAlert(err);
			}

		});

		borderPane.setRight(formatPane);
		borderPane.setTop(menuBar);
		borderPane.setBottom(selectedInfo);

		return mainScene;
	}

	private void updateGridPane(GridPane pane) {
		ArrayList<TextField> fieldsToUpdate = new ArrayList<>(); //lista koja sadrzi textField-ove za update

		if (selectInput.length() > 1) { // Ako je jedna celija--> ima kolonu i red
			Pattern pattern = Pattern.compile("([A-Z]{1})([1-9]{1}[0-9]{0,})");
			Matcher matcher = pattern.matcher(selectInput);
			if (matcher.find()) {
				char cc = matcher.group(1).charAt(0);
				int column = cc - 'A' + 1;
				int row = Integer.parseInt(matcher.group(2));
				int index = row * (t.columns + 1) + column;
				fieldsToUpdate.add((TextField) pane.getChildren().get(index));
			}
		} else {

			char cc = selectInput.charAt(0);
			int column = cc - 'A' + 1;
			for (int i = column + (t.columns + 1); i < (t.columns + 1) * (t.rows + 1); i += (t.columns + 1)) {
				fieldsToUpdate.add((TextField) pane.getChildren().get(i));
			}
		}

		Iterator<TextField> iterText = fieldsToUpdate.iterator();

		ArrayList<Cell> cellsToFormat = new ArrayList<>(); //lista koja sadrzi celije ciji prikaz treba da se update

		if (selectInput.length() > 1) { // Ako je jedna celija--> ima kolonu i red
			Pattern pattern = Pattern.compile("([A-Z]{1})([1-9]{1}[0-9]{0,})");
			Matcher matcher = pattern.matcher(selectInput);
			if (matcher.find()) {
				char cc = matcher.group(1).charAt(0);
				int column = cc - 'A';
				int row = Integer.parseInt(matcher.group(2)) - 1;
				int index = row * t.columns + column;
				cellsToFormat.add(t.cells.get(index));
			}
		} else {

			char cc = selectInput.charAt(0);
			int column = cc - 'A';
			for (int i = column; i < t.columns * t.rows; i += t.columns) {
				cellsToFormat.add(t.cells.get(i));
			}
		}
		
		Iterator<Cell> iterCell = cellsToFormat.iterator();
		
		while (iterText.hasNext()) { //paralelni prolazak oba iteratora
			Cell cell=iterCell.next();
			iterText.next().setText(cell.getFormat().applyFormat(cell.getCellValue().getValue()));
		}

	}

	public String getSelected() {  //na osnovu selektovanog textField-a vraca osnovne info o njemu
		int row = GridPane.getRowIndex(selected);
		int column = GridPane.getColumnIndex(selected);
		if (row == 0) {
			selectInput = "" + (char) ('A' + column - 1);
			return "Selected column: " + (char) ('A' + column - 1);
		} else {
			selectInput = "" + (char) ('A' + column - 1) + row;
			int index = (row - 1) * t.columns + column - 1;
			return "Selected cell: " + (char) ('A' + column - 1) + row + "; Cell format: "
					+ t.cells.get(index).getFormat().getSign();
		}
	}

	public Scene populateIntroScene(Stage stage) { //popunjava intro prozor
		VBox group = new VBox(20);
		group.setAlignment(Pos.CENTER);
		Scene introScene = new Scene(group, 300, 300);
		group.setBackground(new Background(new BackgroundFill(Color.LAVENDER, null, null)));
		Label label = new Label("Choose file format!");
		label.setFont(Font.font(25));

		group.getChildren().add(label);
		ToggleGroup toggleGroup = new ToggleGroup();
		RadioButton csvRbtn = new RadioButton("CSV");
		csvRbtn.setToggleGroup(toggleGroup);
		csvRbtn.setFont(Font.font(15));
		csvRbtn.setSelected(true);
		RadioButton jsonRbtn = new RadioButton("JSON");
		jsonRbtn.setToggleGroup(toggleGroup);
		jsonRbtn.setFont(Font.font(15));
		group.getChildren().addAll(csvRbtn, jsonRbtn);

		TextField pathTxt = new TextField();
		pathTxt.setMaxWidth(180);
		group.getChildren().add(pathTxt);

		Button nextBtn = new Button("Next");
		nextBtn.setFont(Font.font(15));
		group.getChildren().add(nextBtn);
		nextBtn.setPadding(new Insets(20));

		nextBtn.setOnAction(ae -> {
			String path = pathTxt.getText();
			Toggle choice = toggleGroup.getSelectedToggle();
			try {
				if (choice == csvRbtn) {
					p = new CSVParser(t);
					p.loadTable(path);
				} else {
					p = new JSONParser(t);
					p.loadTable(path);
				}
				mainScene = populateMainScene();
				stage.setScene(mainScene);
			} catch (Exception e) {
				errorAlert(e);
			}
		});
		return introScene;
	}

	public void errorAlert(Exception e) {  //prikaz svih gresaka koje se propagiraju u main
		Alert alert = new Alert(AlertType.ERROR);
		alert.setWidth(300);
		alert.setHeight(300);
		alert.setTitle("Error Dialog");
		alert.setHeaderText("ERROR");
		alert.setContentText(e.getMessage());
		alert.getDialogPane().setPadding(new Insets(0, 10, 0, 10));

		alert.showAndWait();
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("EXCEL za sirotilju");
		try {
			Scene introScene = populateIntroScene(stage);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent arg0) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("EXIT dialog");
					alert.setHeaderText("Are you SURE you want to EXIT");
					// alert.setContentText("Are you ok with this?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						Platform.exit();
					}

					arg0.consume();
				}
			});

			stage.setScene(introScene);

		} catch (Exception e) {
			errorAlert(e);
		}

		stage.show();
	}
}
