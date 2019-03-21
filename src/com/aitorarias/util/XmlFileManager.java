package com.aitorarias.util;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.aitorarias.entity.Product;
import com.aitorarias.wrapper.ProductListWrapper;

/**
 * Helper class to manage the xml files
 * @author AitorArias
 *
 */
public class XmlFileManager {
	
	/**
	 * Saves a list of products stored in a wrapper object to a specific file
	 * 
	 * @param wrapper ProductListWrapper
	 * @param file File
	 * @throws JAXBException
	 */
	public static void saveToXmlFile(ProductListWrapper wrapper, File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ProductListWrapper.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(wrapper, file);
	}
	
	/**
	 * Loads a list of products from a .xml file
	 * 
	 * @param file File
	 * @return List<Product>
	 * @throws JAXBException
	 */
	public static List<Product> loadFromXmlFile(File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ProductListWrapper.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		ProductListWrapper wrapper = (ProductListWrapper) unmarshaller.unmarshal(file);
		
		return wrapper.getProducts();
	}

}
