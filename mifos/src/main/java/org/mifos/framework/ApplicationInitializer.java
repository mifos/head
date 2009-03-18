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

package org.mifos.framework;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.config.Localization;
import org.mifos.config.ProcessFlowRules;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.hibernate.configuration.ConfigureSession;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.spring.SpringUtil;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.TestingService;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;

/**
 * This class should prepare all the sub-systems that are required by the app.
 * Cleanup should also happen here when the application is shutdown.
 */
public class ApplicationInitializer implements ServletContextListener,
		ServletRequestListener {

	private static MifosLogger LOG = null;

	private static class DatabaseError {
		boolean isError = false;
		DatabaseErrorCode errorCode = DatabaseErrorCode.NO_DATABASE_ERROR;
		String errmsg = "";
		Throwable error = null;

		void logError() {
			LOG.fatal(errmsg, false, null, error);
		}
	}

	public static void setDatabaseError(DatabaseErrorCode errcode,
			String errmsg, Throwable error) {
		databaseError.isError = true;
		databaseError.errorCode = errcode;
		databaseError.error = error;
		databaseError.errmsg = errmsg;
	}

	public static void clearDatabaseError() {
		databaseError.isError = false;
		databaseError.errorCode = DatabaseErrorCode.NO_DATABASE_ERROR;
		databaseError.error = null;
		databaseError.errmsg = null;
	}

	private static String getDatabaseConnectionInfo() {
        TestingService testingService = new TestingService();
        Properties hibernateCfg = new Properties();
        String info = "Using Mifos database connection settings";
        try {
            hibernateCfg = testingService.getDatabaseConnectionSettings();
            info += " from file(s): " + Arrays.toString(testingService.getAllSettingsFilenames());
        } catch (IOException e) {
            /*
             * not sure if we can actually do anything useful with this
             * exception since we're likely running during container
             * initialization
             */
            e.printStackTrace();
        }
        info += " Connection URL=" + hibernateCfg.getProperty("hibernate.connection.url");
        info += ". Username=" + hibernateCfg.getProperty("hibernate.connection.username");
        info += ". Password=********";
        return info;
    }

	private static DatabaseError databaseError = new DatabaseError();

	public void contextInitialized(ServletContextEvent ctx) {
		init();
	}

	public void init() {
		try {
			synchronized (ApplicationInitializer.class) {
				initializeLogger();
				initializeHibernate();

				/*
				 * getLogger() cannot be called statically (ie: when
				 * initializing LOG) because MifosLogManager.initializeLogger()
				 * hasn't been called yet, so MifosLogManager.loggerRepository
				 * will be null.
				 */
				LOG = MifosLogManager
						.getLogger(LoggerConstants.FRAMEWORKLOGGER);
				LOG.info("Logger has been initialised", false, null);

				LOG.info(getDatabaseConnectionInfo(), false, null);

				DatabaseVersionPersistence persistence = new DatabaseVersionPersistence();
				try {
					/*
					 * This is an easy way to force an actual database query to
					 * happen via Hibernate. Simply opening a Hibernate session
					 * may not actually connect to the database.
					 */
					persistence.isVersioned();
				}
				catch (Throwable t) {
					setDatabaseError(DatabaseErrorCode.CONNECTION_FAILURE,
							"Unable to connect to database.", t);
				}

				if (!databaseError.isError) {
					try {
						persistence.upgradeDatabase();
					}
					catch (Throwable t) {
						setDatabaseError(DatabaseErrorCode.UPGRADE_FAILURE,
								"Failed to upgrade database.", t);
					}
				}


				if (databaseError.isError) {
					databaseError.logError();
				}
				else {
					// this method is called so that supported locales will be
					// loaded
					// from db and stored in cache for later use
					Localization.getInstance().init();
					// Check ClientRules configuration in db and config file(s)
					// for errors. Also caches ClientRules values.
					ClientRules.init();
					// Check ProcessFlowRules configuration in db and config
					// file(s) for errors.
					ProcessFlowRules.init();
					initializeSecurity();

					Money.setDefaultCurrency(AccountingRules.getMifosCurrency());

					// 1/4/08 Hopefully a temporary change to force Spring
					// to initialize here (rather than in struts-config.xml
					// prior to loading label values into a
					// cache in MifosConfiguration. When the use of the
					// cache is refactored away, we should be able to move
					// back to the struts based Spring initialization
					SpringUtil.initializeSpring();

					// Spring must be initialized before FinancialInitializer
					FinancialInitializer.initialize();
					EntityMasterData.getInstance().init();
					initializeEntityMaster();
					(new MifosScheduler()).registerTasks();

					Configuration.getInstance();
					MifosConfiguration.getInstance().init();
					configureAuditLogValues(Localization.getInstance()
							.getMainLocale());

				}
			}
		}
		catch (Exception e) {
			throw new Error(e);
		}
	}

	public static void printDatabaseError(XmlBuilder xml, int dbVersion) {
		synchronized (ApplicationInitializer.class) {
			if (databaseError.isError) {
				addDatabaseErrorMessage(xml, dbVersion);
			}
			else {
				addNoFurtherDetailsMessage(xml);
			}
		}
	}

    private static void addNoFurtherDetailsMessage(XmlBuilder xml) {
        xml.startTag("p");
        xml.text("I don't have any further details, unfortunately.");
        xml.endTag("p");
        xml.text("\n");
    }

    private static void addDatabaseErrorMessage(XmlBuilder xml, int dbVersion) {
        xml.startTag("p", "style",
        		"font-weight: bolder; color: red; font-size: x-large;");

        xml.text(databaseError.errmsg);
        xml.text("\n");

        if (databaseError.errorCode.equals(DatabaseErrorCode.UPGRADE_FAILURE)) {
        	addDatabaseVersionMessage(xml, dbVersion);
        }
        xml.endTag("p");
        if (databaseError.errorCode.equals(DatabaseErrorCode.CONNECTION_FAILURE)) {
        	addConnectionFailureMessage(xml);
        }
        xml.text("\n");
        xml.startTag("p");
        xml.text("More details:");
        xml.endTag("p");
        xml.text("\n");

        if (null != databaseError.error.getCause()) {
        	xml.startTag("p", "style",
        			"font-weight: bolder; color: blue;");
        	xml.text(databaseError.error.getCause().toString());
        	xml.endTag("p");
        	xml.text("\n");
        }

        xml.startTag("p", "style", "font-weight: bolder; color: blue;");
        xml.text(getDatabaseConnectionInfo());
        xml.endTag("p");
        xml.text("\n");
        addStackTraceHtml(xml);
    }

    private static void addConnectionFailureMessage(XmlBuilder xml) {
        xml.startTag("p");
        xml.text("Possible causes:");

        xml.startTag("ul");
        xml.startTag("li");
        xml.text("MySQL is not running");
        xml.endTag("li");

        xml.startTag("li");
        xml.text("MySQL is listening on a different port than Mifos is expecting");
        xml.endTag("li");

        xml.startTag("li");
        xml.text("incorrect username or password");
        xml.endTag("li");
        xml.endTag("ul");
        xml.endTag("p");
        xml.text("\n");

        xml.startTag("p");
        xml.startTag("a", "href", "http://mifos.org/developers/wiki/ConfiguringMifos#database-connection");
        xml.text("More about configuring your database connection.");
        xml.endTag("a");
        xml.endTag("p");
        xml.text("\n");
    }

    private static void addDatabaseVersionMessage(XmlBuilder xml, int dbVersion) {
        if (dbVersion == -1) {
        	xml.text("Database is too old to have a version.\n");
        }
        else {
        	xml.text("Database Version = " + dbVersion + "\n");
        }
        xml.text("Application Version = "
        		+ DatabaseVersionPersistence.APPLICATION_VERSION
        		+ ".\n");
    }

    private static void addStackTraceHtml(XmlBuilder xml) {
        xml.startTag("p");
        xml.text("Stack trace:");
        xml.endTag("p");
        xml.text("\n");

        xml.startTag("pre");
        StringWriter stackTrace = new StringWriter();
        databaseError.error.printStackTrace(new PrintWriter(stackTrace));
        xml.text("\n" + stackTrace.toString());
        xml.endTag("pre");
        xml.text("\n");
    }

	/**
	 * Initializes Hibernate by making it read the hibernate.cfg file and also
	 * setting the same with hibernate session factory.
	 */
	public static void initializeHibernate() throws AppNotConfiguredException {
		try {
		    ConfigureSession.configure();
		}
		catch (HibernateStartUpException e) {
			throw new AppNotConfiguredException(e);
		}
	}

	/**
	 * Initializes the logger using loggerconfiguration.xml
	 * 
	 * @throws AppNotConfiguredException -
	 *             IF there is any exception while configuring the logger
	 */
	private void initializeLogger() throws AppNotConfiguredException {
		try {
			MifosLogManager.configure(FilePaths.LOG_CONFIGURATION_FILE);
		}
		catch (LoggerConfigurationException lce) {

			lce.printStackTrace();
			throw new AppNotConfiguredException(lce);
		}

	}

	/**
	 * This function initialize and bring up the authorization and
	 * authentication services
	 * 
	 * @throws AppNotConfiguredException -
	 *             IF there is any failures during init
	 */
	private void initializeSecurity() throws AppNotConfiguredException {
		try {
			ActivityMapper.getInstance().init();

			AuthorizationManager.getInstance().init();

			HierarchyManager.getInstance().init();

		}
		catch (XMLReaderException e) {

			throw new AppNotConfiguredException(e);
		}
		catch (ApplicationException ae) {
			throw new AppNotConfiguredException(ae);
		}
		catch (SystemException se) {
			throw new AppNotConfiguredException(se);
		}

	}

	private void initializeEntityMaster() throws HibernateProcessException {
		EntityMasterData.getInstance().init();
	}

	private void configureAuditLogValues(Locale locale) throws SystemException {
		AuditConfigurtion.init(locale);
	}

	public void contextDestroyed(ServletContextEvent ctx) {

	}

	public void requestDestroyed(ServletRequestEvent event) {
		StaticHibernateUtil.closeSession();
	}

	public void requestInitialized(ServletRequestEvent event) {

	}

}
