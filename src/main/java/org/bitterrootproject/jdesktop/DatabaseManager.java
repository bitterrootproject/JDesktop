package org.bitterrootproject.jdesktop;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.table.TableUtils;
import lombok.extern.slf4j.Slf4j;
import org.bitterrootproject.jdesktop.models.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;


/**
 * Database manager for the SQLite database used by Bitterroot JDesktop.
 */
@Slf4j
public final class DatabaseManager {
	private static final String DATABASE_DRIVER = "org.sqlite.JDBC";
	
	private static String getDatabaseUrl() {
		String prefix = "jdbc:sqlite:";
		Path path = FileManager.getAppDataDirectory().resolve("db.sqlite3");
		
		try {
			if (!path.getParent().toFile().exists())
				Files.createDirectories(path.getParent());
		} catch (IOException e) {
			log.error("Failed to create parent directories to the sqlite file", e);
		}
		
		
		if (!path.toFile().exists()) {
			try {
				Files.createFile(path);
			} catch (FileAlreadyExistsException ignored) {
			} catch (IOException e) {
				log.error("Failed to create new SQLite database file", e);
			}
		}
		
		return prefix + path;
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
			System.exit(1);
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
				System.exit(1);
			} catch (Exception e) {
				log.error("An unknown error occurred.", e);
				System.exit(1);
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
		// try {
		return new JdbcConnectionSource(getDatabaseUrl());
		// } catch (IOException e) {
		// 	log.error("Failed to create or get database file.", e);
		// 	System.exit(1);
		// 	return null;
		// }
	}
	
	
	public static void main(String[] args) {
		System.out.println(System.getProperty( "javafx.runtime.version" ));
	// 	try (JdbcConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL)) {
	// 		// initializeTables(connectionSource);
	//
	// 		// var subjectDao = Subject.createDao(connectionSource);
	// 		// var domainDao = Domain.createDao(connectionSource);
	// 		// var callNumberDao = CallNumber.createDao(connectionSource);
	// 		//
	// 		// subjectDao.queryForAll();
	//
	// 		// Subject subj = new Subject();
	// 		// subj.setName("Linguistics");
	// 		// subj.setNumber("LI");
	// 		// subjectDao.create(subj);
	// 		//
	// 		// Domain dom = new Domain();
	// 		// dom.setName("Literature");
	// 		// dom.setNumber("LT");
	// 		// dom.setSubject(subj);
	// 		// domainDao.create(dom);
	// 		//
	// 		// CallNumber cn = new CallNumber();
	// 		// cn.setSubject(subj);
	// 		// cn.setDomain(dom);
	// 		// callNumberDao.create(cn);
	//
	//
	// 	} catch (SQLException e) {
	// 		Logger.error(e, "A database operation failed.");
	// 		System.exit(1);
	// 	} catch (Exception e) {
	// 		Logger.error(e, "An unexpected error occurred.");
	// 		System.exit(1);
	// 	}
	}
}
