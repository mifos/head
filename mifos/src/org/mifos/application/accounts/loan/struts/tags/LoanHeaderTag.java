/**

 * LoanHeaderTag.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.accounts.loan.struts.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.mifos.application.accounts.loan.business.util.helpers.LoanHeaderObject;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.CustomerMaster;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

/**
 * This tag class is used to display header links on the loan account related pages
 * @author ashishsm
 *
 */
public class LoanHeaderTag extends TagSupport {

	/**
	 * 
	 */
	public LoanHeaderTag() {
		super();
		
	}
	
	private LoanHeaderObject loanHeader = new LoanHeaderObject(); 
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.Tag#doStartTag()
	 */
	public int doStartTag() throws JspException {
		
		
		
				
    
    	StringBuilder link = new StringBuilder();
			
		link = link.append(getOfficeLink());
		link = link.append(getCenterLink());
		link = link.append(getGroupLink());
		link = link.append(getClientLink());
		try {
			pageContext.getOut().write(link.toString());
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	/**
	 * This method returns the client link.
	 * @return
	 */
	private Object getClientLink() {
		StringBuilder clientLink = new StringBuilder();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside client link creation");
		CustomerMaster clientCustomerMaster = getCustomerMaster(CustomerConstants.CLIENT_LEVEL_ID);
		if(null != clientCustomerMaster){
			clientLink.append("<a href=\"clientCreationAction.do?method=getDetails&globalCustNum="+clientCustomerMaster.getGlobalCustNum()+"&recordOfficeId="+clientCustomerMaster.getOfficeId()+"&recordLoanOfficerId="+clientCustomerMaster.getPersonnelId()+"\">"+ clientCustomerMaster.getDisplayName()+"</a> / ");
		}
		return clientLink;
	}

	/**
	 * This method returns the group link
	 * @return
	 */
	private Object getGroupLink() {
		StringBuilder groupLink = new StringBuilder();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside group link creation");
		CustomerMaster groupCustomerMaster = getCustomerMaster(CustomerConstants.GROUP_LEVEL_ID);
		if(null != groupCustomerMaster){
			groupLink.append("<a href=\"GroupAction.do?method=getDetails&globalCustNum="+groupCustomerMaster.getGlobalCustNum()+"&recordOfficeId="+groupCustomerMaster.getOfficeId()+"&recordLoanOfficerId="+groupCustomerMaster.getPersonnelId()+"\">"+ groupCustomerMaster.getDisplayName()+"</a> / ");
		}
		return groupLink.toString();
	}

	/**
	 * returns the customer master based on the customer level id.
	 * @return
	 */
	private CustomerMaster getCustomerMaster(short customerLevel) {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside getCustomerMaster of table tag.");
		List<CustomerMaster> customerMasterList = loanHeader.getCustomerMasterList();
		if(null != customerMasterList && ! customerMasterList.isEmpty()){
			for(CustomerMaster customerMaster : customerMasterList){
				if(customerLevel == customerMaster.getCustomerLevelId()){
					return customerMaster;
				}
			}
		}
		return null;
	}

	/**
	 * This method returns the center link
	 * @return
	 */
	private Object getCenterLink() {
		StringBuilder centerLink = new StringBuilder();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside center link creation");
		CustomerMaster centerCustomerMaster = getCustomerMaster(CustomerConstants.CENTER_LEVEL_ID);
		if(null != centerCustomerMaster){
			centerLink.append("<a href=\"centerAction.do?method=getDetails&globalCustNum="+centerCustomerMaster.getGlobalCustNum()+"&recordOfficeId="+centerCustomerMaster.getOfficeId()+"&recordLoanOfficerId="+centerCustomerMaster.getPersonnelId()+"\">"+ centerCustomerMaster.getDisplayName()+"</a> / ");
		}
		
		return centerLink.toString();
	}

	/**
	 * This method returns the office link.
	 * @return
	 */
	private Object getOfficeLink() {
		StringBuilder officeLink = new StringBuilder();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside office link creation");
		officeLink.append("<a href=\"CustomerSearchAction.do?method=getOfficeHomePage&officeId="+loanHeader.getOfficeId()+"&recordOfficeId=0&recordLoanOfficerId=0"+"\">"+loanHeader.getOfficeName()+"</a> / ");
		return officeLink.toString();
	}

	/**
	 * @return Returns the loanHeader}.
	 */
	public LoanHeaderObject getLoanHeader() {
		return loanHeader;
	}

	/**
	 * @param loanHeader The loanHeader to set.
	 */
	public void setLoanHeader(LoanHeaderObject loanHeader) {
		this.loanHeader = loanHeader;
	}
	
	

}
