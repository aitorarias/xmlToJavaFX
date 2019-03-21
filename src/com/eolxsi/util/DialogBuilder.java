package com.eolxsi.util;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

/**
 * Helper class to display Dialogs messages to the user.
 * 
 * @author LuisDaniel
 *
 */
public class DialogBuilder {

	/**
	 * Displays a Information Dialog
	 * 
	 * @param title String
	 * @param header String
	 * @param content String
	 */
	public static void showInformationDialog(String title, String header, String content) {

		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
	}
	
	/**
	 * Displays a Warning Dialog
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
	 * Displays a Error Dialog
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
	 * Displays a simple confirm dialog to the user. The user can choose between confirm and cancel
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
	 * Display a text input dialog to the user
	 * 
	 * @param title String
	 * @param header String
	 * @param content String
	 * @return Optional<String>
	 */
	public static Optional<String> showTextInputDialog(String title, String header, String content){
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.setTitle(title);
		inputDialog.setHeaderText(header);
		inputDialog.setContentText(content);
		
		Optional<String> result = inputDialog.showAndWait();
	
		return result;
	}
}
