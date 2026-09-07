package org.bitterrootproject.jdesktop;

import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileManager {
	// public final String macOSDataDirectory = "/Li"
	
	public static Path getAppDataDirectory() throws RuntimeException {
		Path home = SystemUtils.getUserHome().toPath();
		if (SystemUtils.IS_OS_MAC_OSX) {
			return home.resolve("Library/Application Support/JDesktop");
		} else if (SystemUtils.IS_OS_LINUX) {
			String xdg_data_home = System.getenv("XDG_DATA_HOME");
			// if (xdg_data_home == null) xdg_data_home = ".local/share"
			// return home + xdg_data_home +
			
			if (xdg_data_home == null) {
				return home.resolve(".local/share/JDesktop");
			} else {
				return Path.of(xdg_data_home).resolve("JDesktop");
			}
		} else if (SystemUtils.IS_OS_WINDOWS) {
			String AppData = System.getenv("APPDATA");
			if (AppData == null) {
				throw new NullPointerException("%appdata% variable cannot be null.");
			} else {
				return Path.of(AppData).resolve("JDesktop");
			}
		} else {
			throw new RuntimeException("Unknown operating system");
		}
	}
}
