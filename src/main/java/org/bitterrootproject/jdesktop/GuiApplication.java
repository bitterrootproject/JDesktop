package org.bitterrootproject.jdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.bitterrootproject.jdesktop.gui.InventoryManagerController;

import java.io.IOException;

@Slf4j
public class GuiApplication extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		System.setProperty("apple.awt.application.name", "Bitterroot JDesktop");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Bitterroot JDesktop");
		
		AlertHandler globalExceptionHandler = new AlertHandler();
		Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);
		
		primaryStage.setTitle("Bitterroot Project JDesktop");
		
		FXMLLoader fxmlLoader = new FXMLLoader();
		
		fxmlLoader.setLocation(InventoryManagerController.RESOURCE);
		Parent parent = fxmlLoader.load();
		
		Scene scene = new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		log.info("Loaded scene: InventoryManagerView");
		
		primaryStage.show();
	}
}


@Slf4j
class AlertHandler implements Thread.UncaughtExceptionHandler {
	public void uncaughtException(Thread thread, Throwable e) {
		GuiUtils.displayJavaExceptionAlert(e);
	}
}
