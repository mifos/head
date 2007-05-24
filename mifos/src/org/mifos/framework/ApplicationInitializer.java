package org.mifos.framework;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.PersonRoles;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.ResourceLoader;
/**
 * This class should prepare all the sub-systems that are required by the app.
 * Cleanup should also happen here, when the application is shutdown. 
 * */
public class ApplicationInitializer implements ServletContextListener {
	
	private static Throwable databaseVersionError;
	
	public void contextInitialized(ServletContextEvent ctx) {
		init();
	}
	
	public void init() {
		try{
			synchronized(ApplicationInitializer.class){
				initializeLogger();
				initializeHibernate();
				MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info(
						"Logger has been initialised", false, null);
				try{
					syncDatabaseVersion();
				}catch(Throwable t){
					databaseVersionError = t;
					MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).fatal(
							"Failed to upgrade/downgrade database version", false, null, t);
				}
				if(databaseVersionError==null){
					initializeSecurity();
					configureAdminUser();
					FinancialInitializer.initialize();
					EntityMasterData.getInstance().init();
					initializeEntityMaster();
					(new MifosScheduler()).registerTasks();
					
					Configuration.getInstance();
					configureAuditLogValues();
					MifosConfiguration.getInstance().init();
				}
			}
		}catch(Exception e){
			throw new Error(e);
		}
	}
	
	public static void printDatabaseError(XmlBuilder xml) {
		synchronized(ApplicationInitializer.class){
	        if(databaseVersionError != null) {
	        	xml.startTag("p");
	        	xml.text("Here are details of what went wrong:");
	        	xml.endTag("p");

	        	xml.startTag("pre");
	        	StringWriter stackTrace = new StringWriter();
				databaseVersionError.printStackTrace(new PrintWriter(stackTrace));
				xml.text("\n" + stackTrace.toString());
	        	xml.endTag("pre");
			}
	        else {
	        	xml.startTag("p");
	        	xml.text("I don't have any further details, unfortunately.");
	        	xml.endTag("p");
	        }
	    }
	}
	
	public static void setDatabaseVersionError(Throwable error) {
		databaseVersionError = error;
	}
	
	/**
	 * Initializes Hibernate by making it read the hibernate.cfg file and also
	 * setting the same with hibernate session factory.
	 */
	public static void initializeHibernate()
			throws AppNotConfiguredException {
		try {
			String hibernatePropertiesPath = FilePaths.HIBERNATE_PROPERTIES;
			try {
				if (ResourceLoader
						.getURI(FilePaths.CONFIGURABLEMIFOSDBPROPERTIESFILE) != null)
					hibernatePropertiesPath = FilePaths.CONFIGURABLEMIFOSDBPROPERTIESFILE;
			} catch (URISyntaxException e) {
				throw new AppNotConfiguredException(e);
			}
			HibernateStartUp.initialize(hibernatePropertiesPath);
		} catch (HibernateStartUpException e) {
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
			MifosLogManager.configure(FilePaths.LOGFILE);
		} catch (LoggerConfigurationException lce) {

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

			AuthorizationManager.getInstance().init();

			HierarchyManager.getInstance().init();

		} catch (XMLReaderException e) {

			throw new AppNotConfiguredException(e);
		} catch (ApplicationException ae) {
			throw new AppNotConfiguredException(ae);
		} catch (SystemException se) {
			throw new AppNotConfiguredException(se);
		}

	}
	/**
	 * This method sets the password for admin user. The user name would be
	 * "mifos" and password would be "mifos". This would expect that a row in
	 * personnel table already exists where the user name is "mifos". Insertion
	 * of that row is taken care by master data script.This is a temporary
	 * method and would be altered when the set module is done.
	 */
	private void configureAdminUser() throws XMLReaderException,
			URISyntaxException, HibernateProcessException, SystemException {
		Session session = null;
		byte[] password = 
			EncryptionService.getInstance().createEncryptedPassword(
				"testmifos");
		try {
			session = HibernateUtil.openSession();
			Transaction trxn = session.beginTransaction();
			PersonRoles personRoles = (PersonRoles) session.createQuery(
					"from PersonRoles p where p.loginName ='mifos'")
					.uniqueResult();
			if (null != personRoles) {
				personRoles.setPassword(password);

				session.update(personRoles);
			}
			trxn.commit();
			MifosLogManager
					.getLogger(LoggerConstants.FRAMEWORKLOGGER)
					.debug(
							"password in personnel table for user name mifos has been updated ");
		} catch (HibernateException he) {
			MifosLogManager
					.getLogger(LoggerConstants.FRAMEWORKLOGGER)
					.error(
							"password in personnel table for user name mifos could not be updated  ",
							false, null, he);

		} finally {
			HibernateUtil.closeSession(session);
		}

	}
	
	private void initializeEntityMaster() throws HibernateProcessException {
		EntityMasterData.getInstance().init();
	}
	
	private void syncDatabaseVersion() throws SQLException, Exception {
		DatabaseVersionPersistence persistance = new DatabaseVersionPersistence();
		persistance.upgradeDatabase();
	}

	private void configureAuditLogValues() throws SystemException {
		AuditConfigurtion.init();
	}
	
	public void contextDestroyed(ServletContextEvent ctx) {
		
	}

}
