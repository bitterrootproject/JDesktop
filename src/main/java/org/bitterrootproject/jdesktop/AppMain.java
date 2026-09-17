package org.bitterrootproject.jdesktop;

import javafx.application.Application;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;


/**
 * This class is the main entrypoint for JDesktop. It may contain lots of duplicated code, as it has to set the Log4j
 * up before anything calls it, so it can't use any existing code which logs anything.
 */
@SuppressWarnings({ "DuplicatedCode", "JavaPrintToLogpoint" })
public class AppMain {
	public static void main(String[] args) {
		System.setProperty("apple.awt.application.name", "Bitterroot JDesktop");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Bitterroot JDesktop");
		
		AppDirs appDirs = AppDirsFactory.getInstance();
		
		// Set the right directory for all logs to be stored in.
		// String logLocation;
		if (isRuntimeContextDev()) {
			String logLocation = "dev-data";
			System.setProperty("log.location", logLocation);
			System.out.printf("Set log file path: %s\n", logLocation);
		} else {
			@org.jetbrains.annotations.NotNull String logLocation = appDirs.getUserLogDir(
				"JDesktop",
				null,
				"Bitterroot Project"
			);
			System.setProperty("log.location", logLocation);
			System.out.printf("Set log file path: %s\n", logLocation);
		}
		
		// Launch the application
		System.out.println("Starting application...");
		Application.launch(GuiApplication.class, args);
		System.out.println("Application started.");
	}
	
	
	private static boolean isRuntimeContextDev() {
		@Nullable String rawValue = System.getenv("DEV");
		
		if (rawValue == null) {
			return false;
			
		} else {
			String[] truths = {"t", "true", "y", "yes", "1"};
			return Arrays.stream(truths).anyMatch(
					rawValue.toLowerCase(Locale.ROOT)::startsWith
			);
		}
	}
}
