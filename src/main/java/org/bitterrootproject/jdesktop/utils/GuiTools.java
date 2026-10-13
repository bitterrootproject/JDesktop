package org.bitterrootproject.jdesktop.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;


@Log4j2
public class GuiTools {
	/**
	 * Create and display a new alert designed to display and handle exceptions, caught or otherwise, with a custom
	 * message string.
	 * @param message Some message text.
	 * @param exception The throwable exception to display to the user.
	 */
	public static void displayJavaExceptionAlert(@Nullable String message, Throwable exception) {
		log.info("Caught exception, displaying as JavaFX Alert", exception);
		
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> displayJavaExceptionAlert(message, exception));
			return;
		}
		
		displayJavaExceptionAlertOnFxThread(message, exception);
	}
	
	private static void displayJavaExceptionAlertOnFxThread(@Nullable String message, Throwable exception) {
		
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("Exception caught");
		alert.setHeaderText("Caught exception: " + exception.getClass().getName());
		alert.setContentText(message != null ? message : "An exception occurred");
		
		// Get the full exception stack trace
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		exception.printStackTrace(printWriter);
		
		String exceptionText = stringWriter.toString();
		
		// Create expandable Exception element
		Label label = new Label("Stack trace");
		
		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);
		
		GridPane expandableContent = new GridPane();
		expandableContent.setMaxWidth(Double.MAX_VALUE);
		expandableContent.add(label, 0, 0);
		expandableContent.add(textArea, 0, 1);
		
		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expandableContent);
		
		alert.showAndWait();
	}
	
	
	/**
	 * Create and display a new alert designed to display and handle exceptions, caught or otherwise.
	 * @param exception The throwable exception to display to the user.
	 */
	public static void displayJavaExceptionAlert(Throwable exception) {
		displayJavaExceptionAlert(null, exception);
	}
}
