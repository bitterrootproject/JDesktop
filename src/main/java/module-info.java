module org.bitterrootproject.jdesktop {
	// JavaFX stuff
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
	
	// ORMLite and SQLite
	requires ormlite.jdbc;
	requires java.sql;
	requires org.xerial.sqlitejdbc;
	
	// Misc. language helpers and util libraries
	requires static lombok;
	requires org.jetbrains.annotations;
	requires org.apache.commons.lang3;
	
	// Used to get the right folders on each OS
	requires net.harawata.appdirs;
	
	// Logging
	requires org.apache.logging.log4j.core;
	requires org.apache.logging.log4j;
	
	// requires java.desktop;
	
	
	
	// Need to explicitly add these so jlink works
	uses org.apache.logging.log4j.spi.Provider;
	uses org.apache.logging.log4j.util.PropertySource;
	uses java.sql.DriverManager;
	
	// Allow each package to open the specific libraries they need
	opens org.bitterrootproject.jdesktop to javafx.fxml, net.harawata.appdirs;
	opens org.bitterrootproject.jdesktop.gui to javafx.fxml;
	opens org.bitterrootproject.jdesktop.models to ormlite.jdbc, javafx.base;
	opens org.bitterrootproject.jdesktop.utils to javafx.fxml, net.harawata.appdirs;
	
	// Not sure what this does
    exports org.bitterrootproject.jdesktop;
	exports org.bitterrootproject.jdesktop.models to ormlite.jdbc;
	exports org.bitterrootproject.jdesktop.utils;
}