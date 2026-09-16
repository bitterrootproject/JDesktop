package org.bitterrootproject.jdesktop.utils;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;


/**
 * This class provides helpful wrappers around {@link System#getenv(String)} to get environment variables, providing
 * null-checking, optional defaults to fall back to, and safe type casting for Strings, booleans, integers, floats,
 * and Paths. It's inspired by how interacting with environment variables works in Python with
 * {@code os.environ.get()} and the 3rd-party <a href="https://pypi.org/project/environs/">environs</a> package.
 */
@Log4j2
public class EnvTools {
	/**
	 * Get a string environment variable.
	 *
	 * @param variableName
	 * @return The variable's value (as a String) if set, or null if unset
	 */
	@Nullable
	public static String getString(String variableName) {
		return System.getenv(variableName);
	}
	
	/**
	 * Get a string environment variable, with a specified default if the variable is unset.
	 *
	 * @param variableName
	 * @param defaultValue
	 * @return The variable's value (as a String) if set, or the given default value if not
	 */
	@NotNull
	public static String getString(String variableName, String defaultValue) {
		@Nullable String value = getString(variableName);
		
		return (value != null)
				? value
				: defaultValue;
	}
	
	
	/**
	 * Get a boolean environment variable.
	 *
	 * @param variableName
	 * @return The variable's value (as a Boolean) if set, or null if unset
	 */
	@Nullable
	static Boolean getBoolean(String variableName) {
		@Nullable String rawValue = System.getenv(variableName);
		
		String[] truths = { "t", "true", "y", "yes", "1" };
		String[] falses = { "f", "false", "n", "no", "0" };
		
		if (rawValue == null) {
			return null;
		} else if (Arrays.stream(truths).anyMatch(rawValue::startsWith)) {
			return true;
		} else if (Arrays.stream(falses).anyMatch(rawValue::startsWith)) {
			return false;
		} else {
			return null;
		}
	}
	
	
	/**
	 * Get a boolean environment variable, with a specified default if the variable is unset.
	 * @param variableName
	 * @param defaultValue
	 * @return The variable's value (as a boolean) if set, or the given default value if not.
	 */
	public static boolean getBoolean(String variableName, boolean defaultValue) {
		@Nullable Boolean value = getBoolean(variableName);
		
		return (value != null) ? value : defaultValue;
	}
	
	
	/**
	 * Get an integer environment variable, throwing an exception if the variable's value isn't an integer, or
	 * returning null if it isn't set.
	 * @param variableName
	 * @return The parsed integer if is set and is an int, or null if unset
	 * @throws NumberFormatException The value's format isn't an integer or cannot be parsed as one
	 */
	@Nullable
	public static Integer getInteger(String variableName) throws NumberFormatException {
		@Nullable String rawValue = System.getenv(variableName);
		
		return (rawValue != null) ? Integer.valueOf(rawValue) : null;
	}
	
	/**
	 * Get an integer environment variable, with a specified default if the variable is unset or cannot
	 * be parsed as an integer.
	 * @param variableName
	 * @param defaultValue
	 * @return The parsed integer if set (and can be parsed as an int), or the given default if otherwise
	 */
	public static int getInteger(String variableName, int defaultValue) {
		try {
			Integer value = getInteger(variableName);
			
			return (value != null) ? value : defaultValue;
		} catch (NumberFormatException e) {
			log.info("Env var {} is not an integer, using given default {}", variableName, defaultValue);
			return defaultValue;
		}
	}
	
	
	/**
	 * Get a floating point environment variable, throwing an exception if the variable's value isn't a float, or
	 * returning null if it isn't set.
	 * @param variableName
	 * @return The parsed floating point number if it is set and is a float, or null if unset
	 * @throws NumberFormatException The value's format isn't a float or cannot be parsed as one
	 */
	@Nullable
	public static Float getFloat(String variableName) throws NumberFormatException {
		@Nullable String rawValue = System.getenv(variableName);
		
		return (rawValue != null) ? Float.valueOf(variableName) : null;
	}
	
	/**
	 * Get a floating point environment variable, with a specified default if the variable is unset or cannot
	 * be parsed as a float.
	 * @param variableName
	 * @param defaultValue
	 * @return The parsed float if set (and can be parsed as a float), or the given default otherwise
	 */
	public static float getFloat(String variableName, float defaultValue) {
		try {
			@Nullable Float value = getFloat(variableName);
			return (value != null) ? value : defaultValue;
		} catch (NumberFormatException e) {
			log.info("Env var {} is not a float, using given default {}", variableName, defaultValue);
			return defaultValue;
		}
	}
	
	
	/**
	 * Get a Path environment variable, throwing an exception if the variable's value isn't a path, or returning a
	 * null if it isn't set.
	 * @param variableName
	 * @return The parsed path if it is set and is a Path, or null if unset
	 * @throws InvalidPathException The variable's value is not a path or cannot be parsed as one
	 */
	@Nullable
	public static Path getPath(String variableName) throws InvalidPathException {
		@Nullable String rawValue = System.getenv(variableName);
		
		return (rawValue != null) ? Path.of(rawValue) : null;
	}
	
	/**
	 * Get a Path environment variable, with a specified default Path if the variable is unset or cannot be
	 * parsed as a {@link Path}.
	 * @param variableName
	 * @param defaultValue Default path (as a {@link Path} object) to use if the retrieved value is invalid
	 * @return The parsed path if it is set (and can be parsed as a Path), or the given default otherwise
	 */
	@NotNull
	public static Path getPath(String variableName, Path defaultValue) {
		try {
			@Nullable Path value = getPath(variableName);
			return (value != null) ? value : defaultValue;
		} catch (InvalidPathException e) {
			log.info("Env var {} is not a path, using given default {}", variableName, defaultValue);
			return defaultValue;
		}
	}
	
	/**
	 * Get a Path environment variable, with a specified default Path if the variable is unset or cannot be
	 * 	 * parsed as a {@link Path}.
	 * @param variableName
	 * @param defaultValue Default path (as a String) to use if the retrieved value is invalid
	 * @return The parsed path if it is set (and can be parsed as a Path), or the given default (as a Path) otherwise
	 * @throws InvalidPathException The given default cannot be parsed as a path.
	 */
	@NotNull
	public static Path getPath(String variableName, String defaultValue) throws InvalidPathException {
		Path parsedDefaultPath = Path.of(defaultValue);
		
		return getPath(variableName, parsedDefaultPath);
	}
}
