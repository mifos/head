/**

 * ApplyChargesActionForm.java    version: xxx

 

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

import org.mifos.framework.struts.actionforms.MifosActionForm;

/**
 * This class is used as ActionForm for Applying Charges for customers.
 * @author ashishsm
 *
 */
public class ApplyChargesActionForm extends MifosActionForm {

	/**
	 * 
	 */
	public ApplyChargesActionForm() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This indicates the level of the customer for which charges are being applied.
	 * This comes as a request parameter on the click on the link in the UI.
	 */
	private String customerLevel;
	
	/**
	 * This indicates the customerId for which the fees is being added.
	 */
	private String customerId;
	
	/**
	 * FeeId of the fee selected by the user in the UI. 
	 */
	private String feeId;
	
	/**
	 * Fee Amount of the fee selected by the user in the UI.
	 */
	private String feeAmount;
	
	/**
	 * This indicates the accountFeeId of the recurring fee to be removed from the customerAccount.
	 * 
	 */
	private String accountFeeId;

	/**
	 * @return Returns the customerLevel}.
	 */
	public String getCustomerLevel() {
		return customerLevel;
	}

	/**
	 * @param customerLevel The customerLevel to set.
	 */
	public void setCustomerLevel(String customerLevel) {
		this.customerLevel = customerLevel;
	}

	/**
	 * @return Returns the customerId}.
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId The customerId to set.
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return Returns the feeAmount}.
	 */
	public String getFeeAmount() {
		return feeAmount;
	}

	/**
	 * @param feeAmount The feeAmount to set.
	 */
	public void setFeeAmount(String feeAmount) {
		this.feeAmount = feeAmount;
	}

	/**
	 * @return Returns the feeId}.
	 */
	public String getFeeId() {
		return feeId;
	}

	/**
	 * @param feeId The feeId to set.
	 */
	public void setFeeId(String feeId) {
		this.feeId = feeId;
	}

	/**
	 * @return Returns the accountFeeId}.
	 */
	public String getAccountFeeId() {
		return accountFeeId;
	}

	/**
	 * @param accountFeeId The accountFeeId to set.
	 */
	public void setAccountFeeId(String accountFeeId) {
		this.accountFeeId = accountFeeId;
	}

}
