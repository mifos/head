/**

 * AccountTrxnActionForm.java    version: xxx



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

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.util.valueobjects.AccountTrxn;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.PaymentType;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceConstants;

public class AccountTrxnActionForm extends MifosActionForm{

	/**
	 *
	 */
	private static final long serialVersionUID = 154646457657L;




	public  AccountTrxnActionForm() {
		this.paymentType=new PaymentType();
	}

	private String paymentId;
	private String accountId;
	private String accountType;
	private PaymentType paymentType;
	private String amount;
	private String receiptNumber;
	private String voucherNumber;
	private String checkNumber;
	private String accountNumber;
	private String bankName;
	private String paymentDate;
	private String installmentId;
	private String actionDate;
	private String dueDate;
	private String receiptDate;

	//private Set<AccountTrxn> accountTrxn=new HashSet<AccountTrxn>();

	/**
	 * @return Returns the accountId.
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return Returns the accountTrxn.
	 */
	/*public Set<AccountTrxn> getAccountTrxn() {
		return accountTrxn;
	}*/

	/**
	 * @param accountTrxn The accountTrxn to set.
	 */
	/*public void setAccountTrxn(Set<AccountTrxn> accountTrxn) {
		this.accountTrxn = accountTrxn;
	}*/

	/**
	 * @return Returns the accountType.
	 */
	public String getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType The accountType to set.
	 */
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	/**
	 * @return Returns the actionDate.
	 */
	public String getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate The actionDate to set.
	 */
	public void setActionDate(String actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @return Returns the amount.
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount The amount to set.
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @return Returns the bankName.
	 */
	public String getBankName() {
		return bankName;
	}

	/**
	 * @param bankName The bankName to set.
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	/**
	 * @return Returns the checkNumber.
	 */
	public String getCheckNumber() {
		return checkNumber;
	}

	/**
	 * @param checkNumber The checkNumber to set.
	 */
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	/**
	 * @return Returns the dueDate.
	 */
	public String getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate The dueDate to set.
	 */
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * @return Returns the installmentId.
	 */
	public String getInstallmentId() {
		return installmentId;
	}

	/**
	 * @param installmentId The installmentId to set.
	 */
	public void setInstallmentId(String installmentId) {
		this.installmentId = installmentId;
	}

	/**
	 * @return Returns the paymentDate.
	 */
	public String getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate The paymentDate to set.
	 */
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return Returns the paymentId.
	 */
	public String getPaymentId() {
		return paymentId;
	}

	/**
	 * @param paymentId The paymentId to set.
	 */
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	/**
	 * @return Returns the paymentType.
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}

	/**
	 * @param paymentType The paymentType to set.
	 */
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * @return Returns the receiptNumber.
	 */
	public String getReceiptNumber() {
		return receiptNumber;
	}

	/**
	 * @param receiptNumber The receiptNumber to set.
	 */
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	/**
	 * @return Returns the voucherNumber.
	 */
	public String getVoucherNumber() {
		return voucherNumber;
	}

	/**
	 * @param voucherNumber The voucherNumber to set.
	 */
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("inside AccountTrxnActionForm with reset method : ");
		if(request.getParameter(MethodNameConstants.METHOD) !=null && request.getParameter(MethodNameConstants.METHOD).equals(MethodNameConstants.LOAD)) {
			accountId=null;
			paymentType=new PaymentType();
			amount=null;
			receiptNumber=null;
			voucherNumber=null;
			checkNumber=null;
			bankName=null;
			receiptDate=null;
			paymentDate=DateHelper.getCurrentDate(getUserLocale(request));
			//dateOfTrxn=DateHelper.getCurrentDate(getUserLocale(request));
		}
	}
/*
	public ActionErrors validate(ActionMapping arg0, ServletRequest arg1) {
		//do custom validations here
		return null;
	}*/

	public ActionErrors customValidate(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
		String methodCalled= request.getParameter(MethodNameConstants.METHOD);
		// System.out.println("Action form method called; "+ methodCalled );
		if(null !=methodCalled) {
			if( MethodNameConstants.LOAD.equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}else if(MethodNameConstants.CREATE.equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}else if(MethodNameConstants.GETINSTALLMENTHISTORY.equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}else if(MethodNameConstants.CANCEL.equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}else if(MethodNameConstants.PREVIOUS.equals(methodCalled)) {
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}

		}
		return null;
	}

	/**
	 * @return Returns the accountNumber.
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber The accountNumber to set.
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return Returns the receiptDate.
	 */
	public String getReceiptDate() {
		return receiptDate;
	}

	/**
	 * @param receiptDate The receiptDate to set.
	 */
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
}
