/**
 * 
 */
package org.mifos.application.accounts.loan.struts.actionforms;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.productdefinition.util.valueobjects.PaymentType;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;

/**
 * @author krishankg
 *
 */
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

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		String method = request.getParameter("method");
		ActionErrors errors = null;
		if(method!=null && method.equals("loadRepayment")||
						method.equals("makeRepayment")||
						method.equals("validate")||
						method.equals("previous")||
						method.equals("cancel")){
		}else{
			errors = new ActionErrors();
			errors.add(super.validate(mapping,request));			
		}
		
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
	
		return errors;
	}
		
	
}
