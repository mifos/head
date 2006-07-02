/**

 * AccountsApplyChargesActionForm.java    version: 1.0

 

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
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;

/**
 * @author rajenders
 *
 */
public class AccountsApplyChargesActionForm extends MifosActionForm {
	
	private static final long serialVersionUID=8844444456456456l;
	
	/**
	 * This would hold the chargetype
	 */
	private String chargeType;
	/**
	 * This would hold the fee amount
	 */
	private String chargeAmount;
	/**
	 * This function returns the chargeAmount
	 * @return Returns the chargeAmount.
	 */
	
	private String formula;
	
	private String globalAccountNum;
	
	
	
	private String formulaplaceHolder;
	
	
	private String accountId;

	
	private String selectedChargeAmount;
	
	public String getSelectedChargeAmount() {
		return selectedChargeAmount;
	}


	public void setSelectedChargeAmount(String selectedChargeAmount) {
		this.selectedChargeAmount = selectedChargeAmount;
	}
	
	
	public String getAccountId() {
		return accountId;
	}


	/**
	 * This function sets the accountId
	 * @param accountId the accountId to set.
	 */
	
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}


	/**
	 * This function returns the formula
	 * @return Returns the formula.
	 */
	
	public String getFormula() {
		return formula;
	}
	
	
	/**
	 * This function sets the formula
	 * @param formula the formula to set.
	 */
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public String getChargeAmount() {
		return chargeAmount;
	}
	
	/**
	 * This function sets the chargeAmount
	 * @param chargeAmount the chargeAmount to set.
	 */
	
	public void setChargeAmount(String chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	/**
	 * This function returns the chargeType
	 * @return Returns the chargeType.
	 */
	
	public String getChargeType() {
		return chargeType;
	}
	/**
	 * This function sets the chargeType
	 * @param chargeType the chargeType to set.
	 */
	
	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}
	/**
	 * This function returns the formulaplaceHolder
	 * @return Returns the formulaplaceHolder.
	 */
	
	public String getFormulaplaceHolder() {
		return formulaplaceHolder;
	}
	/**
	 * This function sets the formulaplaceHolder
	 * @param formulaplaceHolder the formulaplaceHolder to set.
	 */
	
	public void setFormulaplaceHolder(String formulaplaceHolder) {
		this.formulaplaceHolder = formulaplaceHolder;
	}
	public ActionErrors customValidate(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
		String methodCalled= request.getParameter(MethodNameConstants.METHOD);
		
		if(null !=methodCalled) {
			if(MethodNameConstants.CANCEL.equals(methodCalled)||MethodNameConstants.LOAD.equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}	
		}
		return null;	
	}


	/**
	 * This function returns the globalAccountNum
	 * @return Returns the globalAccountNum.
	 */
	
	public String getGlobalAccountNum() {
		return globalAccountNum;
	}


	/**
	 * This function sets the globalAccountNum
	 * @param globalAccountNum the globalAccountNum to set.
	 */
	
	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}
}
