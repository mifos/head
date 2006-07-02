/**

 * AccountSearchActionForm.java    version: 1.0

 

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
package org.mifos.application.accounts.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.ValidateMethods;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * @author rajenders
 *
 */
public class AccountsSearchActionForm extends MifosSearchActionForm {
	private static final long serialVersionUID=8844444444444444444l;
	private String searchType;
	/**
	 * This function returns the searchType
	 * @return Returns the searchType.
	 */
	
	public String getSearchType() {
		return searchType;
	}
	/**
	 * This function sets the searchType
	 * @param searchType the searchType to set.
	 */
	
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	

	
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		
		 MifosLogger accountsLogger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
		ActionErrors errors=new ActionErrors();
		String methodCalled= request.getParameter("method");
		if(null !=methodCalled) {
			accountsLogger.info("In Account Search Action Form Custom Validate");
			if(!(MethodNameConstants.SEARCH.equals(methodCalled))) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
			if(MethodNameConstants.SEARCH.equals(methodCalled)) {
				String searchString=getSearchNode(AccountConstants.SEARCH_STRING);
				accountsLogger.info("Passed value of search string"+searchString);	
				if(ValidateMethods.isNullOrBlank(searchString)){
					
					accountsLogger.info("Search string is null so putting the errors");	
					errors.add(AccountConstants.NO_SEARCH_STRING,new ActionMessage(AccountConstants.NO_SEARCH_STRING));
					
				}
			}
		}
		return errors;
	}

}
