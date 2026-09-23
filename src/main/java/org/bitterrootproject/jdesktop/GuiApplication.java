package org.bitterrootproject.jdesktop;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitterrootproject.jdesktop.gui.InventoryManagerController;
import org.bitterrootproject.jdesktop.utils.GuiTools;

@Log4j2
public class GuiApplication extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		Logger log = LogManager.getLogger();
		
		GuiAlertHandler globalExceptionHandler = new GuiAlertHandler();
		Thread.setDefaultUncaughtExceptionHandler(globalExceptionHandler);
		
		try {
			primaryStage.setTitle("Bitterroot Project JDesktop");
			
			FXMLLoader fxmlLoader = new FXMLLoader();
			
			fxmlLoader.setLocation(InventoryManagerController.RESOURCE);
			Parent parent = fxmlLoader.load();
			
			Scene scene = new Scene(parent);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			log.info("Loaded scene: InventoryManagerView");
			
			primaryStage.show();
		} catch (Exception e) {
			log.error("Failed to start the GUI", e);
			e.printStackTrace();
			// GuiTools.displayJavaExceptionAlert("Failed to start the GUI", e);
			Platform.exit();
		}
	}
	
}

@Log4j2
class GuiAlertHandler implements Thread.UncaughtExceptionHandler {
	public void uncaughtException(Thread thread, Throwable e) {
		try {
			log.error(e);
			GuiTools.displayJavaExceptionAlert(e);
		} catch (Exception exception) {
			log.error("Failed to display JavaFX exception alert", exception);
		}
	}
}


