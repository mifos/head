/**
 * 
 */
package org.mifos.application.accounts.loan.struts.actionforms;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.struts.actionforms.AccountApplyPaymentActionForm;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.util.helpers.Money;

public class LoanDisbursmentActionForm extends AccountApplyPaymentActionForm {

	private Money loanAmount;

	private String paymentModeOfPayment;

	@Override
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ResourceBundle resources = ResourceBundle.getBundle ("org.mifos.application.accounts.loan.util.resources.LoanUIResources", getUserLocale(request));
		
		String methodCalled= request.getParameter(MethodNameConstants.METHOD);
		ActionErrors errors =new ActionErrors();
		ActionErrors errors1 =super.validate(mapping, request);
		if(errors1!=null)errors.add(errors1);
		
		if(methodCalled!=null&& methodCalled.equals("preview")){
			
			if ( this.getAmount()!=null&&this.getAmount().getAmountDoubleValue()>0.0)
			if( this.paymentModeOfPayment==null||this.paymentModeOfPayment.equals("")){
				errors.add(AccountConstants.ERROR_MANDATORY,new ActionMessage(AccountConstants.ERROR_MANDATORY,resources.getString("loan.paymentid")));
			}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
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

	public void clear() {
		super.clear();
		this.loanAmount = null;
	}

}
