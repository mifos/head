/**

 * FundActionForm  version: 1.0



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

package org.mifos.application.fund.struts.actionforms;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.StringUtils;

public class FundActionForm extends BaseActionForm {	
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER);
	private String fundCodeId;
	private String fundName;
	private String fundCode;
	
	public String getFundCode() {
		return fundCode;
	}
	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}
	public String getFundCodeId() {
		return fundCodeId;
	}
	public void setFundCodeId(String fundCodeId) {
		this.fundCodeId = fundCodeId;
	}
	public String getFundName() {
		return fundName;
	}
	public void setFundName(String fundName) {
		this.fundName = fundName;
	}
	
	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
		String method = request.getParameter(ProductDefinitionConstants.METHOD);
		logger.debug("validate method of Fund Action form method called :" + method);
		if (method != null && method.equals(Methods.preview.toString())) {
			validateForPreview(request, errors);
		}else if (method != null && method.equals(Methods.previewManage.toString())) {
			validateForPreviewManage(request, errors);
		}
		if (method != null && !method.equals(Methods.validate.toString())) {
			request.setAttribute(ProductDefinitionConstants.METHODCALLED, method);
		}
		logger.debug("validate method of Fund Action form called and error size:" + errors.size());
		return errors;
	}

	private void validateForPreview(HttpServletRequest request,
			ActionErrors errors) {
		UserContext userContext = (UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT);
		Locale locale = userContext.getPreferredLocale();
		ResourceBundle resources = ResourceBundle.getBundle
				(FilePaths.FUND_UI_RESOURCE_PROPERTYFILE, locale);
		String fundName = resources.getString("funds.fundName");
		String fundCode = resources.getString("funds.fundCode");
		logger.debug("start validateForPreview method of Fund Action form method :"	+ fundName);
		if (StringUtils.isNullOrEmpty(getFundName()))
			addError(errors, "fundName", FundConstants.ERROR_MANDATORY, fundName);
		if (StringUtils.isNullOrEmpty(getFundCode()))
			addError(errors, "fundCode", FundConstants.ERROR_SELECT, fundCode);
		logger.debug("validateForPreview method of Fund Action form method called :" + fundName);
	}
	
	private void validateForPreviewManage(HttpServletRequest request,
			ActionErrors errors) {
		logger.debug("start validateForPreviewManage method of Fund Action form method :"	+ fundName);
		if (StringUtils.isNullOrEmpty(getFundName())) {
			UserContext userContext = (UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT);
			Locale locale = userContext.getPreferredLocale();
			ResourceBundle resources = ResourceBundle.getBundle
			(FilePaths.FUND_UI_RESOURCE_PROPERTYFILE, locale);
			String fundName = resources.getString("funds.fundName");
			addError(errors, "fundName", FundConstants.ERROR_MANDATORY, fundName);
		}
		logger.debug("validateForPreview method of Fund Action form method called :" + fundName);
	}
}
