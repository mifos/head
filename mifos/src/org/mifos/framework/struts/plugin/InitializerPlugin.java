/**

 * InitializerPlugin.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

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
import org.mifos.application.master.util.helpers.CacheInitializer;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.cronjobs.MifosScheduler;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
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
import org.mifos.framework.security.authentication.EncryptionService;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.PersonRoles;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.StringToMoneyConverter;

/**
 * This is the plugin which is called when the system starts up for the first
 * time. It initializes various things in the system like the logger,reads the
 * initialization.xml and confiures the application
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
	 */
	public void init(ActionServlet servlet, ModuleConfig config)
			throws ServletException {

		try {
			initializeLogger();
			initializeHibernate(this.hibernatePropertiesPath);
			MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info(
					"Logger has been initialised", false, null);
			initializeSecurity();
			configureAdminUser();
			FinancialInitializer.initialize();
			CacheInitializer.initialize();
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
	 * This method creates an instance of StringToMoney converter and registers
	 * it with BeanUtils so that when struts uses this bean utils to populate
	 * action form from request parameters it can use this converter.
	 */
	private void registerConverterWithBeanUtils() {
		Converter stringToMoney = new StringToMoneyConverter();
		BeanUtilsBean.getInstance().getConvertUtils().register(stringToMoney,
				Money.class);

	}

	/**
	 * Initializes Hibernate by making it read the hibernate.cfg file and also
	 * setting the same with hibernate session factory.
	 */
	private void initializeHibernate(String hibernatePropertiesPath)
			throws AppNotConfiguredException {
		try {
			try {
				if (ResourceLoader
						.getURI(FilePaths.CONFIGURABLEMIFOSDBPROPERTIESFILE) != null)
					hibernatePropertiesPath = FilePaths.CONFIGURABLEMIFOSDBPROPERTIESFILE;
			} catch (URISyntaxException e) {
			}
			HibernateStartUp.initialize(hibernatePropertiesPath);
		} catch (HibernateStartUpException e) {
			e.printStackTrace();
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
		byte[] password;

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
		AuditConfigurtion.init();
	}

	void initializeConfiguration(ActionServlet servlet)
			throws AppNotConfiguredException {

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

	private void initializeEntityMaster() throws HibernateProcessException {
		EntityMasterData.getInstance().init();
	}

	private void initializeFieldConfiguration(ActionServlet servlet)
			throws HibernateProcessException, ApplicationException {
		FieldConfigItf fieldConfigItf = FieldConfigImplementer.getInstance();
		fieldConfigItf.init();
		// TODO Remove this code after M1 code migration.
		servlet.getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,
				fieldConfigItf.getEntityMandatoryFieldMap());
	}

	private void setAttribute(ServletContext servletContext, String key) {
		servletContext.setAttribute(getKey(key), key);
	}

	private String getKey(String key) {
		return "LABEL_" + key.toUpperCase();
	}
}
