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
 
package org.mifos.application.acceptedpaymenttype.struts.actionform;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.acceptedpaymenttype.util.helpers.PaymentTypeData;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;



public class AcceptedPaymentTypeActionForm extends BaseActionForm{
	
	private MifosLogger logger = MifosLogManager
	.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
	
	private String[] fees;
	private String[] disbursements;
	private String[] repayments;
	private String[] withdrawals;
	private String[] deposits;
	List<PaymentTypeData> allPaymentTypes;
	
	
	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("Inside validate method");
		String method = request.getParameter(Methods.method.toString());
		request.setAttribute(Constants.CURRENTFLOWKEY, request
				.getParameter(Constants.CURRENTFLOWKEY));

		ActionErrors errors = new ActionErrors();
		if (method.equals(Methods.update.toString())) {
			errors = super.validate(mapping, request);
			
		} 
		if (!errors.isEmpty()) {
			request.setAttribute(Methods.method.toString(), method);
		}

		logger.debug("outside validate method");
		return errors;
	}
	
	public String[] getFees() {
		return fees;
	}

	public void setFees(String[] fees) {
		this.fees = fees;
	}

	// reset fields on form
	public void clear() 
	{
		this.fees = null;
		this.disbursements = null;
		this.repayments = null;
		this.withdrawals = null;
		this.deposits = null;
	}

	public String[] getDeposits() {
		return deposits;
	}

	public void setDeposits(String[] deposits) {
		this.deposits = deposits;
	}

	public String[] getDisbursements() {
		return disbursements;
	}

	public void setDisbursements(String[] disbursements) {
		this.disbursements = disbursements;
	}

	public String[] getRepayments() {
		return repayments;
	}

	public void setRepayments(String[] repayments) {
		this.repayments = repayments;
	}

	public String[] getWithdrawals() {
		return withdrawals;
	}

	public void setWithdrawals(String[] withdrawals) {
		this.withdrawals = withdrawals;
	}

	public List<PaymentTypeData> getAllPaymentTypes() {
		return allPaymentTypes;
	}

	public void setAllPaymentTypes(List<PaymentTypeData> allPaymentTypes) {
		this.allPaymentTypes = allPaymentTypes;
	}

	
}


