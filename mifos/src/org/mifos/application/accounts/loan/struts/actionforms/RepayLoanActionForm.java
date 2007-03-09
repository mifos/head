/**
 * 
 */
package org.mifos.application.accounts.loan.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.framework.util.helpers.Money;

public class RepayLoanActionForm extends ValidatorActionForm {

	private String accountId;
	private Money amount;
	private String receiptNumber;
	private String recieptDate;
	private String dateOfPayment;
	private String paymentTypeId;

	public RepayLoanActionForm() {}

	public String getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public String getDateOfPayment() {
		return dateOfPayment;
	}

	public void setDateOfPayment(String dateOfPayment) {
		this.dateOfPayment = dateOfPayment;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public String getRecieptDate() {
		return recieptDate;
	}

	public void setRecieptDate(String recieptDate) {
		this.recieptDate = recieptDate;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping, 
			HttpServletRequest request) {
		String method = request.getParameter("method");
		ActionErrors errors = new ActionErrors();
		if (method.equals("loadRepayment")||
			method.equals("makeRepayment")||
			method.equals("validate")||
			method.equals("previous")||
			method.equals("cancel")) {
			// nothing to validate (apparently)
		}
		else {
			errors.add(super.validate(mapping,request));			
		}
		
		if (!errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
	
		return errors;
	}
		
	
}
