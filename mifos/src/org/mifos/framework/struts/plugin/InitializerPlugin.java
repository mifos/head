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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringToMoneyConverter;

/**
 * This plugin is called when the system starts up. 
 * It mainly initializes struts-related things.
 * See also {@link org.mifos.framework.ApplicationInitializer}.
 * 
 * It reads the initialization.xml (? - or used to?)
 */
public class InitializerPlugin implements PlugIn {

	
	/**
	 * This is the central method which is called by the struts framework. This
	 * function then delegates the initialization part to different methods of
	 * the class.
	 * 
	 */
	public void init(ActionServlet servlet, ModuleConfig config)
			throws ServletException {

		try {
			initializeConfiguration(servlet);
			initializeFieldConfiguration(servlet);
			registerConverterWithBeanUtils();
			
		} catch (Exception ane) {
			ane.printStackTrace();
			UnavailableException ue = new UnavailableException(
					ane.getMessage(), 0);
			ue.initCause(ane);
			throw ue;
		}

	}


	public void destroy() {
	}

	

	void initializeConfiguration(ActionServlet servlet)
			throws AppNotConfiguredException {

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

	
	private void initializeFieldConfiguration(ActionServlet servlet)
			throws HibernateProcessException, ApplicationException {
		FieldConfig fieldConfig = FieldConfig.getInstance();
		fieldConfig.init();
		servlet.getServletContext().setAttribute(Constants.FIELD_CONFIGURATION,
				fieldConfig.getEntityMandatoryFieldMap());
	}

	private void setAttribute(ServletContext servletContext, String key) {
		servletContext.setAttribute(getKey(key), key);
	}

	private String getKey(String key) {
		return "LABEL_" + key.toUpperCase();
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
}
