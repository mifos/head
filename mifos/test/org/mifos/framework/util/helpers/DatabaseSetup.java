package org.mifos.framework.util.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.JdbcDriver;
import net.sourceforge.mayfly.MayflyException;
import net.sourceforge.mayfly.MayflySqlException;
import net.sourceforge.mayfly.datastore.DataStore;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.persistence.SqlUpgrade;

public class DatabaseSetup {
	
	private static final String DEFAULT_HIBERNATE_TEST_PROPERTIES =
		"conf/HibernateTest.properties";
	private static final String LOCAL_HIBERNATE_TEST_PROPERTIES =
		"conf/my.HibernateTest.properties";

	private static DataStore standardMayflyStore;
	
	private static SessionFactory mayflySessionFactory;

	/**
	 * Set up log4j (this is required for hibernate, as well as perhaps
	 * other parts of MIFOS).
	 */
	public static void configureLogging() {
		if (!MifosLogManager.isConfigured()) {
			MifosLogManager.configure(FilePaths.LOGFILE);
		}
	}

	public static void initializeHibernate() {
		if (new File(LOCAL_HIBERNATE_TEST_PROPERTIES).exists()) {
			initializeHibernate(LOCAL_HIBERNATE_TEST_PROPERTIES);
		} else {
			initializeHibernate(DEFAULT_HIBERNATE_TEST_PROPERTIES);
		}
	}

	public static void initializeHibernate(String hibernatePropertiesFileName) {
		DatabaseSetup.configureLogging();

		if (HibernateSessionFactory.isConfigured()) {
			return;
		}

		boolean testAgainstMysql = true;
		if (testAgainstMysql) {
			setMysql(hibernatePropertiesFileName);
		} else {
			setMayfly();
		}
	}

	public static void setMysql(String hibernatePropertiesFileName) {
		HibernateStartUp.initialize(hibernatePropertiesFileName);
	}

	public static Configuration getHibernateConfiguration() {
		configureLogging();
		
		Configuration configuration = new Configuration();
		// This step is slow (about 1-2 s for me) because it
		// it reading and parsing a whole bunch of xml files.
		// That's why we try to share it between different tests.
		configuration.configure(
		    new File("src/" + FilePaths.HIBERNATECFGFILE)
		);
		return configuration;
	}

	public static Configuration mayflyConfiguration() {
		String url = JdbcDriver.create(DatabaseSetup.getStandardStore());

		Configuration configuration = getHibernateConfiguration();
		configuration.setProperty(
			"hibernate.connection.driver_class", 
			"net.sourceforge.mayfly.JdbcDriver");
		configuration.setProperty(
			"hibernate.connection.url", url);
		configuration.setProperty(
			"hibernate.dialect", 
			"org.mifos.framework.util.helpers.MayflyDialect");
		return configuration;
	}

	public static void setMayfly() {
		HibernateSessionFactory.setFactory(mayflySessionFactory());
	}
	
	public static synchronized SessionFactory mayflySessionFactory() {
		if (mayflySessionFactory == null) {
			mayflySessionFactory = mayflyConfiguration().buildSessionFactory();
		}
		return mayflySessionFactory;
	}

	public static synchronized DataStore getStandardStore() {
		if (standardMayflyStore == null) {
			standardMayflyStore = createStandardStore();
		}
		return standardMayflyStore;
	}

	private static DataStore createStandardStore() {
		Database database = new Database();
	    // Should be the same as the files in build.xml
	    executeScript(database, "sql/latest-schema.sql");
	    executeScript(database, "sql/latest-data.sql");

	    executeScript(database, "sql/testdbinsertionscript.sql");

	    return database.dataStore();
	}

	public static void executeScript(Database database, String name) {
        Reader sql = openFile(name);

        try {
	        database.executeScript(sql);
	    }
	    catch (MayflyException e) {
	    	if (e.startLineNumber() == -1) {
	    		throw e;
	    	}
	    	else {
		        throw new RuntimeException(
		            "error at line " + e.startLineNumber() +
		            " column " + e.startColumn() + " in file " + name,
		            e
		        );
	    	}
	    }
	    finally {
	    	try {
				sql.close();
			}
	    	catch (IOException e) {
				throw new RuntimeException(e);
			}
	    }
	}
	
	public static void executeScript(Connection connection, String name) 
	throws FileNotFoundException, SQLException {
		FileInputStream sql = new FileInputStream(name);
        try {
        	SqlUpgrade.execute(sql, connection);
	    }
	    catch (MayflySqlException e) {
	    	if (e.startLineNumber() == -1) {
	    		throw e;
	    	}
	    	else {
		        String failingCommand = e.failingCommand();
				throw new RuntimeException(
		            "error at line " + e.startLineNumber() +
		            " column " + e.startColumn() + " in file " + name +
		            (failingCommand == null ? "" :
			            "\ncommand was: " + failingCommand)
			        ,
		            e
		        );
	    	}
	    }
	}

	private static Reader openFile(String name) {
		try {
			return new FileReader(name);
		}
        catch (FileNotFoundException e) {
	    	throw new RuntimeException(e);
		}
	}

}
