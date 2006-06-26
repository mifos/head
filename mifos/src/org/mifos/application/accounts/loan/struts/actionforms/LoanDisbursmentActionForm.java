/**
 * 
 */
package org.mifos.application.accounts.loan.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.actionforms.MifosActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * @author krishankg
 *
 */
public class LoanDisbursmentActionForm extends MifosActionForm  {
	
	private String accountId;
	
	private String disbursmentDate;
	
	private String receiptId;
	
	private String receiptDate;
	
	private String disbursmentModeOfPayment;
	
	private String paymentModeOfPayment;
	
	private String loanAmount;
	
	private String interestAmount;
	
	/*
	 * This is the custom method to do any custom validations. This method is
	 * also used to specify the methods where validation is to be skipped.
	 *
	 * @param mapping
	 * @param request
	 * @return
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String methodCalled = request.getParameter("method");
				if (null != methodCalled) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("In Disburse Loan Action Form Custom Validate");
			if ("load".equals(methodCalled))
					 {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			}
		}
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).info("End");		
		return null;
	}

	/**
	 * @return Returns the disbursmentDate.
	 */
	public String getDisbursmentDate() {
		return disbursmentDate;
	}

	/**
	 * @param disbursmentDate The disbursmentDate to set.
	 */
	public void setDisbursmentDate(String disbursmentDate) {
		this.disbursmentDate = disbursmentDate;
	}

	/**
	 * @return Returns the disbursmentModeOfPayment.
	 */
	public String getDisbursmentModeOfPayment() {
		return disbursmentModeOfPayment;
	}

	/**
	 * @param disbursmentModeOfPayment The disbursmentModeOfPayment to set.
	 */
	public void setDisbursmentModeOfPayment(String disbursmentModeOfPayment) {
		this.disbursmentModeOfPayment = disbursmentModeOfPayment;
	}

	/**
	 * @return Returns the interestAmount.
	 */
	public String getInterestAmount() {
		return interestAmount;
	}

	/**
	 * @param interestAmount The interestAmount to set.
	 */
	public void setInterestAmount(String interestAmount) {
		this.interestAmount = interestAmount;
	}

	/**
	 * @return Returns the loanAmount.
	 */
	public String getLoanAmount() {
		return loanAmount;
	}

	/**
	 * @param loanAmount The loanAmount to set.
	 */
	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	/**
	 * @return Returns the paymentModeOfPayment.
	 */
	public String getPaymentModeOfPayment() {
		return paymentModeOfPayment;
	}

	/**
	 * @param paymentModeOfPayment The paymentModeOfPayment to set.
	 */
	public void setPaymentModeOfPayment(String paymentModeOfPayment) {
		this.paymentModeOfPayment = paymentModeOfPayment;
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

	/**
	 * @return Returns the receiptId.
	 */
	public String getReceiptId() {
		return receiptId;
	}

	/**
	 * @param receiptId The receiptId to set.
	 */
	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}

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

		

}
