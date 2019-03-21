package com.aitorarias.entity;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class to store data from a product
 * 
 * @author AitorArias
 *
 */
public class Product {

	private final StringProperty name; // name of the product
	private final IntegerProperty quantity; // quantity of the product
	private final DoubleProperty unitPrice; // unit price of the product
	private final StringProperty filePath; // path image file

	/**
	 * Default constructor
	 */
	public Product() {
		this(null, 0, 0.0, null);
	}

	/**
	 * Constructor using fields
	 * 
	 * @param name      String
	 * @param quantity  Integer
	 * @param unitPrice Double
	 * @param filePath  String
	 */
	public Product(String name, int quantity, double unitPrice, String filePath) {
		this.name = new SimpleStringProperty(name);
		this.quantity = new SimpleIntegerProperty(quantity);
		this.unitPrice = new SimpleDoubleProperty(unitPrice);
		this.filePath = new SimpleStringProperty(filePath);
	}

	// Getters & Setters

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public StringProperty getNameProperty() {
		return name;
	}

	public int getQuantity() {
		return quantity.get();
	}

	public void setQuantity(int quantity) {
		this.quantity.set(quantity);
	}

	public IntegerProperty getQuantityProperty() {
		return quantity;
	}

	public double getUnitPrice() {
		return unitPrice.get();
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice.set(unitPrice);
	}

	public DoubleProperty getUnitPriceProperty() {
		return unitPrice;
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

	@Override
	public String toString() {
		return "Product [name=" + name.get() + ", quantity=" + quantity.get() + ", unitPrice=" + unitPrice.get()
				+ ", filePath=" + filePath.get() + "]";
	}
}
