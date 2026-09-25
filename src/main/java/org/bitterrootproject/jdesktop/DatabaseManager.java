package org.bitterrootproject.jdesktop;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.extern.log4j.Log4j2;
import org.bitterrootproject.jdesktop.models.*;
import org.bitterrootproject.jdesktop.utils.EnvTools;
import org.bitterrootproject.jdesktop.utils.FileManager;

import java.nio.file.Path;
import java.sql.SQLException;


/**
 * Database manager for the SQLite database used by Bitterroot JDesktop.
 */
@Log4j2
public final class DatabaseManager {
	// private static final String DATABASE_DRIVER = "org.sqlite.JDBC";
	
	/**
	 * Get the String path to the database file.
	 * @param local Store the database file in the repository. Useful for testing.
	 * @return String path to the database file.
	 */
	private static String getDatabaseUrl(boolean local) {
		if (local) {
			return "jdbc:sqlite:dev-data/db.sqlite3";
		} else {
			String prefix = "jdbc:sqlite:";
			Path path = FileManager.USER_DATA.resolve("db.sqlite3");
			FileManager.createFileIfNotExists(path, true);
			return prefix + path;
		}
	}
	
	private static DatabaseManager INSTANCE;
	
	private final JdbcConnectionSource connectionSource;
	
	public final Dao<Subject, Long> subjects;
	public final Dao<Domain, Long> domains;
	public final Dao<Root, Long> roots;
	public final Dao<Aspect, Long> aspects;
	public final Dao<Topic, Long> topics;
	public final Dao<AuthorPublisher, Long> authorPublishers;
	public final Dao<CallNumber, Long> callNumbers;
	
	
	/**
	 * Creates the tables, but only if they don't already exist.
	 */
	private void initializeTables() {
		try {
			TableUtils.createTableIfNotExists(connectionSource, Subject.class);
			TableUtils.createTableIfNotExists(connectionSource, Domain.class);
			TableUtils.createTableIfNotExists(connectionSource, Root.class);
			TableUtils.createTableIfNotExists(connectionSource, Aspect.class);
			TableUtils.createTableIfNotExists(connectionSource, Topic.class);
			TableUtils.createTableIfNotExists(connectionSource, AuthorPublisher.class);
			TableUtils.createTableIfNotExists(connectionSource, CallNumber.class);
		} catch (SQLException e) {
			log.error("Failed to create a table.", e);
			// System.exit(1);
		}
	}
	
	
	/**
	 * Initialize a new Database Manager.
	 * @param connectionSource Connection to the database
	 * @throws SQLException If a DAO could not be created.
	 */
	private DatabaseManager(JdbcConnectionSource connectionSource) throws SQLException {
		this.connectionSource = connectionSource;
		initializeTables();
		
		this.subjects = DaoManager.createDao(connectionSource, Subject.class);
		this.domains = DaoManager.createDao(connectionSource, Domain.class);
		this.roots = DaoManager.createDao(connectionSource, Root.class);
		this.aspects = DaoManager.createDao(connectionSource, Aspect.class);
		this.topics = DaoManager.createDao(connectionSource, Topic.class);
		this.authorPublishers = DaoManager.createDao(connectionSource, AuthorPublisher.class);
		this.callNumbers = DaoManager.createDao(connectionSource, CallNumber.class);
	}
	
	
	/**
	 * Get the database manager singleton instance, creating a new one if needed. If a connection could not be
	 * established or another error occurs, this function fails and exits(1).
	 * @return The database manager singleton.
	 */
	public static DatabaseManager getInstance() {
		if (INSTANCE == null) {
			try (JdbcConnectionSource connectionSource = DatabaseManager.openConnection()) {
				
				INSTANCE = new DatabaseManager(connectionSource);
				
			} catch (SQLException e) {
				log.error("Failed to initialize database manager.", e);
				// System.exit(1);
			} catch (Exception e) {
				log.error("An unknown error occurred.", e);
				// System.exit(1);
			}
		}
		return INSTANCE;
	}
	
	/**
	 * Open a new connection to the database
	 * @return Database connection
	 * @throws SQLException If a connection could not be made
	 */
	private static JdbcConnectionSource openConnection() throws SQLException {
		boolean useLocalDb = EnvTools.getBoolean("DEV", false);
		
		var dbUrl = getDatabaseUrl(useLocalDb);
		log.info("Using database: {}", dbUrl);
		
		return new JdbcConnectionSource(dbUrl);
		// } catch (IOException e) {
		// 	log.error("Failed to create or get database file.", e);
		// 	System.exit(1);
		// 	return null;
		// }
	}
	
	/**
	 * Get the DAO for the specified class name
	 * @param partClassName Name of the class (in PascalCase)
	 * @return The DAO for the specified class name
	 */
	public Dao<? extends CallNumberPart, Long> getDao(String partClassName) {
		return switch (partClassName) {
			case "Subject" -> this.subjects;
			case "Domain" -> this.domains;
			case "Root" -> this.roots;
			case "Aspect" -> this.aspects;
			case "Topic" -> this.topics;
			case "AuthorPublisher" -> this.authorPublishers;
			default -> null;
		};
	}
	
	/**
	 * Get the DAO for the given class
	 * @param partClass The actual class (not it's name or an instance)
	 * @return The DAO for the given class
	 */
	public Dao<? extends CallNumberPart, Long> getDao(Class<? extends CallNumberPart> partClass) {
		return getDao(partClass.getSimpleName());
	}
}
