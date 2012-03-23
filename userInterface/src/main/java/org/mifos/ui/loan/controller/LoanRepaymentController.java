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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.application.servicefacade.ListItem;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.dto.screen.ExpectedPaymentDto;
import org.mifos.security.MifosUser;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller(value="loanRepaymentController")
public class LoanRepaymentController {

	private final LoanAccountServiceFacade loanAccountServiceFacade;
	private final AccountServiceFacade accountServiceFacade;
	
	@Autowired
    public LoanRepaymentController(LoanAccountServiceFacade loanAccountServiceFacade, AccountServiceFacade accountServiceFacade) {
		this.loanAccountServiceFacade = loanAccountServiceFacade;
		this.accountServiceFacade = accountServiceFacade;
    }
	
	// called by spring webflow
	public ExpectedPaymentDto retrieveExpectedPayment(final String loanGlobalAccountNumber, LoanRepaymentFormBean loanRepaymentFormBean) {
	
		LocalDate paymentDueAsOf = new LocalDate();
		
		ExpectedPaymentDto result = this.loanAccountServiceFacade.retrieveExpectedPayment(loanGlobalAccountNumber, paymentDueAsOf);
		
		Date lastPaymentDate = this.accountServiceFacade.retrieveLatPaymentDate(loanGlobalAccountNumber);
		LocalDate lastPaymentDateJoda =  null;
		if (lastPaymentDate != null) {
		    lastPaymentDateJoda = new LocalDate(lastPaymentDate);
		}
		
		loanRepaymentFormBean.setGlobalAccountNumber(result.getGlobalAccountNumber());
		loanRepaymentFormBean.setLastPaymentDate(lastPaymentDateJoda);
		
		if (loanRepaymentFormBean.getPaymentAmount().equals(BigDecimal.ZERO)) {
		    Double amount = Double.valueOf(result.getAmount().doubleValue());
    		loanRepaymentFormBean.setPaymentAmount(BigDecimal.valueOf(amount));
    		loanRepaymentFormBean.setPaymentDate(paymentDueAsOf);
		} else {
		    loanRepaymentFormBean.setPaymentDate(new LocalDate());
		}
		return result;
	}
	
	// called by spring webflow
	public String applyPayment(final String loanGlobalAccountNumber, LoanRepaymentFormBean loanRepaymentFormBean, MessageContext messageContext) {
		
		String returnCode = "";
		try {
			BigDecimal repaymentAmount = BigDecimal.valueOf(loanRepaymentFormBean.getPaymentAmount().doubleValue());
			
			this.loanAccountServiceFacade.applyLoanRepayment(loanGlobalAccountNumber, loanRepaymentFormBean.getPaymentDate(), repaymentAmount,
			        loanRepaymentFormBean.getReceiptId(), loanRepaymentFormBean.getReceiptDate(), loanRepaymentFormBean.getPaymentTypeId());
			returnCode = "success";
		} catch (BusinessRuleException e) {
			MessageBuilder builder = new MessageBuilder()
					.error()
					.source("paymentAmount")
					.codes(Arrays.asList(e.getMessageKey()).toArray(
							new String[1])).defaultText(e.getMessage())
					.args(e.getMessageValues());

			messageContext.addMessage(builder.build());
			
			returnCode = "error";
		} 
		
		return returnCode;
	}
	
	//called by spring webflow
	public Map<String, String> retrievePaymentTypes() {
	    MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    List<ListItem<Short>> paymentTypes = this.accountServiceFacade.constructPaymentTypeListForLoanRepayment(user.getPreferredLocaleId());
	    
	    Map<String, String> result = new HashMap<String, String>();
	    for (ListItem<Short> listItem : paymentTypes) {
	        result.put(String.valueOf(listItem.getId()), listItem.getDisplayValue());
	    }
	    
	    return result;
	}
}
