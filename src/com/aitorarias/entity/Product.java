package com.aitorarias.entity;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Clase para almacenar datos desde un producto. 
 * 
 * @author AitorArias
 *
 */

 // Clase principal
public class Product {

	private final StringProperty nombre; // nombre del producto
	private final IntegerProperty cantidad; // cantidad
	private final DoubleProperty precio; // precio
	private final StringProperty filePath; // path del archivo

	/**
	 * Constructor por defecto
	 */
	public Product() {
		this(null, 0, 0.0, null);
	}

	/**
	 * Constructor usando los campos
	 * 
	 * @param nombre      String
	 * @param cantidad  Integer
	 * @param precio Double
	 * @param filePath  String
	 */
	public Product(String nombre, int cantidad, double precio, String filePath) {
		this.nombre = new SimpleStringProperty(nombre);
		this.cantidad = new SimpleIntegerProperty(cantidad);
		this.precio = new SimpleDoubleProperty(precio);
		this.filePath = new SimpleStringProperty(filePath);
	}

	// Getters & Setters

	public String getName() {
		return nombre.get();
	}

	public void setName(String nombre) {
		this.nombre.set(nombre);
	}

	public StringProperty getNameProperty() {
		return nombre;
	}

	public int getCantidad() {
		return cantidad.get();
	}

	public void setCantidad(int cantidad) {
		this.cantidad.set(cantidad);
	}

	public IntegerProperty getCantidadProperty() {
		return cantidad;
	}

	public double getUnitPrice() {
		return precio.get();
	}

	public void setUnitPrice(double precio) {
		this.precio.set(precio);
	}

	public DoubleProperty getUnitPriceProperty() {
		return precio;
	}

	public String getFilePath() {
		return filePath.get();
	}

	public void setFilePath(String filePath) {
		this.filePath.set(filePath);
	}

	public StringProperty getFilePathProperty() {
		return filePath;
	}
	// Sobreescribo
	@Override
	// parseado a String
	public String toString() {
		// devuelve todo
		return "Producto [nombre=" + nombre.get() + ", cantidad=" + cantidad.get() + ", precio=" + precio.get()
				+ ", filePath=" + filePath.get() + "]";
	}
}
