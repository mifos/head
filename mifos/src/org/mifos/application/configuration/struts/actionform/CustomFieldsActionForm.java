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

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;



public class CustomFieldsActionForm extends BaseActionForm{
	
	private MifosLogger logger = MifosLogManager
	.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
	
	private String categoryType;
	private String labelName;
	private boolean mandatoryCheckBox;
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

	public boolean isMandatoryCheckBox() {
		return mandatoryCheckBox;
	}

	public void setMandatoryCheckBox(boolean mandatoryCheckBox) {
		this.mandatoryCheckBox = mandatoryCheckBox;
	}
	
	private void validateForPreview(ActionErrors errors) {
		
		if (StringUtils.isNullOrEmpty(getCategoryType()))
			addError(errors, "configuration.category", "errors.mandatory_selectbox", new String[]{null});
		if (StringUtils.isNullOrEmpty(getDataType()))
			addError(errors, "configuration.datatype", "errors.mandatory_selectbox", new String[]{null});

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
		}
		

		if (!errors.isEmpty()) {
			request.setAttribute(Methods.method.toString(), method);
		}
		return errors;
	}
	
	// reset fields on form
	public void clear() 
	{
		
	}

}
