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
 * Clase para manejar los archivos xml
 * @author AitorArias
 *
 */
public class XmlFileManager {
	
	/**
	 * Guardar la lista de productos almacenadas en el objecto wrapper a un archivo especifico
	 * 
	 * @param wrapper ProductListWrapper
	 * @param file File
	 * @throws JAXBException
	 */
	public static void saveToXmlFile(ProductListWrapper wrapper, File file) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ProductListWrapper.class);
		// La clase Marshaller es responsable de gobernar el proceso de serialización de los árboles de contenido Java en datos XML. 
		// Proporciona los métodos básicos de clasificación:
		Marshaller marshaller = context.createMarshaller();
		// Creamos el Marshaller
		// Referencias: https://docs.oracle.com/javase/7/docs/api/javax/xml/bind/Marshaller.html
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(wrapper, file);
	}
	
	/**
	 * Carga la lista de productos desde un .xml
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
