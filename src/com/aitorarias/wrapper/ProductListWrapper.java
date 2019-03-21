package com.aitorarias.wrapper;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aitorarias.entity.Product;

/**
 * Wrapper class to persist data in xml file.
 * 
 * @author AitorArias
 *
 */
 // Referencias en: https://docs.oracle.com/javase/tutorial/collections/implementations/wrapper.html

// Cuando una clase de nivel superior o un tipo enum se anota con la anotación @XmlRootElement, 
// su valor se representa como elemento XML en un documento XML.
@XmlRootElement(name = "products")
public class ProductListWrapper {
	// traigo Product
	private List<Product> products;
	// ¿qué devuelve en el XML?
	@XmlElement(name = "product")
	public List<Product> getProducts(){
		return products;
	}
	// establece los productos
	public void setProducts(List<Product> products) {
		//llama a products
		this.products = products;
	}
}
