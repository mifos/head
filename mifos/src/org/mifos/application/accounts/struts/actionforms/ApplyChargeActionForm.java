package org.mifos.application.accounts.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class ApplyChargeActionForm extends BaseActionForm {

	private String accountId;

	private String chargeType;

	private String chargeAmount;
	
	private String charge;
	
	private String selectedChargeFormula;
	
	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(String chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public String getChargeType() {
		return chargeType;
	}

	public void setChargeType(String chargeType) {
		this.chargeType = chargeType;
	}

	public String getCharge() {
		return charge;
	}

	public void setCharge(String charge) {
		this.charge = charge;
	}
		
	public String getSelectedChargeFormula() {
		return selectedChargeFormula;
	}

	public void setSelectedChargeFormula(String selectedChargeFormula) {
		this.selectedChargeFormula = selectedChargeFormula;
	}

	@Override
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		String methodCalled = request.getParameter(Methods.method.toString());
		if (null != methodCalled) {
			if ((Methods.update.toString()).equals(methodCalled)) {
				if(!StringUtils.isNullOrEmpty(selectedChargeFormula)){
					validateRate(errors, request);
					
				}
				errors.add(super.validate(mapping, request));
			}
		}
		if (null != errors && !errors.isEmpty()) {
			request.setAttribute(Globals.ERROR_KEY, errors);
			request.setAttribute("methodCalled", methodCalled);
		}
		return errors;
	}

	private void validateRate(ActionErrors errors,HttpServletRequest request) {
		if(getDoubleValue(chargeAmount) > Double.valueOf("999")){
			errors.add(AccountConstants.RATE,
					new ActionMessage(AccountConstants.RATE_ERROR));
			request.setAttribute("selectedChargeFormula" ,selectedChargeFormula);
		}
		
	}

}
