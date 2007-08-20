/**

 * CustomFieldsActionForm.java

 

 * Copyright (c) 2005-2007 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2007 Grameen Foundation USA 
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

package org.mifos.application.configuration.struts.actionform;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomFieldType;
import java.text.DateFormat;
import java.util.Locale;



public class CustomFieldsActionForm extends BaseActionForm{
	
	private MifosLogger logger = MifosLogManager
	.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
	
	private String categoryType;
	private String labelName;
	private boolean mandatoryField = false;
	private String defaultValue;
	private String dataType;
	
	
	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public boolean isMandatoryField() {
		return mandatoryField;
	}

	public void setMandatoryField(boolean mandatoryField) {
		this.mandatoryField = mandatoryField;
	}
	
	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;
		HttpSession session = request.getSession();
		if (session != null) {
			UserContext userContext = (UserContext) session
					.getAttribute(LoginConstants.USERCONTEXT);
			if (null != userContext) {
				locale = userContext.getPreferredLocale();
				if (null == locale) {
					locale = userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}
	
	private void validateDefaultValue(ActionErrors errors, HttpServletRequest request){
		Short dataTypeValue = Short.parseShort(dataType);
		if (dataTypeValue.equals(CustomFieldType.NUMERIC.getValue()) && (!StringUtils.isEmpty(defaultValue)))
		{
			try
			{
				Double.parseDouble(defaultValue);
			}
			catch (NumberFormatException e)
			{
				addError(errors, defaultValue, "errors.default_value_not_number", new String[]{null});
			}
		}
		else if (dataTypeValue.equals(CustomFieldType.DATE.getValue()) && (!StringUtils.isEmpty(defaultValue)))
		{
			try
			{
				Locale locale = getUserLocale(request);
				DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
				df.parse(defaultValue);

			}
			catch (Exception e)
			{
				addError(errors, defaultValue, "errors.default_value_not_date", new String[]{null});
			}
		}
		
		
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("Inside validate method");
		String method = request.getParameter(Methods.method.toString());
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));

		ActionErrors errors = new ActionErrors();
		if (method.equals(Methods.preview.toString())) 
		{
			errors = super.validate(mapping, request);
			if (!StringUtils.isEmpty(defaultValue))
				validateDefaultValue(errors, request);
		}
		

		if (!errors.isEmpty()) {
			request.setAttribute(Methods.method.toString(), method);
		}
		return errors;
	}
	
	public void clear()
	{
		
	}
	
	// reset fields on form
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
		String method = request.getParameter(Methods.method.toString());
		if (method != null && method.equals(Methods.preview.toString())) {
			mandatoryField = false;
			
		}
	}

}
