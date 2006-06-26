/**

 * InitializerPlugin.java    version: 1.0



 * Copyright  2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */

package org.mifos.framework.struts.plugin;

import java.io.File;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.framework.business.handlers.Delegator;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.cronjobs.MifosScheduler;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.EncryptionException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.HibernateSystemException;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.XMLReaderException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authentication.Authenticator;
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.PersonRoles;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.InitializationReader;
import org.mifos.framework.util.helpers.MifosNode;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.StringToMoneyConverter;

/**
 * This is the plugin which is called when the system starts up for the first
 * time. It initializes various things in the system like the logger,reads the
 * initialization.xml and confiures the application
 * 
 * @author ashishsm
 * 
 */
public class InitializerPlugin implements PlugIn {

	 private String hibernatePropertiesPath;

	public String getHibernatePropertiesPath() {
		return hibernatePropertiesPath;
	}

	public void setHibernatePropertiesPath(String hibernatePropertiesPath) {
		this.hibernatePropertiesPath = hibernatePropertiesPath;
	}

	/**
	 * This is the central method which is called by the struts framework. This
	 * function then delegates the initialization part to different methods of
	 * the class.
	 * 
	 * @throws ServletException -
	 *             whenever application initialization fails
	 * @see org.apache.struts.action.PlugIn#init(org.apache.struts.action.ActionServlet,
	 *      org.apache.struts.config.ModuleConfig)
	 * 
	 */
	public void init(ActionServlet servlet, ModuleConfig config)
			throws ServletException {

		try {
			initializeLogger();
			// System.out.println("logger has been initialized");
			initializeApplication(servlet, config);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info(
					"Logger has been initialised", false, null);
			initializeHibernate(this.hibernatePropertiesPath);
			initializeSecurity();
			configureAdminUser();
			FinancialInitializer.initialize();
			EntityMasterData.getInstance().init();
			initializeEntityMaster();
			(new MifosScheduler()).registerTasks();
			registerConverterWithBeanUtils();
			initializeConfiguration(servlet);
			initializeFieldConfiguration(servlet);
			Configuration.getInstance();
			configureAuditLogValues();
		} catch (Exception ane) {
			ane.printStackTrace();
			UnavailableException ue = new UnavailableException(
					ane.getMessage(), 0);
			ue.initCause(ane);
			throw ue;
		}

	}

	/**
	 *This method creates an instance of StringToMoney converter
	 *and registers it with BeanUtils so that when struts uses this bean utils
	 *to populate action form from request parameters it can use this converter. 
	 */
	private void registerConverterWithBeanUtils() {
		Converter stringToMoney = new StringToMoneyConverter();
		BeanUtilsBean.getInstance().getConvertUtils().register(stringToMoney, Money.class);
		
	}

	/**
	 * Initializes Hibernate by making it read the hibernate.cfg file and also
	 * setting the same with hibernate session factory.
	 * 
	 * @throws AppNotConfiguredException
	 */
	private void initializeHibernate(String hibernatePropertiesPath) throws AppNotConfiguredException {
		try {
			HibernateStartUp.initialize(hibernatePropertiesPath);
		} catch (HibernateStartUpException hsue) {
			hsue.printStackTrace();
			throw new AppNotConfiguredException(hsue);
		}

	}

	/**
	 * Reads the iinitialization.xml and configures the system.Also reads the
	 * dsn passed as servlet config and initializes the <code>DAO</code> with
	 * the datasource.
	 * 
	 * @param servlet
	 * @param config
	 * @throws AppNotConfiguredException -
	 *             when it fails to read initialization.xml or fails to
	 *             initialize the <code>DAO</code> with the datasource
	 */
	private void initializeApplication(ActionServlet servlet,
			ModuleConfig config) throws AppNotConfiguredException {
		InitializationReader initReader = new InitializationReader();
		MifosNode node, anotherNode = null;
		Delegator delegator = null;
		String businessProcessorImplementation = null;

		try {
			// read the dsn and set it in the DAO
			DAO.initializeDataSourceName(servlet.getInitParameter("DSN"));

			// read delegator
			node = getNode(FilePaths.INITIALIZATIONFILE, Constants.DELEGATOR);
			delegator = (Delegator) Class.forName(
					node.getElement(Constants.DELEGATOR)).newInstance();
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info(
					"Delegator has been read from xml file", false, null);
			// read businessprocessor implementation
			node = getNode(FilePaths.INITIALIZATIONFILE,
					Constants.BUSINESSPROCESSORIMPLEMENTATION);
			businessProcessorImplementation = node
					.getElement(Constants.BUSINESSPROCESSORIMPLEMENTATION);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
					"BusinessProcessor impl value is "
							+ businessProcessorImplementation, false, null);
			// set businessprocessor implementation in the delegator
			// so that it can be used to return appropriate service locator
			delegator
					.setBusinessProcessorImplementation(businessProcessorImplementation);

		} catch (XMLReaderException xre) {

			xre.printStackTrace();
			throw new AppNotConfiguredException(xre);
		} catch (URISyntaxException urise) {

			urise.printStackTrace();
			throw new AppNotConfiguredException(urise);
		} catch (InstantiationException ie) {

			ie.printStackTrace();
			throw new AppNotConfiguredException(ie);
		} catch (IllegalAccessException iae) {

			iae.printStackTrace();
			throw new AppNotConfiguredException(iae);
		} catch (ClassNotFoundException cnfe) {

			cnfe.printStackTrace();
			throw new AppNotConfiguredException(cnfe);
		}

		servlet.getServletContext()
				.setAttribute(Constants.DELEGATOR, delegator);
		servlet.getServletContext().setAttribute(
				Constants.BUSINESSPROCESSORIMPLEMENTATION,
				businessProcessorImplementation);

	}

	/**
	 * Reads the xml file passed as argument and returns the object of
	 * {@link MifosNode} corresponding to the node name passed as argument
	 * 
	 * @param filePath -
	 *            relative path of the file which it is supposed to read.
	 * @param nodeName -
	 *            name of the node in the file to be read.
	 * @return - the <code>MifosNode</code> object corresponding the node read
	 * @throws XMLReaderException
	 * @throws URISyntaxException
	 */
	private MifosNode getNode(String filePath, String nodeName)
			throws XMLReaderException, URISyntaxException {
		InitializationReader initReader = new InitializationReader();
		MifosNode node = null;
		node = (MifosNode) initReader.getElement(new File(ResourceLoader
				.getURI(filePath)), nodeName);
		return node;
	}

	/**
	 * Initializes the logger using loggerconfiguration.xml
	 * 
	 * @throws AppNotConfiguredException -
	 *             IF there is any exception while configuring the logger
	 */
	private void initializeLogger() throws AppNotConfiguredException {
		// System.out.println("Inside the logger initialization");
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
		}  catch (EncryptionException e) {
			throw new AppNotConfiguredException(e);
		} catch (HibernateSystemException e) {
			throw new AppNotConfiguredException(e);
		} catch (ApplicationException ae) {
			throw new AppNotConfiguredException(ae);
		} catch (org.mifos.framework.exceptions.SystemException se) {
			throw new AppNotConfiguredException(se);
		}

	}

	/**
	 * This method sets the password for admin user. The user name would be
	 * "mifos" and password would be "mifos". This would expect that a row in
	 * personnel table already exists where the user name is "mifos". Insertion
	 * of that row is taken care by master data script.This is a temporary
	 * method and would be altered when the set module is done.
	 * 
	 * @throws URISyntaxException
	 * @throws XMLReaderException
	 * @throws EncryptionException
	 * @throws HibernateProcessException
	 */
	private void configureAdminUser() throws XMLReaderException,
			URISyntaxException, EncryptionException, HibernateProcessException,
			SystemException {
		MifosNode algorithmNode = null;
		Session session = null;
		String algorithm;
		byte[] password;

		MifosLogManager
				.getLogger(LoggerConstants.FRAMEWORKLOGGER)
				.debug(
						"Inside configureAdminUser method trying to read the algorithm for encryption");
		InitializationReader initializationReader = new InitializationReader();
		algorithmNode = initializationReader.getElement(new File(ResourceLoader
				.getURI(FilePaths.INITIALIZATIONFILE)), Constants.ALGORITHM);
		algorithm = algorithmNode.getElement(Constants.ALGORITHM);
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
				"The algorithm for encrypting the password is : " + algorithm);

		// setting the alogithm in the encryption factory
		// EncryptionFactory encryptionFactory =
		// EncryptionFactory.getInstance();
		// encryptionFactory.setAlgorithm(algorithm);

		// getting the encrypted password
		// Encryptor encryptor = encryptionFactory.createEncryptor();
		// password= encryptor.encrypt("mifos", "mifos");
		password = EncryptionService.getInstance().createEncryptedPassword(
				"mifos");
		try {
			session = HibernateUtil.getSession();
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

	public void destroy() {
	}

	private void configureAuditLogValues() throws SystemException {
		AuditConfigurtion auditConfigurtion = new AuditConfigurtion();
		auditConfigurtion.createEntityValueMap();
	}
	void initializeConfiguration(ActionServlet servlet)throws AppNotConfiguredException{
		
		
		MifosConfiguration.getInstance().init();
		initializeLabelConstants(servlet);
		
	}
	private void initializeLabelConstants(ActionServlet servlet) {

		ServletContext servletContext = servlet.getServletContext();
		setAttribute(servletContext, ConfigurationConstants.BRANCHOFFICE);
		setAttribute(servletContext, ConfigurationConstants.BULKENTRY);
		setAttribute(servletContext, ConfigurationConstants.OFFICE);
		setAttribute(servletContext, ConfigurationConstants.CLIENT);
		setAttribute(servletContext, ConfigurationConstants.GROUP);
		setAttribute(servletContext, ConfigurationConstants.CENTER);
	}
	
	private void initializeEntityMaster() throws HibernateProcessException{
		EntityMasterData.getInstance().init();
	}
	private void initializeFieldConfiguration(ActionServlet servlet) throws HibernateProcessException{
		FieldConfigItf fieldConfigItf=FieldConfigImplementer.getInstance();
		fieldConfigItf.init();
		//TODO Remove this code after M1 code migration.
		servlet.getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,fieldConfigItf.getEntityMandatoryFieldMap());
	}


	private void setAttribute(ServletContext servletContext, String key) {
		servletContext.setAttribute(getKey(key), key);
	}


	private String getKey(String key) {
		return "LABEL_" + key.toUpperCase();
	}
}