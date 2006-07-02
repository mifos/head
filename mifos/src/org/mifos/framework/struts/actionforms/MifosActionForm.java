/**

 * MifosActionForm.java    version: xxx

 

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

package org.mifos.framework.struts.actionforms;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationConstant;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelper;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.MethodInvoker;

/**
 * @author ashishsm
 * 
 */
public class MifosActionForm extends ValidatorActionForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 543543667657661L;
	public String input;

	public String getInput() {
		return this.input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	/**
	 * This method is the method which would be called for any validation in the application.
	 * This method checks the method of the action called and based on that it will call validate of
	 * the super class and the customValidate of the sub class. If the call is for validate method, it
	 * will return empty ActionErrors.  
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 * 
	 * @see org.apache.struts.validator.ValidatorForm#validate(org.apache.struts.action.ActionMapping,
	 *      javax.servlet.http.HttpServletRequest)
	 */
	public final ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).info(
				"Inside base action form validate method");
		ActionErrors errors = new ActionErrors();
		String methodCalled = request.getParameter("method");
		if (null != methodCalled && "validate".equalsIgnoreCase(methodCalled)) {
			return errors;
		}
		try {
			
			ActionErrors error = (ActionErrors) MethodInvoker
					.invokeWithNoException(this, "customValidate",
							new Object[] { mapping, request }, new Class[] {
									ActionMapping.class,
									HttpServletRequest.class });
			// skip validations if set so in the custom validate method or if this attribute is null
			if(null != request.getAttribute(Constants.SKIPVALIDATION) && true == (Boolean)request.getAttribute(Constants.SKIPVALIDATION)){
				
			}else{
				errors.add(super.validate(mapping, request));
			}
			if (null != error && !error.isEmpty()) {
				errors.add(error);
			}
			if (null != errors && !errors.isEmpty()) {
				request.setAttribute("methodCalled", methodCalled);
				request.setAttribute(Globals.ERROR_KEY, errors);
			}
		} catch (ApplicationException ape) {
			ape.printStackTrace();
		} catch (SystemException se) {
			se.printStackTrace();
		}
		return errors;
	}
	
	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale=null;
		HttpSession session= request.getSession();
		if(session !=null) {
			UserContext userContext= (UserContext)session.getAttribute(LoginConstants.USERCONTEXT);
			if(null !=userContext) {
				locale=userContext.getPereferedLocale();
				if(null==locale) {
					locale=userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}
	
	public void checkForMandatoryFields(Short entityId,ActionErrors errors,HttpServletRequest request){
		Map<Short,List<FieldConfigurationEntity>> entityMandatoryFieldMap=(Map<Short,List<FieldConfigurationEntity>>)request.getSession().getServletContext().getAttribute(Constants.FIELD_CONFIGURATION);
		List<FieldConfigurationEntity> mandatoryfieldList=entityMandatoryFieldMap.get(entityId);
		for(FieldConfigurationEntity fieldConfigurationEntity : mandatoryfieldList){
			String propertyName=request.getParameter(fieldConfigurationEntity.getLabel());
			if(propertyName!=null && !propertyName.equals("") ){
				String propertyValue=request.getParameter(propertyName);
				Locale locale=((UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT)).getPereferedLocale();
				if(propertyValue==null || propertyValue.equals(""))
					errors.add(fieldConfigurationEntity.getLabel(),
							new ActionMessage(FieldConfigurationConstant.EXCEPTION_MANDATORY,
									FieldConfigurationHelper.getLocalSpecificFieldNames(fieldConfigurationEntity.getLabel(),locale)));
			}
		}
	}
	
	
	
}
