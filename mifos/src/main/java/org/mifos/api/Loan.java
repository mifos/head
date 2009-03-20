/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.api;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.service.LoanBusinessService;
import org.mifos.framework.exceptions.ServiceException;


public class Loan {
	
	private final LoanBO bo;
		
	Loan(LoanBO businessObject) throws ServiceException {
		bo = businessObject;
	}

	private static LoanBusinessService getBusinessService() {
		return new LoanBusinessService();
	}
	
	public static Loan getLoan(Integer id) throws Exception { 
		// We may want to check exceptions in this one...
		LoanBO loanBo = getBusinessService().getAccount(id);
		if (loanBo == null) {
			throw new Exception("Loan " + id + " not found");
		}
		return new Loan(loanBo);
	}
	
	public Integer getId() {
		return bo.getAccountId();
	}
	
	public String getBorrowerName() {
		return bo.getCustomer().getDisplayName();
	}
	
	public double getLoanBalance() {
		return bo.getLoanBalance().getAmountDoubleValue();
	}
	
	public String getLoanCurrency() {
		return bo.getTotalAmountDue().getCurrency().getCurrencyName();
	}
	
	// This is, in fact, a little more complicated.
	//public void writeOff(String reason) throws AccountException {
	//	bo.writeOff(reason);
	//}
}
