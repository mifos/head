/**

 * AccountProcessFlowActionForm.java    version: xxx

 

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

package org.mifos.application.productdefinition.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.mifos.framework.struts.actionforms.MifosActionForm;

/**
 * This class might need to have list of value objects in it.
 */
public class AccountProcessFlowActionForm extends MifosActionForm {

	private static final long serialVersionUID = 97784575656663211L;

	public AccountProcessFlowActionForm() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	private String clientsSubmitApproval;
	private String groupsSubmitApproval;
	private String loansSubmitApproval;
	private String loanDisbursLO;
	private String savingsSubmitApproval;
	private String insuranceSubmitApproval;

	/**
	 * @return Returns the clientsSubmitApproval.
	 */
	public String getClientsSubmitApproval() {
		return clientsSubmitApproval;
	}
	/**
	 * @param clientsSubmitApproval The clientsSubmitApproval to set.
	 */
	public void setClientsSubmitApproval(String clientsSubmitApproval) {
		this.clientsSubmitApproval = clientsSubmitApproval;
	}
	/**
	 * @return Returns the groupsSubmitApproval.
	 */
	public String getGroupsSubmitApproval() {
		return groupsSubmitApproval;
	}
	/**
	 * @param groupsSubmitApproval The groupsSubmitApproval to set.
	 */
	public void setGroupsSubmitApproval(String groupsSubmitApproval) {
		this.groupsSubmitApproval = groupsSubmitApproval;
	}
	/**
	 * @return Returns the insuranceSubmitApproval.
	 */
	public String getInsuranceSubmitApproval() {
		return insuranceSubmitApproval;
	}
	/**
	 * @param insuranceSubmitApproval The insuranceSubmitApproval to set.
	 */
	public void setInsuranceSubmitApproval(String insuranceSubmitApproval) {
		this.insuranceSubmitApproval = insuranceSubmitApproval;
	}
	/**
	 * @return Returns the loanDisbursLO.
	 */
	public String getLoanDisbursLO() {
		return loanDisbursLO;
	}
	/**
	 * @param loanDisbursLO The loanDisbursLO to set.
	 */
	public void setLoanDisbursLO(String loanDisbursLO) {
		this.loanDisbursLO = loanDisbursLO;
	}
	/**
	 * @return Returns the loansSubmitApproval.
	 */
	public String getLoansSubmitApproval() {
		return loansSubmitApproval;
	}
	/**
	 * @param loansSubmitApproval The loansSubmitApproval to set.
	 */
	public void setLoansSubmitApproval(String loansSubmitApproval) {
		this.loansSubmitApproval = loansSubmitApproval;
	}
	/**
	 * @return Returns the savingsSubmitApproval.
	 */
	public String getSavingsSubmitApproval() {
		return savingsSubmitApproval;
	}
	/**
	 * @param savingsSubmitApproval The savingsSubmitApproval to set.
	 */
	public void setSavingsSubmitApproval(String savingsSubmitApproval) {
		this.savingsSubmitApproval = savingsSubmitApproval;
	}
	/* (non-Javadoc)
	 * @see org.apache.struts.validator.ValidatorForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		clientsSubmitApproval=null;
		groupsSubmitApproval=null;
		loansSubmitApproval=null;
		loanDisbursLO=null;
		savingsSubmitApproval=null;
		insuranceSubmitApproval=null;
	}
	
}
