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

public class ApplyAdjustmentActionForm extends MifosActionForm{

	private String accountId;
	
	private String paymentId;
	
	private String amount;
	
	private String note;
	
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
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("In apply adjustment Action Form Custom Validate");
			if ("load".equals(methodCalled))
					 {
				request.setAttribute(Constants.SKIPVALIDATION, Boolean
						.valueOf(true));
			}
		}
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("End");		
		return null;
	}


	/**
	 * @return Returns the note.
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note The note to set.
	 */
	public void setNote(String note) {
		this.note = note;
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
}
