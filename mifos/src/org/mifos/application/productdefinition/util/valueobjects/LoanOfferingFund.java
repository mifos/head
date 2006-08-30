/**

 * LoanOfferingFund.java    version: xxx

 

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

package org.mifos.application.productdefinition.util.valueobjects;

import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class acts as value object for LoanOfferingFund.
 * This denotes the funds for loan offering.
 */
public class LoanOfferingFund extends ValueObject {
	/**
	 * default constructor
	 */
	public LoanOfferingFund() {
	}
	/**
	 * serial version UID for serialization
	 */
	private static final long serialVersionUID = 453656767567887681L;
	/** 
	 * This corresponds to loanOfferingFundId  
	 */
	private Short loanOfferingFundId;
	
	/**
	 * This corresponds to associated fund object.  
	 */
	private Fund fund;
	
	/**
	 * This corresponds to associated loan offering object.
	 */
	private LoanOffering loanOffering;

	/**
	 * @return Returns the fund}.
	 */
	public Fund getFund() {
		return fund;
	}

	/**
	 * @param fund The fund to set.
	 */
	public void setFund(Fund fund) {
		this.fund = fund;
	}

	/**
	 * @return Returns the loanOffering}.
	 */
	public LoanOffering getLoanOffering() {
		return loanOffering;
	}

	/**
	 * @param loanOffering The loanOffering to set.
	 */
	public void setLoanOffering(LoanOffering loanOffering) {
		this.loanOffering = loanOffering;
	}

	/**
	 * @return Returns the loanOfferingFundId}.
	 */
	public Short getLoanOfferingFundId() {
		return loanOfferingFundId;
	}

	/**
	 * @param loanOfferingFundId The loanOfferingFundId to set.
	 */
	public void setLoanOfferingFundId(Short loanOfferingFundId) {
		this.loanOfferingFundId = loanOfferingFundId;
	}

	public boolean equals(Object object) {
		LoanOfferingFund loanOfferingFund=null;
		boolean value=false;
		if(object !=null) {
			loanOfferingFund=(LoanOfferingFund)object;
			if(loanOfferingFund.getLoanOfferingFundId().equals(this.loanOfferingFundId)) {
				value= true;
			}
		}
		return value;
	}
	
	

}
