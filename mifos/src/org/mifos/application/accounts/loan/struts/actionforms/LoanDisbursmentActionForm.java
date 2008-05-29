/**
 * 
 */
package org.mifos.application.accounts.loan.struts.actionforms;

import static org.mifos.framework.util.helpers.StringUtils.isNullOrEmpty;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;

public class LoanDisbursmentActionForm extends AccountApplyPaymentActionForm {

	private Money loanAmount;

	private String paymentModeOfPayment;

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		ActionErrors errors1 = super.validate(mapping, request);
		if (errors1 != null)
			errors.add(errors1);

		String method = request.getParameter(MethodNameConstants.METHOD);
		if (isPreviewMethod(method) && isAmountGreaterThanZero(getAmount())
				&& isNullOrEmpty(paymentModeOfPayment)) {
			String errorMessage = getResourceBundle(getUserLocale(request)).getString("loan.paymentid");
			errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(
					AccountConstants.ERROR_MANDATORY, errorMessage));
		}

		if (!errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", method);
		}
		return errors;
	}

	private ResourceBundle getResourceBundle(Locale userLocale) {
		return ResourceBundle.getBundle(
				FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE, userLocale);
	}

	private boolean isAmountGreaterThanZero(Money amount) {
		return amount != null && amount.getAmountDoubleValue() > 0.0;
	}

	private boolean isPreviewMethod(String methodName) {
		return methodName != null && methodName.equals(MethodNameConstants.PREVIEW);
	}

	public String getPaymentModeOfPayment() {
		return paymentModeOfPayment;
	}

	public void setPaymentModeOfPayment(String paymentModeOfPayment) {
		this.paymentModeOfPayment = paymentModeOfPayment;
	}

	public Money getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(Money loanAmount) {
		this.loanAmount = loanAmount;
	}

	@Override
	public void clear() {
		super.clear();
		this.loanAmount = null;
	}

}
