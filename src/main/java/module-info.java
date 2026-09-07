module org.bitterrootproject.jdesktop {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
	requires ormlite.jdbc;
	requires java.sql;
	requires static lombok;
	requires org.slf4j;
	// requires org.apache.logging.log4j;
	requires java.desktop;
	requires org.xerial.sqlitejdbc;
	requires org.apache.commons.lang3;
	
	opens org.bitterrootproject.jdesktop to javafx.fxml;
	opens org.bitterrootproject.jdesktop.gui to javafx.fxml;
	opens org.bitterrootproject.jdesktop.models to ormlite.jdbc, javafx.base;
	
    exports org.bitterrootproject.jdesktop;
	exports org.bitterrootproject.jdesktop.models to ormlite.jdbc;
}