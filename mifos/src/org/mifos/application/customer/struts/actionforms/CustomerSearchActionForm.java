/**

 * CustomerSearchActionForm.java    version: xxx

 

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

package org.mifos.application.customer.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class is used as ActionForm to search for customers.
 */
public class CustomerSearchActionForm extends MifosSearchActionForm {

	public CustomerSearchActionForm() {
	}
	
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = -8223886736340566424L;
	
	/**
	 * Loan Officer Id
	 */
	private String loanOfficerId;
	
	/**
	 * Office id under which the loan officers are to be searched.
	 */
	private String officeId;
	
	/**
	 * Office Name under which the loan officers are to be searched.
	 */
	private String officeName;
	
	/**
	 * @return Returns the officeName.
	 */
	public String getOfficeName() {		
		return officeName;
	}

	/**
	 * @param officeName The officeName to set.
	 */
	public void setOfficeName(String officeName) {		
		this.officeName = officeName;
	}

	/**
	 * @return Returns the officeId.
	 */
	public String getOfficeId() {		
		return officeId;
	}

	/**
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	/**
	 * @return Returns the loanOfficerId.
	 */
	public String getLoanOfficerId() {
		return loanOfficerId;
	}

	/**
	 * @param loanOfficerId The loanOfficerId to set.
	 */
	public void setLoanOfficerId(String loanOfficerId) {		
		this.loanOfficerId = loanOfficerId;
	}
	
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors=new ActionErrors();
		String methodCalled= request.getParameter("method");		
		if(null !=methodCalled) {
			MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info("In Savings Product Action Form Custom Validate");
			if(!"search".equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}else if("getHomePage".equals(methodCalled)  || "loadAllBranches".equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
			
			if("search".equals(methodCalled)) {
				String searchString=getSearchNode(CustomerSearchConstants.CUSTOMERSEARCSTRING);
				String searchBranch=getSearchNode(CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID);
				if(ValidateMethods.isNullOrBlank(searchString)){
					errors.add(CustomerSearchConstants.NAMEMANDATORYEXCEPTION,
							new ActionMessage(CustomerSearchConstants.NAMEMANDATORYEXCEPTION));
					
				}
				if(ValidateMethods.isNullOrBlank(searchBranch)){
					errors.add(CustomerSearchConstants.OFFICEIDMANDATORYEXCEPTION,
							new ActionMessage(CustomerSearchConstants.OFFICEIDMANDATORYEXCEPTION));
					
				}
			}
		}
		return errors;
	}
}
