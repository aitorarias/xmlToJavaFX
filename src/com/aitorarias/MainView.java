package com.aitorarias;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.xml.bind.JAXBException;

import com.aitorarias.entity.Product;
import com.aitorarias.enums.FileOption;
import com.aitorarias.util.DialogBuilder;
import com.aitorarias.util.XmlFileManager;
import com.aitorarias.wrapper.ProductListWrapper;

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
 * Main View class of the app. Clase principal para la vista de la app. 
 * 
 * Esta clase es la responsable de enseñar la interfaz grafica (UI) y manejar sus eventos.
 * 
 * @author AitorArias
 *
 */
public class MainView extends Application {

	// Dimensiones de la pantalla
	private static final int SCREEN_WIDTH = 600;
	private static final int SCREEN_HEIGHT = 500;

	// ¿Qué elementos tiene la pantalla? Elementos importados de JavaFX: 
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
	 * Aquí empieza la aplicación
	 */
	@Override
	public void start(Stage primaryStage) {

		// Simple TextField que usa el TextFormatter que nos permite el doble valor. Sacado de aquí: 
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

		// la cantidad sólo puede permitir valores numéricos
		quantityTextField.setTextFormatter(new TextFormatter<>(filter));
		quantityTextField.setMaxWidth(Double.MAX_VALUE);

		// los precios sólo pueden ser numéricos
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
		// Capa donde metemos el nombre del producto. 
		inputGridPane.add(new Label("Nombre del producto:"), 0, 0);
		inputGridPane.add(productNameTextField, 1, 0);
		// Capa donde metemos la cantidad que deseamos
		inputGridPane.add(new Label("Cantidad: "), 0, 1);
		inputGridPane.add(quantityTextField, 1, 1);
		// Capa donde metemos el precio unitario
		inputGridPane.add(new Label("Precio: "), 0, 2);
		inputGridPane.add(unitPriceTextField, 1, 2);
		inputGridPane.add(addImageButton, 0, 3);
		inputGridPane.add(imageLabel, 1, 3);

		// Creamos las tablas y sus columnas
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
		// Interfaz gráfica para el botón de "Borrar"
		deleteButtonColumn.setPrefWidth(100);
		deleteButtonColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		deleteButtonColumn.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button deleteButton = new Button("Borrar");
			// Sobreescribe el método
			@Override
			// Función para actualizar el Item. Si no existe elemento en la tabla, la interfaz nos devuelve null
			protected void updateItem(Product product, boolean item) {
				super.updateItem(product, item);
				if (product == null) {
					setGraphic(null);
					return;
				}
			// Si existe y borramos un item, ésta tabla adquiere 0 valor
				setGraphic(deleteButton);
				// Acción de borrar: 
				deleteButton.setOnAction(event -> getTableView().getItems().remove(product));
			}
		});
		// Diseño del botón "Imagen"
		previewButtonColumn.setPrefWidth(75);
		previewButtonColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		previewButtonColumn.setCellFactory(param -> new TableCell<Product, Product>() {
			private final Button previewButton = new Button("View");

			@Override
			// Volvemos a actualizar el producto como en el deleteItem. Si no existe imagen, devuelve null
			protected void updateItem(Product product, boolean item) {
				super.updateItem(product, item);
				if (product == null) {
					setGraphic(null);
					return;
				}
			// Si existe imagen, entonces podemos verla
				setGraphic(previewButton);
				previewButton.setOnAction(event -> {
					Desktop desktop = Desktop.getDesktop();
					File file = new File(product.getFilePath());
					// lanzamos error. UBUNTU 18.04 tiene problemas para abrir, no así en mi Windows. ¡OJO!
					try {
						desktop.open(file);
					} catch (IOException e) {
						// sino, dialogo de error 
						DialogBuilder.showErrorgDialog("Previsualización imagen", null, "Errror al abrir el archivo: " + e.getMessage());
						e.printStackTrace();
					}
				});
			}
		});
		// Documentado desde: https://docs.oracle.com/javafx/2/ui_controls/table-view.htm#sthref119
		productsTableView.setEditable(true);
		productsTableView.setItems(products);
		// Añadimos TODAS las columnas
		productsTableView.getColumns().addAll(productNameColumn, productQuantityColumn, productUnitPriceColumn,
				previewButtonColumn, deleteButtonColumn);

		addProductButton.setPrefWidth(150);
		addProductButton.setOnAction((ActionEvent e) -> {
			// Manejo del botón Añadir
			handleAddButtonClick();
		});

		addImageButton.setPrefWidth(200);
		addImageButton.setOnAction((ActionEvent e) -> {
			// Manejo de la Imagen. Stage funciona perfectamente en Windows 10
			handleImageButtonClick(primaryStage);
		});

		// Creando los menús
		menuBar.setPrefWidth(200);
		menuBar.getMenus().add(fileMenu);
		fileMenu.getItems().addAll(saveAsMenuItem, loadMenuItem);
		// Le damos "vida" al boton "guardar como"
		saveAsMenuItem.setOnAction((ActionEvent e) -> {
			// NO PUEDE HABER PRODUCTO VACIO, SINO: 
			if (isProductListEmpty()) {
				DialogBuilder.showErrorgDialog("Guardando la lista", null,
						"La lista debe contener al menos un producto.");
				return;
			} else {
				// Si no está vacio, abre pantalla para guardar el path
				showFileChooser(FileOption.SAVE, primaryStage);
			}
		});
		// Adding action for clicking on the load menu. Añadimos vida al clickar el cargar lista. 
		loadMenuItem.setOnAction((ActionEvent e) -> {
			ButtonType option = DialogBuilder.showSimpleConfirmDialog("Importa desde archivo", null,
					"¿Estás seguro de importart? Todos tus datos se perderán.");
			if (option == ButtonType.OK) {
				// si clickamos en OK, volvemos al primaryStage
				showFileChooser(FileOption.LOAD, primaryStage);
			}
		});
		// VBox propio de JavaF1
		VBox vbox1 = new VBox();
		vbox1.setSpacing(10);
		vbox1.setPadding(new Insets(5));
		vbox1.getChildren().addAll(inputGridPane, addProductButton, productsTableView);

		BorderPane mainPane = new BorderPane();
		mainPane.setTop(menuBar);
		mainPane.setCenter(vbox1);
		// Clase contenedora para todo el contenido en la Scene gráfica. El background de la escena está cubierta con la propiedad fill
		Scene scene = new Scene(mainPane);
		//primaryStage ya definidos durante todo el proyecto
		primaryStage.setTitle("Java XML Invoice");
		primaryStage.setWidth(SCREEN_WIDTH);
		primaryStage.setHeight(SCREEN_HEIGHT);
		primaryStage.setMaximized(false);
		primaryStage.resizableProperty().setValue(Boolean.FALSE);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Empieza la app
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// lanzamiento
		launch(args);
	}

	/**
	 * Checkeamos los datos inválidos
	 * 
	 * @return String
	 */

	// inInputValid controla todos los posibles errores. Los tres campos han de ser cubiertos, sino, mensaje de alerta: 
	private String isInputValid() {
		StringBuilder builder = new StringBuilder();
		if (productNameTextField.getText().length() == 0) {
			builder.append("El nombre no puede estar vacío");
		}
		if (quantityTextField.getText().length() == 0) {
			builder.append("\nLa cantidad no puede estar vacía");
		}
		if (unitPriceTextField.getText().length() == 0) {
			builder.append("\nEl precio no puede estar vacío");
		}

		return builder.toString();
	}

	/**
	 * Checkeamos si la "lista de nuestra compra" está vacía
	 * 
	 * @return true si la lista esta vacia y false si no lo está.
	 */
	private boolean isProductListEmpty() {
		// condicional ternario nuevo en Java: igual que el if pero en una línea. Si ? entonces :
		return products.isEmpty() ? true : false;
	}

	/**
	 * Maneja el evento cuando clickamos el boton de añadir. Este método captura los valores desde el input
	 * y añade los productos al ObservableList. 
	 * Después de añadir los valores a la lista los inputs se borran de la pantalla. 
	 */
	private void handleAddButtonClick() {
		String errorMessage = isInputValid();
		if (errorMessage.length() > 0) {
			// Enseña el diálogo
			DialogBuilder.showErrorgDialog("Input inválido", null, errorMessage);
			return;
		}
		// Obligación del String
		String name = productNameTextField.getText().toString();
		// Obligación del Int
		Integer quantiy = Integer.valueOf(quantityTextField.getText().toString());
		// Obligacion del Double
		Double price = Double.valueOf(unitPriceTextField.getText().toString());
		String filePath = (!imageLabel.getText().equals("No se ha seleccionado imagen")) ? imageLabel.getText() : null;
		// Se añaden los productos
		products.add(new Product(name, quantiy, price, filePath));
		// se borran todos los campos
		clearInputFields();
	}
	
	/**
	 * Manejamos la acción para clickar el boton "Añadir image"
	 * Cuando clickamos en este botón, se abre ventana
	 * El usuario puede elegir una imagen y luego el link Label imageLabel
	 * El nombre del archivo para el usuario
	 * 
	 * @param primaryStage
	 */
	private void handleImageButtonClick(Stage primaryStage) {
		// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
		FileChooser fileChooser = new FileChooser();
		List<FileChooser.ExtensionFilter> filters = Arrays.asList(new FileChooser.ExtensionFilter("Todas las imagenes", "*.*"),
		// Extensiones disponibles: .JPG y .PNG
		// Documentacion para elegir extensiones: https://www.codejava.net/java-se/swing/add-file-filter-for-jfilechooser-dialog
				new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"));
		configureFileChooser(fileChooser, "Selecciona las imagenes...", filters);
		File file = fileChooser.showOpenDialog(primaryStage);
		// si el archivo no está vacio, entonces
		if (file != null) {
			imageLabel.setText(file.getAbsolutePath());
		}
	}

	/**
	 * Limpiamos todos los campos
	 */

	// Nuevamente, reseteamos todos los campos una vez completada una acción con éxito 
	private void clearInputFields() {
		productNameTextField.clear();
		quantityTextField.clear();
		unitPriceTextField.clear();
		imageLabel.setText("No existe imagen...");
	}

	/**
	 * Enseña ventana para elegir archivo al usuario. Este metodo es usado cuando el usuario
	 * quiere guardar o cargar un nuevo archivo.
	 * 
	 * @param option FileOption
	 * @param stage  Stage
	 */

	// Pasados como argumentos FileOption y Stage en la función que nos enseña archivo a elegir. 
	// Documentación aquí: https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html
	private void showFileChooser(FileOption option, Stage stage) {
		FileChooser fileChooser = new FileChooser();
		// Lista que me da a elegir con el File Chooser con su respectiva extension. En este caso solo guardamos XML.
		List<FileChooser.ExtensionFilter> filters = Arrays.asList(new FileChooser.ExtensionFilter("XML", "*.xml"));
		// Filtro queda configurado en nuestra carpeta util > XmlFilter
		configureFileChooser(fileChooser, "Guardar archivo", filters);
		// Lanzamos opciones con el switch 
		switch (option) {
		// Si es Save
		case SAVE:
			saveFile(stage, fileChooser);
			break;
		// Si es LOAD
		case LOAD:
			loadFile(stage, fileChooser);
			break;
		// Si no es ninguna de los dos, opcion por defecto y rompe el bucle
		default:
			DialogBuilder.showErrorgDialog("Menu", null, "Opcion invalida");
			break;
		}
	}

	/**
	 * Configurando el el archivo a elegir
	 * Todos los parámetros importados
	 * @param fileChooser FileChooser 
	 * @param title       String
	 * @param filters     List<FileChooser.ExtensionFilter>
	 */
	private void configureFileChooser(FileChooser fileChooser, String title,
			// Extensiones importada del utils
			List<FileChooser.ExtensionFilter> filters) {
		fileChooser.setTitle(title);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		// Añade todo
		fileChooser.getExtensionFilters().addAll(filters);
	}

	/**
	 * Carga la lista desde nuestra ventana
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
