/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.ui.loan.controller;

import java.math.BigDecimal;

import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.dto.screen.ExpectedPaymentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller(value="loanRepaymentController")
public class LoanRepaymentController {

	private LoanAccountServiceFacade loanAccountServiceFacade;

	@Autowired
    public LoanRepaymentController(LoanAccountServiceFacade loanAccountServiceFacade) {
		this.loanAccountServiceFacade = loanAccountServiceFacade;
    }
	
	// called by spring webflow
	public ExpectedPaymentDto retrieveExpectedPayment(final String loanGlobalAccountNumber, LoanRepaymentFormBean loanRepaymentFormBean) {
	
		LocalDate paymentDueAsOf = new LocalDate();
		
		ExpectedPaymentDto result = this.loanAccountServiceFacade.retrieveExpectedPayment(loanGlobalAccountNumber, paymentDueAsOf);
		
		loanRepaymentFormBean.setGlobalAccountNumber(result.getGlobalAccountNumber());
		
		Double amount = Double.valueOf(result.getAmount().doubleValue());
		loanRepaymentFormBean.setPaymentAmount(BigDecimal.valueOf(amount));
		loanRepaymentFormBean.setPaymentDate(paymentDueAsOf);
		
		return result;
	}
	
	// called by spring webflow
	public void applyPayment(final String loanGlobalAccountNumber, LoanRepaymentFormBean loanRepaymentFormBean) {
		
		BigDecimal repaymentAmount = BigDecimal.valueOf(loanRepaymentFormBean.getPaymentAmount().doubleValue());
		
		this.loanAccountServiceFacade.applyLoanRepayment(loanGlobalAccountNumber, loanRepaymentFormBean.getPaymentDate(), repaymentAmount);
	}
}
