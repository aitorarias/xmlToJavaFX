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

@XmlRootElement(name = "products")
public class ProductListWrapper {

	private List<Product> products;
	
	@XmlElement(name = "product")
	public List<Product> getProducts(){
		return products;
	}
	
	public void setProducts(List<Product> products) {
		this.products = products;
	}
}
