package com.eolxsi;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.xml.bind.JAXBException;

import com.eolxsi.entity.Product;
import com.eolxsi.enums.FileOption;
import com.eolxsi.util.DialogBuilder;
import com.eolxsi.util.XmlFileManager;
import com.eolxsi.wrapper.ProductListWrapper;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

/**
 * Main View class of the app.
 * 
 * This class is responsible for displaying the UI and handling it's events.
 * 
 * @author LuisDaniel
 *
 */
public class MainView extends Application {

	// Screen dimensions
	private static final int SCREEN_WIDTH = 600;
	private static final int SCREEN_HEIGHT = 500;

	// Screen elements
	private TextField productNameTextField = new TextField();
	private TextField quantityTextField = new TextField();
	private TextField unitPriceTextField = new TextField();
	private Button addProductButton = new Button("Add");
	private Button addImageButton = new Button("Add image...");
	private Label imageLabel = new Label("No image selected...");
	private TableView<Product> productsTableView = new TableView<>();
	private TableColumn<Product, String> productNameColumn = new TableColumn<>("Product Name");
	private TableColumn<Product, Integer> productQuantityColumn = new TableColumn<>("Quantity");
	private TableColumn<Product, Double> productUnitPriceColumn = new TableColumn<>("Unit Price");
	private TableColumn<Product, Product> deleteButtonColumn = new TableColumn<>("Remove");
	private TableColumn<Product, Product> previewButtonColumn = new TableColumn<>("View Pic");
	private MenuBar menuBar = new MenuBar();
	private Menu fileMenu = new Menu("File");
	private MenuItem saveAsMenuItem = new MenuItem("Save as...");
	private MenuItem loadMenuItem = new MenuItem("Load...");

	private ObservableList<Product> products = FXCollections.observableArrayList();

	/**
	 * Starts the application
	 */
	@Override
	public void start(Stage primaryStage) {

		// A Simple Javafx TextField Using TextFormatter To Allow Only Double Value
		// ref.:https://gist.github.com/karimsqualli96/f8d4c2995da8e11496ed
		UnaryOperator<TextFormatter.Change> filter = new UnaryOperator<TextFormatter.Change>() {
			@Override
			public TextFormatter.Change apply(TextFormatter.Change t) {
				if (t.isReplaced())
					if (t.getText().matches("[^0-9]"))
						t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));
				if (t.isAdded()) {
					if (t.getControlText().contains(".")) {
						if (t.getText().matches("[^0-9]")) {
							t.setText("");
						}
					} else if (t.getText().matches("[^0-9.]")) {
						t.setText("");
					}
				}

				return t;
			}
		};

		// quantity can only be numeric
		quantityTextField.setTextFormatter(new TextFormatter<>(filter));
		quantityTextField.setMaxWidth(Double.MAX_VALUE);

		// unit price can only be numeric
		unitPriceTextField.setTextFormatter(new TextFormatter<>(filter));
		unitPriceTextField.setMaxWidth(Double.MAX_VALUE);

		productNameTextField.setMaxWidth(Double.MAX_VALUE);

		ColumnConstraints labelColumnConstraint = new ColumnConstraints();
		labelColumnConstraint.setPercentWidth(30);

		ColumnConstraints textFielColumnConstraint = new ColumnConstraints();
		textFielColumnConstraint.setPercentWidth(70);

		GridPane inputGridPane = new GridPane();
		inputGridPane.setPadding(new Insets(10));
		inputGridPane.setVgap(5);
		inputGridPane.setHgap(10);
		inputGridPane.getColumnConstraints().addAll(labelColumnConstraint, textFielColumnConstraint);
		inputGridPane.add(new Label("Product name:"), 0, 0);
		inputGridPane.add(productNameTextField, 1, 0);
		inputGridPane.add(new Label("Quantity: "), 0, 1);
		inputGridPane.add(quantityTextField, 1, 1);
		inputGridPane.add(new Label("Unit price: "), 0, 2);
		inputGridPane.add(unitPriceTextField, 1, 2);
		inputGridPane.add(addImageButton, 0, 3);
		inputGridPane.add(imageLabel, 1, 3);

		// Creating the tableview and columns
		productNameColumn.setPrefWidth(250);
		productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		productNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		productNameColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product, String>>() {
			@Override
			public void handle(CellEditEvent<Product, String> t) {
				((Product) t.getTableView().getItems().get(t.getTablePosition().getRow())).setName(t.getNewValue());
			}
		});

		productQuantityColumn.setPrefWidth(100);
		productQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		productQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		productQuantityColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product, Integer>>() {
			@Override
			public void handle(CellEditEvent<Product, Integer> t) {
				((Product) t.getTableView().getItems().get(t.getTablePosition().getRow())).setQuantity(t.getNewValue());

			}
		});

		productUnitPriceColumn.setPrefWidth(75);
		productUnitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
		productUnitPriceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		productUnitPriceColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product, Double>>() {
			@Override
			public void handle(CellEditEvent<Product, Double> t) {
				((Product) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setUnitPrice(t.getNewValue());
			}
		});

		deleteButtonColumn.setPrefWidth(100);
		deleteButtonColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		deleteButtonColumn.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button deleteButton = new Button("Delete");

			@Override
			protected void updateItem(Product product, boolean item) {
				super.updateItem(product, item);
				if (product == null) {
					setGraphic(null);
					return;
				}

				setGraphic(deleteButton);
				deleteButton.setOnAction(event -> getTableView().getItems().remove(product));
			}
		});

		previewButtonColumn.setPrefWidth(75);
		previewButtonColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		previewButtonColumn.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button previewButton = new Button("View");

			@Override
			protected void updateItem(Product product, boolean item) {
				super.updateItem(product, item);
				if (product == null) {
					setGraphic(null);
					return;
				}

				setGraphic(previewButton);
				previewButton.setOnAction(event -> {
					Desktop desktop = Desktop.getDesktop();
					File file = new File(product.getFilePath());
					try {
						desktop.open(file);
					} catch (IOException e) {
						DialogBuilder.showErrorgDialog("Image preview", null, "Error opening image: " + e.getMessage());
						e.printStackTrace();
					}
				});
			}
		});

		productsTableView.setEditable(true);
		productsTableView.setItems(products);
		productsTableView.getColumns().addAll(productNameColumn, productQuantityColumn, productUnitPriceColumn,
				previewButtonColumn, deleteButtonColumn);

		addProductButton.setPrefWidth(150);
		addProductButton.setOnAction((ActionEvent e) -> {
			handleAddButtonClick();
		});

		addImageButton.setPrefWidth(200);
		addImageButton.setOnAction((ActionEvent e) -> {
			handleImageButtonClick(primaryStage);
		});

		// Creating the menus
		menuBar.setPrefWidth(200);
		menuBar.getMenus().add(fileMenu);
		fileMenu.getItems().addAll(saveAsMenuItem, loadMenuItem);
		// Adding action for clicking on the save as menu
		saveAsMenuItem.setOnAction((ActionEvent e) -> {
			if (isProductListEmpty()) {
				DialogBuilder.showErrorgDialog("Save product list", null,
						"Product list must contain at least 1 (one) item");
				return;
			} else {
				showFileChooser(FileOption.SAVE, primaryStage);
			}
		});
		// Adding action for clicking on the load menu
		loadMenuItem.setOnAction((ActionEvent e) -> {
			ButtonType option = DialogBuilder.showSimpleConfirmDialog("Load from file", null,
					"Load data from file? All current data will be lost.");
			if (option == ButtonType.OK) {
				showFileChooser(FileOption.LOAD, primaryStage);
			}
		});

		VBox vbox1 = new VBox();
		vbox1.setSpacing(10);
		vbox1.setPadding(new Insets(5));
		vbox1.getChildren().addAll(inputGridPane, addProductButton, productsTableView);

		BorderPane mainPane = new BorderPane();
		mainPane.setTop(menuBar);
		mainPane.setCenter(vbox1);

		Scene scene = new Scene(mainPane);

		primaryStage.setTitle("Java XML Invoice");
		primaryStage.setWidth(SCREEN_WIDTH);
		primaryStage.setHeight(SCREEN_HEIGHT);
		primaryStage.setMaximized(false);
		primaryStage.resizableProperty().setValue(Boolean.FALSE);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Starts the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Check for invalid input data
	 * 
	 * @return String
	 */
	private String isInputValid() {
		StringBuilder builder = new StringBuilder();
		if (productNameTextField.getText().length() == 0) {
			builder.append("Product name cannot be empty");
		}
		if (quantityTextField.getText().length() == 0) {
			builder.append("\nQuantity cannot be empty");
		}
		if (unitPriceTextField.getText().length() == 0) {
			builder.append("\nUnit price cannot be empty");
		}

		return builder.toString();
	}

	/**
	 * Check if the product list is empty
	 * 
	 * @return true if the list is empty and false if the list is not.
	 */
	private boolean isProductListEmpty() {
		return products.isEmpty() ? true : false;
	}

	/**
	 * Handles the event click on the add button. This method capture the values
	 * from the input textfields and add to the products ObservableList.
	 * 
	 * After adding the values to the list the inputs textfields are removed from
	 * screen.
	 */
	private void handleAddButtonClick() {
		String errorMessage = isInputValid();
		if (errorMessage.length() > 0) {
			DialogBuilder.showErrorgDialog("Invalid input", null, errorMessage);
			return;
		}
		String name = productNameTextField.getText().toString();
		Integer quantiy = Integer.valueOf(quantityTextField.getText().toString());
		Double price = Double.valueOf(unitPriceTextField.getText().toString());
		String filePath = (!imageLabel.getText().equals("No image selected...")) ? imageLabel.getText() : null;
		products.add(new Product(name, quantiy, price, filePath));
		clearInputFields();
	}
	
	/**
	 * Handles action for clicking the button "Add Image..."
	 * When the user clicks this button, a File Chooser window will pop-up.
	 * The user can select a picture and then the {@link Label imageLabel} will show 
	 * the file name for the user. 
	 * 
	 * @param primaryStage
	 */
	private void handleImageButtonClick(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();
		List<FileChooser.ExtensionFilter> filters = Arrays.asList(new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"));
		configureFileChooser(fileChooser, "Picture selection...", filters);
		File file = fileChooser.showOpenDialog(primaryStage);
		if (file != null) {
			imageLabel.setText(file.getAbsolutePath());
		}
	}

	/**
	 * Clear all the input fields
	 */
	private void clearInputFields() {
		productNameTextField.clear();
		quantityTextField.clear();
		unitPriceTextField.clear();
		imageLabel.setText("No image selected...");
	}

	/**
	 * Displays the file chooser to the user. This method is used when the user
	 * wants to save a file or to load a file
	 * 
	 * @param option FileOption
	 * @param stage  Stage
	 */
	private void showFileChooser(FileOption option, Stage stage) {
		FileChooser fileChooser = new FileChooser();
		List<FileChooser.ExtensionFilter> filters = Arrays.asList(new FileChooser.ExtensionFilter("XML", "*.xml"));
		configureFileChooser(fileChooser, "Save file", filters);
		switch (option) {
		case SAVE:
			saveFile(stage, fileChooser);
			break;
		case LOAD:
			loadFile(stage, fileChooser);
			break;
		default:
			DialogBuilder.showErrorgDialog("File menu", null, "Invalid option");
			break;
		}
	}

	/**
	 * Configures the file chooser
	 * 
	 * @param fileChooser FileChooser
	 * @param title       String
	 * @param filters     List<FileChooser.ExtensionFilter>
	 */
	private void configureFileChooser(FileChooser fileChooser, String title,
			List<FileChooser.ExtensionFilter> filters) {
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(filters);
	}

	/**
	 * Loads the data from file
	 * 
	 * @param stage       Stage
	 * @param fileChooser FileChooser
	 */
	private void loadFile(Stage stage, FileChooser fileChooser) {
		File file = fileChooser.showOpenDialog(stage);
		if (file != null) {
			try {
				List<Product> list = XmlFileManager.loadFromXmlFile(file);
				products.clear();
				products.addAll(list);
			} catch (JAXBException e) {
				DialogBuilder.showErrorgDialog("Load file", null, "Error loading file: " + e.getMessage());
			}
		}
	}

	/**
	 * Saves the file
	 * 
	 * @param stage       Stage
	 * @param fileChooser FileChooser
	 */
	private void saveFile(Stage stage, FileChooser fileChooser) {
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			// First, save the images to the internal files
			products.forEach(product -> {
				File source = new File(product.getFilePath());
				File destination = new File("pictures/", source.getName());
				if(!destination.exists()) {
					DirectoryChooser dirChooser = new DirectoryChooser();
					dirChooser.setTitle("Select directory to save image files...");
					dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
					destination = dirChooser.showDialog(stage);
					destination = new File(destination.getPath(), source.getName());
				}
				try {
					Files.copy(source.toPath(), destination.toPath());
					product.setFilePath(destination.getAbsolutePath());
				} catch (IOException e) {
					DialogBuilder.showErrorgDialog("Saving pictures", null, "Error while saving pictures: " + e.getMessage());
					e.printStackTrace();
				}
			});
			ProductListWrapper wrapper = new ProductListWrapper();
			wrapper.setProducts(products);
			try {
				XmlFileManager.saveToXmlFile(wrapper, file);
				DialogBuilder.showInformationDialog("Save file", null, "File saved");
			} catch (JAXBException e) {
				DialogBuilder.showErrorgDialog("Save file", null, "Error saving file: " + e.getMessage());
			}
		}
	}
}
