/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.util.helpers;

import java.io.IOException;
import java.io.InputStream;
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
import org.mifos.core.ClasspathResource;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.hibernate.helper.HibernateConstants;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.SqlResource;
import org.mifos.framework.persistence.SqlUpgrade;

public class DatabaseSetup {
	private static DataStore standardMayflyStore;
	
	private static SessionFactory mayflySessionFactory;

	public static void initializeHibernate() {
			initializeHibernate(false);
	}
	
	public static void initializeHibernate(boolean useInMemoryDatabase) {
		MifosLogManager.configureLogging();

        // Make sure TestService is aware that we're running integration tests.
        // This is for integration test cases that use a database, but could
        // also apply to other "black box" tests.
        System.setProperty("mifos.test.mode", "integration");

		if (HibernateSessionFactory.isConfigured()) {
			return;
		}

		if (useInMemoryDatabase) {
		    setMayfly();
		} else {
			setMysql();
		}
	}

	public static void setMysql() {
	    StaticHibernateUtil.initialize();
	}

	public static Configuration getHibernateConfiguration() {
		MifosLogManager.configureLogging();
		
		Configuration configuration = new Configuration();
        try {
            configuration.configure(ClasspathResource.getURI(FilePaths.HIBERNATECFGFILE).toURL());
        } catch (Exception e) {
            throw new HibernateStartUpException(
                    HibernateConstants.CFGFILENOTFOUND, e);
        }
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
	    executeScript(database, "latest-schema.sql");
	    executeScript(database, "latest-data.sql");

	    executeScript(database, "testdbinsertionscript.sql");

	    return database.dataStore();
	}

	public static void executeScript(Database database, String name) {
	    
        Reader sql = SqlResource.getInstance().getAsReader(name);

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
	throws SQLException, IOException {
		InputStream sql = SqlResource.getInstance().getAsStream(name);
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

}
