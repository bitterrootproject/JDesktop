package org.bitterrootproject.jdesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.bitterrootproject.jdesktop.gui.CallNumberTableController;

import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class GuiApplication extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		System.setProperty("apple.awt.application.name", "Bitterroot JDesktop");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Bitterroot JDesktop");
		// var appDataDir = FileManager.getAppDataDirectory();
		// if (!appDataDir.toFile().exists()) {
		// 	try {
		// 		Files.createDirectories(appDataDir);
		// 	} catch (IOException e) {
		// 		log.error("Failed to create application data directory", e);
		// 		System.exit(1); return;
		// 	}
		// }
		// System.setProperty("org.slf4j.simpleLogger.logFile", appDataDir.resolve("JDesktop.log").toString());
		
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(CallNumberTableController.RESOURCE);
		Parent loaded = fxmlLoader.load();
		
		Scene scene = new Scene(loaded, 1000, 800);
		primaryStage.setTitle("Bitterroot Project JDesktop");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
