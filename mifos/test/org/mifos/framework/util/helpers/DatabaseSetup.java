package org.mifos.framework.util.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.JdbcDriver;
import net.sourceforge.mayfly.MayflyException;
import net.sourceforge.mayfly.datastore.DataStore;

import org.hibernate.cfg.Configuration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;

public class DatabaseSetup {
	
	private static DataStore standardMayflyStore;

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
		if (HibernateSessionFactory.isConfigured()) {
			return;
		}

		boolean testAgainstMysql = true;
		if (testAgainstMysql) {
			setMysql();
		} else {
			setMayfly();
		}
	}

	public static void setMysql() {
		HibernateStartUp.initialize("conf/HibernateTest.properties");
	}

	private static Configuration hibernateConfiguration = null;

	public static Configuration getHibernateConfiguration() {
		if (hibernateConfiguration == null) {
			configureLogging();
			
			Configuration configuration = new Configuration();
			// This step is slow (about 1-2 s for me) because it
			// it reading and parsing a whole bunch of xml files.
			// That's why we try to share it between different tests.
			configuration.configure(
			    new File("src/" + FilePaths.HIBERNATECFGFILE)
			);
			hibernateConfiguration = configuration;
		}
		return hibernateConfiguration;
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
		HibernateSessionFactory.setConfiguration(mayflyConfiguration());
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
	    try {
	        Reader sql = new FileReader(name);
	
	        database.executeScript(sql);
	    }
	    catch (MayflyException e) {
	    	if (e.startLineNumber() == -1) {
	    		throw e;
	    	}
	    	else {
		        throw new RuntimeException(
		            "error at line " + e.startLineNumber() +
		            " column " + e.startColumn(),
		            e
		        );
	    	}
	    } catch (FileNotFoundException e) {
	    	throw new RuntimeException(e);
		}
	}

}
