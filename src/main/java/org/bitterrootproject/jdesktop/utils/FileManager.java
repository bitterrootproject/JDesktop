package org.bitterrootproject.jdesktop.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;


/**
 * A helper class to make getting important application directories easy.
 */
@Log4j2
public enum FileManager {
	USER_HOME,
	USER_CONFIG,
	USER_DATA,
	USER_LOG;
	
	private final Path home;
	private final AppDirs appDirs;
	private final AppStateTools.RuntimeContext context;
	
	FileManager() {
		// Set these at runtime, not compile time.
		this.home = SystemUtils.getUserHome().toPath();
		this.appDirs = AppDirsFactory.getInstance();
		this.context = AppStateTools.getRuntimeContext();
	}
	
	
	/**
	 * Get the OS-specific Path directory of the chosen enum member using net.harawata.appdirs.
	 * @return The absolute {@link Path} of the chosen enum member
	 * @see <a href="https://github.com/harawata/appdirs/tree/master">net.harawata.appdirs</a>
	 */
	@NotNull
	public Path getPath() {
		switch (context) {
			case DEV -> {
				Path projectRoot = findProjectRoot();
				switch (this) {
					case USER_HOME -> { return this.home; }
					case USER_CONFIG, USER_DATA, USER_LOG -> { return projectRoot.resolve("dev-data"); }
					
					default -> throw new RuntimeException("Somehow tried to get path for a nonexistent Directory enum member");
				}
			}
			
			case PROD -> {
				String appName = "JDesktop";
				String appAuthor = "Bitterroot Desktop";
				switch (this) {
					case USER_HOME -> { return this.home; }
					case USER_CONFIG -> { return Path.of(appDirs.getUserConfigDir(appName, null, appAuthor)); }
					case USER_DATA -> { return Path.of(appDirs.getUserDataDir(appName, null, appAuthor)); }
					case USER_LOG -> { return Path.of(appDirs.getUserLogDir(appName, null, appAuthor)); }
					
					default -> throw new RuntimeException("Somehow tried to get path for a nonexistent Directory enum member");
				}
			}
			
			default -> throw new RuntimeException("Somehow tried to get paths for a nonexistent RuntimeContext enum member");
		}
	}
	
	/**
	 * Get the String representation of the underlying {@link Path} object.
	 */
	@NotNull
	public String getString() {
		return getPath().toString();
	}
	
	
	/**
	 * {@inheritDoc}
	 * <br><br>
	 *
	 * Want to get the String representation of the underlying {@link Path} object? Use
	 * {@link FileManager#getString()} instead.
	 */
	public String toString() {
		return super.toString();
	}
	
	/**
	 * Finds the project root directory by walking up from the current working directory
	 * looking for build.gradle.kts or .git directory.
	 * @return The {@link Path} to the project root
	 * @throws RuntimeException if project root cannot be found
	 */
	@NotNull
	private static Path findProjectRoot() {
		Path currentDir = Path.of(System.getProperty("user.dir"));
		Path searchDir = currentDir;
		
		while (searchDir != null) {
			// Check for build.gradle.kts (Gradle project marker)
			if (Files.exists(searchDir.resolve("build.gradle.kts"))) {
				return searchDir;
			}
			// Check for .git directory (Git repository marker)
			if (Files.exists(searchDir.resolve(".git"))) {
				return searchDir;
			}
			searchDir = searchDir.getParent();
		}
		
		throw new RuntimeException(
			"Could not find project root. Searched from: " + currentDir +
			" (looking for build.gradle.kts or .git directory)"
		);
	}
	
	/// @see Path#resolve(String)
	@NotNull
	public Path resolve(@NotNull String other) {
		return this.getPath().resolve(other);
	}
	
	/// @see Path#resolve(Path)
	@NotNull
	public Path resolve(@NotNull Path other) {
		return this.getPath().resolve(other);
	}
	
	
	/**
	 * Create the file at the given path. The file's parent directories must exist.
	 * @param path The file to create
	 * @return {@code true} if creation successful or file already exists, {@code false} otherwise
	 */
	private static boolean createFileIfNotExists(Path path) {
		try {
			Files.createFile(path);
			return true;
		} catch (FileAlreadyExistsException e) {
			log.debug("File '{}' already exists, not creating", path);
			return true;
		} catch (IOException e) {
			log.error("Failed to create file: {}", path, e);
			return false;
		}
	}
	
	/**
	 * Create the file at the given path, optionally creating its parent dir(s) if they don't exist.
	 * @param path The file to create
	 * @param makeParentDirs Whether its parent dirs(s) will be created if they don't exist
	 * @return {@code true} if creation successful 2or file already exists, {@code false} otherwise
	 */
	public static boolean createFileIfNotExists(Path path, boolean makeParentDirs) {
		if (path.getParent().toFile().exists()) {
			return createFileIfNotExists(path);
		} else if (makeParentDirs) {
			try {
				Files.createDirectories(path.getParent());
				return createFileIfNotExists(path);
			} catch (IOException e) {
				log.error("Failed to create parent directories to the file: {}", path, e);
				return false;
			}
		} else {
			log.error("Parent dir for '{}' doesn't exist, and I'm not supposed to make it", path);
			return false;
		}
	}
}
