/**

 * AccountApplyPaymentActionForm.java    version: xxx

 

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

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;

public class AccountApplyPaymentActionForm extends BaseActionForm {
	private String input;

	private String transactionDate;

	private Money amount;

	private String receiptId;

	private String receiptDate;

	private String paymentTypeId;

	private String globalAccountNum;

	private String accountId;

	private String prdOfferingName;

	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public String getInput() {
		return input;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		String methodCalled = request.getParameter(MethodNameConstants.METHOD);
		ActionErrors errors = null;
		ResourceBundle resources = ResourceBundle
				.getBundle(
						"org.mifos.application.accounts.util.resources.accountsUIResources",
						getUserLocale(request));

		if (methodCalled != null && methodCalled.equals("preview")) {
			errors = new ActionErrors();
			ActionErrors errors2 = validateDate(this.transactionDate, resources
					.getString("accounts.date_of_trxn"), request);
			if (null != errors2 && !errors2.isEmpty())
				errors.add(errors2);
			if (this.paymentTypeId == null || this.paymentTypeId.equals("")) {
				errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
						AccountConstants.ERROR_MANDATORY, resources
								.getString("accounts.mode_of_payment")));
			}
			if (this.receiptDate != null && !this.receiptDate.equals("")) {
				errors2 = validateDate(this.receiptDate, resources
						.getString("accounts.receiptdate"), request);
				if (null != errors2 && !errors2.isEmpty())
					errors.add(errors2);
			}
			String accountType = request.getParameter("accountType");
			if (accountType != null
					&& Short.valueOf(accountType).equals(
							AccountTypes.LOANACCOUNT.getValue())) {
				if (amount == null || amount.getAmountDoubleValue() <= 0.0) {
					errors.add(AccountConstants.ERROR_MANDATORY,
							new ActionMessage(AccountConstants.ERROR_MANDATORY,
									resources.getString("accounts.amt")));
				}
			}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
	}

	protected ActionErrors validateDate(String date, String fieldName,
			HttpServletRequest request) {
		ActionErrors errors = null;
		java.sql.Date sqlDate = null;
		if (date != null && !date.equals("")) {
			try {
				sqlDate = DateHelper.getDateAsSentFromBrowser(date);
				if (DateHelper.whichDirection(sqlDate) > 0) {
					errors = new ActionErrors();
					errors.add(AccountConstants.ERROR_FUTUREDATE,
							new ActionMessage(
									AccountConstants.ERROR_FUTUREDATE,
									fieldName));
				}
			}
			catch (InvalidDateException e) {
				errors = new ActionErrors();
				errors.add(AccountConstants.ERROR_INVALIDDATE,
						new ActionMessage(AccountConstants.ERROR_INVALIDDATE,
								fieldName));
			}
		}
		else {
			errors = new ActionErrors();
			errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
					AccountConstants.ERROR_MANDATORY, fieldName));
		}
		return errors;
	}

	protected Locale getUserLocale(HttpServletRequest request) {
		Locale locale = null;
		HttpSession session = request.getSession();
		if (session != null) {
			UserContext userContext = (UserContext) session
					.getAttribute(LoginConstants.USERCONTEXT);
			if (null != userContext) {
				locale = userContext.getPereferedLocale();
				if (null == locale) {
					locale = userContext.getMfiLocale();
				}
			}
		}
		return locale;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}

	protected void clear() {
		this.amount = null;
		this.paymentTypeId = null;
		this.receiptDate = null;
		this.receiptId = null;
	}
}
