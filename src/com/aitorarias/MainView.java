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
 * Clase principal para la vista de la app. 
 * 
 * Esta clase es la responsable de enseñar la interfaz grafica (UI) y manejar sus eventos.
 * 
 * @author AitorArias
 *
 */

// Hereda todas las props de Application el MainView
public class MainView extends Application {

	// Dimensiones de la pantalla
	private static final int SCREEN_WIDTH = 600;
	private static final int SCREEN_HEIGHT = 500;
	// Invocamos tambien a Product.java
	// Innacesibles desde otros partes del proyecto. ¿Qué elementos tiene la pantalla? Elementos importados de JavaFX: 
	private TextField productNombreTextField = new TextField();
	private TextField cantidadTextField = new TextField();
	private TextField precioTextField = new TextField();
	private Button addProductButton = new Button("Añadir");
	private Button addImageButton = new Button("Añadir imagen...");
	private Label imageLabel = new Label("No se ha seleccionado imagen...");
	private TableView<Product> productsTableView = new TableView<>();
	private TableColumn<Product, String> productNombreColumn = new TableColumn<>("Nombre");
	private TableColumn<Product, Integer> productCantidadColumn = new TableColumn<>("Cantidad");
	private TableColumn<Product, Double> productPrecioColumn = new TableColumn<>("Precio");
	private TableColumn<Product, Product> deleteButtonColumn = new TableColumn<>("Eliminar");
	private TableColumn<Product, Product> previewButtonColumn = new TableColumn<>("Ver imagen");
	private MenuBar menuBar = new MenuBar();
	private Menu fileMenu = new Menu("Opciones");
	private MenuItem saveAsMenuItem = new MenuItem("Guardar como...");
	private MenuItem loadMenuItem = new MenuItem("Importar...");

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
		cantidadTextField.setTextFormatter(new TextFormatter<>(filter));
		cantidadTextField.setMaxWidth(Double.MAX_VALUE);

		// los precios sólo pueden ser numéricos
		precioTextField.setTextFormatter(new TextFormatter<>(filter));
		precioTextField.setMaxWidth(Double.MAX_VALUE);

		productNombreTextField.setMaxWidth(Double.MAX_VALUE);
		// Eclipse diseño
		ColumnConstraints labelColumnConstraint = new ColumnConstraints();
		labelColumnConstraint.setPercentWidth(30);

		ColumnConstraints textFielColumnConstraint = new ColumnConstraints();
		textFielColumnConstraint.setPercentWidth(70);
		// GridPane importado desde JavaFX: https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/GridPane.html
		GridPane inputGridPane = new GridPane();
		inputGridPane.setPadding(new Insets(10));
		inputGridPane.setVgap(5);
		inputGridPane.setHgap(10);
		inputGridPane.getColumnConstraints().addAll(labelColumnConstraint, textFielColumnConstraint);
		// Capa donde metemos el nombre del producto. 
		inputGridPane.add(new Label("Nombre del producto:"), 0, 0);
		inputGridPane.add(productNombreTextField, 1, 0);
		// Capa donde metemos la cantidad que deseamos
		inputGridPane.add(new Label("Cantidad: "), 0, 1);
		inputGridPane.add(cantidadTextField, 1, 1);
		// Capa donde metemos el precio unitario
		inputGridPane.add(new Label("Precio: "), 0, 2);
		inputGridPane.add(precioTextField, 1, 2);
		inputGridPane.add(addImageButton, 0, 3);
		inputGridPane.add(imageLabel, 1, 3);

		// Creamos las tablas y sus columnas
		productNombreColumn.setPrefWidth(250);
		productNombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
		productNombreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		productNombreColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product, String>>() {
			@Override
			public void handle(CellEditEvent<Product, String> t) {
				((Product) t.getTableView().getItems().get(t.getTablePosition().getRow())).setNombre(t.getNewValue());
			}
		});

		productCantidadColumn.setPrefWidth(100);
		// Tipos de  parametros en JavaFX:
		// 	S - Tabla genérica (i.e. S == TableView<S>)
		// 	T - El tipo de contenido en todas las celdas es TableColum
		productCantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
		productCantidadColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		productCantidadColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product, Integer>>() {
			@Override
			public void handle(CellEditEvent<Product, Integer> t) {
				// conseguimos el TableView, los Items, la posicion de la Tabla, columnna, cantidad, y nuevo valor
				// TODO ELLO VIENE DE PRODUCT.JAVA
				((Product) t.getTableView().getItems().get(t.getTablePosition().getRow())).setCantidad(t.getNewValue());

			}
		});

		productPrecioColumn.setPrefWidth(75);
		productPrecioColumn.setCellValueFactory(new PropertyValueFactory<>("Precio"));
		productPrecioColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
		productPrecioColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Product, Double>>() {
			@Override
			public void handle(CellEditEvent<Product, Double> t) {
				((Product) t.getTableView().getItems().get(t.getTablePosition().getRow()))
						.setPrecio(t.getNewValue());
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
			private final Button previewButton = new Button("Ver Imagen");

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
		productsTableView.getColumns().addAll(productNombreColumn, productCantidadColumn, productPrecioColumn,
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
		// VBox propio de JavaFX
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
		if (productNombreTextField.getText().length() == 0) {
			builder.append("El nombre no puede estar vacío");
		}
		if (cantidadTextField.getText().length() == 0) {
			builder.append("\nLa cantidad no puede estar vacía");
		}
		if (precioTextField.getText().length() == 0) {
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
		String nombre = productNombreTextField.getText().toString();
		// Obligación del Int
		Integer cantidad = Integer.valueOf(cantidadTextField.getText().toString());
		// Obligacion del Double
		Double precio = Double.valueOf(precioTextField.getText().toString());
		String filePath = (!imageLabel.getText().equals("No se ha seleccionado imagen")) ? imageLabel.getText() : null;
		// Se añaden los productos
		products.add(new Product(nombre, cantidad, precio, filePath));
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
		productNombreTextField.clear();
		cantidadTextField.clear();
		precioTextField.clear();
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
		// OJO! AQUI ES DONDE PUEDE HABER PROBLEMAS WINDOWS VS UBUNTU. ¡¡¡PROXIMA ACTUALIZACIÓN!!!
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
		// posible error, por lo tanto lanzo un try y un catch:
		// si no existe archivo, entonces
		if (file != null) {
			try {
				// Importado
				List<Product> list = XmlFileManager.loadFromXmlFile(file);
				products.clear();
				products.addAll(list);
			} catch (JAXBException e) {
				// muestrame el error con una ventana emergente
				DialogBuilder.showErrorgDialog("Cargando archivo", null, "Error al cargar el archivo: " + e.getMessage());
			}
		}
	}

	/**
	 * Guardando el archivo
	 * 
	 * @param stage       Stage
	 * @param fileChooser FileChooser
	 */

	// Este evento se hace en dos etapas
	// Una vez guardamos el archivo, posteriormente nos indica el sitio donde queremos guardar las imagenes. 
	// Esto se puede mejorar, pero no supe hacerlo de otra manera que seguro existe
	// En nuevas actualizaciones se intentará optimizar dichas funciones 
	private void saveFile(Stage stage, FileChooser fileChooser) {
		// https://www.programcreek.com/java-api-examples/?class=javafx.stage.FileChooser&method=showSaveDialog
		// Método que utilice para llamar a la imagen
		File file = fileChooser.showSaveDialog(stage);
		if (file != null) {
			//Primero, guardamos las imagenes de manera interna
			// Para cada producto
			products.forEach(product -> {
				// Origen
				File source = new File(product.getFilePath());
				// Destino
				// Pictures es como tengo yo definidio mi path en Ubuntu para que fuese correctamente. En Windows imagenes o images
				File destination = new File("pictures/", source.getNombre());
				if(!destination.exists()) {
					// https://docs.oracle.com/javase/8/javafx/api/javafx/stage/DirectoryChooser.html desde Java FX
					DirectoryChooser dirChooser = new DirectoryChooser();
					dirChooser.setTitle("Selecciona un directorio para guardar las imagenes...");
					dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
					destination = dirChooser.showDialog(stage);
					destination = new File(destination.getPath(), source.getNombre());
				}
				// try y catch para checkear que todo está correcto
				try {
					Files.copy(source.toPath(), destination.toPath());
					product.setFilePath(destination.getAbsolutePath());
				} catch (IOException e) {
					DialogBuilder.showErrorgDialog("Guardando las imagenes", null, "Error mientras guardamos la imagen: " + e.getMessage());
					e.printStackTrace();
				}
			});
			// Disponible en nuestra carpeta wrapper > ProductListWrapper.java. Nuevo Objeto ProductListWrapper
			ProductListWrapper wrapper = new ProductListWrapper();
			// coge los prodcutos
			wrapper.setProducts(products);
			try {
				//XmlFileMager disponible en nuestra carpeta util > XmlFileManager
				XmlFileManager.saveToXmlFile(wrapper, file);
				DialogBuilder.showInformationDialog("Guarda los archivos", null, "Archivo guardado");
			} catch (JAXBException e) {
				// lanza error
				DialogBuilder.showErrorgDialog("Guarda el archivo", null, "Error al guardar: " + e.getMessage());
			}
		}
	}
}
