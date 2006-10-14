package org.mifos.framework.util.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.JdbcDriver;
import net.sourceforge.mayfly.MayflySqlException;
import net.sourceforge.mayfly.datastore.DataStore;

import org.hibernate.cfg.Configuration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;

public class DatabaseSetup {

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
		String url = JdbcDriver.create(DatabaseSetup.STANDARD_STORE);

		Configuration configuration = getHibernateConfiguration();
		configuration.setProperty(
			"hibernate.connection.driver_class", 
			"net.sourceforge.mayfly.JdbcDriver");
		configuration.setProperty(
			"hibernate.connection.url", url);
		configuration.setProperty(
			"hibernate.dialect", "org.hibernate.dialect.GenericDialect");
		return configuration;
	}

	public static void setMayfly() {
		HibernateSessionFactory.setConfiguration(mayflyConfiguration());
	}

	public static final DataStore STANDARD_STORE = createStandardStore();

	private static DataStore createStandardStore() {
		Database database = new Database();
	    executeScript(database, "sql/mifosdbcreationscript.sql");
	    executeScript(database, "sql/Iteration13-DBScripts25092006.sql");
	
	    executeScript(database, "sql/mifosmasterdata.sql");
		return database.dataStore();
	}

	public static void executeScript(Database database, String name) {
	    /*try {
	        Reader sql = new FileReader(
                "c:/Documents and Settings/jkingdon/" +
                "workspace/mifos/" +
                name
            );
	
	        database.executeScript(sql);
	    } catch (MayflySqlException e) {
	        throw new RuntimeException(
	            "error at line " + e.startLineNumber() +
	            " column " + e.startColumn(),
	            e
	        );
	    } catch (FileNotFoundException e) {
	    	throw new RuntimeException(e);
		}*/
	}

}
