/**

 * AccountsApplyCharges.java    version: 1.0

 

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
package org.mifos.application.accounts.util.valueobjects;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This is place holder value object for apply charges action 
 * @author rajenders
 *
 */
public class AccountsApplyCharges extends ValueObject {
	private static final long serialVersionUID =22233l;
	
	/**
	 * This would hold the type of charge
	 */
	private Short chargeType;
	/**
	 * Thsi would hold the amount we want to apply
	 */
	private Double chargeAmount;
	
	/**
	 * Thsi would hold the accountId
	 */
	private Integer accountId;
	
	/**
	 * This would hold the information from where we are coming 
	 */
	private String input;

	/**
	 * This function returns the input
	 * @return Returns the input.
	 */
	
	public String getInput() {
		return input;
	}
	/**
	 * This function sets the input
	 * @param input the input to set.
	 */
	
	public void setInput(String input) {
		this.input = input;
	}
	/**
	 * @return Returns the accountId.
	 */
	public Integer getAccountId() {
		return accountId;
	}
	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	/**
	 * This function returns the chargeAmount
	 * @return Returns the chargeAmount.
	 */
	
	public Double getChargeAmount() {
		return chargeAmount;
	}
	/**
	 * This function sets the chargeAmount
	 * @param chargeAmount the chargeAmount to set.
	 */
	
	public void setChargeAmount(Double chargeAmount) {
		this.chargeAmount = chargeAmount;
	}
	/**
	 * This function returns the chargeType
	 * @return Returns the chargeType.
	 */
	
	public Short getChargeType() {
		return chargeType;
	}
	/**
	 * This function sets the chargeType
	 * @param chargeType the chargeType to set.
	 */
	
	public void setChargeType(Short chargeType) {
		this.chargeType = chargeType;
	}
	

}
