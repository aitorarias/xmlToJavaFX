package com.aitorarias.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

/**
 * Clase que controla TODAS las ventanas de diálogo
 * 
 * @author AitorArias
 *
 */
public class DialogBuilder {

	/**
	 * Enseña un Dialogo de información. Tres parámetros como String
	 * 
	 * @param title String
	 * @param header String
	 * @param content String
	 */
	public static void showInformationDialog(String title, String header, String content) {
		// lanza la alerta
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
	}
	
	/**
	 * Enseña un dialogo de alerta
	 * 
	 * @param title String
	 * @param header String
	 * @param content String
	 */
	public static void showWarningDialog(String title, String header, String content) {

		Alert alert = new Alert(AlertType.WARNING);

		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
	}
	
	/**
	 * Enseña un Dialogo de error
	 * 
	 * @param title String
	 * @param header String
	 * @param content String
	 */
	public static void showErrorgDialog(String title, String header, String content) {

		Alert alert = new Alert(AlertType.ERROR);

		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
	}
	
	/**
	 * 
	 * Enseña un simple dialogo de confirmaacion al usuario. El usuario puede elegir entre confirmar y cancelar
	 * 
	 * @param title String
	 * @param header String
	 * @param content String
	 * @return ButtonType
	 */
	public static ButtonType showSimpleConfirmDialog(String title, String header, String content) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		
		Optional<ButtonType> result = alert.showAndWait();
		
		return result.get();
	}
	
	/**
	 * Text input de dialogo al usuario
	 * 
	 * @param title String
	 * @param header String
	 * @param content String
	 * @return Optional<String>
	 */

	 // Optional: https://www.adictosaltrabajo.com/2015/03/02/optional-java-8/
	public static Optional<String> showTextInputDialog(String title, String header, String content){
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.setTitle(title);
		inputDialog.setHeaderText(header);
		inputDialog.setContentText(content);
		
		Optional<String> result = inputDialog.showAndWait();
		// retorna resultado final 
		return result;
	}
}
